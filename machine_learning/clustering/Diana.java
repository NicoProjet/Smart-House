package clustering;

//package clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Diana {

    ArrayList<Point> allPoints;
    ArrayList<Point> resultML = new ArrayList<Point>();
    final static int NUM_MIN_POINTS = 0;
    final static double DISTANCE_MIN = 10;
    int NUM_ALL_POINTS;
    double[][] distanceMatrix;

	public Diana (ArrayList<Point> allPoints) {
		this.allPoints = allPoints;
		this.NUM_ALL_POINTS = allPoints.size();
        this.distanceMatrix = new double[NUM_ALL_POINTS][];
	}

	public void createDistanceMatrix () {
		for (int i=0; i < NUM_ALL_POINTS; i++) {
            distanceMatrix[i] = new double[(i+1)];
            for (int j=0; j < (i+1); j++) {
                if ( i != j) {
                    distanceMatrix[i][j] = Point.distance(allPoints.get(i),allPoints.get(j));
                } else {
                    distanceMatrix[i][j] = 0;
                }
            }
            System.out.println(Arrays.toString(distanceMatrix[i]) + "\n");
        }
    }

    public boolean needKmeans(Cluster cluster) {
        System.out.println(distanceMatrix.length);
        //boolean res = false;
        if (cluster.getPoints().size() < 2) {
            return false;
        }
        for (Point point : cluster.getPoints()) {
            for (Point pointAway: cluster.getPoints()) {
                System.out.println("id du point current : " + point.getId() );
                System.out.println("id du point current : " + pointAway.getId() );
                System.out.println(distanceMatrix.length + " et " + distanceMatrix[1].length);
                //System.out.println("\ndistance aux autres points : " + distanceMatrix[1][0]);
		        if (point.getId() > pointAway.getId()) {
                    System.out.println("\ndistance aux autres points 1cas: " + distanceMatrix[point.getId()][pointAway.getId()]);
                    if (distanceMatrix[point.getId()][pointAway.getId()] > DISTANCE_MIN) {
                        System.out.println("\n on continue premier cas\n");
                        return true;
               		}
                } else {
                    System.out.println("\ndistance aux autres points 2cas : " + distanceMatrix[pointAway.getId()][point.getId()]);
                    if (distanceMatrix[pointAway.getId()][point.getId()] > DISTANCE_MIN) {
                        System.out.println("\n on continue deuxième cas\n");
                        return true;
                    }
                }
            }
        }
        //System.out.println("\n est-cuqe oui????? " + res);
        return false;
    }

    public ArrayList<Point> computeDIANA(){
      createDistanceMatrix();
      Cluster baseCluster = new Cluster();
      for (Point pt : allPoints){
        baseCluster.addPoint(pt);
      }
      computeDIANA_step(baseCluster);
      System.out.println("\n\n\n\n\n\n\n le résultat : " + resultML.size());
      return resultML;
    }

    private void computeDIANA_step(Cluster cluster){
      System.out.println("\n okokok");
      cluster.plotCluster();
      if(needKmeans(cluster)){
        System.out.println("\nCe cluster a besoin\n");
        KMeans kmeans = new KMeans(cluster);
        ArrayList<Cluster> resultsStep = kmeans.getResult(distanceMatrix);
        /*for (int i=0; i<KMeans.getNumCluster();i++){
          computeDIANA_step(resultsStep.get(i));
        }*/
        for (Cluster clusterToKmean : resultsStep) {
            if (clusterToKmean.getPoints().size() != 0) {            
                computeDIANA_step(clusterToKmean);
            }
        }
      } else {
          if (cluster.getSize() > NUM_MIN_POINTS){
              System.out.println("\n\n!!!!!!!!!!!!!!! on ajoute !!!!!!!!!!!!!!\n\n");
              System.out.println("\n\n\n\n\n\n\n le résultat : " + resultML.size());
        	  resultML.add(cluster.getCentroid());
          }
        }
      }
    }
