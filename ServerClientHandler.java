import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class deals with handling with a single client connected to the server. It inherits from thread as it is
 * run concurrently with other parts of the ChatServer (such as the ChatServer and the ServerUserInput Thread).
 */
public class ServerClientHandler extends Thread {

    /**
     * Socket representing the server side endpoint for communication between the client and server.
     */
    private final Socket clientSocket;

    /**
     * ChatServer representing the server that ServerClientThread is instantiated from.
     */
    private final ChatServer server;
    /**
     * int representing the client's ID.
     */
    private final int clientID;
    /**
     * PrintWriter which writes text to the client.
     */
    private PrintWriter clientOut;
    /**
     * BufferedReader which reads the text from the client.
     */
    private BufferedReader clientIn;
    /**
     * senderType which represents the type of the client
     */
    private SenderType clientType;
    /**
     * Boolean which represents whether the client is in a DoD game or not.
     */
    private boolean inDoDGame = false;
    /**
     * Boolean representing whether this thread should terminate or not.
     */
    private boolean terminate;

    /**
     * The constructor initialises the clientSocket, server, clientID, clientIn, clientOut, clientType and terminate variables.
     *
     * @param clientSocket Socket representing the server side endpoint for communication between the client and server
     * @param server       ChatServer representing the server that ServerClientThread is instantiated from
     * @param clientID     int representing the client's ID
     */
    public ServerClientHandler(Socket clientSocket, ChatServer server, int clientID) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.clientID = clientID;
        this.terminate = initialiseStreams(); // returns whether the clientIn, clientOut and clientType have been successfully initialised

    }

    /**
     * Attempts to initialise clientIn, clientOut and clientType and returns whether the attempt is successful or not.
     *
     * @return Boolean representing whether clientIn, clientOut and clientType have been successfully initialised
     */
    private boolean initialiseStreams() {
        try {
            this.clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Setup the ability to read the data from the client
            this.clientOut = new PrintWriter(clientSocket.getOutputStream(), true); // Setup the ability to send the data to the client
            this.clientType = SenderType.valueOf(clientIn.readLine()); // Reads the message from the client containing their type.
            return false; // Initialisation succeeded, so the thread should not terminate
        } catch (IOException e) { // Reaches this point if the initialisation of one of the above has failed
            return true; // terminates the thread as something is wrong with the initialisation of one of the variables above
        }
    }

    /**
     * Attempts to close the clientIn and clientOut streams
     */
    private void closeStreams() {
        try {
            clientIn.close(); // Closes the BufferedReader corresponding with reading text from the client
            clientOut.close(); // Closes the PrintWriter corresponding with writing text to the client
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Implementation of the abstract run method which is how the ServerClientHandler should run.
     * It runs the handleClientSocket which deals with reading from the client and dealing with their inputs,
     * Once the main loop is broken from, the client is removed from the server and the streams are closed.
     */
    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) { // This is reached when client forcibly disconnects or if the socket is closed
            Utility.print("Client " + clientID + " socket's closed.");
        } finally {
            closeStreams(); // Closes the clientIn and clientOut streams
            removeClientFromServer(); // Closes Client Socket
            Utility.print("Client " + clientID + " disconnected.");
        }
    }

    private void removeClientFromServer() {
        try {
            server.removeClient(clientID); // Removes client from server and DoDClient (if one exists)
        } catch (IOException e) { // This is reached if the client socket fails to close
            Utility.print("Socket " + getClientSocket() + " failed to close. Close streams.");
        }
    }

    /**
     * Keeps reading the client's input and deal with it in another method until the "terminate" boolean value is true
     * or if an IOException is thrown.
     *
     * @throws IOException If a client forcibly disconnects
     */
    private void handleClientSocket() throws IOException {
        String userInput;
        while (!terminate) {
            userInput = clientIn.readLine();
            if (userInput == null) {
                throw new IOException();
            }
            dealWithClientMsg(userInput); // Deals with the client's message depending on the message and the mode they are in
        }
    }

    /**
     * Sends a message to the client using the PrintWriter.
     *
     * @param message String representing the message to the sent to the client
     */
    public void sendMessage(String message) {
        clientOut.println(message);
    }

    /**
     * Returns the type of the client.
     *
     * @return senderType which represents the type of the client
     */
    public SenderType getClientType() {
        return clientType;
    }

    /**
     * Returns the clientID.
     *
     * @return int representing the client's ID
     */
    public int getClientID() {
        return clientID;
    }

    /**
     * Sets the terminate variable to true.
     */
    public void terminateThread() {
        terminate = true;
    }

    /**
     * Returns whether the client is in the middle of playing a DoD game.
     *
     * @return Boolean representing whether the client is in the middle of playing a DoD game
     */
    public boolean getInDoDGame() {
        return inDoDGame;
    }

    /**
     * Sets the inDoDGame variable to the boolean value provided.
     *
     * @param inDoDGame Boolean representing the value the inDoDGame variable should be set to
     */
    public void setInDoDGame(boolean inDoDGame) {
        this.inDoDGame = inDoDGame;
    }

    /**
     * Returns the clientSocket
     *
     * @return Socket representing the server side endpoint for communication between the client and server.
     */
    public Socket getClientSocket() {
        return clientSocket;
    }


    /**
     * Deals with the client's message depending on their the client's type, the message and their current mode.
     *
     * @param message String containing the message received from the client
     */
    private void dealWithClientMsg(String message) {
        MessageType messageType;
        if (clientType == SenderType.DoDBot) { // If the client is a DoDBot then the message is unpacked
            String[] messageSplit = message.split(" ", 3); // the String[] contains the clientID, whether this is the last message from the DoDClient and the message to client
            messageType = Boolean.parseBoolean(messageSplit[1]) ? MessageType.DoDToClientMetaData : MessageType.DoDToClient;
            message = messageSplit[0] + " " + messageSplit[2];
        } else if (inDoDGame) { // If the client is in the middle of a DoD game, then the message is forwarded to the DoDClient
            messageType = MessageType.DoDMidGame;
            message = clientID + " " + message;
        } else if (message.equalsIgnoreCase("JOIN")) { // If the user requests to start a DoD game
            messageType = MessageType.DoDNewGame;
        } else { // All other messages are broadcasted
            messageType = clientType == SenderType.client ? MessageType.broadcastClient : MessageType.broadcastBot;
            String userType = clientType == SenderType.client ? "Client " : "Bot ";
            message = userType + clientID + ": " + message;
        }
        server.dealWithClientMsg(clientID, messageType, message);
    }
}