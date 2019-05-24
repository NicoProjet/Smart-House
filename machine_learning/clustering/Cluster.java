package clustering;

import java.util.ArrayList;

import clustering.Point;

public class Cluster {

	public ArrayList<Point> points;
	public Point centroid;
	public int id;
	static int number_id=0;

	//Creates a new Cluster
	/*public Cluster(int id) {
		this.id = id;
		this.points = new ArrayList<Point>();
		this.centroid = null;
	}*/

	public Cluster(){
		this.id = number_id;
		number_id++;
		this.points = new ArrayList<Point>();
		this.centroid = null;
	}

	public int getSize(){
		return points.size();
	}

	public ArrayList<Point> getPoints() {
		return points;
	}

	public void addPoint(Point point) {
		this.points.add(point);
	}

	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}

	public Point getCentroid() {
		return centroid;
	}

	public void setCentroid(Point centroid) {
		this.centroid = centroid;
	}

	public int getId() {
		return id;
	}

	public void clear() {
		this.points.clear();
	}

	public void plotCluster() {
		//System.out.println("[Cluster: " + this.id+"]");
		System.out.println("[Centroid: " + this.centroid + "]");
		System.out.println("[Points: \n");
		//System.out.println(Arrays.toString(this.points));
		for(Point p : points) {
			System.out.println(p);
		}
		System.out.println("]");
	}

}
