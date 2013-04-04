package kmeans;

import hac.Cluster;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import toolkit.Matrix;
import toolkit.SupervisedLearner;

@SuppressWarnings("unused")
public class KMeansLearner extends SupervisedLearner {

	private int _k;
	static double MISSING = Double.MAX_VALUE;
	private DecimalFormat df;
	
	public KMeansLearner() {
		_k = 4;
		df = new DecimalFormat("0.#");
	}
	
	@Override
	public void train(Matrix features, Matrix labels, int k) throws Exception {
		
		System.out.println("k = " + k);
		
		List<Cluster> clusters = new ArrayList<Cluster>();
		for (int i = 0; i < k; i++) {
			int x = (int) (Math.random() * features.rows());
//			System.out.println(x + " ");
			clusters.add(new Cluster(i, features.row(x)));
		}
			
		initialClusterAssignment(features, clusters);
				
		double sse = 0;
		double prev_sse = 0;
		boolean keepGoing = true;
		
		for (Cluster cluster : clusters) {
			sse += cluster.getSSE(features);
		}
//		System.out.println("Sum squared-distance of" +
//				" each row with its centroid = " 
//				+ sse);
		
		while (keepGoing) {
			
			prev_sse = sse;
			sse = 0;
			
			
			for (Cluster cluster : clusters) {
				cluster.updateCentroid(features);
			}
			
//			System.out.println("============================================" +
//					"===========================================");
//			System.out.println("Recomputing the centroids of each cluster...");
//			printCentroids(features, clusters);
			
			reassignInstances(features, clusters);
			
			
//			System.out.println("The cluster assignments are:");
			for (Cluster cluster : clusters) {
//				cluster.printInstances();
//				System.out.println("# of instances: " + cluster._instances.size() +
//						" : SSE = " + cluster.getSSE(features));
			}			
			
			for (Cluster cluster : clusters) {
				sse += cluster.getSSE(features);
			}
//			System.out.println("Sum squared-distance of" +
//					" each row with its centroid = " 
//					+ sse);
			if (Math.abs(sse - prev_sse) < 0.0001)
				keepGoing = false;
		}
		
		double min = 100000;
		for (Cluster finalCluster : clusters) {
			for (Cluster otherCluster : clusters) {
				if (otherCluster != finalCluster) {
					double distance = 
							getEuclidDistance(otherCluster._centroid
									, finalCluster._centroid
									, features);
					if (min > distance)
						min = distance;
				}
			}
		}
		System.out.println(min);
		
//		List<double[]> attributeSSE = new ArrayList<double[]>();
//		for (Cluster cluster : clusters) {
//			double[] individualSSE = cluster.updateCentroid(features);
//			attributeSSE.add(individualSSE);
//		}
//		double[] totals = new double[features.cols()];
//		for (double[] x : attributeSSE) {
//			for (int i = 0; i < x.length; i++) {
//				totals[i] += x[i];
//			}
//		}
//		for (int i = 0; i < features.cols(); i++)
//			System.out.print(features.attrName(i) + "\t");
//		System.out.println();
//		for (int i = 0; i < totals.length; i++) {
//			System.out.print(df.format(totals[i]) + "\t");
//		}
//		System.out.println();
		
//		System.out.println(sse);
		
	}

	private void initialClusterAssignment(Matrix features
										, List<Cluster> clusters) {
		
//		printCentroids(features, clusters);
//		System.out.println("Assigning each row to the cluster of " +
//				"the nearest centroid...");
		
		for (int i = 0; i < features.rows(); i++) {
			
			int correctCluster = -1;
			double min = Double.MAX_VALUE;
			for (Cluster cluster : clusters) {
				double distanceToCentroid = 
						getEuclidDistance(features.row(i)
										, cluster._centroid
										, features);
				
				if (distanceToCentroid < min) {
					min = distanceToCentroid;
					correctCluster = cluster._id;
				}
			}
			clusters.get(correctCluster).addInstance(i);
			
		}
		
//		System.out.println("The cluster assignments are:");
		for (Cluster cluster : clusters) {
			cluster.printInstances();
//			System.out.println("# of instances: " + cluster._instances.size() +
//					" : SSE = " + cluster.getSSE(features));
		}
		
	}

