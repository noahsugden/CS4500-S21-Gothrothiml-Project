Player would be its own class. A Player will have a unique ID number, 
a name field represented as a String, a boolean representing if that Player is 
still in the game, a Tile that represents where the Player is located, and a field that is 
a List of Tiles that represent which Tiles can be seen by the Player. In the future, if the 
inventory system is implemented, then if other items are found they would be placed here. 
There will be a method move(Tile destination), which takes the Player to the given 
unoccupied Tile. Inside this method, another helper method, interact() will be 
called, which updates the Player's status based on the Player's interactions with the items on 
the Tile they moved to.

There will be a Dungeon class, which will have a field that is a list of Levels, each Level will
be its own class and consist of Rooms and Hallways, which are also separate classes. It will
also have a HashMap<Integer, Integer> which represent the PlayerId and status of that Player. The 
status can be either dead, alive or has reached the final exit, which will be represented as 0, 1,
and -1. It will have a field that is a List<Player> and another field that is a List<Adversary>. 

The Level class will contain a boolean that represents if the key was found and also has a 
method that will unlock the exit on another Tile. It will also have a field that is a 
HashMap<Integer, Boolean>, Integer will represent a PlayerId and boolean will represent if that 
Player has been removed from the Level. 
 
The Room and Hallway class will contain a list of Tiles, which is also its own class. 

Each Tile will be able to contain one Item. For now, an Item can be either a key or an exit. 
It will also have an integer field, isOccupied, which represents if the Tile is occupied by a Player, 
Adversary or neither, which will be represented by 0, 1 and -1, respectively. There will also 
be two integer fields that represent the Player and Adversary on the Tile. It will be -1 if there's 
no Player or Adversary on the Tile. 

Item will be an interface. Key and Exit will be classes that implement this interface. In the 
future, if more Items are placed in the game, they will be classes that implement this interface.

The Exit class will contain a boolean that represents if the exit has been unlocked. 

There will be also an Adversary interface, that will represent the enemies players may encounter
during the game. Ghost, Zombies and any other enemies that may be added if the future, will 
implement this interface. This interface will have a field that's a Tile that will represent its
location. Like the Player class, this interface will also have a method move(Tile destination) and 
an interact() method that, if a Player is on the destination Tile, then it would kill the Player.

Milestone 2:

Implement the Player class, Adversary and Item interface.

Milestone 3: 

Implement the Tile, Room, Level and Hallway classes.

Milestone 4:

Start making the demo and design and run tests that will throughly cover our code for the demo.

Milestone 5: 

We'll have a working demo, which is Player and Items in one Level.

Milestone 6:

Implement the whole Dungeon and enable Player to move to a deeper Level.

Milestone 7: 

Add Adversary to Dungeon chasing Players and taking turns.

Milestone 8:

More tests that will cover the new features. 

Milestone 9: 

Add ending the game. 

Milestone 10:

Add new features if needed, according to instructors. 


