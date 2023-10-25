import java.util.ArrayList;
import java.util.Collections;

/** CodingQuestionOne - You are not allowed to change the method signature,
 *  and the sorting algorithm must follow the pseudocode.
 *
 *  A main method is provided which allows you to test your solution.
 */
public class CodingQuestionOne {


    /**
     * Sorts the ArrayList of Strings in-place such that the sorted list
     * is in reverse alphabetical order.
     *
     * @param  input   an ArrayList of strings that needs to be sorted
     */
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

        CodingQuestionOne.sortingAlgorithm(test);
        for(String item: test) {
            System.out.println(item);
        }

    }
}
