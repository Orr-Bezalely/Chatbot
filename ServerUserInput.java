import java.io.IOException;

/**
 * This class deals with the server user's request if they wish to close down the server. It extends Thread as it is
 * run concurrently with other parts of the ChatServer (such as the ChatServer and the ServerClientHandler Thread)
 */
public class ServerUserInput extends Thread {
    /**
     * ChatServer representing the server that ServerUserInput is instantiated from.
     */
    private final ChatServer server;


    /**
     * Constructor that initialises the server and the userInput
     *
     * @param server ChatServer representing the server that ServerUserInput is instantiated from
     */
    public ServerUserInput(ChatServer server) {
        this.server = server;
    }

    /**
     * Implementation of the abstract run method which is how the ServerUserInput should run.
     * It runs the main loop which deals with reading the user's input and closing the server when appropriate.
     */
    @Override
    public void run() {
        try {
            mainLoop();
        } catch (IOException e) {
            Utility.print("Failed to close a socket.");
        } catch (InterruptedException e) {
            Utility.print("Client thread did not join");
        }
    }

    /**
     * Keeps reading the user's input until they enter exit, at which point the server is shut down cleanly and the loop is broken from.
     *
     * @throws IOException If one of the server's clients cannot close connection.
     */
    /**
     * Keeps reading the user's input until they enter exit, at which point the server is shut down cleanly and the loop is broken from.
     *
     * @throws IOException          If one of the server's clients cannot close connection.
     * @throws InterruptedException If a thread did not join
     */
    private void mainLoop() throws IOException, InterruptedException {
        while (true) {
            String serverUserInput = Utility.userInput(); // Gets user's input from keyboard
            if (serverUserInput != null && serverUserInput.equalsIgnoreCase("exit")) { // Checks if user's input is 'exit' - case insensitive
                server.cleanShutDown(); // Shuts down server cleanly
                break; // Breaks from this loop, allowing this thread to finish.
            }
        }
    }

}

