import java.io.BufferedReader;
import java.io.IOException;

/**
 * This class deals with reading data from the server and displaying it to the user. It inherits from thread as it is
 * run concurrently to the write part of the ClientChat.
 */
public class ClientRead extends Thread {

    /**
     * BufferedReader which reads the text from the server.
     */
    private final BufferedReader serverIn;

    /**
     * Constructor that initialises the serverIn
     *
     * @param serverIn BufferedReader which reads the text from the server.
     */
    public ClientRead(BufferedReader serverIn) {
        this.serverIn = serverIn; // Set up the ability to read the data from the server
    }

    /**
     * Implementation of the abstract run method which is how the ClientRead should run.
     * It runs the main loop which deals with reading from the server, and once the main loop is broken from,
     * the thread ends.
     */
    @Override
    public void run() {
        try {
            readLoop();
        } catch (IOException e) { // This is reached if the server is closed forcibly.
            Utility.print("Server forcibly closed.");
        } finally {
            Utility.print("Shutting Down client. Press enter to close...");
        }
    }

    /**
     * Keeps reading the data received from the server, and displaying it to the user
     * This only stops if an IOException is thrown (if the server is forcibly closed) or if the serverResponse is null,
     * which occurs if the server closed peacefully.
     *
     * @throws IOException if the server is closed forcibly
     */
    private void readLoop() throws IOException {
        while (true) {
            String serverResponse = serverIn.readLine(); // Reads the data from the server
            if (serverResponse == null) {
                break;
            } // Breaks if serverMsg is null as that implies the server has been shut down
            Utility.print(serverResponse); // Displays serverResponse to the user
        }
    }
}

