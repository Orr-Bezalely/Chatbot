/**
 * This class represents a type of client, a human ChatClient.
 */
public class ChatClient extends Client {

    /**
     * The constructor initialises userInput, socket (after validation), serverIn and serverOut.
     * It then sends the server a message informing it which type of client it is.
     *
     * @param consoleAddress String representing the address entered into the console (default is "localhost" if none was entered)
     * @param consolePort    String representing the port number entered into the console (default is "14001" if none was entered)
     */
    public ChatClient(String consoleAddress, String consolePort) {
        super(consoleAddress, consolePort); // Sets up the connection with the server and initialises the required BufferedReaders and PrintWriter
        serverOut.println(clientType()); // Sends the server a message informing it which type of client it is
    }

    /**
     * The main method, creates an instance of ChatClient using console arguments as input for portString and address
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
        Utility.print("address: " + addressIP + " at port " + portString);
        ChatClient myChatClient = new ChatClient(addressIP, portString); // Creates an instance of ChatClient
        myChatClient.go(); // Calls the go method
    }

    /**
     * Implementation of the abstract method which is how the ChatClient should run.
     * It sets up two threads, one to read the data from the server and one to write the data to the server
     * (This is so both reading and writing can occur concurrently using multi-threading),
     * and once their main loops are broken from, the BufferedReaders are closed and then the socket is closed.
     */
    @Override
    public void go() {
        ClientRead read = new ClientRead(serverIn); // Sets up a read thread which deals with reading data from the server
        ClientWrite write = new ClientWrite(socket, serverOut); // Sets up a write thread which deals with sending data to the server
        read.start();
        write.start();
        try {
            read.join(); // Waits for the read thread to complete its execution which ensures after this point the read thread is closed.
            cleanShutDown(); // Closes the PrintWriter, BufferedReaders and socket
            write.join(); // Waits for the write thread to complete its execution which ensures after this point the write thread is closed.
        } catch (InterruptedException e) {
            Utility.print("Failed to join read / write threads.");
            cleanShutDown();
        }
    }

    /**
     * Implementation of the abstract method which returns the client type.
     *
     * @return senderType senderType.client which represents the type of the client is ChatClient
     */
    @Override
    protected SenderType clientType() {
        return SenderType.client; // Represents the type of the client is ChatClient
    }
}

