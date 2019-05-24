//package Application;

import machineLearning.ADT.Cluster;
import machineLearning.ADT.DistanceMatrix;
import machineLearning.ADT.Point;
import machineLearning.Algorithms.Diana;
//import java.util.concurrent.TimeUnit
/*import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;*/

import java.util.ArrayList;
import java.util.Random;

public class Main {// extends Application {

/*    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }
*/

/*    public static void main(String[] args) {
        launch(args);
    }
*/
    public static  void main(String[] args){
        int minX=0,maxX=30,minY=0,maxY=22000,number=10000;
        Random r=new Random();

        ArrayList<Point> points = new ArrayList<>(number);
        for(int i = 0; i < number; i++) { //for(int i = 0; i &lt; number; i++) {
            Point tmp = new Point(minX+(maxX-minX)*r.nextDouble(),minY+(maxY-minX)*r.nextDouble());
            points.add(tmp);
            System.out.println(tmp);
        }
        //--------------------------------
        //print points sur le graph mettre un thread.sleep entre le plot des points
        //-----------------------------------------------
        Diana test=new Diana(points);
        System.out.println(String.format("%d clusters produced :\n",test.getClusters().length));
        for (Cluster cluster : test.getClusters()) {
            System.out.println(String.format("cluster n° %d, size : %d, max distance : %.3f",cluster.getId(),cluster.size(),cluster.getMaxDistance()));
            /*try {
                Thread.sleep(1000);
            } catch(InterruptedException ex) {
                 Thread.currentThread().interrupt();
            }
            System.out.println(cluster);*/
        }

    }
}
/* Plan pour printer les points sur le graphique 
phase1: print liste de points transmise au ML sur le phrage 
    -> java.main/main()
phase 2:    - highlight vieux centroides ,les supprimer (gérer la première fois), les supprimer du graph et print les centroides (couleur différente des points existants ) 
    -> Diana.java/split()
            - changement de couleurs des points en fonction de leur distances aux centroides (on peut les aggrandire pour qu on coprennent de quel points on parle et/ou frizé apres que les points ont été assignés )
    -> Diana.java/split()
phase 3: on récupére la liste de prédictions et on print sur le graph (en meme tps que la maison)
    -> Main.java/main()
*/
