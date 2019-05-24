package clustering;

//package clustering;

import java.util.ArrayList;
import java.util.List;

import clustering.*;

public class KMeans {

	//Number of Clusters. This metric should be related to the number of points
    private static final int MIN_X = 0;
    private static final int MAX_X = 2500;
    private static final int MIN_Y = 0;
    private static final int MAX_Y = 1000;
    private static int NUM_CLUSTERS = 2;
    //Number of Points
    private int NUM_POINTS;
    //Min and Max X and Y
    //private static final int MIN_COORDINATE = 0;
    //private static final int MAX_COORDINATE = 10;

    public ArrayList<Point> points;
    public ArrayList<Cluster> clusters;

    public KMeans(Cluster cluster) {//envoyer l'objet cluster
	//this.NUM_CLUSTERS = NUM_CLUSTERS;
	//this.NUM_POINTS = NUM_POINTS;
    	this.points = cluster.getPoints();
        for (Point point : points) {
                System.out.println("les points donne à kmeans" + point);
        }
    	this.clusters = new ArrayList<Cluster>();
		this.NUM_POINTS = points.size();
    }

   /* public static void main(String[] args) {

    	KMeans kmeans = new KMeans();
    	kmeans.init();
    	kmeans.calculate();
    }*/

    public int getNumPoints(){
    	return NUM_POINTS;
    }

    public static int getNumCluster(){
      return NUM_CLUSTERS;
    }

    public ArrayList<Cluster> getResult(double[][] distanceMatrix){
      this.init(distanceMatrix);
      this.calculate();
      return clusters;
    }

    public ArrayList<Point> createFirstCentroids (double[][] distanceMatrix){
        //permet de creer les deux premiers centroids, ATTENTION que pour K=2!!!!!!!!!!!!
        double max=0.0;
        ArrayList<Point> res = new ArrayList<Point>();
        res.add(new Point(0.0,0.0));
        res.add(new Point(0.0,0.0));
        //double distance;
        System.out.println("\n\n\n nombre de points dqns le cluster a kmeans" + NUM_POINTS);
        for (int i=0; i < NUM_POINTS; i++) {
            for (int j=i; j < NUM_POINTS; j++) {
                //if (points.get(i).getId() > points.get(j).getId()) {
                    double tmp = (points.get(i).getId()>points.get(j).getId())?distanceMatrix[points.get(i).getId()][points.get(j).getId()]:distanceMatrix[points.get(j).getId()][points.get(i).getId()];
                    if (tmp>max) {
                        System.out.println("tepm print " + tmp);
                        max=tmp;
                        //res.clear();
                        //res.clear();
                        //res.add(points.get(i));
                        //res.add(points.get(j));
                        res.set(0, points.get(i));
                        res.set(1, points.get(j));
                        System.out.println("premier centroid "+res.get(0));
                        System.out.println("second centroid " +res.get(1));
                    }

               //  }
             }
           }
           return res;
    }

                


    //Initializes the process
    public void init(double[][] distanceMatrix) {
    	//Create Clusters
    	//Set Random Centroids
    	/*for (int i = 0; i < NUM_CLUSTERS; i++) {
    		Cluster cluster = new Cluster(i);
    		Point centroid = Point.createRandomPoint(MIN_COORDINATE,MAX_COORDINATE);
    		cluster.setCentroid(centroid);
    		clusters.add(cluster);*/
		//ArrayList<Point> centroidsFirst = createFirstCentroids(distanceMatrix);
		System.out.println("le nombre de poitns dans chaque cluster dans kmeans: " + points.size());
		for (int i = 0; i < NUM_CLUSTERS; i++) {
			Cluster cluster = new Cluster();
			/////TO DO : fct createPointCentroid, optimiser pour chauffage
			points.get(i).setX(points.get(i).getX()+2.05);
			points.get(i).setY(points.get(i).getY()+3.5);
			Point centroid = points.get(i);//centroidsFirst.get(i);
			cluster.setCentroid(centroid);
			clusters.add(cluster);
    	}

    	//Print Initial state
    	//plotClusters();
    }

    /*private void plotClusters() {
    	for (int i = 0; i < NUM_CLUSTERS; i++) {
    		Cluster c = clusters.get(i);
    		c.plotCluster();
    	}
    }*/

	//The process to calculate the K Means, with iterating method.
    public void calculate() {
        boolean flag = false;
        //int iteration = 0;
        int iteration = 0;
        System.out.println("\n je susi rentere dans calcultieareaunretnrt\n");
        // Add in new data, one at a time, recalculating centroids with each new one.
        while(!flag) {
        	//Clear cluster state
            System.out.println(" nomber de 'itération kmains : " + iteration);
            iteration++;
        	clearClusters();

        	ArrayList<Point> lastCentroids = getCentroids();

        	//Assign points to the closer cluster
        	assignCluster();

            //Calculate new centroids.
        	calculateCentroids();

        	//iteration++;

        	ArrayList<Point> currentCentroids = getCentroids();

        	//Calculates total distance between new and old Centroids
        	double distance = 0;
        	for(int i = 0; i < lastCentroids.size(); i++) {
                if (lastCentroids.get(i) != null || currentCentroids.get(i) != null) {
        		distance += Point.distance(lastCentroids.get(i),currentCentroids.get(i));
            }
        	}
          /*
          System.out.println("#####
"############");
        	System.out.println("Iteration: " + iteration);
        	System.out.println("Centroid distances: " + distance);
          */
        	//plotClusters();

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
    	ArrayList<Point> centroids = new ArrayList<Point>(NUM_CLUSTERS);
    	for(Cluster cluster : clusters) {
    		Point aux = cluster.getCentroid();
    		Point point = new Point(aux.getX(),aux.getY());
            System.out.println("coord centroid: " + aux.getX() + "     et  " + aux.getY());
    		centroids.add(point);
    	}
    	return centroids;
    }

    private void assignCluster() {
        double max = Double.MAX_VALUE;
        double min = max;
        int cluster = 0;
        int cluster_id = 0;
        double distance = 0.0;

        for(Point point : points) {
        	min = max;
            System.out.println("le nombre de cluster dans assignCluster: "+NUM_CLUSTERS);
            for(int i = 0; i < NUM_CLUSTERS; i++) {
            	Cluster c = clusters.get(i);
                distance = Point.distance(point, c.getCentroid());
                System.out.println("print le centroid du cluster "+c.getCentroid());
                if(distance < min){
                    min = distance;
                    cluster = i;
                    cluster_id = c.getId();
                }
            }
            point.setCluster(cluster_id);
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
