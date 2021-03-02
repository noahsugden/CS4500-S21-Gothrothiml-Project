To: CS4500 Professors

From: Benjamin Lau, Bingqing Teng and Noah Sugden

Subject: Player interface



The player class is a player client that communicates with the human being and the game manager.
It will read information from the human being and communicates the chosen action to the game manager.
It contains:
- String uniquePlayername
- Integer playerID
- Position currentPlayerPosition
- List\<Object> inventories
- List\<String> abilities

When a player launches the Snarl game, a new player client is generated. It will communicate with the game manager,and
then takes in the unique player name and then send it to the game manager. The game manager will accept the player, and then
once all players have been accepted, the game manager will start the game. Afterwards, the game manager will send the level
information and player's initial position back to the player client. 

//The functions registers the player

- void sendName(String uniqueName)

Our player game state will only send the player's location in relation to the level's origin, and the available tiles
that the human being can make a move to.

For each turn, the game manager will send the game state to the player, so that the player client knows that it's time
for the human being to make a move.

//The function receives the game state

- GameState receiveGameState()


A player might choose the stay put, and the player client will send the current position of the player to the game manager.
After the human being makes a move, the player client will send the move to the game manager. 

//The function sends a move to the game manager

- void sendMove(Position)

If the move is invalid, game manager and this player's interaction ends.

If a player loses the game, the game manager will send the result to the player client with an option to start over.

If a player wins the level, the game manager will send the new level and reinitialize the game state information.

If a player wins the game, the game manager will send the result to the player.

//The function receives the result from the game manager

- String receiveResult()