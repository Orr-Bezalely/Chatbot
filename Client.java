import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class contains the connection process of a client to the server, required variables to read/write to the server
 * and their initialisation/closures methods.
 * It is abstract as ChatClients, ChatBots and DoDClients are all types of clients and so they inherit from Client.
 */
public abstract class Client {


    /**
     * Socket representing the client side endpoint for communication between the client and server.
     */
    protected Socket socket;

    /**
     * BufferedReader which reads the text from the server.
     */
    protected BufferedReader serverIn;

    /**
     * PrintWriter which writes text to the server.
     */
    protected PrintWriter serverOut;


    /**
     * The constructor initialises userInput, socket (after validation), serverIn and serverOut.
     *
     * @param consoleAddress String representing the address entered into the console (default is "localhost" if none was entered)
     * @param consolePort    String representing the port number entered into the console (default is "14001" if none was entered)
     */
    public Client(String consoleAddress, String consolePort) {
        initialiseSocketConnection(consoleAddress, consolePort); // Initialises the connection to the server in a while loop
        initialiseReadWrite(); // Initialises the serverIn BufferedReader and the serverOut PrintWriter
        Utility.print("Connected to server.");
    }

    /**
     * Initialises the connection to the server in a while loop, so by the end of this method, a connection is established.
     *
     * @param consoleAddress String representing the address entered into the console (default is "localhost" if none was entered)
     * @param consolePort    String representing the port number entered into the console (default is "14001" if none was entered)
     */
    protected void initialiseSocketConnection(String consoleAddress, String consolePort) {
        String address = consoleAddress;
        String portString = consolePort;
        // Loops until a connection is made with the server
        while (true) {
            try {
                connectToServer(address, portString); // Attempts connecting to the server given the address and portString supplied
                break;
            } catch (NumberFormatException numberFormatException) { // This is reached if the port number is not an integer between 1024 and 65535
                Utility.print("Port supplied is not a valid number. Please input the port number between 1024-65535.");
                portString = Utility.userInput();
            } catch (UnknownHostException unknownHostException) { // This is reached if the address supplied does not contain a host
                Utility.print("Address supplied is not valid. Please input the address correctly.");
                address = Utility.userInput();
            } catch (IOException e) { // This is reached if the port supplied does not contain a server
                Utility.print("Port supplied does not contain a server. Please input the correct port!");
                portString = Utility.userInput();
            }
        }
    }

    /**
     * Attempts connecting to the server given the address and portString supplied.
     *
     * @param address    String representing the address supplied
     * @param portString String representing the port supplied
     * @throws IOException           If address supplied does not contain a host or if port supplied does not contain a server
     * @throws NumberFormatException If port supplied is not an integer between 1024 and 65535 inclusive
     */
    private void connectToServer(String address, String portString) throws IOException, NumberFormatException {
        int port = Integer.parseInt(portString);
        if (!(1024 <= port && port <= 65535)) {
            throw new NumberFormatException();
        }
        this.socket = new Socket(address, port);
    }

    /**
     * Initialises the serverIn BufferedReader and the serverOut PrintWriter
     */
    private void initialiseReadWrite() {
        try {
            this.serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Set up the ability to read the data from the server
            this.serverOut = new PrintWriter(socket.getOutputStream(), true); // Set up the ability to send the data to the server
        } catch (IOException e) {
            Utility.print("Failed to initialise read write");
        }
    }

    /**
     * Abstract method which will be called once the client is initialised
     */
    public abstract void go();


    /**
     * Abstract method which will return the type of the client
     *
     * @return senderType which represents the type of the client
     */
    protected abstract SenderType clientType();

    /**
     * Closes the serverIn BufferedReader
     */
    protected void closeReader() {
        try {
            serverIn.close();
        } catch (IOException e) {
            Utility.print("Failed to close buffered reader");
        }
    }

    /**
     * Closes the userInput BufferedReader
     */
    protected void closeUserInput() {
        try {
            Utility.userInput.close();
        } catch (IOException e) {
            Utility.print("Failed to close user input");
        }
    }

    /**
     * Closes the socket Socket
     */
    protected void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            Utility.print("Failed to close socket");
        }
    }

    /**
     * Closes the serverOut PrintWriter
     */
    private void closeWriter() {
        serverOut.close();
    }

    protected void cleanShutDown() {
        closeReader(); // Closes the serverIn BufferedReader.
        Utility.print("Reader closed");
        closeWriter(); // Closes the serverOut PrintWriter.
        Utility.print("Writer closed");
        closeSocket(); // Closes the socket once the BufferedReaders are closed.
        Utility.print("Socket closed");
        closeUserInput(); // Closes the userInput BufferedReader.
        Utility.print("User input closed");
    }

}
