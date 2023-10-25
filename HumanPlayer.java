import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a type of player, a human player.
 */
public class HumanPlayer extends Player {

    String nextAction;

    /**
     * The constructor initialises a human player with the coordinate they are in and the array of commands that they accept
     *
     * @param playerCoords       Coordinate which represents the player's coordinate
     * @param validCommandsArray ArrayList of Commands which contains the commands this player accepts
     */
    public HumanPlayer(Coordinate playerCoords, ArrayList<Command> validCommandsArray) {
        super(playerCoords, validCommandsArray);
    }


    /**
     * Implementation of the abstract method which is how a human player does an action.
     * It asks for a user input from the console and loops through each of the commands the player has and checks whether
     * it has the correct command name and arguments.
     *
     * @return List of strings which contains the command name and its arguments that the player made.
     */
    @Override
    public List<String> nextAction() {
        for (Command command : validCommandsArray) { // Loops through each command and checks if the user input is that command with correct arguments
            List<String> commandAndArgs = command.correctCommandAndArgs(nextAction);
            if (commandAndArgs != null) return commandAndArgs;
        }
        return null;
    }

    public void setNextAction(String nextAction) {
        this.nextAction = nextAction;
    }


    /**
     * Implementation of the abstract method which is how a human player deals with the return of an action.
     * The human just prints the returnVal to the console for the user to see.
     *
     * @param commandName String containing the command name the player made
     * @param returnVal   String containing the return value of the command the player has called
     */
    @Override
    public void handleCommandCallback(String commandName, String returnVal) {
        System.out.println(returnVal);
    }


    /**
     * Implementation of the abstract method which is whether the player is human or not, returns true.
     *
     * @return boolean true
     */
    @Override
    public boolean isHuman() {
        return true;
    }


    /**
     * Implementation of the abstract method which is to get the player's character
     *
     * @return character 'P'
     */
    @Override
    public char getPlayerChar() {
        return 'P';
    }
}