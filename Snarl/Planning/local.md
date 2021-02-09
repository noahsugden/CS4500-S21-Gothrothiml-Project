                                  Player round


                              +----------------------------------------------------------------------------------------------------------------------------+

                              Player             Destination Tile for Player          Level                             Dungeon                     Next Level
                                   +                           +
                                   |                           |                         +                                 +                            +
                                   |                           |                         |                                 |                            |
                                   |                           |                         |                                 |                            |
      Move to the Destination Tile | +------------------------>+                         |                                 |                            |
                                   |                           |                         |                                 |                            |
                                   |                           |                         |                                 |                            |
                                   |                           |                         |                                 |                            |
                                   |                           |                         |                                 |                            |
       Interact with the  key      +-------------------------->+                         |                                 |                            |
                                   |                           |                         |                                 |                            |
                                   |                           |  Update tile's status   |                                 |                            |
                                   |                           |  including tracking     |                                 |                            |
Update status after interaction    +<--------------------------+  player and the key     |                                 |                            |
                                   |                           |                         |                                 |                            |
                                   |                           |                         |                                 |                            |
                                   |                           |                         |                                 |                            |
                                   |                           >                         |                                 |                            |
                                   |                           |                         |                                 |                            |
                                   |                           |                         | Update Exit's status if key     |                            |
                                   |                           +------------------------>+                                 |                            |
                                   |                           |                         | is found by a player            |                            |
                                   |                           |                         |                                 |                            |
                                   |                           |                         |                                 |                            |
                                   |                           |                         |                                 |                            |
                                   |                           |                         |                                 |                            |
                                  ......                      ....                      .....                           ......                       ......
                                   |                           |                         |                                 |                            |
                                   |                           |                         |                                 |                            |
           Move to the Exit tile   |                           |                         |                                 |                            |
                                   |                           |                         |                                 |                            |
                                   +--------------------------->                         |                                 |                            |
                                   |                           |  Update tile's status   |                                 |                            |
                                   |                           |                         |                                 |                            |
                                   |                           |                         |                                 |                            |
                                   |                           | +---------------------->+  Update Player status           |                            |
                                   +                           |                         |                                 |                            |
                                                               |                         |                                 |                            |
                                                               +>                        |                                 |                            |
                                                                                         |                                 |                            |
                                                                                         +--------------------------------->  Move Players to the next  |
                                                                                         |                                 |  Level                     |
                                                                                         |                                 |                            |
                                                                                         |                                 +--------------------------->+
                                                                                         |                                 |                            |
                                                                                         |                                 |                            |
                                                                                         |                                 |                            |
                                                                                         |                                 |                            |
                                                                                         ++                                |                            ++
                                                                                                                           |
                                                                                                                           |
                                                                                                                           |
                                                                                                                           ++
.....





















Adversary   round


+----------------------------------------------------------------------------------------------------------------------------+

Player                                   Destination Tile for Adversary                  Adversary

     +                                                     +                                    +
     |                                                     |                                    |
     |                                                     |                                    |
     |                                                     |                                    |
     |                                                     |                                    |
     |                                                     | <----------------------------------+  Moves to a new tile
     |                            Update Tile such that    |                                    |
     |                            it tracks adversary      |                                    |
     |                                                     |                                    | Update Adversary's location
     |                                                     |                                    |
     |                                                     |                                    |
     |                                                     |                                    |
     |                                                     |                                    |
     | <---------------------------------------------------+                                    |
     |                                                     | kill the player if there is a      |
     |                                                     | player on this tile                |
     | Update Player status to dead                        |                                    |
     |                                                     |                                    |
     |                                                     |                                    |
     |                                                     |                                    |
     |                                                     |                                    |
     |                                                     |                                    |
     |                                                     |                                    |
     |                                                     |                                    |
     |                                                     |                                    |
     |                                                     |                                    |
     |                                                     |                                    |
     |                                                     |                                    |
     |                                                     |                                    |
     |                                                     |                                    +
     |                                                     |
     |                                                     |
     |                                                     |
     |                                                     |
     |                                                     |
     |                                                     +
     |
     |
     |
     +




                                                            |                                 |                            |
                                                            |                                 |                            |
                                                            |                                 |                            |
                                                            ++                                |                            ++
                                                                                              |
                                                                                              |
                                                                                              |
                                                                                              ++
