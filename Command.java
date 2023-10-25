import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * A class in which each object represents a command.
 */
public class Command {


    /**
     * String representing the command user should enter to call this command
     */
    private final String commandName;

    /**
     * Set representing the valid arguments the command receives
     */
    private final Set<String> validArgs;

    /**
     * Minimum number of arguments the command is allowed to receive
     */
    private final int minArgNum;

    /**
     * Maximum number of arguments the command is allowed to receive.
     */
    private final int maxArgNum;

    /**
     * A method reference representing which function should be called
     */
    private final Function<List<String>, String> functionPointer; // A pointer to the function in GameLogic that needs to be called when the command is called


    /**
     * The constructor of the Command class
     *
     * @param commandName     String representing the command user should enter to call this command
     * @param validArgs       Set representing the valid arguments the command receives.
     * @param minArgNum       Minimum number of arguments the command is allowed to receive.
     * @param maxArgNum       Maximum number of arguments the command is allowed to receive.
     * @param functionPointer A method reference representing which function should be called.
     */
    public Command(String commandName, Set<String> validArgs, int minArgNum, int maxArgNum, Function<List<String>, String> functionPointer) {
        this.commandName = commandName;
        this.validArgs = validArgs;
        this.minArgNum = minArgNum;
        this.maxArgNum = maxArgNum;
        this.functionPointer = functionPointer;
    }


    /**
     * Runs the method using the arguments passed
     *
     * @param args List of Strings representing the arguments that should be passed into the command
     * @return String which is returned from the command in GameLogic
     */
    public String runMethod(List<String> args) {
        return functionPointer.apply(args);
    }


    /**
     * Gets the input and checks whether the input is in the correct format for this command (i.e. correct command and arguments)
     *
     * @param input String representing the player's input
     * @return List of Strings representing the command and its arguments
     */
    public List<String> correctCommandAndArgs(String input) {
        String[] listOfArgs = input.split(" ");
        // Checks if the command name is not the same as the one inputted or the number of args is not between the minimum and maximum
        if ((!this.commandName.equals(listOfArgs[0])) || listOfArgs.length - 1 < this.minArgNum || listOfArgs.length - 1 > this.maxArgNum) {
            return null;
        }
        for (int arg = 1; arg < listOfArgs.length; arg++) {
            if (!validArgs.contains(listOfArgs[arg])) return null; // Checks if a given argument is invalid
        }
        return Arrays.asList(listOfArgs); // Returns the list of arguments
    }
}


