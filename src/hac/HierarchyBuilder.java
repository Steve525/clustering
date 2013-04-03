package hac;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import toolkit.Matrix;

public class HierarchyBuilder {

	private List<ClusterPair> distances;
	private List<Cluster> clusters;
	private int _k;

	public List<ClusterPair> getDistances() {
		return distances;
	}

	public List<Cluster> getClusters() {
		return clusters;
	}

	public HierarchyBuilder(List<Cluster> clusters
						  , List<ClusterPair> distances
						  , int k) {
		this.clusters = clusters;
		this.distances = distances;
		this._k = k;
	}

	public void agglomerate(LinkingRule linkageStrategy
			, int counter) {
		Collections.sort(distances);
		if (distances.size() > 0) {
			ClusterPair minDistLink = distances.remove(0);
			clusters.remove(minDistLink.getrCluster());
			clusters.remove(minDistLink.getlCluster());

			Cluster oldClusterL = minDistLink.getlCluster();
			Cluster oldClusterR = minDistLink.getrCluster();
			Cluster newCluster = minDistLink.agglomerate(null);

			for (Cluster iClust : clusters) {
				ClusterPair link1 = findByClusters(iClust, oldClusterL);
				ClusterPair link2 = findByClusters(iClust, oldClusterR);
				ClusterPair newLinkage = new ClusterPair();
				newLinkage.setlCluster(iClust);
				newLinkage.setrCluster(newCluster);
				Collection<Double> distanceValues = new ArrayList<Double>();
				if (link1 != null) {
					distanceValues.add(link1.getLinkageDistance());
					distances.remove(link1);
				}
				if (link1 != null) {
					distanceValues.add(link2.getLinkageDistance());
					distances.remove(link2);
				}
				Double newDistance = linkageStrategy
				        .calculateDistance(distanceValues);
				newLinkage.setLinkageDistance(newDistance);
				distances.add(newLinkage);

			}
			System.out.println("Iteration " + counter);
			System.out.println("Size: " + clusters.size());
			System.out.println("Total Distance: " +
					newCluster.getTotalDistance());
			System.out.println("========================");
			clusters.add(newCluster);
		}
	}
	
	private static double MISSING = Double.MAX_VALUE;
	
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
	
	private double getSSEOfCluster() {
		double sse = 0;
		
		
		
		return -1;
	}

	private ClusterPair findByClusters(Cluster c1, Cluster c2) {
		ClusterPair result = null;
		for (ClusterPair link : distances) {
			boolean cond1 = link.getlCluster().equals(c1)
			        && link.getrCluster().equals(c2);
			boolean cond2 = link.getlCluster().equals(c2)
			        && link.getrCluster().equals(c1);
			if (cond1 || cond2) {
				result = link;
				break;
			}
		}
		return result;
	}

	public boolean isTreeComplete() {
		return (clusters.size() <= _k);
	}

	public Cluster getRootCluster() {
		if (!isTreeComplete()) {
			throw new RuntimeException("No root available");
		}
		return clusters.get(0);
	}
	
}
