import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UserClient implements User{

  String name;
  int playerID;
  Position currentPlayerPosition;
  List<Object> inventories;
  List<String> abilities;
  HashMap<String, Position> surroundingPositions;
  int[][] visibleTiles;

  public UserClient(String name, List<Object> inventories, List<String> abilities) {
    this.name = name;
    this.inventories = inventories;
    this.abilities = abilities;
  }


  public UserClient(String address, int port) {
    Socket client;
    BufferedReader inServer;
    BufferedReader inUser;
    DataOutputStream out;
    try {
      client = new Socket(address, port);
      inServer = new BufferedReader(new InputStreamReader(client.getInputStream()));
      inUser = new BufferedReader(new InputStreamReader(System.in));
      out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
      System.out.println("Connected to server...");
      String line = inServer.readLine();
      System.out.println(line);
    } catch (Exception e) {

    }

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
      formatted = original.substring(1, size - 2);
      char[] chars= formatted.toCharArray();
      for(int i =0; i<chars.length;i++) {
        char temp = chars[i];
        //3 means a player
        if (temp == '3') {
          chars[i] = 'p';
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

