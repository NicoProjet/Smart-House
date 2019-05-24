flags= -Xlint -Werror

all: communication server

communication: classes/communication/Request.class classes/communication/RequestChange.class classes/communication/RequestChangeSchedule.class


server: classes/Server.class
# application: classes/HeatControler.class

classes/Server.class: classes/database/DatabaseAccess.class classes/scheduleControl/Update_daemon.class classes/machineLearning/DayPlanner_daemon.class classes/communication/Listen_thread.class classes/machineLearning/Scheduler.class classes/communication/Response.class classes/communication/ResponseServer.class
	javac $(flags) -cp "classes" server/Server.java -d classes

classes/communication/Listen_thread.class: classes/machineLearning/Scheduler.class
	javac $(flags) -cp "classes" server/Listen_thread.java -d classes

classes/scheduleControl/Update_daemon.class: classes/database/DatabaseAccess.class classes/machineLearning/Scheduler.class
	javac $(flags) -cp "classes" server/Update_daemon.java -d classes


classes/machineLearning/DayPlanner_daemon.class: classes/scheduleControl/Schedule.class classes/scheduleControl/Day.class classes/database/DatabaseAccess.class classes/machineLearning/Scheduler.class classes/machineLearning/Algorithms/Diana.class classes/machineLearning/ADT/Point.class classes/machineLearning/ADT/Centroid.class
	javac $(flags) -cp "classes" server/DayPlanner_daemon.java -d classes

classes/machineLearning/Scheduler.class: classes/scheduleControl/Day.class classes/heatControl/HeaterManager.class classes/database/DatabaseAccess.class
	javac $(flags) -cp "libraries/commons-math3-3.6.1/commons-math3-3.6.1.jar:classes" server/Scheduler.java -d classes

classes/machineLearning/ADT/Centroid.class: classes/machineLearning/ADT/AbstractPoint.class
	javac $(flags) -cp "classes" machineLearning/ADT/Centroid.java -d classes

classes/machineLearning/ADT/Point.class: classes/machineLearning/ADT/AbstractPoint.class
	javac $(flags) -cp "classes" machineLearning/ADT/Point.java -d classes

classes/machineLearning/ADT/AbstractCluster.class: classes/machineLearning/ADT/Point.class classes/machineLearning/ADT/Centroid.class classes/machineLearning/Algorithms/ClusterValidityChecker.class
	javac -cp "classes" machineLearning/ADT/AbstractCluster.java -d classes

classes/machineLearning/ADT/Cluster.class: classes/machineLearning/ADT/AbstractCluster.class classes/machineLearning/ADT/Point.class classes/machineLearning/ADT/Centroid.class classes/machineLearning/Algorithms/ClusterSplitter.class classes/machineLearning/Algorithms/ClusterValidityChecker.class
	javac -cp "classes" machineLearning/ADT/Cluster.java -d classes

classes/machineLearning/ADT/AbstractPoint.class:
	javac $(flags) machineLearning/ADT/AbstractPoint.java -d classes

classes/machineLearning/ADT/DistanceMatrix.class:
	javac $(flags) machineLearning/ADT/DistanceMatrix.java -d classes

classes/machineLearning/Algorithms/ClusterSplitter.class: classes/machineLearning/ADT/AbstractCluster.class classes/machineLearning/ADT/Point.class
	javac $(flags) -cp "classes" machineLearning/Algorithms/ClusterSplitter.java -d classes

classes/machineLearning/Algorithms/ClusterValidityChecker.class: classes/machineLearning/ADT/Point.class
	javac $(flags) -cp "classes" machineLearning/Algorithms/ClusterValidityChecker.java -d classes

classes/machineLearning/Algorithms/Diana.class: classes/machineLearning/ADT/DistanceMatrix.class classes/machineLearning/ADT/Centroid.class classes/machineLearning/ADT/Cluster.class classes/machineLearning/ADT/Point.class classes/machineLearning/Algorithms/ClusterSplitter.class classes/machineLearning/Algorithms/ClusterValidityChecker.class classes/machineLearning/Algorithms/KMeans.class classes/machineLearning/ADT/AbstractCluster.class
	javac $(flags) -cp "classes" machineLearning/Algorithms/Diana.java -d classes

classes/machineLearning/Algorithms/KMeans.class: classes/machineLearning/ADT/Cluster.class classes/machineLearning/ADT/AbstractCluster.class
	javac $(flags) -cp "classes" machineLearning/Algorithms/KMeans.java -d classes


classes/database/DatabaseAccess.class: classes/scheduleControl/Schedule.class classes/database/DatabaseManager.class classes/scheduleControl/Day.class
	javac $(flags) -cp "libraries/mysql-connector-java-5.0.8/mysql-connector-java-5.0.8-bin.jar:classes" server/DatabaseAccess.java -d classes

classes/database/DatabaseManager.class:
	javac $(flags) -cp "libraries/mysql-connector-java-5.0.8/mysql-connector-java-5.0.8-bin.jar" server/DatabaseManager.java -d classes

classes/heatControl/HeaterManager.class: classes/heatControl/SerialInterface.class
	javac $(flags) -cp "classes" server/HeaterManager.java -d classes
# /usr/java/jre1.8.0_111/lib/ext/RXTXcomm.jar

classes/heatControl/SerialInterface.class:
	javac -cp ".:/usr/share/java/RXTXcomm.jar" server/SerialInterface.java -d classes

classes/scheduleControl/Day.class:
	javac $(flags) server/Day.java -d classes

classes/scheduleControl/Schedule.class:
	javac $(flags) server/Schedule.java -d classes


# classes/HeatControler.class: classes/communication/NetworkAccess.class
# 	javac $(flags) -cp "classes" application/HeatControler.java -d classes

# classes/communication/NetworkAccess.class: classes/communication/Request.class classes/communication/RequestChange.class classes/communication/RequestChangeSchedule.class classes/schedule/Schedule.class
# 	javac $(flags) -cp "classes:classes" GUI/MyApplication/app/src/main/java/communication/NetworkAccess.java -d classes

# classes/schedule/Schedule.class: classes/communication/Response.class
# 	javac $(flags) -cp "classes" GUI/MyApplication/app/src/main/java/communication/Schedule.java -d classes


classes/communication/Request.class:
	javac $(flags) -cp "classes" GUI/MyApplication/app/src/main/java/communication/Request.java -d classes

classes/communication/RequestChange.class:
	javac $(flags) -cp "classes" GUI/MyApplication/app/src/main/java/communication/RequestChange.java -d classes

classes/communication/RequestChangeSchedule.class:
	javac $(flags) -cp "classes" GUI/MyApplication/app/src/main/java/communication/RequestChangeSchedule.java -d classes

classes/communication/Response.class: classes/scheduleControl/Day.class
	javac $(flags) -cp "classes" communication/Response.java -d classes

classes/communication/ResponseServer.class: classes/scheduleControl/Day.class classes/communication/Response.class
	javac $(flags) -cp "classes" communication/ResponseServer.java -d classes


clean:
	rm -R classes/*