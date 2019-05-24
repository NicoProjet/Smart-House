package machineLearning.Algorithms;

import machineLearning.ADT.Centroid;
import machineLearning.ADT.AbstractPoint;
import machineLearning.ADT.AbstractCluster;
import machineLearning.ADT.Cluster;
import machineLearning.ADT.DistanceMatrix;
import machineLearning.ADT.Point;

import java.util.ArrayList;

import static java.lang.System.exit;

public class Diana {

    public static final double MAXDISTANCE=60;
    private static final int K=2;
    private ArrayList<Cluster> _clusters;
    private DistanceMatrix _distances;

    public Diana(ArrayList<Point> list) {
        System.out.println("DIANA.java:21  ->  ML STARTED");
        AbstractCluster.resetCounter();
        _clusters=new ArrayList<Cluster>(1);
        _distances=new DistanceMatrix(list);
        defineChecker();
        defineSplitter();
        Cluster tmp=createCluster(list.get(0));
        for (int i=1; i<list.size(); ++i)
            tmp.add(list.get(i));
        _clusters.add(tmp);
    }

    private Cluster createCluster(Point point) {
        try {return new Cluster(point);}
        catch (Exception e) {
            System.err.println(e);
            defineChecker();
            defineSplitter();
            try {return new Cluster(point);}
            catch (Exception f) {
                System.err.println(f);
                exit(1);
            }
        }
        return null;
    }

    private void defineSplitter() {
        Cluster.setSplitter(new ClusterSplitter() {
            @Override
            public Cluster[] split(AbstractCluster self) { //créée nouveau clusters et assigne un centroid
                Cluster[] result=new Cluster[K];
                result[0]=(Cluster)self;
                Point[] centroids=chooseCentroids(self);
                //-----------------------------------------
                //print les nouveaux centroides
                //------------------------------
                ArrayList<Point> frame=self.reset(centroids);
                for (int i=1; i<K; ++i)
                    result[i]=createCluster(centroids[i]);
                assignPoints(initAssignment(result,frame),result,frame);
                //------------------------------
                //changer les couleurs des points en fonction de la distance aux centroids
                //--------------------------------
                return result;
            }

            private ArrayList<ArrayList<Double>> initAssignment(Cluster[] result, ArrayList<Point> frame) {
                ArrayList<ArrayList<Double>> clusterDistances = new ArrayList<ArrayList<Double>>(result.length);
                for (int i=0; i<K; ++i) {
                    clusterDistances.add(new ArrayList<Double>(frame.size()));
                    for (int j=0; j<frame.size(); ++j)
                        clusterDistances.get(i).add(_distances.get(result[i].getFirstPointId(),frame.get(j).getId()));
                }
                return clusterDistances;
            }

            private void assignPoints(ArrayList<ArrayList<Double>> clusterDistances, Cluster[] result, ArrayList<Point> frame) {
                while (frame.size()!=0) {
                    int cluster=0,point=0;
                    Double min=clusterDistances.get(cluster).get(point);
                    for (int i=0; i<K; ++i)
                        for (int j=0; j<frame.size(); ++j)
                            if (min>clusterDistances.get(i).get(j)) {
                                min=clusterDistances.get(i).get(j);
                                cluster=i;
                                point=j;
                            }
                    Point tmp=frame.remove(point);
                    for (ArrayList<Double> list : clusterDistances) list.remove(point);
                    result[cluster].add(tmp);
                    for (int i=0; i<frame.size(); ++i) clusterDistances.get(cluster).set(i,result[cluster].getDistance(frame.get(i)));
                }
            }

            @Override
            public Point[] chooseCentroids(AbstractCluster self) { // prend les deux points les plus distants comme nouveaux centroids du cluster
                if (K!=2) {
                    System.err.println("The used splitter is not suppose to be used with K>2. This program will shut down"); //#like
                    exit(1);
                }
                Point[] result={self.get(0),self.get(0)};
                double max=0;
                for (int i=0; i<self.size(); ++i)
                    for (int j=0; j<i; ++j)
                        if (max<_distances.get(self.get(i).getId(),self.get(j).getId())) {
                            max=_distances.get(self.get(i).getId(),self.get(j).getId());
                            result[0]=self.get(i);
                            result[1]=self.get(j);
                        }
                return result;
            }
        });
    }

    private void defineChecker() { // check si le cluster doit etre envoyé à Kmeans
        Cluster.setChecker(new ClusterValidityChecker() {
            @Override
            public boolean checkValidity(ArrayList<Point> points) {
                boolean flag=true;
                for (int i=0; flag && i<points.size(); ++i)
                    for (int j=0; flag && j<i; ++j)
                        flag=_distances.get(points.get(i).getId(),points.get(j).getId())<=MAXDISTANCE;
                return flag;
            }
        });
    }

    public Cluster[] getClusters() {
        if (!_clusters.get(0).isValid()) process();
        Cluster[] result=new Cluster[_clusters.size()];
        _clusters.toArray(result);
        return result;
    }

    public Centroid[] getCentroids() {
        if (!_clusters.get(0).isValid()) process();
        Centroid[] result;
        int i=0;
        result=new Centroid[_clusters.size()];
        for (Cluster cluster : _clusters)
            result[i++]=cluster.getCentroid();
        return result;
    }

    private void process() { //moment ou on envoie au Kmeans

        Cluster current=_clusters.get(0);
        while (!current.isValid()) {
            KMeans kmeans=new KMeans(K,current);
            for (Cluster cluster : kmeans.getResult())
                if (cluster.getId()!=current.getId())
                    _clusters.add(cluster);
            for (; current.isValid() && current.getId()+1<_clusters.size(); current=_clusters.get(current.getId()+1));
        }
    }
}
