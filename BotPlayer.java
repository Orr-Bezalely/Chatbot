import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents a type of player, a bot player.
 */
public class BotPlayer extends Player {
    /**
     * Integer representing the maximum number of moves the bot should make before looking
     */
    private final int maxMoveWithoutLook = 2;
    /**
     * CroppedMap representing the vision the bot has from its last call to "LOOK" function
     */
    private final CroppedMap croppedMap = new CroppedMap(new ArrayList<>(), new Coordinate(0, 0));
    /**
     * Integer representing the number of moves the bot has done without looking
     */
    private int moveWithoutLook = this.maxMoveWithoutLook;

    /**
     * The constructor initialises a bot player with the coordinate they are in and the array of commands that they accept
     *
     * @param playerCoords       Coordinate which represents the player's coordinate
     * @param validCommandsArray ArrayList of Commands which contains the commands this player accepts
     */
    public BotPlayer(Coordinate playerCoords, ArrayList<Command> validCommandsArray) {
        super(playerCoords, validCommandsArray);
    }

    /**
     * Takes in the bot's and human's coordinates and returns the manhattan distance between them.
     *
     * @param botPos   Coordinate which represents the bot's coordinate
     * @param humanPos Coordinate which represents the human's coordinate
     * @return integer representing the manhattan distance between the bot and player
     */
    private int manhattanDistance(Coordinate botPos, Coordinate humanPos) {
        return Math.abs(botPos.x - humanPos.x) + Math.abs(botPos.y - humanPos.y);
    }

    /**
     * Loops through each of the valid directions and checks through all the humans on the cropped map for the manhattan distance from them,
     * then returns the move which gets the bot closest to a human.
     *
     * @param validNeighbours HashMap with key String direction and value of the coordinates corresponding to the position after that move
     * @param humansPositions ArrayList of Coordinates which represents the humans' coordinates
     * @return String representing the direction the bot should move.
     */
    private String manhattanSearch(HashMap<String, Coordinate> validNeighbours, ArrayList<Coordinate> humansPositions) {
        String bestMove = "N";
        int bestMoveScore = this.lookRange;
        for (HashMap.Entry<String, Coordinate> pair : validNeighbours.entrySet()) { // Loops through each neighbour checking for nearest human
            int manhattanDist = this.lookRange;
            for (Coordinate humansPosition : humansPositions) { // Loop through each human and get the nearest human to the neighbour
                manhattanDist = Math.min(manhattanDistance(pair.getValue(), humansPosition), manhattanDist);
            }
            if (manhattanDist < bestMoveScore) { // Checks if distance to nearest human is shorter than what found so far
                bestMoveScore = manhattanDist;
                bestMove = pair.getKey();
            }
        }
        return bestMove;
    }

    /**
     * Picks a random move from the list of valid neighbours the bot has and returns it
     *
     * @param validNeighbours HashMap with key String direction and value of the coordinates corresponding to the position after that move
     * @return String representing the direction the bot should move.
     */
    private String randomMove(ArrayList<String> validNeighbours) {
        int randomNumber = DoDUtility.getRandNum(validNeighbours.size());
        return validNeighbours.get(randomNumber);
    }

    /**
     * Decides whether the bot should do a manhattan search or move randomly based on whether a human is visible or not,
     * and then return the move decided upon using the method decided
     *
     * @return String representing the direction the bot should move.
     */
    private String moveDirection() {
        HashMap<String, Coordinate> validNeighbours = this.croppedMap.getValidNeighbours(this.playerCoords);
        ArrayList<Coordinate> humansPositions = this.croppedMap.getHumansPositions();
        // returns move based on whether humans appear in cropped map or not
        return (humansPositions.size() == 0) ? randomMove(new ArrayList<>(validNeighbours.keySet())) : manhattanSearch(validNeighbours, humansPositions);
    }

    /**
     * Checks whether the bot should look on this turn, based on whether they are near an edge or that they didn't look for a while.
     *
     * @return boolean representing whether the bot should look on this turn
     */
    private boolean callLook() {
        return this.moveWithoutLook == this.maxMoveWithoutLook + 1 || this.croppedMap.onEdge(this.playerCoords);
    }

    /**
     * Implementation of the abstract method which is how a bot player does an action.
     * If they didn't look for a while then they look, otherwise they make a move.
     *
     * @return List of Strings containing the command name and any arguments the command has
     */
    @Override
    public List<String> nextAction() {
        List<String> returnVal = new ArrayList<>();
        this.moveWithoutLook++;
        if (callLook()) {
            this.moveWithoutLook = 0;
            returnVal.add("LOOK");
        } else {
            returnVal.add("MOVE");
            returnVal.add(moveDirection());
        }
        return returnVal;
    }

    /**
     * Implementation of the abstract method which is how a human player deals with the return of an action.
     *
     * @param commandName String containing the command name the bot made
     * @param returnVal   String containing the return value of the command the bot has called
     */
    @Override
    public void handleCommandCallback(String commandName, String returnVal) {
        if ("LOOK".equals(commandName)) { // updates the cropped map if the command was look
            this.croppedMap.setCroppedMap(Arrays.asList(returnVal.split("\n")));
            this.croppedMap.setCentreCoord(new Coordinate(this.getPlayerCoords().x, this.getPlayerCoords().y));
        }
    }

    /**
     * Implementation of the abstract method which is whether the player is human or not, returns false.
     *
     * @return boolean false
     */
    @Override
    public boolean isHuman() {
        return false;
    }

    /**
     * Implementation of the abstract method which is to get the player's character
     *
     * @return character 'B'
     */
    @Override
    public char getPlayerChar() {
        return 'B';
    }
}
