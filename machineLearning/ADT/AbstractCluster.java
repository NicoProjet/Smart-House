package machineLearning.ADT;

import machineLearning.Algorithms.ClusterValidityChecker;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractCluster extends ArrayList<Point> {
    private Centroid _centroid;
    private int _id;
    private static ClusterValidityChecker _checker=null;
    private static ReentrantLock _mutex=new ReentrantLock();
    private static int _generalId=0;

    public AbstractCluster(Point first) {
        if (_checker==null) {
            throw new RuntimeException("Error : Validity condition not known. Please specify it with the setChecker(ClusterValidityChecker) method");
        }
        _mutex.lock();
        _id=_generalId++;
        _mutex.unlock();
        set(first);
    }
    public static void resetCounter(){
        _generalId = 0;
    }
    protected void set(Point centroid) {
        clear();
        _centroid=new Centroid(centroid);
        mark(centroid);
        super.add(centroid);
    }

    private void mark(Point point) {
        //if (point.getClusterId()!=-1) System.out.println("utenarustenart--\n");
        if (point.getClusterId()==-1) point.setClusterId(_id);
    }

    @Override
    public boolean add(Point point) {
        _centroid.moveCloser(point);
        mark(point);
        return super.add(point);
    }

    /*public ArrayList<Point> reset(Point newCentroid) {
        ArrayList<Point> result=new ArrayList<Point>(size()-1);
        for (Point point : this)
            if (point.getId()!=newCentroid.getId()) {
                point.reset();
                result.add(point);
            }
        set(newCentroid);
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

        set(this.get(0));
        return result;
    }

    public double getDistance(AbstractPoint other) {return AbstractPoint.distance(_centroid,other);}
    public static boolean hasChecker() {return _checker!=null;}
    public static void setChecker(ClusterValidityChecker checker) {_checker=checker;}
    public abstract AbstractCluster[] splitCluster();
    public Centroid getCentroid() {return _centroid;}
    public int getFirstPointId() {return (size()==1)?get(0).getId():-1;}
    public int getId() {return _id;}
    public boolean isValid() {return _checker.checkValidity(this);}

    public double getMaxDistance() {
        double res=0;
        for (Point first : this)
            for (Point second : this)
                res=(res<AbstractPoint.distance(first,second))?AbstractPoint.distance(first,second):res;
        return res;
    }

    public String toString() {
        System.out.println("[Centroid: " + _centroid);
        System.out.println("points: ");
        for (Point p : this) System.out.println(p);
        return "---------------------";
    }
}
