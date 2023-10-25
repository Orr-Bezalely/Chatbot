Welcome to the Dungeon of Doom ReadMe file:



*OVERVIEW*

An explorer has stumbled upon a dungeon full of wondrous treasures.
The explorer (represented by a human player) enters the dungeon and attempts to leave it with a certain threshold of treasures (represented by gold coins).
In the dungeon lies a villain (represented by a bot player), who attempts to capture the intruding explorer.
Can you escape the dungeon alive?



*MAP REPRESENTATION*

The map consists of 6 types of tiles:
"#" - This is the default non-traversable tile (i.e. wall tile)
"." - This is the default traversable tile (i.e. floor tile)
"E" - This is a traversable tile which represents an exit
"G" - This is a traversable tile currently containing gold coin
"P" - This is a traversable tile currently containing a human player
"B" - This is a traversable tile currently containing a bot player



*MAP SETUP*

At the beginning of the game, the default map is loaded.
The chosen file must contain a map name, the threshold of gold coins required to win, and a representation of the map.
All the above need to be in a specific format (that can be seen in default_map.txt).



*COMMANDS*

The game is a turn-based game between the human players and the bot players. 
Each turn, a human player can enter one of the following commands:
HELLO - This command has no arguments and is used when a human player wants to know the threshold of gold coins required to win.
	The output is of the following format "Gold to win: <gold>" where <gold> is the threshold.
GOLD - This command has no arguments and is used when a human player wants to know the current number of gold coins possessed by the human players.
	The output is of the following format "Gold owned: <number>" where <number> is the number of gold coins currently possessed by human players.
MOVE <direction> - This command takes in an argument (being direction, which is one of the following: "N", "E", "S", "W") and is used when a player wishes to move.
	It outputs whether the move was successful or not. A successful move is a move such that the player ends up in a traversable tile.
	The output is of the following format "Success" if the move was successful and "Fail" if the move was unsuccessful.
PICKUP - This command has no arguments and is used when a human player wants to pick up a gold coin from the tile they are on. 
	The output is of the following format "Success. Gold owned: <number>" if successful and "Fail. Gold owned: <number>" if unsuccessful, 
	where <number> is the number of gold coins currently possessed by human players.
LOOK - This command has no arguments and is used when a player wishes to see their surroundings on the map.
	The output is a small capture of the map (5x5 tiles centred on the player). If a visible tile is outside the map, then it is shown to the player as a non-traversable tile.
QUIT - This command has no arguments and is used when a player wishes to quit the game.
	The output is of the following format "WIN, Congratulations! You have won the game" if the player met the threshold of gold coins and is on an exit tile, and "LOSE" otherwise.

Note that each input (whether it is a valid or invalid input) takes up a player's turn.



*ENDING THE GAME*

Win condition (for human players):
The human players win when they collectively possess equal to or above the threshold of gold coins required AND a human player calls the QUIT command when standing on an exit tile.

Lose condition (for human players):
A human player calls the QUIT command without the win condition being satisfied OR if a bot moves to a tile that is occupied by a human player and vice versa.



*BOT IMPLEMENTATION*

The bot is implemented in the following way:
- Start by calling the LOOK command to get a visual map of their surroundings
- If the bot has reached the edge of their visual map or has moved 2 turns without looking then it calls the LOOK command again
- Otherwise the bot calls the MOVE command with the following argument:
	1. If the visual map contains at least one player, then the bot tries to move as close as it can to the closest human player.
	2. If the visual map does not contain a player, then the bot moves in a random valid direction.



*EXTENDED VERSION*
As you might have noticed, throughout this ReadMe I referred to multiple human and bot players as I extended the game to accommodate for more than one human player and more than one bot per game.
If you wish to attempt that, then all you need to do is to go to the main function and change the arguments sent to the GameLogic accordingly.