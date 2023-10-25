import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 * This class represents a type of client, a ChatBot.
 */
public class ChatBot extends Client {

    /**
     * Hashmap with a String representing the prompt as a key and a list of Strings representing responses for the given prompt.
     */
    private final HashMap<String, ArrayList<String>> botResponses = new HashMap<>();

    /**
     * Random which is used to generate random numbers to pick the bot response's randomly from the list of responses.
     */
    Random rand = new Random();


    /**
     * The constructor initialises userInput, socket (after validation), serverIn and serverOut.
     * It then sends the server a message informing it which type of client it is and initialises the bot responses.
     *
     * @param consoleAddress String representing the address entered into the console (default is "localhost" if none was entered)
     * @param consolePort    String representing the port number entered into the console (default is "14001" if none was entered)
     */
    public ChatBot(String consoleAddress, String consolePort) {
        super(consoleAddress, consolePort); // Sets up the connection with the server and initialises the required BufferedReaders and PrintWriter
        serverOut.println(clientType()); // Sends the server a message informing it which type of client it is
        initialiseBotResponses(); // Initialises the botResponses HashMap.
    }

    /**
     * The main method, creates an instance of ChatBot using console arguments as input for portString and address
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
        ChatBot myChatBot = new ChatBot(addressIP, portString); // Creates an instance of ChatClient
        myChatBot.go(); // Calls the go method
    }

    /**
     * Implementation of the abstract method which is how the ChatBot should run.
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
     * Keeps reading the data received from the server, and replying to the server with a random response
     * from a predetermined list of responses if a key word is contained in the data received.
     * This only stops if an IOException is thrown (if the server is forcibly closed) or if the serverMsg is null,
     * which occurs if the server closed peacefully.
     *
     * @throws IOException if the server is closed forcibly
     */
    private void mainLoop() throws IOException {
        String msg;
        while (true) {
            String serverMsg = serverIn.readLine(); // Reads the data from the server
            if (serverMsg == null) {
                break;
            } // Breaks if serverMsg is null as that implies the server has been shut down
            /*
            This link explains how to remove punctuation from the string.
            https://www.studytonight.com/java-examples/how-to-remove-punctuation-from-string-in-java
             */
            msg = " " + serverMsg.replaceAll("\\p{Punct}", " ") + " ";
            botResponse(msg); // Deals with responding to the message received
        }
    }

    /**
     * Searches for key words in the serverMsg. If one is found, then a random response (from a predetermined list of responses)
     * is sent back to the server.
     *
     * @param serverMsg String containing the data received from the server
     */
    private void botResponse(String serverMsg) {
        // Loops through each key in the botResponses HashMap checking whether it is contained in the serverMsg
        for (String response : botResponses.keySet()) {
            if (serverMsg.toLowerCase().contains(" " + response + " ")) {
                serverOut.println(randomReply(botResponses.get(response))); // Sends the server the random response from predetermined list of responses
                return; // Breaks out of the for loop in order to avoid responding to multiple key words
            }
        }
    }

    /**
     * Implementation of the abstract method which returns the client type.
     *
     * @return senderType senderType.chatBot which represents the type of the client is ChatBot
     */
    @Override
    public SenderType clientType() {
        return SenderType.chatBot;
    }

    /**
     * Initialises the botResponses HashMap
     */
    public void initialiseBotResponses() {
        ArrayList<String> greetings = new ArrayList<>(Arrays.asList( // Greeting responses list
                "Hello there fellow client I am a bot",
                "Howdy, how are you doing?",
                "Good morning, good afternoon and good night!",
                "Hello world"
        ));

        /*
        I do not claim any ownership for these jokes. They were found on the internet on this website:
        https://www.countryliving.com/life/a27452412/best-dad-jokes/
         */
        ArrayList<String> jokes = new ArrayList<>(Arrays.asList( // Joke responses list
                "How do you make an octopus laugh? With ten-tickles.",
                "Five guys walk into a bar. You think one of them would've seen it.",
                "How do you make holy water? You boil the hell out of it.",
                "When does a joke become a 'dad joke'? When it becomes apparent.",
                "For years I was searching for a book with all kind of maps. Atlas, I found it.",
                "I am going to tell you a TCP joke. And I am going to keep telling it until you get it.",
                "I've got a great joke about construction, but I'm still working on it."
        ));

        ArrayList<String> mood = new ArrayList<>(Arrays.asList( // Mood responses list
                "I am good, Thank you for asking, How are you?",
                "I've had better days...",
                "I'm hanging in there.",
                "Medium well."
        ));

        ArrayList<String> quotes = new ArrayList<>(Arrays.asList( // Quotes responses list
                "Whether you think you can or you think you can’t, you’re right. - Henry Ford",
                "When everything seems to be going against you, remember that the airplane takes off against the wind, not with it. - Henry Ford",
                "A person who never made a mistake never tried anything new. - Albert Einstein",
                "Be yourself; everyone else is already taken. - Oscar Wilde",
                "Turn your wounds into wisdom. – Oprah Winfrey",
                "You never fail until you stop trying. – Albert Einstein",
                "Yesterday you said tomorrow. Just do it. – Nike",
                "You miss 100% of the shots you don’t take. – Wayne Gretzky",
                "Life is 10% what happens to me and 90% of how I react to it. – Charles Swindoll",
                "The best time to plant a tree was 20 years ago. The second best time is now. – Chinese Proverb",
                "You can never cross the ocean until you have the courage to lose sight of the shore. – Christopher Columbus"
        ));

        // Puts the key word with the list of responses into the botResponses HashMap
        botResponses.put("hi", greetings);
        botResponses.put("how are you", mood);
        botResponses.put("joke", jokes);
        botResponses.put("quote", quotes);
    }

    /**
     * Picks a random String from a given list of Strings
     *
     * @param list Arraylist of Strings representing the list of replies the bot should randomly choose from
     * @return String being the chosen reply the bot should send the server
     */
    public String randomReply(ArrayList<String> list) {
        return list.get(rand.nextInt(list.size()));
    }
}