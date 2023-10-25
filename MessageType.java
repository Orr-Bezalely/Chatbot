/**
 * Represents the type of message that should be sent
 */
public enum MessageType {
    /**
     * If a human client (of class ChatClient) sends a broadcast
     */
    broadcastClient,

    /**
     * If a bot client (of class ChatBot) sends a broadcast
     */
    broadcastBot,

    /**
     * If a new DoD game should be started
     */
    DoDNewGame,

    /**
     * If a client sends a message while they are in a DoD game
     */
    DoDMidGame,

    /**
     * If DoD sends a message to the client with the output from the game
     */
    DoDToClient,

    /**
     * If DoD sends a message to the client with whether the game is over
     */
    DoDToClientMetaData
}
