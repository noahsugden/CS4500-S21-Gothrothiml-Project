To: CS4500 Professors

From: Benjamin Lau, Bingqing Teng and Noah Sugden

Subject: Adversary interface



The adversary class is an adversary client that communicates with the human being and the game manager.
It will read information from the human being and communicates the chosen action to the game manager.
It contains:
- String uniqueAdversaryName
- Integer adversaryID
- Position currentAdversaryPosition
- List\<String> abilities
- HashMap<Position, Integer> levelLayout
- HashMap<String, Position> playerPositions

When an adversary launches the Snarl game, a new adversary client is generated. It will communicate 
with the game manager,and then takes in the unique adversary name and then send it to the game manager. 
The game manager will accept the adversary, and then once all players and adversaries have been 
accepted, the game manager will start the game. Afterwards, the game manager will send the level
information, including player locations, and adversary's initial position back to the adversary client. 

//The functions registers the adversary

- void sendName(String uniqueName)



For each turn, the game manager will send the player positions to the adversary, only when the 
adversary is going to make a turn. 
//The function receives a HashMap<String, Position> of player positions.

- HashMap<String, Position> receivePlayerPositions()


An adversary might choose the stay put, and the adversary client will send the current position of 
the adversary to the game manager.
After the human being makes a move, the adversary client will send the move to the game manager. 

//The function sends a move to the game manager

- void sendMove(Position)

If the move is invalid, game manager and this adversary's interaction ends.

If the adversaries eject all players on a level, the adversaries win the game, and the game manager 
will send the result to the adversary. 

If all players reach the unlocked exit in the last level, the adversaries lose the game and the game
manager will send the result to the adversary. 

//The function receives the result from the game manager

- String receiveResult()

If all players reach the unlocked exit in a level that is not the last level, then the game manager
will send the next level layout and a new Position to the adversary. 

//Receives levelLayout from GameManager 
- HashMap<Position, Integer> receiveLevelLayout()

//Receives Position from GameManager
- Position receivePosition()





