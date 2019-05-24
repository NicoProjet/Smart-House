package clustering;

import java.util.ArrayList;
import java.util.Random;

public class Point {

    private double x = 0;
    private double y = 0;
    private int cluster_number = 0;
    public int id = 0;
    static int numberId = 0;

    public Point(double x, double y)
    {
        this.id = numberId;
        this.x = x;
        this.y = y;
        numberId ++;
    }

    public void setId(int newId){
        this.id = newId;
    }

    public int getId() {
        return this.id;
    }


    public void setX(double x) {
        this.x = x;
    }

    public double getX()  {
        return this.x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return this.y;
    }

    public void setCluster(int n) {
        this.cluster_number = n;
    }

    public int getCluster() {
        return this.cluster_number;
    }

    //Calculates the distance between two points.
    public static double distance(Point p, Point centroid) {
        //System.out.println("point a distance 1 " + p);
        //System.out.println("point a distance 2 " + centroid);
        double res = Math.sqrt(Math.pow((centroid.getY() - p.getY()), 2) + Math.pow((centroid.getX() - p.getX()), 2));
        //System.out.println("distance calcule : " + res);
        return res;
    }

    public double distanceCurrentPoint(Point away) {
        return Math.sqrt(Math.pow((away.getY() - y), 2) + Math.pow((away.getX() - x), 2));
    }


    //Creates random point
    public static Point createRandomPoint(int minX, int maxX, int minY, int maxY) {
    	Random r = new Random();
    	double x = minX + (maxX - minX) * r.nextDouble();
    	double y = minY + (maxY - minX) * r.nextDouble();
    	return new Point(x,y);
    }

    public static ArrayList<Point> createRandomPoints(int minX, int maxX, int minY, int maxY, int number) {
    	ArrayList<Point> points = new ArrayList<Point>(number);
    	for(int i = 0; i < number; i++) { //for(int i = 0; i &lt; number; i++) {
    		points.add(createRandomPoint(minX,maxX,minY,maxY));
    	}
    	return points;
    }

    public String toString() {
    	return "("+x+","+y+")";
    }
}
