We decided to implement the specifications given in Java. 

We defined the classes: Character, Town and Network.

In the Character class, we decided to add a new field, characterName, since 
it would make it easier to map a Town to a Character. We also added a getter method 
for characterName. 

In the Town class, it contains the fields townName and reachableTowns. These fields 
represent the name of the Town and Towns that can be reached from this Town.

In the TownNetwork class, it contains the fields town, paths, and characterPositions, 
which represent the Towns, path between Towns, and Character positions, accordingly. 

If a Town isn't occupied by a Character, in the HashMap, the Town will be given an empty
String as its value. 

All tests can be found on the tests directory. 
