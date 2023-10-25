import java.util.*;
import java.util.function.Function;

/**
 * This class is the orchestrator and it interacts with all the other classes and combines them into a playable game.
 */
public final class GameLogic {


    /**
     * Represents the map of the game
     */
    private final Map map;

    /**
     * represents a Mapping between all the command names and the corresponding commands created
     */
    private final HashMap<String, Command> validCommands = new HashMap<>();

    /**
     * Arraylist that stores all players so we can loop over them
     */
    private final ArrayList<Player> playerArray = new ArrayList<>();

    /**
     * List of strings that contain all the valid human commands
     */
    private final List<String> humanCommands = Arrays.asList("HELLO", "GOLD", "MOVE", "LOOK", "PICKUP", "QUIT");

    /**
     * List of strings that contain all the valid bot commands
     */
    private final List<String> botCommands = Arrays.asList("MOVE", "LOOK");
    /**
     * Stores the number of humans in the game
     */
    private final int numberOfHumans;
    /**
     * Stores the number of bots in the game
     */
    private final int numberOfBots;
    /**
     * Boolean representing whether the game is running or not.
     */
    private boolean gameRunning = true;
    /**
     * Integer representing which index in the ArrayList of playerArray is the current player in (the player who's turn it is)
     */
    private int currentPlayerIndex = 0;
    /**
     * This is initialised in game rather than in human as my game allows for multiple humans / bots to play,
     * so humans collect gold as a team
     */
    private int gold = 0;


    /**
     * The constructor for GameLogic, it loads in the map and creates the commands and players
     *
     * @param numberOfHumans integer representing how many human players in the game
     * @param numberOfBots   integer representing how many bot players in the game
     */
    public GameLogic(int numberOfHumans, int numberOfBots) {
        validateHumanAndBotNum(numberOfHumans, numberOfBots);
        this.map = new Map(numberOfHumans + numberOfBots);
        this.numberOfHumans = numberOfHumans;
        this.numberOfBots = numberOfBots;
    }


    /**
     * Initialises the game
     */
    public void init() {
        this.loadMap();
        createCommands();
        createPlayers();
    }


    /**
     * Gets the player given the player index
     *
     * @param index player index in PlayerArray
     * @return object of type Player representing a player.
     */
    private Player getPlayer(int index) {
        return playerArray.get(index);
    }


    /**
     * This is one of the functions that a human player can call.
     * Receives a list of arguments (which is length 0) and returns how much gold is required to leave with win.
     *
     * @param args an empty list of Strings.
     * @return String saying how much gold is required to leave with win.
     */
    private String hello(List<String> args) {
        return "Gold to win: " + this.map.getGoldRequired();
    }


    /**
     * This is one of the functions that a human player can call.
     * Receives a list of arguments (which is length 0) and returns how much gold the players have collaboratively.
     *
     * @param args an empty list of Strings.
     * @return String saying how much gold the players have collaboratively.
     */
    private String gold(List<String> args) {
        return "Gold owned: " + this.gold;
    }


    /**
     * Receives a list of arguments (of length 1) and tries to move the player in the corresponding direction they asked
     *
     * @param args List of strings containing the direction the user wants to move in
     * @return String which is "Success" or "Fail" based on whether the move was successful or not.
     */
    private String move(List<String> args) {
        String direction = args.get(1);
        Player curPlayer = getPlayer(this.currentPlayerIndex);
        Coordinate playerCoords = new Coordinate(curPlayer.getPlayerCoords().x, curPlayer.getPlayerCoords().y);
        Coordinate newPos = playerCoords.addCoord(DoDUtility.TurnToVector.get(direction));
        if (this.map.vectorInMap(newPos) && this.map.getTile(newPos) != '#') { // Checks if player can make the move
            curPlayer.setPlayerCoords(newPos);
            return "Success";
        }
        return "Fail";
    }


    /**
     * Returns the tile in position (x,y) that should be displayed to the bot/player.
     *
     * @param vector Coordinate representing the position of the tile to get
     * @return character representing the character that should be displayed to the bot/player in position (x,y)
     */
    private char coordTile(Coordinate vector) {
        if (this.map.vectorInMap(vector)) {
            for (Player player : playerArray) { // Loops through each player, checking whether they are on that tile
                if (Coordinate.equalVectors(player.getPlayerCoords(), vector))
                    return player.getPlayerChar(); // Checks if player is on that tile
            }
            return this.map.getTile(vector);
        }
        return '#';
    }


