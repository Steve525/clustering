package hac;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import toolkit.Matrix;

public class Cluster {

	private String _name;

	private Cluster _parent;

	private List<Cluster> _children;

	private Double _distance;
	
	private double[] centroid;
	
	private Set<Integer> instances;
	
	private static double MISSING = Double.MAX_VALUE;
	
	public void setInstance(int i) {
		instances.add(i);
	}
	
	public Set<Integer> getInstances() { return instances; }
	
	public void updateCentroid(Matrix features) {
		centroid = new double[features.cols()];
		for (int i = 1; i < features.cols(); i++) {
			
			int attributeValues = features.valueCount(i);
			if (attributeValues == 0) {
				
				centroid[i] =
						features.averagedContinuousValue(i, instances);
				
				
			}
			else {
				
				centroid[i] = features.mostCommonValue(i, instances);
				
				
			}
			
		}
		
	}
	
	public double getSSE (Matrix features) {
		assert(!instances.isEmpty());
		double sse = 0;
		for (int instance : instances) {
			double[] x = features.row(instance);
			sse += getEuclidDistance(x , centroid, features);
		}
		
		return sse;
	}
	
	public double[] getCentroid() { return centroid; }
	
	private static double getEuclidDistance ( double[] x
			, double[] y
			, Matrix features) {

		assert(x.length == y.length);
		double distance = 0;
		for (int k = 1; k < x.length; k++) {
		
			if (x[k] == MISSING || y[k] == MISSING) {
				distance += 1;
			}
			else {
				int attributeValues = features.valueCount(k);
				if (attributeValues == 0) {	// attribute is continuous
					double tmp = x[k] - y[k];
					distance += (tmp * tmp);
				}
				else {	// attribute is nominal
					if (x[k] != y[k])
					distance += 1;
				}
			}
		
		}
		return Math.abs(distance);
	}

	public Double getDistance() {
		return _distance;
	}

	public void setDistance(Double distance) {
		this._distance = distance;
	}

	public List<Cluster> getChildren() {
		if (_children == null) {
			_children = new ArrayList<Cluster>();
		}

		return _children;
	}

	public void setChildren(List<Cluster> children) {
		this._children = children;
	}

	public Cluster getParent() {
		return _parent;
	}

	public void setParent(Cluster parent) {
		this._parent = parent;
	}


	public Cluster(String name) {
		this._name = name;
		instances = new HashSet<Integer>();
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	public void addChild(Cluster cluster) {
		getChildren().add(cluster);

	}

	public boolean contains(Cluster cluster) {
		return getChildren().contains(cluster);
	}

	@Override
	public String toString() {
		return "Cluster " + _name;
	}

	@Override
	public boolean equals(Object obj) {
		String otherName = obj != null ? obj.toString() : "";
		return toString().equals(otherName);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public boolean isLeaf() {
		return getChildren().size() == 0;
	}

	public int countLeafs() {
	    return countLeafs(this, 0);
	}

    public int countLeafs(Cluster node, int count) {
        if (node.isLeaf()) count++;
        for (Cluster child : node.getChildren()) {
            count += child.countLeafs();
        }
        return count;
    }
    
    public void toConsole(int indent) {
        for (int i = 0; i < indent; i++) {
            System.out.print("  ");
            
        }
        String name = getName() + 
        		(isLeaf() ? " (leaf)" : "") + 
        		(_distance != null ? "  distance: " + _distance : "\n");
        System.out.println(name);
        for (Cluster child : getChildren()) {
            child.toConsole(indent + 1);
        }
    }
    
    public void findChildren(Set<Integer> instances) {
    	if (isLeaf()) {
    		instances.add(Integer.parseInt(_name));
    	}
    	
    	for (Cluster child : getChildren())
    		child.findChildren(instances);
    }
    
    public void setAllInstances(Set<Integer> i) {
    	this.instances = i;
    }

    public double getTotalDistance() {
        double dist = getDistance() == null ? 0 : getDistance();
        if (getChildren().size() > 0) {
            dist += _children.get(0).getTotalDistance();
        }
        return dist;

    }
	
}
