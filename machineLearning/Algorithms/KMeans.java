package machineLearning.Algorithms;

import machineLearning.ADT.Cluster;
import machineLearning.ADT.AbstractCluster;

import java.util.ArrayList;

public class KMeans {

    private ArrayList<Cluster> _clusters;
    private int _k;

    public KMeans(int k, Cluster cluster) {
        _k=k;
        _clusters=new ArrayList<Cluster>(k);
        _clusters.add(cluster);
    }

    private void process() {
        for (AbstractCluster cluster : _clusters.get(0).splitCluster())
            if (cluster.getId()!=_clusters.get(0).getId())
                _clusters.add((Cluster)cluster);
    }

    public ArrayList<Cluster> getResult() {
        if (_k!=_clusters.size()) process();
        return _clusters;
    }
}
