package machineLearning.Algorithms;

import machineLearning.ADT.Point;
import java.util.ArrayList;

public interface ClusterValidityChecker {

    abstract boolean checkValidity(ArrayList<Point> points);
}
