package machineLearning.ADT;

import java.util.concurrent.locks.ReentrantLock;

public class Point extends AbstractPoint {

    private int _id;
    private int _clusterId;
    private static int _generalId=0;
    private static ReentrantLock _mutex=new ReentrantLock();
    private static final double STANDARDWEIGHT=1;

    public Point(double x, double y) {
        super(x, y, STANDARDWEIGHT);
        _mutex.lock();
        _id=_generalId++;
        _mutex.unlock();
        _clusterId=-1;
    }

    public Point(double x, double y, double weight) {
        super(x, y, weight);
        _mutex.lock();
        _id=_generalId++;
        _mutex.unlock();
        _clusterId=-1;
    }

    public void setClusterId(int id) {
        _clusterId=id;
    }

    public int getClusterId() {return _clusterId;}

    public int getId() {
        return _id;
    }

    public void reset() {_clusterId=-1;}

    public static void resetCounter() {
        _mutex.lock();
        _generalId=0;
        _mutex.unlock();
    }

    // protected double getX() {return super.getX();}
    // protected double getY() {return super.getY();}
    // protected void setX(double x) {super.setX(x);}
    // protected void setY(double y) {super.setY(y);}
}
