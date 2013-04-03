package hac;

import java.util.ArrayList;
import java.util.List;

import toolkit.Matrix;

public class ClusteringAlgorithm {

	public List<Cluster> performClustering(double[][] distances
			, String[] clusterNames
			, LinkingRule linkingRule
			, int k
			, Matrix features) {

		/* Argument checks */
		if (distances == null || distances.length == 0
		        || distances[0].length != distances.length) {
			throw new IllegalArgumentException("Invalid distance matrix");
		}
		if (distances.length != clusterNames.length) {
			throw new IllegalArgumentException("Invalid cluster name array");
		}
		if (linkingRule == null) {
			throw new IllegalArgumentException("Undefined linkage strategy");
		}

		/* Setup model */
		List<Cluster> clusters = createClusters(clusterNames);
		List<ClusterPair> linkages = createLinkages(distances, clusters);

		/* Process */
		HierarchyBuilder builder = 
				new HierarchyBuilder(clusters
								   , linkages
								   , k);
		int counter = 0;
		while (!builder.isTreeComplete()) {
			builder.agglomerate(linkingRule, counter, features);
			counter++;
		}

		return builder.getRootCluster();
	}

	private List<ClusterPair> createLinkages(double[][] distances,
	        List<Cluster> clusters) {
		List<ClusterPair> linkages = new ArrayList<ClusterPair>();
		for (int col = 0; col < clusters.size(); col++) {
			for (int row = col + 1; row < clusters.size(); row++) {
				ClusterPair link = new ClusterPair();
				link.setLinkageDistance(distances[col][row]);
				link.setLeft(clusters.get(col));
				link.setRight(clusters.get(row));
				linkages.add(link);
			}
		}
		return linkages;
	}

	private List<Cluster> createClusters(String[] clusterNames) {
		List<Cluster> clusters = new ArrayList<Cluster>();
		for (int col = 0; col < clusterNames.length; col++) {
			Cluster cluster = new Cluster(clusterNames[col]);
			clusters.add(cluster);
		}
		return clusters;
	}
	
}
