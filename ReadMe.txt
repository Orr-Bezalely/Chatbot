Welcome to the Simple Chatting System ReadMe file:

*OVERVIEW*
This is a simple chatting system where clients can speak to each other from remote places through
connection to a common server. Additional features include ChatBots connecting to the server and replying to some of
the client's messages and ability to play a Dungeon of Doom (DoD) game with the DoDClient.


*COMPILING FILES*
Navigate to the correct directory (ob461-CW1) using "cd [directory]" and then type "javac *.java".
This will compile all the java files.


*SERVER SETUP*
To run the server, you should start by navigating to the correct directory (ob461-CW1) and then type "java ChatServer".
This command accepts arguments (separated by a space) which are sent to the ChatServer program.
If "-csp" is entered then the following argument will be taken as the port number.
If multiple "-csp" arguments are entered, then the last of them is the decisive one.
A valid port number must be an integer between 1024-65535 inclusive, so any invalid entry is queried until a valid entry is given.
If no "-csp" argument is supplied then the default port is 14001.


*CLIENT SETUP OF ANY CLIENT TYPE*
To run the client, you should start by navigating to the correct directory (ob461-CW1) and then type "java [filename]"
where [filename] should be replaced with the correct file.
1. To run a normal human client, the [filename] should be replaced with ChatClient.
2. To run a chat bot client, the [filename] should be replaced with ChatBot.
3. To run a DoD client, the [filename] should be replaced with DoDClient.
This command accepts arguments (separated by a space) which are sent to the program.
If "-cca" is entered then the following argument will be taken as the IP address.
If "-ccp" is entered then the following argument will be taken as the port number.
If multiple "-cca" / "-ccp" arguments are entered, then the last of them is the decisive one.
A valid address must be of the form of an IP address (or localhost), so any invalid entry is queried until a valid entry is given.
A valid port number must be an integer between 1024-65535 inclusive, so any invalid entry is queried until a valid entry is given.
These prompts are queried until a connection to a server is established.
If no "-cca" argument is supplied then the default IP address is localhost.
If no "-ccp" argument is supplied then the default port is 14001.


*MULTIPLE CLIENTS*
Multiple human clients (ChatClient) may be connected simultaneously to the server
Multiple chat bots (ChatBot) may be connected simultaneously to the server
Only up to one DoD Client (DoDClient) can be connected to the server at any given time.
If a DoDClient attempts to connect to the server with the server already having a DoDClient, the server will disconnect
the new DoDClient, keeping the old one.


*BROADCAST MODE*
Once connected to the server, a client is put by default onto broadcast mode.
This means that any message the client sends will be displayed to everyone else which is in broadcast mode
(excluding the DoDClient).


*CHAT BOT CONVERSATION*
The ChatBot is programmed to reply to messages which contain prompts by picking a random response from a list of
pre-scripted responses for the given prompt.
The following are prompts:
"hi" - The bot will reply with a greeting
"how are you" - The bot will reply with the bot's mood
"joke" - The bot will reply with a cringe joke
"quote" - The bot will reply with an inspirational quote


*STARTING A DOD GAME*
Once a DoD client is connected to the server, if a client types in "join", then they are moved into DoD mode,
and a new game is started.


*PLAYING A DOD GAME*
Check the DOD readme file. Map Choice got removed, and the default map is always initialised.


*FINISHING DOD GAME*
Once a DoD game is finished, the client is returned to broadcast mode.


*TERMINATING CLIENT*
The only way a client can be terminated is forcibly (i.e. the program is exited abruptly).
The server deals with a client disconnection by removing the client from the server's list (vector) of clients and:
1. If the client is not a DoDClient then the server notifies the DoDClient about the disconnection in order to remove
   them from a game if one exists
2. If the client is a DoDClient then the server returns all clients which were mid DoD game to broadcast mode.


*TERMINATED SERVER*
The server continues running until terminated, even if there are no clients on it.
The server can be terminated in one of 2 ways.
1. Gracefully - the server's user types in "exit".
   In this case, all the BufferedReaders, PrintWriters are closed before exiting, leading to the server shutting down cleanly.
2. Forcibly - the program is exited abruptly.
In either case, the clients programs will not crash and they will cleanly close after
closing all the BufferedReaders, PrintWriters and Sockets.

