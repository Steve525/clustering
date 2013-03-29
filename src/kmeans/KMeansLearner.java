package kmeans;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import toolkit.Matrix;
import toolkit.SupervisedLearner;

@SuppressWarnings("unused")
public class KMeansLearner extends SupervisedLearner {

	private int _k;
	static double MISSING = Double.MAX_VALUE;
	private DecimalFormat df;
	
	public KMeansLearner() {
		_k = 5;
		df = new DecimalFormat("0.###");
	}
	
	@Override
	public void train(Matrix features, Matrix labels) throws Exception {

		List<Cluster> clusters = new ArrayList<Cluster>();
		for (int i = 0; i < _k; i++) {
			clusters.add(new Cluster(i, features.row(i)));
		}
		
		printCentroid(features, clusters);
		System.out.println("Assigning each row to the cluster of " +
				"the nearest centroid...");
			
		for (int i = 0; i < features.rows(); i++) {
			
			int correctCluster = -1;
			double min = Double.MAX_VALUE;
			for (Cluster cluster : clusters) {
				double distanceToCentroid = 
						getEuclidDistance(features.row(i)
										, cluster._meanVector
										, features);
				
				if (distanceToCentroid < min) {
					min = distanceToCentroid;
					correctCluster = cluster._id;
				}
			}
			clusters.get(correctCluster).addInstance(i);
			
		}
		
		System.out.println("The cluster assignments are:");
		for (Cluster cluster : clusters) {
			cluster.printInstances();
			System.out.println(" (" + cluster._instances.size() + ")");
		}
		
		
		
		boolean change = true;
//		while (change) {
			for (Cluster cluster : clusters) {
				for (int instance : cluster._instances) {
					
					
					double internalDistance =
							getEuclidDistance(features.row(instance)
											, cluster._meanVector
											, features);
					
					for (Cluster otherCluster : clusters) {
						
						double externalDistance =
								getEuclidDistance(features.row(instance)
												, otherCluster._meanVector
												, features);
						
						if (externalDistance < internalDistance) {
							cluster.removeInstance(instance);
							otherCluster.addInstance(instance);
							change = true;
						}
						else
						{
							change = false;
						}
					}
					
					
				}
			}
//		}
		
			for (Cluster cluster : clusters) {
				cluster.printInstances();
				System.out.println(" (" + cluster._instances.size() + ")");
			}
		
	}

	private void printCentroid(Matrix features, List<Cluster> clusters) {
		for (Cluster cluster : clusters) {
			System.out.print("Centroid " + cluster._id + " = ");
			for (int i = 1; i < features.cols(); i++) {
				
				double val = features.get(cluster._id, i);
				if (val == MISSING)
					System.out.print("?");
				else {
					int attributeValues = features.valueCount(i);
					if (attributeValues == 0)
						System.out.print(df.format(val));
					else
						System.out.print(features.attrValue(i, (int) val));
				}
				
				if (i == features.cols()-1)
					System.out.println();
				else
					System.out.print(", ");
				
			}
		}
	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		// TODO Auto-generated method stub

	}
	
	private class Cluster {
		
		// instances that are in the cluster
		private Set<Integer> _instances;
		// the centroid
		private double[] _meanVector;
		// group number
		private int _id;
		
		public Cluster(int id, double[] meanVector) {
			_id = id;
			_instances = new HashSet<Integer>();
			_meanVector = meanVector;
		}
		
		public void addInstance(int instance) {
			_instances.add(instance);
		}
		
		public void removeInstance(int instance) {
			_instances.remove(instance);
		}
		
		public void printInstances() {
			System.out.print("Cluster " + _id + " = ");
			for (int instance : _instances) {
				System.out.print(instance + ", ");
			}
//			System.out.println();
		}
	}
	
	
	private Set<Integer> findFarthestPoints(Matrix features) {
		double max = -1;
		int t1 = -1;
		int t2 = -1;
		for (int i = 0; i < features.rows(); i++) {
			double[] current = features.row(i);
			for (int j = i + 1; j < features.rows(); j++) {
				double[] next = features.row(j);
				double distance = getEuclidDistance(next, current, features);
				if (distance > max) {
					max = distance;
					t1 = i;
					t2 = j;
				}
			}
		}
		Set<Integer> farthestPoints = new HashSet<Integer>();
		farthestPoints.add(t1);
		farthestPoints.add(t2);
		return farthestPoints;
	}
	
	private static double getEuclidDistance ( double[] x
											, double[] y
											, Matrix features) {
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
		return distance;
	}
	
	private int findNextFarthestPoint(Matrix features
									, Set<Integer> farthestPoints) {
		
		int nextFarthestPoint = -1;
		double max = 0;
		List<Double> distances = new ArrayList<Double>();
		for (int i = 0; i < features.rows(); i++) {
			
			if (!farthestPoints.contains(i)) {
				distances.clear();
				double[] current = features.row(i);
				for (int point : farthestPoints) {
					distances.add(getEuclidDistance(current
												, features.row(point)
												, features));
				}
				double totalDistance = 0;
				for (double distance : distances) {
					totalDistance += distance;
				}
				if (totalDistance > max) {
					totalDistance = max;
					nextFarthestPoint = i;
				}
			}
			
		}
		
		return nextFarthestPoint;
	}
}
