
all: classes/Main.class

classes/Main.class: classes/Point.class classes/Cluster.class classes/KMeans.class classes/Diana.class
	javac -cp ".:classes" Main.java -d classes

classes/Diana.class: classes/Point.class classes/Cluster.class classes/KMeans.class
	javac -cp ".:classes" clustering/Diana.java -d classes

classes/KMeans.class: classes/Point.class classes/Cluster.class
	javac -cp ".:classes" clustering/KMeans.java -d classes

classes/Cluster.class: classes/Point.class
	javac -cp ".:classes" clustering/Cluster.java -d classes

classes/Point.class:
	javac clustering/Point.java -d classes

