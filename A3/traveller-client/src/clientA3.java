import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.JsonParser;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class clientA3 {

  public static void main(String[] args) {
    List<JsonElement> jsonElements = new ArrayList<>();
    Reader reader = new InputStreamReader(System.in);
    JsonReader jsonReader = new JsonReader(reader);

    //To check whether a command line argument is given
    if(args[0] == null) {
      throw new IllegalArgumentException("Not given any command line arguments");
    }
    // Waits for input from STDIN
    while (true) {
      try {
        // reads the next available numJson element
        jsonElements.add(JsonParser.parseReader(jsonReader));
      } catch (Exception e) {
        // once EOF is reached, an exception
        break;
      }
    }

    for (int i = 0; i < jsonElements.size(); i++) {
      JsonElement curr = jsonElements.get(i);
      if (curr.isJsonObject()) {
        JsonElement command = curr.getAsJsonObject().get("command");
        JsonElement action = curr.getAsJsonObject().get("params");
        if (!command.isJsonNull()) {
          // create a new TownNetwork
          TownNetwork newTN = new TownNetwork();
          boolean roadsCalled = false;
          switch (command.toString()) {
            case "roads":
              // ensures this command is first and only used once
              if (i == 0 && !roadsCalled) {
                if (action.isJsonArray()) {
                  JsonArray actionArr = action.getAsJsonArray();
                  for (JsonElement arrElement : actionArr) {
                    if (arrElement.isJsonObject()) {
                      JsonElement from = arrElement.getAsJsonObject().get("from");
                      JsonElement to = arrElement.getAsJsonObject().get("to");
                      // create Towns
                      Town fromTown = new Town(from.toString());
                      Town toTown = new Town(to.toString());
                      // add Towns to Network
                      newTN.addTown(fromTown);
                      newTN.addTown(toTown);
                      // add paths to TownNetwork
                      newTN.addPath(fromTown, toTown);
                    } else {
                      throw new IllegalArgumentException("Not an object.");
                    }
                  }
                } else {
                  throw new IllegalArgumentException("Roads params not followed by an array.");
                }
                roadsCalled = true;
              }
              break;
            case "place":
              if (roadsCalled) {
                if (action.isJsonObject()) {
                  JsonElement character = action.getAsJsonObject().get("character");
                  JsonElement town = action.getAsJsonObject().get("town");
                  // create Character and Town
                  Character currCharacter = new Character(character.toString());
                  Town currTown = new Town(town.toString());
                  // place Character in Town
                  newTN.placeCharacter(currCharacter, currTown);
                } else {
                  throw new IllegalArgumentException("Not an object.");
                }
              } else {
                throw new IllegalArgumentException("Road network of towns not yet created.");
              }
              break;
            case "passage-safe?":
              if (roadsCalled) {
                if (action.isJsonObject()) {
                  JsonElement character = action.getAsJsonObject().get("character");
                  JsonElement town = action.getAsJsonObject().get("town");
                  // create Character and Town
                  Character currCharacter = new Character(character.toString());
                  Town currTown = new Town(town.toString());
                  // query if a character can move to another town
                  newTN.canReach(currCharacter, currTown);
                } else {
                  throw new IllegalArgumentException("Not an object.");
                }
              } else {
                throw new IllegalArgumentException("Road network of towns not yet created.");
              }
              break;
          }
        } else {
          throw new IllegalArgumentException("Cannot be null.");
        }
      } else {
        throw new IllegalArgumentException("Not an object.");
      }
    }


  }
}