import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class represents the multi-threaded server.
 */
public class ChatServer {

    /**
     * Vector storing ServerClientHandler threads representing the clients
     */
    private final Vector<ServerClientHandler> clientThreadVector = new Vector<>();
    /**
     * AtomicInteger representing the ID that should be assigned to the next client that joins
     */
    private final AtomicInteger clientID = new AtomicInteger(0);
    /**
     * ServerSocket representing the server which is used to accept the client connections
     */
    private ServerSocket mySocket;
    /**
     * Boolean representing whether the server is being closed
     */
    private Boolean serverShutDown = false;

    /**
     * The constructor initialises mySocket (after validation).
     *
     * @param consolePort String representing the port number entered into the console (default is "14001" if none was entered)
     */
    public ChatServer(String consolePort) {
        String portString = consolePort;
        while (true) {
            try {
                createServerSocket(portString); // Attempts hosting a server using the portString supplied
                break;
            } catch (NumberFormatException numberFormatException) { // This is reached if the port number is not an integer between 1024 and 65535
                Utility.print("Port supplied is not a valid number. Please input the port number between 1024-65535.");
                portString = Utility.userInput();
            } catch (IOException e) { // This is reached if an I/O error occurs when opening the socket
                Utility.print("I/O error occurs when opening the socket. Please enter PortString again.");
                portString = Utility.userInput();
            }
        }
    }

