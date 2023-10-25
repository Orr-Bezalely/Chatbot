import java.util.ArrayList;
import java.util.List;

/**
 * This class contains information about a player.
 * It is abstract as both bots and humans are types of players and so they inherit from player
 */
public abstract class Player {


    /**
     * Integer representing the size of the cropped map that each player should get
     */

    public int lookRange = 5;
    /**
     * Coordinate which represents the player's position on the map
     */

    protected Coordinate playerCoords;
    /**
     * ArrayList of commands which stores the valid commands the player accepts
     */

    protected ArrayList<Command> validCommandsArray; // Commands the player accepts


    /**
     * The constructor initialises a player with the coordinate they are in and the array of commands that they accept
     *
     * @param playerCoords       Coordinate which represents the player's coordinate
     * @param validCommandsArray ArrayList of Commands which contains the commands this player accepts
     */
    public Player(Coordinate playerCoords, ArrayList<Command> validCommandsArray) {
        this.playerCoords = playerCoords;
        this.validCommandsArray = validCommandsArray;
    }


    /**
     * Abstract method which will be the action a player should take at their turn.
     *
     * @return List of strings which contains the command name and its arguments that the player made.
     */
    public abstract List<String> nextAction();


    /**
     * Abstract method which will be the way each player deals with the return value that their action called
     *
     * @param commandName String containing the command name the player made
     * @param returnVal   String containing the return value of the command the player has called
     */
    public abstract void handleCommandCallback(String commandName, String returnVal);


    /**
     * Abstract method which will represent whether each player is human or not
     *
     * @return boolean representing whether the player is human or not
     */
    public abstract boolean isHuman();


    /**
     * Abstract method which will return the player's character
     *
     * @return character representing the player's character.
     */
    public abstract char getPlayerChar();


    /**
     * Getter which returns the player's current coordinates
     *
     * @return Coordinate representing the player's coordinates on the map
     */
    public Coordinate getPlayerCoords() {
        return playerCoords;
    }


    /**
     * Setter which sets the player's current coordinates
     *
     * @param playerCoords Coordinate representing the coordinates which need to be stored as the new player's coordinates
     */
    public void setPlayerCoords(Coordinate playerCoords) {
        this.playerCoords = playerCoords;
    }
}
