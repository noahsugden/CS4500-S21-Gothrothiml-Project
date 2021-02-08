Player would be its own class. A Player will have a unique ID number, 
a name field represented as a String, a boolean representing if that Player is 
still in the game, a Tile that represents where the Player is located, a boolean to 
determine if the Player has found the key, and a field that is a List of Tiles
that represent which Tiles can be seen by the Player. In the future, if the inventory 
system is implemented, then if the key is found, it would be placed into that inventory 
system, along other items might be found. 
There will be a method move(Tile destination), which takes the Player to the given 
unoccupied Tile. Inside this method, another helper method, interact(Object) will be 
called, which updates the Player's status based on the Player's interactions with the items on the Tile they 
moved to.

There will be a Dungeon class, which consists of several floor levels, each Level will
be its own class and consist of Rooms and Hallways, which are also separate classes.
 
The Room and Hallway class will contain a list of Tiles, which is also its own class. 

