import java.io.IOException;
import java.util.HashMap;

/**
 * This class represents a type of client, a DoDClient.
 */
public class DoDClient extends Client {

    /**
     * HashMap with a String representing the clientID and a GameLogic instance representing the game they are playing
     */
    private final HashMap<String, GameLogic> ongoingGames = new HashMap<>();

    /**
     * The constructor initialises userInput, socket (after validation), serverIn and serverOut.
     * It then sends the server a message informing it which type of client it is.
     *
     * @param consoleAddress String representing the address entered into the console (default is "localhost" if none was entered)
     * @param consolePort    String representing the port number entered into the console (default is "14001" if none was entered)
     */
    public DoDClient(String consoleAddress, String consolePort) {
        super(consoleAddress, consolePort); // Sets up the connection with the server and initialises the required BufferedReaders and PrintWriter
        serverOut.println(clientType()); // Sends the server a message informing it which type of client it is
    }

    /**
     * The main method, creates an instance of DoDClient using console arguments as input for portString and address
     * ("14001" and "localhost" are defaults respectively) and calls the go method
     *
     * @param args arguments received from commandline
     */
    public static void main(String[] args) {
        String portString = "14001"; // Default portString is set to 14001 as required
        String addressIP = "localhost"; // Default addressIP is set to localhost as required
        /*
        Loops through all arguments and checks for "-cca" at which point it assigns the addressIP to the next argument
        and "-ccp" at which point it assigns the portString to the next argument.
        Hence, if multiple "-cca"/"-ccp" are entered, the last "-cca"/"-ccp" instance will be the determining one.
         */
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("-cca")) {
                addressIP = args[i + 1];
            }
            if (args[i].equals("-ccp")) {
                portString = args[i + 1];
            }
        }
        DoDClient myDoDClient = new DoDClient(addressIP, portString); // Creates an instance of ChatClient
        myDoDClient.go(); // Calls the go method
    }

    /**
     * Implementation of the abstract method which is how the DoDClient should run.
     * It runs the main loop which deals with reading from and writing to the server, and once the main loop is broken from,
     * the BufferedReaders are closed and then the socket is closed.
     */
    @Override
    public void go() {
        try {
            mainLoop();
        } catch (IOException e) { // This is reached if the server is closed forcibly.
            Utility.print("Server forcibly closed.");
        } finally {
            cleanShutDown(); // Closes the PrintWriter, BufferedReaders and socket
        }
    }

    /**
     * Keeps reading the data received from the server, processing the data and if necessary then replying to the server
     * This only stops if an IOException is thrown (if the server is forcibly closed) or if the serverMsg is null,
     * which occurs if the server closed peacefully.
     *
     * @throws IOException if the server is closed forcibly
     */
    private void mainLoop() throws IOException {
        while (true) {
            String serverResponse = serverIn.readLine(); // Reads the data from the server
            if (serverResponse == null) {
                break;
            } // Breaks if serverMsg is null as that implies the server has been shut down
            String[] serverMsg = serverResponse.split(" ", 2);
            if (serverMsg[0].equals("secondClient")) {
                Utility.print("Another DoDClient connected to server.");
            } else if (serverMsg[0].equals("disconnect")) {
                String clientID = serverMsg[1];
                ongoingGames.remove(clientID);
                Utility.print("Disconnected client " + clientID);
            } // If the message is a disconnect client then client is removed from the list of games
            else {
                dealWithServerMsg(serverMsg);
            } // Deals with the message
        }
    }

    /**
     * Depending on the serverMsg, this will either start a new game for the given clientID or attempt to input the clientID's
     * request into the DoD game, and will send a String reply to the server
     *
     * @param serverMsg String[] that consists of "clientID + ' ' + message" or "newGame + ' ' + clientID"
     */
    private void dealWithServerMsg(String[] serverMsg) {
        String clientID;
        String gameResponse;
        if (serverMsg[0].equals("newGame")) { // If the command sent by the server is to start a new game
            clientID = serverMsg[1];
            gameResponse = createNewGame(clientID); // Creates a new game and stores it in the HashMap corresponding with clientID key
        } else {
            clientID = serverMsg[0];
            gameResponse = ongoingGames.get(clientID).loopTurn(serverMsg[1].toUpperCase()); // Executes a turn in the DoD game
        }
        sendToServer(clientID, gameResponse); // Sends gameResponse to the server
    }

    /**
     * Sends the gameResponse to the server, directed to reach only the client the given clientID
     *
     * @param clientID     String representing the client's ID on the server
     * @param gameResponse String representing the output from the DoD game
     */
    private void sendToServer(String clientID, String gameResponse) {
        // Loops through each row in gameResponse and sends the row to the server
        for (String response : gameResponse.split("\n")) {
            // message sent to server consists of clientID, boolean representing whether it is the last message, and the response line, separated by spaces
            serverOut.println(clientID + " " + false + " " + response);
        }
        serverOut.println(clientID + " " + true + " " + ongoingGames.get(clientID).getGameRunning()); // Last message sends the server whether the game has ended or not
    }

    /**
     * Creates a new GameLogic instance and stores it in the HashMap as the value for the clientID (which is the key)
     *
     * @param clientID String representing the client's ID on the server
     * @return String to return to the server
     */
    private String createNewGame(String clientID) {
        GameLogic logic = new GameLogic(1, 1); // Initialises the map
        logic.init(); // Initialises the game settings
        ongoingGames.put(clientID, logic);
        Utility.print("Client " + clientID + " Started a game.");
        return "Default Map initialised. Good Luck!";

    }

    /**
     * Implementation of the abstract method which returns the client type.
     *
     * @return senderType senderType.DoDBot which represents the type of the client is DoDClient
     */
    @Override
    public SenderType clientType() {
        return SenderType.DoDBot;
    }
}