    /**
     * Builds a square of size lookRange around the player (i.e. player being centralised) and returning it
     *
     * @param args an empty list of Strings.
     * @return String representing the cropped map around the current player
     */
    private String look(List<String> args) {
        Player curPlayer = getPlayer(this.currentPlayerIndex);
        int radius = Math.floorDiv(curPlayer.lookRange, 2);
        StringBuilder croppedMap = new StringBuilder();
        for (int y = curPlayer.getPlayerCoords().y - radius; y <= curPlayer.getPlayerCoords().y + radius; y++) {
            for (int x = curPlayer.getPlayerCoords().x - radius; x <= curPlayer.getPlayerCoords().x + radius; x++) {
                croppedMap.append(coordTile(new Coordinate(x, y)));
            }
            croppedMap.append("\n");
        }
        return croppedMap.toString();
    }


    /**
     * Tries to pick up gold by checking if current player is on a gold tile. returns an appropriate message.
     *
     * @param args an empty list of Strings.
     * @return String describing whether action was successful or not and the current gold owned.
     */
    private String pickup(List<String> args) {
        Player curPlayer = getPlayer(this.currentPlayerIndex);
        if (this.map.checkTile(curPlayer.getPlayerCoords(), 'G')) { // Checks if player is on gold
            this.gold++;
            this.map.changeTile(curPlayer.getPlayerCoords(), '.'); // Changes gold tile to normal traversable tile
            return "Success. Gold owned " + this.gold;
        }
        return "Fail. Gold owned " + this.gold;
    }


    /**
     * Ends the game, and displays whether the humans or bots won (from human prospective).
     *
     * @param args an empty list of Strings.
     * @return String describing whether the humans won or lost.
     */
    private String quitGame(List<String> args) {
        Player curPlayer = getPlayer(this.currentPlayerIndex);
        this.gameRunning = false; // Stops the game from running
        if (this.gold >= map.getGoldRequired() && this.map.checkTile(curPlayer.getPlayerCoords(), 'E')) //Checks if win condition is met
            return "WIN, Congratulations! You have won the game!";
        return "LOSE";
    }


    /**
     * Initialises a command object of type Command and stores it in the validCommands HashMap with the key commandName.
     *
     * @param commandName     String representing the command user should enter to call this command
     * @param validArgs       Set representing the valid arguments the command receives.
     * @param minArgNum       Minimum number of arguments the command is allowed to receive.
     * @param maxArgNum       Maximum number of arguments the command is allowed to receive.
     * @param functionPointer A method reference representing which function should be called.
     */
    private void commandInitialiser(String commandName, Set<String> validArgs, int minArgNum, int maxArgNum, Function<List<String>, String> functionPointer) {
        Command newCommand = new Command(commandName, validArgs, minArgNum, maxArgNum, functionPointer); // Initialises a command
        validCommands.put(commandName, newCommand); // Stores the command name with the command object in the validCommands HashMap
    }


    /**
     * Creates all the commands by calling commandInitialiser for each command to the command with the corresponding arguments.
     */
    private void createCommands() {
        this.commandInitialiser("HELLO", new HashSet<>(), 0, 0, this::hello);
        this.commandInitialiser("GOLD", new HashSet<>(), 0, 0, this::gold);
        this.commandInitialiser("MOVE", DoDUtility.TurnToVector.keySet(), 1, 1, this::move);
        this.commandInitialiser("PICKUP", new HashSet<>(), 0, 0, this::pickup);
        this.commandInitialiser("LOOK", new HashSet<>(), 0, 0, this::look);
        this.commandInitialiser("QUIT", new HashSet<>(), 0, 0, this::quitGame);
    }


    /**
     * Creates a player by assigning it random coordinates and a commands array that is accepts.
     *
     * @param human              boolean representing whether the player is human or not
     * @param validCommandsArray Arraylist of commands storing the valid commands the player can use
     */
    private void createPlayer(boolean human, ArrayList<Command> validCommandsArray) {
        boolean valid = false;
        Coordinate position = null;
        while (!valid) {
            int y = DoDUtility.getRandNum(map.getYDim());
            int x = DoDUtility.getRandNum(map.getXDim(y));
            position = new Coordinate(x, y);
            char tile = this.coordTile(position);
            if (".GE".indexOf(tile) != -1) { // If the tile is one of ".", "G", "E" (i.e. valid tile)
                if (!human || tile != 'G')
                    valid = true;// Humans are not allowed to be places on gold tiles, but bots are
            }
        }
        Player player = human ? new HumanPlayer(position, validCommandsArray) : new BotPlayer(position, validCommandsArray);
        playerArray.add(player);
    }


