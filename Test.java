import java.util.ArrayList;
import java.util.Collections;

public class Test {
//    public static void main(String[] args) {
//        Object lockOne = 1;
//        Object lockTwo = 2;
//        Object lockThree = 3;
//        new Thread(() -> {
//            synchronized (lockOne) {
//                System.out.println("Foo");
//                synchronized (lockTwo) {
//                    try {
//                        Thread.sleep(10000);
//                    } catch (InterruptedException e) { e.printStackTrace(); }
//                }
//            }
//        }).start();
//        new Thread(() -> {
//            synchronized (lockTwo) {
//                System.out.println("Bar");
//                synchronized (lockThree) {
//                    try {
//                        Thread.sleep(10000);
//                    } catch (InterruptedException e) { e.printStackTrace(); }
//                }
//            }
//        }).start();
//
//        new Thread(() -> {
//            synchronized (lockOne) {
//                System.out.println("Baz");
//                synchronized (lockThree) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) { e.printStackTrace(); }
//                }
//            }
//        }).start();
//    }

    public static void sortingAlgorithm(ArrayList<String> input) {
        int start = 0, end = input.size() - 1;
        boolean swapped = true;
        while(swapped){
            swapped = false;
            for(int i=start; i<end; i++){
                if(input.get(i).compareTo(input.get(i + 1)) < 0){
                    Collections.swap(input, i, i+1);
                    swapped = true;
                }
            }
            if (!swapped){
                break;
            }
            swapped = false;
            end --;
            for(int i=end; i>=start; i--){
                if(input.get(i).compareTo(input.get(i + 1)) < 0){
                    Collections.swap(input, i, i+1);
                    swapped = true;
                }
            }
            start++;
        }

        //
        // set start to 0
        // set end to length of input - 1
        // set swapped to true
        //
        // while swapped is true:
        //		set swapped to false
        //		for each i from start to end - 1:
        //			if input[i] lexicographically precedes input[i+1]:
        //				swap input[i] and input[i+1]
        //				set swapped to true
        //
        //		if swapped is false:
        //			break
        //
        //		set swapped to false
        //
        //		end -= 1
        //
        //		for each i from end to start:
        //			if input[i] lexicographically precedes input[i+1]:
        //				swap input[i] and input[i+1]
        //				set swapped to true
        //
        //		start += 1
        //

    }

    /**
     *  Coding Question One:
     *
     *  Please implement the pseudocode in the sortingAlgorithm method so that it
     *  returns the ArrayList of Strings in reverse alphabetical order.
     *
     */
    public static void main(String[] args) {
        ArrayList<String> test = new ArrayList<String>();
        test.add("Raspberry");
        test.add("Banana");
        test.add("Melon");
        test.add("Apple");
        test.add("Mango");
        test.add("Strawberry");
        test.add("Pineapple");
        test.add("Coconut");
        test.add("Grapes");
        test.add("Avacado");

        sortingAlgorithm(test);
        for(String item: test) {
            System.out.println(item);
        }

    }

}
