To: CS4500 Professors
From: Benjamin Lau, Bingqing Teng and Noah Sugden
Subject: Snarl rule checker for game state

The gamestate class will use the methods in the rulechecker class to check the validity of a player or an
adversary's move.

//To check the validity of a player's move to another tile
//The player can not move more than 2 cardinal moves, and the player can only move to traversable tiles
Boolean isValidPlayerMove(Position, Player)

//To check the validity of an adversary's move to another tile
//The adversary class may contain the information of the adversary's ability
//In the future, the adversary may be an interface, so that different kinds of the adversaries will implement the
//interface. For example, if an adversary can make 3 cardinal moves in a turn, 
//there will be an int stored in adversary's class that represents how many cardinal moves that the adversary can make
Boolean isValidAdversaryMove(Position, Adversary)

After the player's move is confirmed valid, the position of the player will be updated to the destination tile.
The determinePlayerInteraction method will use the player's position.
Therefore, we will only consider the interaction between the player and the object on the destination tile and no other tile.
//To determine the interaction between the player and the object on the destination tile
//The method will return different integers for different cases. For example, 0 for key event,1 for locked exit event,
//2 for unlocked exit event, 3 for adversary event, 4 for an empty tile, 5 for another player. 
//levellayout Hashmap contains the information for key and exit positions,exitStatus return true if the key has been picked
Int determinePlayerInteraction(Player, HashMap<Position, Integer> levellayout,
HashMap<Position, Integer> adversaryPositions, HashMap<Position, Integer> playerPositions, Boolean exitStatus)


//To determine the interaction between the adversary and the object on the destination tile.
//Similar to the isValidAdversaryMove() method, the interaction result will be different depending on the different
//abilities of the adversaries.
//1 represents the adversary kills a player, and 0 represents the tile is empty and there is no interaction
Int determineAdversaryInteraction(Adversary, HashMap<Position, Integer> adversaryPositions, 
HashMap<Position, Integer> playerPositions)

//To check if the level has ended
//playerArrived is a boolean that is saved in gamestate, and it will be 0 if any player has arrived the level exit
//while the exit has been unlocked, 1 if the the level is not end, 2 if the level has ended and the current level is the
//final level
Int isLevelEnd(Boolean playerArrived)

//Helper method for isLevelEnd(), it will check if all of the players have been expelled
//the players list is saved in gamestate, and it will be empty if all players are expelled
Boolean allPlayersExpelled(ArrayList<Player> players)

//hasGameEnded is called inside isLevelEnd(), so every time we check if the current level is end, we check if the current
//level is the final level. If it is, the game is end.
//isFinallevel is a boolean that is saved in every level, and it will be true if the current level is the final level
Boolean hasGameEnded(Boolean isFinalLevel)

//To determine if the gamestate is valid
//This function will be called everytime before a new gamestate is generated.
Boolean isGameStateValid(GameState)