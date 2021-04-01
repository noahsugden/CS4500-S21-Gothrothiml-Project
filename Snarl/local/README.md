This game can be played in the terminal and only allows for one player.

Run the game with the main function in GameManager.

Once the game starts, the player will be prompted for their username.  After giving a username, the
player will receive their current game state (5 x 5 of their surrounding tiles).  The player will then
be asked to enter the x and y positions of their next move (x positions are vertical and y positions are horizontal).
After the player moves, the adversaries in the game will move 1 tile closer to the player.  Then, the player 
will recieve their updated game state.  The player will then be prompted to enter their next moves again.

If a player interacts with a key or exit, they will receive messages informing them of this.  If a player interacts
with an adversary, they will be ejected and the game will end (this is because there is only 1 player).
If a player interacts with a key and then an exit, they win the game and will be notified.