package machineLearning.Algorithms;


import machineLearning.ADT.AbstractCluster;
import machineLearning.ADT.Point;

public interface ClusterSplitter {

    abstract AbstractCluster[] split(AbstractCluster self);
    abstract Point[] chooseCentroids(AbstractCluster self);
}
