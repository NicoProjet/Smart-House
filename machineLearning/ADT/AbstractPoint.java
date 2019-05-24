package machineLearning.ADT;

public abstract class AbstractPoint {
    private double _x;
    private double _y;
    private double _weight;

    protected AbstractPoint(double x, double y, double weight) {
        _x=x;
        _y=y;
        _weight=weight;
    }

    static public double distance(AbstractPoint self, AbstractPoint other) {
        return Math.sqrt(Math.pow(self._x-other._x,2)+Math.pow(self._y-other._y,2));
    }

    public String toString() {
        return String.format("(%.3f,%.3f)",_x,_y);
    }

    protected double getX() {return _x;}
    protected double getY() {return _y;}
    protected void setX(double x) {_x=x;}
    protected void setY(double y) {_y=y;}
    protected double getWeight() {return _weight;}
    protected void addWeight(double weight) {_weight+=weight;}
}