	private void reassignInstances(Matrix features, List<Cluster> clusters) {
		
//		System.out.println("Reassigning each row to the cluster of " +
//				"the nearest centroid...");
		List<Map<Integer, Cluster>> instancesToClusters = 
				new ArrayList<Map<Integer, Cluster>>();
		for (Cluster cluster : clusters) {
			for (int instance : cluster._instances) {
				
				double internalDistance =
						getEuclidDistance(features.row(instance)
										, cluster._centroid
										, features);
				
				double min = Double.MAX_VALUE;
				Cluster newCluster = null;
				int inst = -1;
				for (Cluster otherCluster : clusters) {
					
					if (otherCluster != cluster) {
						double externalDistance =
								getEuclidDistance(features.row(instance)
												, otherCluster._centroid
												, features);
						
						if (externalDistance < internalDistance) {
							if (min > externalDistance) {
								min = externalDistance;
								newCluster = otherCluster;
								inst = instance;
							}
						}
					}
					
				}
				if (newCluster != null) {
					Map<Integer, Cluster> relocation = 
							new HashMap<Integer, Cluster>();
					relocation.put(inst, newCluster);
					instancesToClusters.add(relocation);
				}
			}
			if (!instancesToClusters.isEmpty()) {
				for (Map<Integer, Cluster> relocation : instancesToClusters) {
					for (Entry<Integer, Cluster> entry : 
						relocation.entrySet()) {
						
						int i = entry.getKey();
						Cluster someCluster = entry.getValue();
						cluster.removeInstance(i);
						someCluster.addInstance(i);
						
					}
				}
			}
			instancesToClusters.clear();
			
		}
		
	}

	private void printCentroids(Matrix features, List<Cluster> clusters) {
		for (Cluster cluster : clusters) {
			System.out.print("Centroid " + cluster._id + " = ");
			for (int i = 1; i < features.cols(); i++) {
				
				double val = cluster._centroid[i];
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
	
	private class Cluster {
		
		// instances that are in the cluster
		private Set<Integer> _instances;
		// the centroid
		private double[] _centroid;
		// group number
		private int _id;
		
		private double _sse;
		
		private double[] _individualSSE;
		
		public Cluster(int id, double[] meanVector) {
			_id = id;
			_instances = new HashSet<Integer>();
			_centroid = meanVector;
			_sse = 0;
		}
		
		public Set<Integer> getInstances() { return _instances; }
		
		public void addInstance(int instance) {
			_instances.add(instance);
		}
		
		public void removeInstance(int instance) {
			_instances.remove(instance);
		}
		
		public void printInstances() {
//			System.out.print("Cluster " + _id + " = ");
//			List<Integer> sortedList = new ArrayList<Integer>();
//			sortedList.addAll(_instances);
//			Collections.sort(sortedList);
//			for (int instance : sortedList) {
//				System.out.print(instance + ", ");
//			}
		}
		
		public double[] updateCentroid(Matrix features) {
			
			_sse = 0;
			double[] individualSSE = new double[features.cols()];
			for (int i = 0; i < features.cols(); i++) {
				
				int attributeValues = features.valueCount(i);
				if (attributeValues == 0) {
					
					individualSSE[i] = 
							features.calculateContinuousSSE(i
									, _instances
									, _centroid[i]);
					_centroid[i] =
							features.averagedContinuousValue(i, _instances);
					
					
				}
				else {
					
					individualSSE[i] = features.calculateNominalSSE(i
							, _instances
							, _centroid[i]);
					_centroid[i] = features.mostCommonValue(i, _instances);
					
					
				}
				
			}
			
			return individualSSE; 
			
		}
		
		public double getSSE (Matrix features) {
			
			double sse = 0;
			for (int instance : _instances) {
				double[] x = features.row(instance);
				sse += getEuclidDistance(x , _centroid, features);
			}
			
			return sse;
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
	
	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		// TODO Auto-generated method stub

	}
	
}
