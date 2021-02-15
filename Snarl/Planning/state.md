To: CS4500 Professors
From: Benjamin Lau, Bingqing Teng and Noah Sugden
Subject: Snarl game state data representation

There will be an interface, SnarlGameState, which will be implemented by ManagerGameState, 
PlayerGameState and AdversaryGameState, since the visibility of the information differs depending
on whether it is a Manager, Player or Adversary. 

For SnarlGameState it will contain the following methods:

This method is called after the end of each round. To check if any Player has exited the current 
level. 
- boolean anyPlayerExited()

This method is called after the end of each round. To determine if all Players have been ejected
from the game. 
- boolean allPlayersEjected()

This method is called after the end of each round in the final level of the game.
To determine if any Player has exited the final level. 
- boolean playersHaveWon()

This method will be called if anyPlayerExited returns true. To update the level of the game.
- void updateLevel()

This method will send the current game state to a Player or an Adversary.
- void sendState() 

This method will receive the move information from either a Player or Adversary. If given invalid 
move or move raises an exception, their turn will be skipped and they will not move from their 
current position. 
- Position receiveMove()

This method will determine if a move is valid from the initial Position.
- boolean isValidMove(Position initial, Position destination)

This method will be called after a Position is received.
- void updatePosition()

This method will send end of game result to the players.
- void sendResult()

For ManagerGameState it will contain the following fields: 
- The current level of the game. 
- The positions of all Player and Adversary.
- Object placement(just the key, for now).
- Exit status(if it is open or closed)
- The players that have exited or ejected.

For PlayerGameState it will contain the following fields: 
- The current level of the game. 
- The positions of all Player and visible Adversary, depending on "field of vision"
- Exit status
- The players that have exited or ejected.

For AdversaryGameState it will contain the following fields:
- The current level of the game.
- The positions of all Player and Adversary.
