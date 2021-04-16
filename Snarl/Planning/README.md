SnarlServer:
It prints "Server has started..." when the server successfully started.
It prints "XXX has registered." when a client registers.
It prints "Clients have been accepted" when the number of accepted clients reach a maximum, and it will automatically start the game.
When the server has waits for reg_timeout seconds, it prints "Time passed is greater than wait time!" and starts the game.
When the observe option is given, the server display the progress of the game in ascii art form in stdout.
If the player client sends an invalid move, the server will keep requesting for a new move from the client until the move is valid.
It prints "=====The game is over.=====" when all levels are over.
If any of the clients disconnected from the server, the server will terminate.

SnarlClient:
It prints "Connected to server..." when the clients successfully connected to the server.
Then it prints the welcome message, and requests for a username from the user.
The user should enter the username in stdin.
After the game begins, it prints the player update information in ascii art form in stdout.
Then it requests for a direction twice from the player because the player can take two cardinal moves.
===========================
Please type in the direction you want to go:
left/right/up/down/stay
===========================
The user should type in one of left, right, up, down, stay to stdin.
For example, if the user wants to skip the move, the user should type in stay and enter for two times.
If the user has entered an invalid direction which is not any of the left/right/up/down/stay, the client will prompt again until the user entered a valid direction.
The user won't get an update from the server if the user is ejected or has exited the level.
The user won't receive any information until all players have left the level.



