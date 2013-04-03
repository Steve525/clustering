package hac;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import toolkit.Matrix;
import toolkit.SupervisedLearner;

public class HACLearner extends SupervisedLearner {

	static double MISSING = Double.MAX_VALUE;
	private DecimalFormat df;
	
	public HACLearner () {
		df = new DecimalFormat("0.###");
	}
	
	@Override
	public void train(Matrix features, Matrix labels, int k) throws Exception {

		
		double[][] adjacencyMatrix = initializeAdjacencyMatrix(features);
		
		String[] clusterNames = new String[features.rows()];
		for (int i = 0; i < features.rows(); i++) {
			clusterNames[i] = i + "";
		}
		
		// The number of clusters to stop at.
		
		// Change this to change the given linking rule.
		LinkingRule howToCluster = new SingleLinkingRule();
//		LinkingRule howToCluster = new CompleteLinkingRule();
		
		// Main clustering algorithm
		ClusteringAlgorithm algorithm = new ClusteringAlgorithm();
		
		Cluster finalCluster = 
				algorithm.performClustering(adjacencyMatrix
										  , clusterNames
										  , howToCluster
										  , k);
		DendrogramPanel dp = new DendrogramPanel();
		dp.setModel(finalCluster);
		
//		List<Cluster> leafNodes = new ArrayList<Cluster>();
//		finalCluster.findChildren(leafNodes);
		
//		finalCluster.toConsole(0);
//		displayDendrogram(finalCluster);
		
	}
	
	private void displayDendrogram(Cluster root) {
		JFrame frame = new JFrame();
        frame.setSize(400, 300);
        frame.setLocation(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel content = new JPanel();
        DendrogramPanel dp = new DendrogramPanel();

        frame.setContentPane(content);
        content.setBackground(Color.red);
        content.setLayout(new BorderLayout());
        content.add(dp, BorderLayout.CENTER);
        dp.setBackground(Color.WHITE);
        dp.setLineColor(Color.BLACK);
        dp.setScaleValueDecimals(0);
        dp.setScaleValueInterval(1);
        dp.setShowDistances(false);

        dp.setModel(root);
        frame.setVisible(true);
	}
	
	private double[][] initializeAdjacencyMatrix (Matrix features) {
		
		int size = features.rows();
		double[][] adjacencyMatrix = new double[size][size];
		
		double distance = 0;
		for (int i = 0; i < features.rows(); i++) {
			double[] row = features.row(i);
			
			for (int j = i+1; j < features.rows(); j++) {
				
				double[] nextRow = features.row(j);
				distance = getEuclidDistance(row, nextRow, features);
				adjacencyMatrix[i][j] = distance;
				
			}
			
		}
		
		return adjacencyMatrix;
		
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
		
		return distance;
		
	}

	@Override
	public void predict(double[] features, double[] labels) throws Exception {}

}
