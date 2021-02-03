import com.google.gson.*;
import com.google.gson.stream.JsonReader;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class a2 {

  public static void main(String[] args) {
    Operation operation;
    List<JsonElement> numJsons = new ArrayList<>();
    Reader reader = new InputStreamReader(System.in);
    JsonReader jsonReader = new JsonReader(reader);

    //To check whether a command line argument is given
    if(args[0] == null) {
      throw new IllegalArgumentException("Not given any command line arguments");
    }

    //handles the --product and --sum calls for the class, specifying the Operation class needed
    switch (args[0]) {
      case "--sum":
        operation = new Addition();
        break;
      case "--product":
        operation = new Multiplication();
        break;
      default:
        // if not product or sum, the given command line argument is invalid
        throw new IllegalArgumentException("Invalid argument");
    }

    // Waits for input from STDIN
    while (true) {
      try {
        // reads the next available numJson element
        numJsons.add(JsonParser.parseReader(jsonReader));
      } catch (Exception e) {
        // once EOF is reached, an exception
        break;
      }
    }

    String complete = "";
    //Creates a written format for the NumJSON totals and objects
    for (int i = 0; i < numJsons.size(); i++) {
      String object = "{ 'object' : ";
      String total = " 'total' : ";
      if (numJsons.size() == 1) {
        // if there is only one NumJSON given, we can just begin and end the string with brackets
        complete += "[ " + object + numJsons.get(i).toString() + total + operation.jsonMath(numJsons.get(i)) + " }" + " ]";
      } else if (i == 0) {
        // for the first NumJSON given, we ensure it begins with a bracket.
        complete += "[ " + object + numJsons.get(i).toString() + total + operation.jsonMath(numJsons.get(i)) + " }";
      } else if (i == numJsons.size() - 1) {
        // for the last NumJSON given, we insert a comma prior, and add an ending bracket
        complete += ", " + object + numJsons.get(i).toString() + total + operation.jsonMath(numJsons.get(i)) + " }" + " ]";
      } else {
        // for all other cases, we can assume there is a prior NumJSON and a following one, so we insert a comma
        complete += ", " + object + numJsons.get(i).toString() + total + operation.jsonMath(numJsons.get(i)) + " }";
      }
    }
    // Now we output the entire formatted string
    System.out.println(complete);
  }
}
