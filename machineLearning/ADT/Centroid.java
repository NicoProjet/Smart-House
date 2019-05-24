package machineLearning.ADT;

public class Centroid extends AbstractPoint{

    private final static double DEFAULTWEIGHT=0;

    protected Centroid(double x, double y) {
        super(x, y, DEFAULTWEIGHT);
    }

    public Centroid(AbstractPoint target) {
        super(target.getX(),target.getY(),target.getWeight());
    }

    @Override
    public double getX() {return super.getX();}
    @Override
    public double getY() {return super.getY();}

    public void moveCloser(AbstractPoint other) {
        setX(other.getX()+(getWeight()*(getX()-other.getX())/(getWeight()+other.getWeight())));
        setY(other.getY()+(getWeight()*(getY()-other.getY())/(getWeight()+other.getWeight())));
        addWeight(other.getWeight());
    }
}
