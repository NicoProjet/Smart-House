
//package clustering;

import java.util.ArrayList;
import java.util.List;

import clustering.*;

public class KMeans {

	//Number of Clusters. This metric should be related to the number of points
    private int NUM_CLUSTERS = 2;    
    //Number of Points
    private int NUM_POINTS = 10;
    //Min and Max X and Y
    private static final int MIN_COORDINATE = 0;
    private static final int MAX_COORDINATE = 10;
    
    private ArrayList<Point> points;
    private ArrayList<Cluster> clusters;
    
    public KMeans() {//int NUM_CLUSTERS, int NUM_POINTS) {
	//this.NUM_CLUSTERS = NUM_CLUSTERS;
	//this.NUM_POINTS = NUM_POINTS;
    	this.points = new ArrayList();
    	this.clusters = new ArrayList();    	
    }
    
    public static void main(String[] args) {
    	
    	KMeans kmeans = new KMeans();
    	kmeans.init();
    	kmeans.calculate();
    }
    
    //Initializes the process
    public void init() {
    	//Create Points
    	points = Point.createRandomPoints(MIN_COORDINATE,MAX_COORDINATE,NUM_POINTS);
    	
    	//Create Clusters
    	//Set Random Centroids
    	for (int i = 0; i < NUM_CLUSTERS; i++) {
    		Cluster cluster = new Cluster(i);
    		Point centroid = Point.createRandomPoint(MIN_COORDINATE,MAX_COORDINATE);
    		cluster.setCentroid(centroid);
    		clusters.add(cluster);
    	}
    	
    	//Print Initial state
    	plotClusters();
    }

	private void plotClusters() {
    	for (int i = 0; i < NUM_CLUSTERS; i++) {
    		Cluster c = clusters.get(i);
    		c.plotCluster();
    	}
    }
    
	//The process to calculate the K Means, with iterating method.
    public void calculate() {
        boolean flag = false;
        int iteration = 0;
        
        // Add in new data, one at a time, recalculating centroids with each new one. 
        while(!flag) {
        	//Clear cluster state
        	clearClusters();
        	
        	ArrayList<Point> lastCentroids = getCentroids();
        	
        	//Assign points to the closer cluster
        	assignCluster();
            
            //Calculate new centroids.
        	calculateCentroids();
        	
        	iteration++;
        	
        	ArrayList<Point> currentCentroids = getCentroids();
        	
        	//Calculates total distance between new and old Centroids
        	double distance = 0;
        	for(int i = 0; i < lastCentroids.size(); i++) {
        		distance += Point.distance(lastCentroids.get(i),currentCentroids.get(i));
        	}
        	System.out.println("#################");
        	System.out.println("Iteration: " + iteration);
        	System.out.println("Centroid distances: " + distance);
        	plotClusters();
        	        	
        	if(distance == 0) {
        		flag = true;
        	}
        }
    }
    
    private void clearClusters() {
    	for(Cluster cluster : clusters) {
    		cluster.clear();
    	}
    }
    
    private ArrayList<Point> getCentroids() {
    	ArrayList<Point> centroids = new ArrayList(NUM_CLUSTERS);
    	for(Cluster cluster : clusters) {
    		Point aux = cluster.getCentroid();
    		Point point = new Point(aux.getX(),aux.getY());
    		centroids.add(point);
    	}
    	return centroids;
    }
    
    private void assignCluster() {
        double max = Double.MAX_VALUE;
        double min = max; 
        int cluster = 0;                 
        double distance = 0.0; 
        
        for(Point point : points) {
        	min = max;
            for(int i = 0; i < NUM_CLUSTERS; i++) {
            	Cluster c = clusters.get(i);
                distance = Point.distance(point, c.getCentroid());
                if(distance < min){
                    min = distance;
                    cluster = i;
                }
            }
            point.setCluster(cluster);
            clusters.get(cluster).addPoint(point);
        }
    }
    
    private void calculateCentroids() {
        for(Cluster cluster : clusters) {
            double sumX = 0;
            double sumY = 0;
            ArrayList<Point> list = cluster.getPoints();
            int n_points = list.size();
            
            for(Point point : list) {
            	sumX += point.getX();
                sumY += point.getY();
            }
            
            Point centroid = cluster.getCentroid();
            if(n_points > 0) {
            	double newX = sumX / n_points;
            	double newY = sumY / n_points;
                centroid.setX(newX);
                centroid.setY(newY);
            }
        }
    }
}
