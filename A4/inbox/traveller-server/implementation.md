While this was a robust specifications, while implementing we realized a few key details were either missing or mistaken and made the following amendments:
- Added a name to the Character object. We did this because we saw in Task 3 that the JSONs it will be required to parse provide a String indicating the character.
- If a character has no path, the path is set to null, not -1. This is because Java is a strongly typed language.
- Town is a class not an interface. This is so Town objects can be initialized and we can implement specific methods.