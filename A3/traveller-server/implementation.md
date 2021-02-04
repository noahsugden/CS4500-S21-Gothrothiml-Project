We decided to implement the specification given in Java. 

We defined the classes: TownNetwork, Town, and Character.

In the Character class, we decided to add a new field, characterName, since 
it would make it easier to map a Town to a Character. We also added a getter method 
for characterName as the spec asked. 

In the Town class, it contains the fields townName and reachableTowns. These fields 
represent the name of the Town and Towns that can be reached from this Town.  We also 
implemented all of the methods the spec asked for.

In the TownNetwork class, it contains the fields town, paths, and characterPositions, 
which represent the Towns, paths between Towns, and Character positions, accordingly.  We also 
implemented all of the methods the spec asked for.

If a Town isn't occupied by a Character, in the HashMap, the Town will be given an empty
String as its value. 

All tests can be found in the tests directory. 
