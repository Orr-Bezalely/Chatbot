import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class deals with writing data to the server. It inherits from thread as it is
 * run concurrently to the read part of the ClientChat.
 */
public class ClientWrite extends Thread {


    /**
     * Socket representing the client side endpoint for communication between the client and server.
     */
    private final Socket socket;

    /**
     * PrintWriter which writes text to the server.
     */
    private final PrintWriter serverOut;

    /**
     * Constructor which initialises the socket and serverOut
     *
     * @param socket    Socket representing the client side endpoint for communication between the client and server
     * @param serverOut PrintWriter which writes text to the server
     */
    public ClientWrite(Socket socket, PrintWriter serverOut) {
        this.socket = socket;
        this.serverOut = serverOut; // Set up the ability to write the data to the server
    }

    /**
     * Implementation of the abstract run method which is how the ClientWrite should run.
     * It runs the main loop which deals with writing to the server, and once the main loop is broken from,
     * the thread ends.
     */
    @Override
    public void run() {
        writeLoop();
    }

    /**
     * Keeps reading the user's input and writing that data to the server.
     * This only stops if an if the socket is closed down, which occurs if the server closed.
     */
    private void writeLoop() {
        String userInputString;
        while (true) {
            userInputString = Utility.userInput(); // Reads in the user's input
            if (userInputString == null || socket.isClosed()) {
                break;
            }
            serverOut.println(userInputString); // Sends user's input to the server
        }
    }

}