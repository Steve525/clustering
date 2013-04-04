package hac;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
			, int counter
			, Matrix features) {
		Collections.sort(distances);
		if (distances.size() > 0) {
			ClusterPair minDistLink = distances.remove(0);
			clusters.remove(minDistLink.getRight());
			clusters.remove(minDistLink.getLeft());

			Cluster oldClusterL = minDistLink.getLeft();
			Cluster oldClusterR = minDistLink.getRight();
			Cluster newCluster = minDistLink.agglomerate(null);

			for (Cluster iClust : clusters) {
				ClusterPair link1 = findByClusters(iClust, oldClusterL);
				ClusterPair link2 = findByClusters(iClust, oldClusterR);
				ClusterPair newLinkage = new ClusterPair();
				newLinkage.setLeft(iClust);
				newLinkage.setRight(newCluster);
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
			
			Set<Integer> instances = new HashSet<Integer>();
			newCluster.findChildren(instances);
			newCluster.setAllInstances(instances);
			newCluster.updateCentroid(features);
			double sse = newCluster.getSSE(features);
			
			
			System.out.println("Iteration " + counter);
			System.out.println("Merging : " + newCluster.getName());
			System.out.println("Total SSE : " + sse);
			System.out.println("========================");
				
			
			clusters.add(newCluster);
		}
	}

	private ClusterPair findByClusters(Cluster c1, Cluster c2) {
		ClusterPair result = null;
		for (ClusterPair link : distances) {
			boolean cond1 = link.getLeft().equals(c1)
			        && link.getRight().equals(c2);
			boolean cond2 = link.getLeft().equals(c2)
			        && link.getRight().equals(c1);
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

	public List<Cluster> getRootCluster() {
		if (!isTreeComplete()) {
			throw new RuntimeException("No root available");
		}
		return clusters;
	}
	
}
