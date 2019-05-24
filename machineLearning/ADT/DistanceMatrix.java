package machineLearning.ADT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class DistanceMatrix {

    private double[][] _matrix;
    private Point[] _points;

    public DistanceMatrix(ArrayList<Point> list) {
        _points=new Point[list.size()];
        list.toArray(_points);

        Arrays.sort(_points, new Comparator<Point>() {
            @Override
            public int compare(Point first, Point second) {return first.getId()-second.getId();}
        });

        _matrix=new double[_points.length][];
        for (int i=0;i<_points.length;++i) {
            _matrix[i]=new double[i+1];
            _matrix[i][i]=0;
            for (int j=0; j<i; ++j) {
                _matrix[i][j]=AbstractPoint.distance(_points[i],_points[j]);
            }
        }
    }

    public double get(int i, int j) {return (i>j)?_matrix[i][j]:_matrix[j][i];}
    public Point getPoint(int i) {return _points[i];}
}
