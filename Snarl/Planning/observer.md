There will be an Observer class that communicates with the GameManager server. The Observer will 
be a client for the stakeholders to view the game in progress. There could be multiple observers in
one game. Firstly, the GameManager server will wait for Observer connections continuously, so that 
the stakeholders can start observing the game at any time. When an Observer client is initialized,
it will send a registration request to the GameManager server. The GameManager server will register
the Observer and send them a rendering of the current game state. Every time the game state changes,
the new game state will be sent to the Observer. 
Methods:
//To send the registration request to the GameManager server. 
void register()

//To receive the updated GameState from the GameManager.
GameState receiveGameState()

//To render the received GameState from the GameManager. 
void renderGameState()

//It will send an unregistration request to the GameManager server. 
void unregister()

There will be methods added in the GameManager class to send the GameState to every Observer 
every time the game state is updated. 