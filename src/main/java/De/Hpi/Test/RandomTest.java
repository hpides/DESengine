package De.Hpi.Test;

import java.util.*;

public class RandomTest {
    public static void main(String[] args) {
        Random random = new Random();
//        for(int i = 0; i< 10000; i++){
//            int temp = random.nextInt(1000);
//            if(temp == 1)
//            System.out.println(temp);
//        }
//        System.out.println(random.nextInt(1000));
//        System.out.println(random.nextInt(1000));
//        System.out.println(random.nextInt(1000));
//        System.out.println(random.nextInt(1000));
//        System.out.println(random.nextInt(1000));

        List<Double> list =
                Arrays.asList(9304926.0, 9398916.0, 9288468.03, 9394990.0, 9246141.96, 9281877.572, 9302153.97, 9389027.43);

        list.forEach(aDouble -> {

            System.out.print(aDouble * 0.532820653);
            System.out.print(", ");

        });








//        ArrayList<Double> arrayList = new ArrayList<>();
//        arrayList.add(1.0);
//        arrayList.add(2.3);
//        arrayList.add(3.3);
//        arrayList.add(4.3);
//        arrayList.add(5.3);
//        arrayList.add(6.3);

//        Iterator<String> iter = arrayList.iterator();
//        while(iter.hasNext()){
//            String ele = iter.next();
//            if(!ele.equals("WINTER")) {
//                System.out.println(ele);
//            }else{
//                iter.remove();
//                break;
//            }
//        }

//        Double result = arrayList.stream().mapToDouble(item -> item).sum();
//
//        arrayList.stream().limit(3).collect(Collectors.toList()).stream();
//
//        System.out.println(result);


    }
}
