The Network would contain all of the different Towns and all of the Paths. Town would be an interface and each class that implements this interface would contain a Town name (String), a Character, and a Boolean determining if the Town is occupied by a Character. Each Character would have a unique ID as well as a Path of the current path they are on. The Path class would have a unique ID (positive int), a list of Towns (representing the path), and a Boolean determining if that Path is currently occupied.
A town can only have one character in our implementation. When a Character travels on a Path to another Town, the Character’s current Path will be updated if the Path is not already occupied and the Towns the character came/went from will be updated as well if their desired Town is not occupied. The Character can check if another Character will be on their desired Path by checking the Boolean that a Path has and the Boolean the Town has. 
This program should be implemented with Java 8. We should be implementing methods that can determine whether a specific character can reach a designated town without running into any other characters. This can be achieved using the Path ID and the Booleans in Town and Path.
 
Town 
- Name (String)
- Character 
- Boolean (is there a character?)

Character
- ID (int) 
- Path (-1 if no path)

Network 
- Towns
- List of Paths

Paths 
- ID (int) 
- List of Towns 
- Boolean (Is there a character?)
