package De.Hpi.DesisAll.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.Test;

import De.Hpi.DesisAll.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.Dao.Tuple;
import De.Hpi.DesisAll.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.Dao.Window;
import De.Hpi.DesisAll.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.Dao.WindowCollection;

import java.util.ArrayList;

public class CalculationTest {

    public static void main(String[] args){
        int calculationTimes = 10000000;
        int queryNumber = 1;
        double result;




//        long timeBefore1 = System.nanoTime();
        for(int i = 0; i < calculationTimes; i++){
            result = Long.MAX_VALUE / 715;
        }
//        System.out.println(System.nanoTime() - timeBefore1);
//
//        timeBefore1 = System.nanoTime();
//        for(int i = 0; i < calculationTimes; i++){
//            result = Long.MAX_VALUE / 715;
//        }
//        System.out.println(System.nanoTime() - timeBefore1);
//
//        long small = Long.MAX_VALUE / 2;
//        timeBefore1 = System.nanoTime();
//        for(int i = 0; i < calculationTimes; i++){
//            result = 1000 / 715;
//        }
//        System.out.println(System.nanoTime() - timeBefore1);

        //malloc is most expensive
        long timeBefore1 = System.nanoTime();
        for(int i = 0; i < calculationTimes; i++){
            WindowCollection windowCollection = new WindowCollection();
            windowCollection.tuples = new ArrayList<>();
            windowCollection.windowList = new ArrayList<>();
            for(int j = 0; j < 1; j++) {
                Window window = new Window();
                window.queryId = j;
                window.windowId = j;
                window.count = j;
                window.result = j;
                windowCollection.windowList.add(window);
            }
        }
        System.out.println(System.nanoTime() - timeBefore1);

        timeBefore1 = System.nanoTime();
        for(int i = 0; i < calculationTimes; i++){
            WIndowArrayTest wIndowTest = new WIndowArrayTest();
            wIndowTest.queryId = new int[queryNumber];
            wIndowTest.windowId = new int[queryNumber];
            wIndowTest.count = new long[queryNumber];
            wIndowTest.result = new double[queryNumber];
            wIndowTest.tuples = new ArrayList<>();
            for(int j = 0; j < 1; j++) {
                wIndowTest.queryId[j] = j;
                wIndowTest.windowId[j] = j;
                wIndowTest.count[j] = j;
                wIndowTest.result[j] = j;
            }
        }
        System.out.println(System.nanoTime() - timeBefore1);

        timeBefore1 = System.nanoTime();
        for(int i = 0; i < calculationTimes; i++){
            WindowArrayListTest windowArrayListTest = new WindowArrayListTest();
            windowArrayListTest.queryId = new ArrayList<>();
            windowArrayListTest.windowId = new ArrayList<>();
            windowArrayListTest.count = new ArrayList<>();
            windowArrayListTest.result = new ArrayList<>();
            windowArrayListTest.tuples = new ArrayList<>();
            for(int j = 0; j < 1; j++) {
                windowArrayListTest.queryId.add(j);
                windowArrayListTest.windowId.add(j);
                windowArrayListTest.count.add((long) j);
                windowArrayListTest.result.add((double) j);
            }
        }
        System.out.println(System.nanoTime() - timeBefore1);


    }


    public static class WIndowArrayTest {

        public int nodeId;
        //    private int windowId;
        public int[] queryId;
        public int[] windowId;

        //intermediate results
        public double[] result;
        public long[] count;
        public ArrayList<Tuple> tuples;

    }

    public static class WindowArrayListTest {

        public int nodeId;
        //    private int windowId;
        public ArrayList<Integer> queryId;
        public ArrayList<Integer> windowId;

        //intermediate results
        public ArrayList<Double> result;
        public ArrayList<Long> count;
        public ArrayList<Tuple> tuples;

    }
}