    /**
     * The main method, creates an instance of ChatServer using console arguments as input for portString
     * ("14001" as default) and calls the go method
     *
     * @param args arguments received from commandline
     */
    public static void main(String[] args) {
        String portString = "14001"; // Default portString is set to 14001 as required
        /*
        Loops through all arguments and checks for "-csp" at which point it assigns the portString to the next argument.
        Hence, if multiple "-csp" are entered, the last "-csp" instance will be the determining one.
         */
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("-csp")) {
                portString = args[i + 1];
            }
        }
        Utility.print("port: " + portString);
        ChatServer myChatServer = new ChatServer(portString);// Creates an instance of ChatServer
        myChatServer.go(); // Calls the go method
    }

    /**
     * Attempts connecting to the server given the portString supplied.
     *
     * @param portString String representing the port supplied
     * @throws IOException           if an I/O error occurs when opening the socket
     * @throws NumberFormatException if the port number is not an integer between 1024 and 65535
     */
    private void createServerSocket(String portString) throws IOException, NumberFormatException {
        int port = Integer.parseInt(portString);
        if (!(1024 <= port && port <= 65535)) {
            throw new NumberFormatException();
        }
        this.mySocket = new ServerSocket(port);
    }

    /**
     * This method is called once the server is initialised which is how the server should run.
     */
    public void go() {
        Utility.print("Server Listening...");
        // Creates the ServerUserInput thread which deals with reading the server's user input
        ServerUserInput serverUserThread = new ServerUserInput(this);
        serverUserThread.start();
        try {
            while (true) {
                acceptClient();
            } // Accept a connection from a client and creates a thread to handle with their requests
        } catch (IOException e) { // This is reached if the server is closed gracefully
            Utility.print("Disconnected server socket");
        }
    }

    /**
     * This method is responsible for accepting clients to the server and creating a thread that handles with their requests
     *
     * @throws IOException if the server is closed gracefully
     */
    private void acceptClient() throws IOException {
        Socket clientSocket = mySocket.accept(); // Accepts a connection from a client
        Utility.print("Server accepted connection on: " + mySocket.getLocalPort() + " ; " + clientSocket.getPort());
        ServerClientHandler newClientThread = new ServerClientHandler(clientSocket, this, clientID.get()); // Creates a new thread to handle with the client
        clientThreadVector.add(newClientThread);
        checkFirstDoDThread(clientID.get()); // Checks if the client which just connected is another DoDClient. If so it closes the connection with it
        newClientThread.start();
        clientID.incrementAndGet();
    }

    /**
     * Forwards the client's message to the correct method depending on the message type
     *
     * @param senderID    int representing the sender client's ID
     * @param messageType MessageType representing the message's type
     * @param message     String representing the message to be sent
     */
    public void dealWithClientMsg(int senderID, MessageType messageType, String message) {
        switch (messageType) {
            case broadcastClient:
                clientBroadcast(message);
                break;
            case broadcastBot:
                botBroadcast(message);
                break;
            case DoDNewGame:
                createNewDoDGame(senderID);
                break;
            case DoDMidGame:
                forwardMsgToDoDClient(message);
                break;
            case DoDToClient:
                sendClientDoDInfo(message);
                break;
            case DoDToClientMetaData:
                DoDMetaData(message);
                break;
        }
    }

    /**
     * Forwards the message from DoD to the correct client
     *
     * @param message String representing the client ID and message to be sent, separated by a space
     */
    private void sendClientDoDInfo(String message) {
        String[] messageSplit = message.split(" ", 2);
        ServerClientHandler clientThread = getThreadFromID(Integer.parseInt(messageSplit[0])); // Retrieves the ServerClientHandler instance that corresponds with the clientID given
        if (clientThread != null) { // A guard to make sure that if the client disconnected while this is called, the process is discontinued
            clientThread.sendMessage(messageSplit[1]); // Forwards the message to the client with the corresponding client ID
        }
    }

    /**
     * Changes the correct client's mode to broadcast mode if the game is over
     *
     * @param message String representing the client ID and whether the game is over, separated by a space
     */
    private void DoDMetaData(String message) {
        String[] messageSplit = message.split(" ", 2);
        if (!Boolean.parseBoolean(messageSplit[1])) { // If the DoD game has ended, then the client is returned to broadcast mode
            int receiverID = Integer.parseInt(messageSplit[0]);
            removeClientFromDoD(receiverID); // Client is removed from the DoDClient's side
            ServerClientHandler clientThread = getThreadFromID(receiverID); // Retrieves the ServerClientHandler instance that corresponds with the clientID given
            if (clientThread != null) { // A guard to make sure that if the client disconnected while this is called, the process is discontinued
                clientThread.sendMessage("Server: Returning to broadcast mode");
                clientThread.setInDoDGame(false); // Client is returned to broadcast mode
            }
        }
    }

    /**
     * This method forwards the message to all clients which are not in a DoD game and all chatBots.
     *
     * @param message String containing the client's message
     */
    private synchronized void clientBroadcast(String message) {
        for (ServerClientHandler clientThread : clientThreadVector) {
            if (!(clientThread.getInDoDGame()) && clientThread.getClientType() != SenderType.DoDBot) { // Checks the client is not in a DoD game and is not the DoDClient
                clientThread.sendMessage(message); // Sends the client the message
            }
        }
    }

    /**
     * This method forwards the message to all ChatClients which are not in a DoD game.
     * The message is not sent to the chatBots intentionally as the bots do not need to respond to their own messages, only to the clients
     *
     * @param message String containing the client's message
     */
    private synchronized void botBroadcast(String message) {
        for (ServerClientHandler clientThread : clientThreadVector) {
            if (clientThread.getClientType() == SenderType.client && !(clientThread.getInDoDGame())) { // Checks the ChatClient is not in a DoD game
                clientThread.sendMessage(message); // Sends the client the message
            }
        }
    }

    /**
     * This method forwards the message sent by the client to the DoDClient
     *
     * @param message String containing the client's message
     */
    private synchronized void forwardMsgToDoDClient(String message) {
        ServerClientHandler DoDClient = findFirstDoDClient(); // Retrieves the ServerClientHandler instance that corresponds with the DoDClient
        if (DoDClient != null) {
            DoDClient.sendMessage(message);
        } // Sends a message to the DoDClient with the client ID and their message, separated by a space
    }

    /**
     * This method attempts to create a new game in the DoDClient corresponding with the client ID given.
     * The client is then updated to be in DoD mode, which means they will not receive broadcasts.
     *
     * @param clientID int representing the client's ID
     */
    private synchronized void createNewDoDGame(int clientID) {
        ServerClientHandler clientThread = getThreadFromID(clientID); // Retrieves the ServerClientHandler instance that corresponds with the clientID given
        if (clientThread == null) {
            return;
        } // If client disconnected during this process
        ServerClientHandler DoDClient = findFirstDoDClient(); // Retrieves the ServerClientHandler instance that corresponds with the DoDClient
        if (DoDClient == null) { // If there are no DoDClients on the server then the game request is rejected
            clientThread.sendMessage("Server: No DoD client available. Returning to broadcast mode.");
            return;
        }
        clientThread.setInDoDGame(true); // For the client to not receive broadcasts
        clientThread.sendMessage("Server: Entering DoD mode.");
        DoDClient.sendMessage("newGame " + clientID); // Sends a message to the DoDClient requesting a new game for the given client

    }

    /**
     * This method ends a client's DoD game in the DoDClient given the client's ID.
     *
     * @param clientID int representing the client's ID
     */
    private synchronized void removeClientFromDoD(int clientID) {
        ServerClientHandler DoDClient = findFirstDoDClient(); // Retrieves the ServerClientHandler instance that corresponds with the clientID given
        if (DoDClient != null) {
            DoDClient.sendMessage("disconnect " + clientID);
        } // Sends a message to the DoDClient requesting to close the client's game
    }

    /**
     * This method removes a client from the server cleanly given the client's ID.
     *
     * @param clientID int representing the client's ID
     * @throws IOException If socket fails to close
     */
    public synchronized void removeClient(int clientID) throws IOException {
        ServerClientHandler thread = getThreadFromID(clientID); // Retrieves the ServerClientHandler instance that corresponds with the clientID given
        if (thread == null){return;}
        if (thread.getClientType() == SenderType.DoDBot && findFirstDoDClient().getClientID() == clientID) { // If the client is the first DoDClient (lazy checking used for efficiency)
            handleDoDModeClients(); // Returns all clients playing DoD to broadcast mode
        } else { // Ends the client's DoD game in the DoDClient
            removeClientFromDoD(clientID);
        }
        thread.getClientSocket().close();
        if (!serverShutDown) {
            clientThreadVector.remove(thread);
        }
    }

    /**
     * Returns all clients in DoD mode to broadcast mode
     */
    private void handleDoDModeClients() {
        for (ServerClientHandler clientThread : clientThreadVector) {
            if (clientThread.getInDoDGame()) { // Checks if client is in DoD mode
                clientThread.sendMessage("Server: DoD client disconnected. Returning to broadcast mode"); // Sends a message to clients notifying them they have been returned to broadcast mode
                clientThread.setInDoDGame(false); // Returns client to broadcast mode
            }
        }
    }

    /**
     * Returns the ServerClientHandler corresponding with the client's ID
     *
     * @param clientID int representing the client's ID
     * @return ServerClientHandler representing the instance of ServerClientHandler corresponding with the client's ID
     */
    private ServerClientHandler getThreadFromID(int clientID) {
        for (ServerClientHandler clientThread : clientThreadVector) {
            if (clientThread.getClientID() == clientID) { // Checks if the clientID of the thread is identical to the clientID
                return clientThread;
            }
        }
        return null; // If the thread is not found (due to disconnection), then null is returned
    }

    /**
     * Returns the ServerClientHandler corresponding with the first DoDClient on the server
     *
     * @return ServerClientHandler representing the instance of ServerClientHandler corresponding with the first DoDClient
     */
    private ServerClientHandler findFirstDoDClient() {
        for (ServerClientHandler clientThread : clientThreadVector) {
            if (clientThread.getClientType() == SenderType.DoDBot) { // Checks if the client's type is a DoDClient
                return clientThread;
            }
        }
        return null; // If the thread is not found (due to not having a DoDClient on the server), then null is returned
    }

    /**
     * Checks to see whether the client is another (i.e. not the first) DoDClient on the server. If so then they are removed from the server.
     *
     * @param clientID int representing the client's ID
     */
    private void checkFirstDoDThread(int clientID) {
        ServerClientHandler clientThread = getThreadFromID(clientID);
        if (clientThread == null) {
            return;
        }
        // Lazy check does not enter second statement if the first one evaluates false, so firstFirstDodClient() can never return null as it is only called if there is a DoDBot.
        if (clientThread.getClientType() == SenderType.DoDBot && !(findFirstDoDClient().getClientID() == clientID)) { // Checks whether this is a DoDClient which is not the first
            clientThread.sendMessage("secondClient");
            clientThread.terminateThread(); // Stops the thread from running in a clean way
        }
    }

    /**
     * Shuts down the server cleanly, closing all the client sockets and the server sockets
     *
     * @throws IOException          If an I/O error occurs when closing the socket
     * @throws InterruptedException If a thread did not join
     */
    public void cleanShutDown() throws IOException, InterruptedException {
        serverShutDown = true;
        mySocket.close(); // Closes the server socket
        Utility.print("Shutting server Down");
        for (ServerClientHandler clientThread : clientThreadVector) {
            if (clientThread.getClientType() == SenderType.client) {
                clientThread.sendMessage("Server is closing!");
            } // Notify the client server is closing
            clientThread.getClientSocket().close(); // Closes the client's socket, causing the threads to raise an error which is then dealt with
            clientThread.join();
            Utility.print("Client joined");
        }
    }
}
