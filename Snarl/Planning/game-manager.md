To: CS4500 Professors

From: Benjamin Lau, Bingqing Teng and Noah Sugden

Subject: Game manager interface



The game manager class is a server that communicates with multiple player clients.
It will receive registration information from each player client, send the game state to the player clients, and receive
moves from the player clients.
It will contain:

//Single level for now
- ArrayList\<Level> dungeon

- ArrayList\<Player> players

- ArrayList\<Adversary> adversaries

- Integer currentLevel

The game manager will be initialized as soon as a Snarl game is launched, and then it will wait for connections from
the player clients. Once all player information is collected, the game manager will initialize the game state, and then
sends the player game state to each player client. Each player client will receive the game state and send a move back.

//The function registers a player
- void registerPlayer(String uniqueName)

//The function sends the game state to a specific player
- void sendState(GameState, playerID)

//The function receives a move from a player
- Position receiveMove(playerID)

//The function sends the winning/losing result to the players
- void sendResult()





