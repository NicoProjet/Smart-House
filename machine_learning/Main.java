//package clustering;
import clustering.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
	public static void main(String [ ] args)
	{
		ArrayList<Point> points = Point.createRandomPoints(0, 2500, 0, 1000, 5);
		Diana diana = new Diana(points);
		ArrayList<Point> test = diana.computeDIANA();
        System.out.println("\n size of prediction list: " + test.size());
        System.out.println("\nlist of points: ");
        for( Point point : test) {
            //System.out.println("olulolul");
            point.toString();
        }
	}
}
