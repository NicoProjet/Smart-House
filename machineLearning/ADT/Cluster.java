package machineLearning.ADT;

import machineLearning.Algorithms.ClusterSplitter;
import machineLearning.Algorithms.ClusterValidityChecker;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Cluster extends AbstractCluster {
    private static ClusterSplitter _splitter=null;

    public Cluster(Point first) {
        super(first);
        if (_splitter==null) {
            throw new RuntimeException("Error : Cluster splitting policy not known. Please specify it with the setSplitter(ClusterSplitter) method");
        }
    }

   /* public ArrayList<Point> reset(Point centroid) {
        ArrayList<Point> result=new ArrayList<Point>(size()-1);
        for (Point point : this)
            if (point.getId()!=newCentroid.getId()) {
                point.reset();
                result.add(point);
            }
        super.set(newCentroid);
        return result;
    }*/

    public ArrayList<Point> reset(Point[] centroids) {// renvoye tous les points du clusters si différents des points dans la liste centroid et met le premier point comme centroid
        boolean flag = true;
        ArrayList<Point> result=new ArrayList<Point>(size()-1);
        for (Point point : this) {
            flag = true;
            for (Point p : centroids) if (point.getId()==p.getId())
                {
                flag = false;
            }
            if (flag) {
                point.reset();
                result.add(point);
            }
        }
        //this.get(0).setX(this.get(0).getX()+0.1);
        //Point newCentroid = new Point(this.get(0).getX(), this.get(0).getY());
        //super.set(newCentroid);
        // -1 => un doublon
        super.set(this.get(this.size()-1));
        return result;
    }


    public static void setSplitter(ClusterSplitter splitter) {_splitter=splitter;}
    public static boolean hasSplitter() {return _splitter!=null;}

    public AbstractCluster[] splitCluster() {return _splitter.split(this);}

    public double getMaxDistance() {
        double res=0;
        for (Point first : this)
            for (Point second : this)
                res=(res<AbstractPoint.distance(first,second))?AbstractPoint.distance(first,second):res;
        return res;
    }
}
