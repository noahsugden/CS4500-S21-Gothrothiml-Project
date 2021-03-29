For Zombie:

It looks for the player that is the shortest distance from itself.  If the player is left, it will move
one tile left; if the player is right, it will move one tile right; if the player is on top, it will move
one tile up; and if the player is on bottom, it will move one tile down.


For Ghost:

It looks for the player that is the shortest distance from itself.  

First, if the player is in the same room:
If the player is left, it will move one tile left; if the player is right, it will move one tile 
right; if the player is on top, it will move one tile up; and if the player is on bottom, 
it will move one tile down.

If the player is not in the same room:
The ghost will look for the wall tile that is the shortest distance from itself within the same room.
If the wall tile is left, it will move one tile left; if the wall tile is right, it will move one 
tile right; if the wall tile is on top, it will move one tile up; and if the wall tile is on bottom, 
it will move one tile down.  
Once it reaches the wall tile, it is transported to a random position in a random room.