package hac;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

	private String _name;

	private Cluster _parent;

	private List<Cluster> _children;

	private Double _distance;

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
    
    public void findChildren(List<Cluster> leafNodes) {
    	if (isLeaf()) {
    		leafNodes.add(this);
    		System.out.print("Found a leaf: " + getName() + " ");
    		
    		System.out.println(this._parent._distance);
    	}
    	
    	for (Cluster child : getChildren())
    		child.findChildren(leafNodes);
    }

    public double getTotalDistance() {
        double dist = getDistance() == null ? 0 : getDistance();
        if (getChildren().size() > 0) {
            dist += _children.get(0).getTotalDistance();
        }
        return dist;

    }
	
}
