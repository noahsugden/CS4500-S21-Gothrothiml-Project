import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UserClient implements User{

  String name;
  ArrayList<String> otherPlayers;
  int playerID;
  Position currentPlayerPosition;
  List<Object> inventories;
  List<String> abilities;
  HashMap<String, Position> surroundingPositions;
  int[][] visibleTiles;
  Position current;
  Socket client;
  BufferedReader inUser;
  DataOutputStream out;

  public UserClient(String name, List<Object> inventories, List<String> abilities) {
    this.name = name;
    this.inventories = inventories;
    this.abilities = abilities;
  }


  public UserClient(String address, int port) {

    try {
      client = new Socket(address, port);

      DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
      inUser = new BufferedReader(new InputStreamReader(System.in));
      out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));

      client.setKeepAlive(true);
      System.out.println("Connected to server...");

      try {
        //reads the welcome json from the server
        String welcomeJson = readJsonObject(in);
        JSONObject welcome = new JSONObject(welcomeJson);
        String info = welcome.getString("info");
        System.out.print("Welcome!"+"\n");
        System.out.print(info +"\n");
        //reads the username request from the server
        String next = readJsonObject(in);
        while (next.equals("name")) {
          System.out.print("Please enter your username:" + "\n");
          String username = inUser.readLine();
          name = username;
          out.writeChars(username);
          out.flush();
          next = readJsonObject(in);
        }
        respond(next);

        while (client.getKeepAlive()) {
          next = readJsonObject(in);
          respond(next);
        }
        client.close();




      } catch (Exception e) {
        if (e instanceof SocketException) {
          System.out.print("The server has disconnected");
        } else {
          e.printStackTrace();
        }
      }

    } catch (Exception e) {
      if (e instanceof SocketException) {
        System.out.print("The server has disconnected");
      } else {
        e.printStackTrace();
      }
    }

  }

  public void respond(String json) throws Exception {
    switch(json){
      case "move":
        sendMove();
        return;
      case "Invalid":
        System.out.print("The move is invalid. Please enter again." +"\n");
        sendMove();
        return;
      case "Key":
        System.out.print("You picked up the key. Now the exit is unlocked."+"\n");
        return;
      case "Exit":
        System.out.print("You have exited the level. Please wait for other players." +"\n");
        return;
      case "Eject":
        System.out.print("You have been ejected. Please wait for the level to end." +"\n");
        return;
      case "OK":
        System.out.print("You have made a successful move." +"\n");
        return;

    }
    String type = determineJsonObject(json);
    switch(type) {
      case "start-level":
        printStartLevel(json);
        break;
      case "player-update":
        printPlayerUpdate(json);
        break;
      case "end-level":
        printEndLevel(json);
        break;
      case "end-game":
        printEndGame(json);
        client.setKeepAlive(false);

        break;
    }
  }

  public void printEndGame(String json) throws JSONException {
    JSONObject endGame = new JSONObject(json);
    System.out.print("The game is over."+"\n");
    JSONArray scoreList = endGame.getJSONArray("scores");
    for (int i=0;i< scoreList.length();i++) {
      JSONObject playerScore = scoreList.getJSONObject(i);
      String name = playerScore.getString("name");
      int exits = playerScore.getInt("exits");
      int keys = playerScore.getInt("keys");
      int ejects = playerScore.getInt("ejects");
      System.out.print(name + " exited "+ exits +" times." +"\n");
      System.out.print(name +" picked up " + keys +" keys."+"\n");
      System.out.print(name + " was ejected "+ejects+" times." +"\n");
    }
  }

  public void printEndLevel(String json) throws JSONException {
    JSONObject endLevel = new JSONObject(json);
    System.out.print("The level has ended."+"\n");
    Object keyPlayer = endLevel.get("key");
    if (keyPlayer instanceof String) {
      keyPlayer = endLevel.getString("key");
      System.out.print(keyPlayer + " picked up the key." + "\n");
    }
    JSONArray exits = endLevel.getJSONArray("exits");
    JSONArray ejects = endLevel.getJSONArray("ejects");

    for (int i=0;i<exits.length();i++) {
      System.out.print(exits.getString(i) + " exited."+"\n");
    }
    for (int i=0;i<ejects.length();i++) {
      System.out.print(ejects.getString(i) + " was ejected."+"\n");
    }
  }

  public Position getValidMove() throws Exception {
    Position prev = current;
    printDirections();
    //first time requesting
    String direction = inUser.readLine();
    boolean invalidDirection = isValidDirection(direction);
    //keeps asking if the move is invalid
    while (invalidDirection) {
      printDirections();
      direction = inUser.readLine();
      invalidDirection = isValidDirection(direction);
    }
    prev = generateNextMove(direction, prev);
    printDirections();
    //second time requesting
    direction = inUser.readLine();
    invalidDirection = isValidDirection(direction);
    while (invalidDirection) {
     printDirections();
      direction = inUser.readLine();
      invalidDirection =isValidDirection(direction);
    }
    return generateNextMove(direction, prev);

  }

  public boolean isValidDirection(String direction) {
    return !direction.equals("left")&&!direction.equals("right")&&
            !direction.equals("down")&&!direction.equals("up")&&!direction.equals("stay");
  }

  public void printDirections() {
    System.out.print("Please type in the direction you want to go:"+"\n");
    System.out.print("left/right/up/down/stay"+"\n");
  }

  public void sendMove() throws Exception {

    Position move = getValidMove();
    JSONObject nextMove = generateNextMove(move);
    out.writeChars(nextMove.toString());
    out.flush();

  }

  public Position generateNextMove(String direction, Position prev) throws Exception {
    int x = prev.getx();
    int y = prev.gety();
    switch (direction) {
      case "left":
        return new Position(x, y-1);
      case "right":
        return new Position(x, y+1);
      case "up":
        return new Position(x-1, y);
      case "down":
        return new Position(x+1, y);
      case "stay":
        return prev;
    }
    throw new Exception("invalid direction!"+"\n");
  }

  public JSONObject generateNextMove(Position move) throws JSONException {
    JSONObject playerMove = new JSONObject();
    playerMove.put("type", "move");
    JSONArray position = new JSONArray();
    int x = move.getx();
    int y = move.gety();
   if (move.equals(current)) {
        playerMove.put("to", JSONObject.NULL);
        return playerMove;
    }
   position.put(x);
   position.put(y);
    playerMove.put("to", position);
    return playerMove;

  }

  public void printPlayerUpdate(String json) throws JSONException {
    JSONObject object = new JSONObject(json);
    JSONArray layout  = object.getJSONArray("layout");
    JSONArray position = object.getJSONArray("position");
    int x = position.getInt(0);
    int y = position.getInt(1);
    this.current = new Position(x,y);
    JSONArray objects = object.getJSONArray("objects");
    JSONArray actors = object.getJSONArray("actors");
    Object message = object.get("message");
    if (!message.toString().equals("null")) {
      System.out.print(message.toString()+"\n");
    }


    int[][] ascii = generateLayout(layout);
    ascii = addObjects(ascii, position, objects);
    ascii = addActors(ascii,position,actors);
    printAscii(ascii);
    System.out.print("==================="+"\n");

  }

  public int[][] addActors(int[][] ascii, JSONArray position, JSONArray actors) throws JSONException {
    Integer x = position.getInt(0);
    Integer y = position.getInt(1);
    for (int i=0;i<actors.length();i++) {
      JSONObject actor = actors.getJSONObject(i);
      String name = actor.getString("name");
      JSONArray actorPos = actor.getJSONArray("position");
      Integer actorX = actorPos.getInt(0);
      Integer actorY = actorPos.getInt(1);
      if (actor.getString("type").equals("player")) {
        Integer playerID = otherPlayers.indexOf(name) +100;
        ascii[actorX-x+2][actorY-y+2] = playerID;
      } else if (actor.getString("type").equals("zombie")) {
        ascii[actorX-x+2][actorY-y+2] = 11;
      }else if (actor.getString("type").equals("ghost")) {
        ascii[actorX-x+2][actorY-y+2] = 12;
      }
    }
    return ascii;
  }

  public int[][] addObjects(int[][] ascii, JSONArray position, JSONArray objects) throws JSONException {
    Integer x = position.getInt(0);
    Integer y = position.getInt(1);
    for (int i=0;i<objects.length();i++) {
      JSONObject object = objects.getJSONObject(i);
      if (object.getString("type").equals("key")) {
        JSONArray keyPos = object.getJSONArray("position");
        Integer keyX = keyPos.getInt(0);
        Integer keyY = keyPos.getInt(1);
        ascii[keyX-x+2][keyY-y+2] = 7;
      } else {
        JSONArray exitPos = object.getJSONArray("position");
        Integer exitX = exitPos.getInt(0);
        Integer exitY = exitPos.getInt(1);
        ascii[exitX-x+2][exitY-y+2] = 8;
      }
    }
    return ascii;
  }

  public int[][] generateLayout(JSONArray layout) throws JSONException {
    int[][] ascii = new int[5][5];
    for (int i = 0; i < layout.length(); i++) {
      JSONArray curr = layout.getJSONArray(i);
      for (int j = 0; j < curr.length(); j++) {
        int temp = curr.getInt(j);
        //1 representing a traversable non-door tile
        if (temp == 1) {
          ascii[i][j] = 2;
          //2 representing a door
        } else if (temp == 2) {
          ascii[i][j] = 4;
          //everything else is a wall tile
        } else {
          ascii[i][j] =1;
        }
      }
    }
    ascii[2][2] = 3;

    return ascii;
  }

  public void printAscii(int[][] ascii) {
    for (int[] x : ascii)
    {
      for (int y : x)
      {
        switch(y) {
          case 1:
            System.out.print(" * ");
            break;
          case 2:
            System.out.print(" . ");
            break;
          case 3:
            char[] nameArray = name.toCharArray();
            System.out.print(" "+nameArray[0]+" ");
            break;
          case 4:
            System.out.print(" < ");
            break;
          case 7:
            System.out.print(" k ");
            break;
          case 8:
            System.out.print(" $ ");
            break;
          case 11:
            System.out.print(" z ");
            break;
          case 12:
            System.out.print(" g ");
            break;
          case 100:
            nameArray = otherPlayers.get(0).toCharArray();
            System.out.print(" "+nameArray[0]+" ");
            break;
            case 101:
            nameArray = otherPlayers.get(1).toCharArray();
            System.out.print(" "+nameArray[0]+" ");
            break;
            case 102:
            nameArray = otherPlayers.get(2).toCharArray();
            System.out.print(" "+nameArray[0]+" ");
            break;

        }
      }
      System.out.println();
    }
  }


  public void printStartLevel(String json) throws JSONException {
    JSONObject object = new JSONObject(json);
    int natural = object.getInt("level");
    natural +=1;
    System.out.print("======Level "+natural+"======" +"\n");
    System.out.print("Active players in the level : ");
    JSONArray players = object.getJSONArray("players");
    otherPlayers = new ArrayList<>();
    for (int i=0;i<players.length();i++) {
      String player = players.getString(i);
      if (!player.equals(name)) {
        otherPlayers.add(player);
      }
      System.out.print(player+" ");
    }
    System.out.print("\n");
  }


  public String determineJsonObject(String json) throws JSONException {
    JSONObject object = new JSONObject(json);
     return object.getString("type");
  }

  public String readJsonObject(DataInputStream in) throws Exception {
    Character curr = in.readChar();
    StringBuilder valid = new StringBuilder();
    while(curr != '\0') {
      valid.append(curr);
     // System.out.print(valid.toString()+"\n");
     switch(valid.toString()) {
       case "name":
       case "move":
       case "Invalid":
       case "OK":
       case "Key":
       case "Eject":
       case "Exit":
         return valid.toString();
     }

      try {
        JSONObject object = new JSONObject(valid.toString());
        String type = object.getString("type");
        return valid.toString();
      } catch (JSONException e){
        curr = in.readChar();
      }


    }
    throw new Exception("Not a valid string");
  }

  @Override
  public void sendName(String uniqueName) {

  }


  @Override
  public void receiveVisibleTiles() {

  }

  @Override
  public void sendMove(Position pos) {

  }


  @Override
  public String receiveResult() {
    return null;
  }

  @Override
  public Position receiveInitialPosition() {
    return null;
  }

  @Override
  public void createConnection() {


  }

  @Override
  public void renderUpdate() {
    print2D(visibleTiles);
  }

  /**
   * Renders the current surrounding layout.
   * @param array represents the ascii art Array
   */
  @Override
  public void print2D(int[][] array)
  {
    String original = "";
    String formatted = "";
    for (int[] row : array) {
      original = Arrays.toString(row);
      int size = original.length();
      formatted = original.substring(1, size - 1);
      System.out.print(formatted);
      char[] chars= formatted.toCharArray();
      for(int i =0; i<chars.length;i++) {
        char temp = chars[i];
        //3 means a player
        if (temp == '3') {
          char[] nameArray = name.toCharArray();
          chars[i] = nameArray[0];
          //0 means nothing
        } else if (temp == '0') {
          chars[i] = ' ';
        }
        //1 means wall tile
        else if (temp == '1') {
          chars[i] = '*';
          //2 is non-wall tile
        } else if (temp == '2') {
          chars[i] = '.';
          //4 is a door
        } else if (temp == '4') {
          chars[i]='<';
          //5 is a horizontal hallway
        } else if (temp == '5') {
          chars[i]='-';
          //6 is a vertical hallway
        } else if (temp == '6') {
          chars[i]='|';
          //7 is a key
        } else if (temp == '7') {
          chars[i] = 'k';
          //8 is an exit
        } else if (temp == '8') {
          chars[i] = 'e';
        } else if (temp == '9') {
          chars[i] = 'A';
        }
        else if (chars[i] == ',') {
          chars[i] = (char) 0;
        }
      }
      String lastString = new String(chars);
      System.out.println(lastString);
    }
  }

  public static void main(String[] args) {
    int port = 45678;
    String address = "127.0.0.1";
    for(int i = 0; i < args.length; i++) {
    String argument  = args[i];
    switch(argument) {
      case "--address":
        address = args[i + 1];
        break;
      case "--port":
        port = Integer.parseInt(args[i + 1]);
        break;
    }
  }
    UserClient client = new UserClient(address, port);

  }
}