    /**
     * This function creates humans and bots depending on how many players are requested
     */
    private void createPlayers() {
        ArrayList<Command> validHumanCommands = new ArrayList<>();
        ArrayList<Command> validBotCommands = new ArrayList<>();
        // Loops through each Command, assigning it to human and/or bot if it is in their prescribed commands
        for (String commandName : validCommands.keySet()) {
            if (humanCommands.contains(commandName)) {
                validHumanCommands.add(validCommands.get(commandName));
            }
            if (botCommands.contains(commandName)) {
                validBotCommands.add(validCommands.get(commandName));
            }
        }
        // Creates numberOfHumans human players
        for (int human = 0; human < this.numberOfHumans; human++) {
            this.createPlayer(true, validHumanCommands);
        }
        // Creates numberOfBots bot players
        for (int bot = 0; bot < this.numberOfBots; bot++) {
            this.createPlayer(false, validBotCommands);
        }
    }


    /**
     * Executes a turn for the current player, returning a message
     * (corresponding with whether move was executed correctly) to its caller.
     *
     */
    /**
     * Executes a turn for the current player, returning a message
     * (corresponding with whether move was executed correctly) to its caller.
     * @return String representing the current player's DoD response
     */
    private String update() {
        Player curPlayer = getPlayer(this.currentPlayerIndex);
        List<String> commandAndArgs = curPlayer.nextAction(); // Receive the player input (null if not valid)
        if (!(commandAndArgs == null)) {
            String commandName = commandAndArgs.get(0);
            // Calls command corresponding with command name with the arguments given by the user
            String returnString = this.validCommands.get(commandName).runMethod(commandAndArgs);
            if (!curPlayer.isHuman()) {
                curPlayer.handleCommandCallback(commandName, returnString);
            }
            return returnString;
        }
        return "INPUT NOT IN CORRECT FORMAT";
    }


    /**
     * Loads in the default map.
     */
    private void loadMap() {
        this.map.DefaultMap(); // Loads in the map
    }


    /**
     * Checks to see whether there exists a bot that is on the same tile as a human player, if so, then the bots win.
     *
     * @return boolean representing whether the bots won or not
     */
    private boolean botsWin() {
        for (int i = 0; i < this.playerArray.size(); i++) {
            for (int j = i + 1; j < this.playerArray.size(); j++) {
                Player player1 = getPlayer(i);
                Player player2 = getPlayer(j);
                // Checks whether the two players are on the same tile and if one of them is a bot and the other is a human.
                if (Coordinate.equalVectors(player1.getPlayerCoords(), player2.getPlayerCoords()) && (player1.isHuman() ^ player2.isHuman()))
                    return true;
            }
        }
        return false;
    }


    /**
     * Loops the game until either the bots won or its a players turn.
     *
     * @param userInput String representing the user's input
     * @return String representing what should be displayed to the user
     */
    public String loopTurn(String userInput) {
        if (this.gameRunning) {
            // Human player does their turn
            ((HumanPlayer) this.playerArray.get(this.currentPlayerIndex)).setNextAction(userInput);
            String returnToServer = this.update();
            this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.playerArray.size(); // moves turn to next player
            if (botsWin()) { //Checks whether the bots won
                this.gameRunning = false;
                return returnToServer + "\nBOTS WON!";
            }
            // Bot players do their turn
            while (!(getPlayer(this.currentPlayerIndex).isHuman())) {
                String ignoreBotOutput = this.update(); // Does a turn for one player
                this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.playerArray.size(); // moves turn to next player
                if (botsWin()) { //Checks whether the bots won
                    this.gameRunning = false;
                    return returnToServer + "\nBOTS WON!";
                }
            }
            return returnToServer;
        }
        return null;
    }


    /**
     * Checks if the number of humans and number of bots are valid
     *
     * @param numberOfHumans integer representing how many human players in the game
     * @param numberOfBots   integer representing how many bot players in the game
     */
    private void validateHumanAndBotNum(int numberOfHumans, int numberOfBots) {
        if (numberOfHumans < 1 || numberOfBots < 0) {
            this.gameRunning = false;
        }
    }

    /**
     * Getter for the gameRunning boolean variable
     *
     * @return boolean representing whether the game is running or not.
     */
    public boolean getGameRunning() {
        return gameRunning;
    }

}
