import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utility {

    /**
     * BufferedReader which reads the text from the user.
     */
    public static final BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in)); // Set up the ability to read user input from keyboard

    /**
     * Reads in the user's input and returns it.
     *
     * @return String representing user's input
     */
    public synchronized static String userInput() {
        try {
            return userInput.readLine();
        } catch (IOException e) {
            print("Couldn't read in user input");
            return null;
        }
    }

    public static void print(String stringToPrint) {
        synchronized (System.out) {
            System.out.println(stringToPrint);
        }
    }
}
