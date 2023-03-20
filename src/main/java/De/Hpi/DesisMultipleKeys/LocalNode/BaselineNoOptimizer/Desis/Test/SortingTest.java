package De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.Test;

import De.Hpi.DesisMultipleKeys.LocalNode.BaselineNoOptimizer.Desis.Dao.Tuple;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class SortingTest {

    public static void main(String[] args){
        int listSize = 10000;
        int listSize2 = 100;
        Random random = new Random();
        long timeBefore1;

        for(int i = 0; i < listSize; i++){
            Tuple tuple = new Tuple();
            tuple.DATA = random.nextDouble() * 1000000;
        }

//        timeBefore1 = System.nanoTime();
//        TreeSet<Tuple> tupleTreeSetAll = new TreeSet<>((a, b) -> Double.compare(a.DATA, b.DATA));
//        for(int j = 0; j < listSize2; j++) {
//            TreeSet<Tuple> tupleTreeSet = new TreeSet<>((a, b) -> Double.compare(a.DATA, b.DATA));
//            for (int i = 0; i < listSize; i++) {
//                Tuple tuple = new Tuple();
//                tuple.DATA = random.nextDouble() * 1000000;
//                tuple.TIME = 222;
//                tuple.EVENT = 222;
//                tupleTreeSet.add(tuple);
//            }
//            tupleTreeSetAll.addAll(tupleTreeSet);
//        }
//        System.out.println(System.nanoTime() - timeBefore1);

        timeBefore1 = System.nanoTime();
        ArrayList<Tuple> arrayListAll = new ArrayList<>();
        for(int j = 0; j < listSize2; j++) {
            ArrayList<Tuple> arrayList = new ArrayList<>();
            for (int i = 0; i < listSize; i++) {
                Tuple tuple = new Tuple();
                tuple.DATA = random.nextDouble() * 1000000;
                tuple.TIME = 222;
                tuple.EVENT = 222;
                arrayList.add(tuple);
            }
            arrayList.sort((a, b) -> Double.compare(a.DATA, b.DATA));
            arrayListAll.addAll(arrayList);
        }
        timeBefore1 = System.nanoTime();
        arrayListAll.sort((a, b) -> Double.compare(a.DATA, b.DATA));
        System.out.println(System.nanoTime() - timeBefore1);

        timeBefore1 = System.nanoTime();
        LinkedList<Tuple> linkedListAll = new LinkedList<>();
        for(int j = 0; j < listSize2; j++) {
            LinkedList<Tuple> linkedList = new LinkedList<>();
            for (int i = 0; i < listSize; i++) {
                Tuple tuple = new Tuple();
                tuple.DATA = random.nextDouble() * 1000000;
                tuple.TIME = 222;
                tuple.EVENT = 222;
                linkedList.add(tuple);
            }
            linkedList.sort((a, b) -> Double.compare(a.DATA, b.DATA));
            linkedListAll.addAll(linkedList);
        }
        timeBefore1 = System.nanoTime();
        linkedListAll.sort((a, b) -> Double.compare(a.DATA, b.DATA));
        System.out.println(System.nanoTime() - timeBefore1);



    }

}
