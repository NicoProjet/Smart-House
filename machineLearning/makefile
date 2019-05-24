
flags= -Xlint -Werror

all: classes/Main.class

#classes/Main.class: classes/ADT/Centroid.class classes/ADT/Point.class classes/ADT/AbstractCluster.class classes/ADT/Cluster.class classes/ADT/AbstractPoint.class classes/ADT/DistanceMatrix.class classes/Algorithms/ClusterSplitter.class classes/Algorithms/ClusterValidityChecker.class classes/Algorithms/Diana.class classes/Algorithms/KMeans.class
#    javac $(flags) -cp "classes" Main.java -d classes

classes/ADT/Centroid.class: classes/ADT/AbstractPoint.class
	javac $(flags) -cp ".:classes" ADT/Centroid.java -d classes

classes/ADT/Point.class: classes/ADT/AbstractPoint.class
	javac $(flags) -cp ".:classes" ADT/Point.java -d classes

classes/ADT/AbstractCluster.class: classes/ADT/Point.class classes/ADT/Centroid.class classes/Algorithms/ClusterValidityChecker.class
	javac -cp ".:classes" ADT/AbstractCluster.java -d classes

classes/ADT/Cluster.class: classes/ADT/AbstractCluster.class classes/ADT/Point.class classes/ADT/Centroid.class classes/Algorithms/ClusterSplitter.class classes/Algorithms/ClusterValidityChecker.class
	javac -cp ".:classes" ADT/Cluster.java -d classes

classes/ADT/AbstractPoint.class:
	javac $(flags) ADT/AbstractPoint.java -d classes

classes/ADT/DistanceMatrix.class: classes/ADT/Point.class
	javac $(flags) -cp ".:classes" ADT/DistanceMatrix.java -d classes

classes/Algorithms/ClusterSplitter.class: classes/ADT/AbstractCluster.class classes/ADT/Point.class
	javac $(flags) -cp ".:classes" Algorithms/ClusterSplitter.java -d classes

classes/Algorithms/ClusterValidityChecker.class: classes/ADT/Point.class
	javac $(flags) -cp ".:classes" Algorithms/ClusterValidityChecker.java -d classes

classes/Algorithms/Diana.class: classes/ADT/DistanceMatrix.class classes/ADT/Centroid.class classes/ADT/Cluster.class classes/ADT/Point.class classes/Algorithms/ClusterSplitter.class classes/Algorithms/ClusterValidityChecker.class classes/Algorithms/KMeans.class classes/ADT/AbstractCluster.class
	javac $(flags) -cp ".:classes" Algorithms/Diana.java -d classes

classes/Algorithms/KMeans.class: classes/ADT/Cluster.class classes/ADT/AbstractCluster.class
	javac $(flags) -cp ".:classes" Algorithms/KMeans.java -d classes

classes/Main.class: classes/ADT/Centroid.class classes/ADT/Point.class classes/ADT/AbstractCluster.class classes/ADT/Cluster.class classes/ADT/AbstractPoint.class classes/ADT/DistanceMatrix.class classes/Algorithms/ClusterSplitter.class classes/Algorithms/ClusterValidityChecker.class classes/Algorithms/Diana.class classes/Algorithms/KMeans.class
	javac $(flags) -cp ".:classes" Main.java -d classes


clean:
	rm -R classes/*

