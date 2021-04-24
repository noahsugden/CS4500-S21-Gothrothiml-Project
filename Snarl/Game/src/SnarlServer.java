import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class represents the server of the game.
 */
public class SnarlServer {
  ServerSocket server;
  ArrayList<Socket> clients;
  ArrayList<Socket> zombieClients = new ArrayList<>();
  ArrayList<Socket> ghostClients = new ArrayList<>();
  String fileName;
  int clientMax;
  int wait;
  boolean observe;
  ArrayList<String> usernames = new ArrayList<>();
  HashMap<String, Integer> playerKeyCountSum = new HashMap<>();
  HashMap<String, Integer> playerExitCountSum = new HashMap<>();
  HashMap<String, Integer> playerEjectCountSum = new HashMap<>();
  HashMap<String, Integer> playerScore = new HashMap<>();
  GameManager gameManager;
  Socket socket;
  DataOutputStream out;
  DataInputStream in;
  String zombieCode = "1334624065";
  String ghostCode = "7597977791";


  /**
   * Constructor for a SnarlServer.
   * @param fileName a string representing the filename
   * @param clientMax the maximum number of user clients allowed to connect
   * @param wait the amount of time the server will wait for clients to connect
   * @param port an int representing the port number
   * @param address a string representing the address
   * @param observe a boolean determining if the observe view should be displayed
   * @throws UnknownHostException if it cannot find the host
   * @throws JSONException if there is an error with the JSON
   */
  public SnarlServer(String fileName, int clientMax, int wait, int port, String address, boolean observe)
          throws IOException, JSONException {
    InetAddress address1 = InetAddress.getByName(address);
    SocketAddress endpoint = new InetSocketAddress(address1, port);
    this.fileName = fileName;
    this.clientMax = clientMax;
    this.wait = wait;
    this.observe = observe;
    startServer(endpoint);
    acceptClients();
    System.out.println("Clients have been accepted..."+"\n");
    String command = "";

    while (!command.equals("exit")) {
      startOneGame();
      Scanner sc = new Scanner(System.in);
      command = sc.nextLine();
    }


    disconnectAll();

  }

  /**
   * Starts one Snarl game.
   * @throws IOException if there is an error with inputs/outputs
   * @throws JSONException if there is an error with the JSON
   */
  public void startOneGame() throws IOException, JSONException {
    if (zombieClients.size() !=0 || ghostClients.size() !=0) {
      this.gameManager = new GameManager(fileName, usernames, zombieClients.size(), ghostClients.size());
    } else {
      this.gameManager = new GameManager(fileName, usernames);
    }
    sendStartLevel();
    sendPlayerUpdates();
    sendAdversaryUpdates();
    while (true) {
      if (observe) {
        this.gameManager.observe();
      }
      while (!this.gameManager.levelEnd) {
        playOneRound();
      }
      JSONObject endLevel = generateEndLevel();
      sendPlayersJson(endLevel);
      if (this.gameManager.isLastLevel()) {
        break;
      }
      this.gameManager.startNewLevel();
      sendStartLevel();
      sendPlayerUpdates();
    }

      JSONObject endGame = this.gameManager.generateEndGame();
      sendPlayersJson(endGame);
      JSONObject leaderboard = generateLeaderBoard();
      sendPlayersJson(leaderboard);

  }
  /**
   * Starts a server based on an endpoint.
   * @param endpoint the ip address and the port
   * @throws JSONException if there is an error with the JSON
   */
  public void startServer(SocketAddress endpoint) throws JSONException {
    this.clients = new ArrayList<>();
    try {
      this.server = new ServerSocket();
      server.bind(endpoint);
      System.out.println("Server has started...");
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Accepts the clients based on the ClientMax number.
   * @throws JSONException if there is an error with the JSON
   */
  public void acceptClients() throws JSONException {

    JSONObject welcomeJson = generateServerWelcome();
    try {
      while(true) {
        //accepts a new client
        socket = this.server.accept();
        out = new DataOutputStream(new BufferedOutputStream(
                socket.getOutputStream()));
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        try {
          //sends the welcome json to the client
          out.writeChars(welcomeJson.toString());
          out.flush();
          clients.add(socket);
          //request for a username from the client
          requestUsername(out,in, socket);
        } catch(Exception e) {
          e.printStackTrace();
        }

        server.setSoTimeout(this.wait * 1000);
        if(clients.size() == this.clientMax && zombieClients.size()+ghostClients.size()==1) {
          break;
        }
      }
    } catch(SocketTimeoutException e) {
      System.out.println("Time passed is greater than wait time!" +"\n");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Plays one round of the game.
   * @throws IOException if there is an error with the inputs/outputs
   * @throws JSONException if there is an error with the JSON
   */
  public void playOneRound() throws IOException, JSONException {
    for (int i=0;i<clients.size();i++) {
        if (!gameManager.isClientInActive(i)) {
        Socket client = clients.get(i);
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
                client.getOutputStream()));
        in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
        try {
          out.writeChars("move");
          out.flush();
          String response = readJsonObject(in);
          Position move = transformMove(response);
          String result = this.gameManager.performOneMove(i, move);
          while (result.equals("Invalid")) {
            try {
              out.writeChars("move");
              out.flush();
              response = readJsonObject(in);
              move = transformMove(response);
              result = this.gameManager.performOneMove(i, move);
              out.writeChars(result);
              out.flush();
            } catch (Exception e) {
              if (e instanceof SocketException) {
                System.out.print(usernames.get(i)+" has disconnected...");
              } else if (e instanceof EOFException) {
                System.out.print(usernames.get(i)+" has disconnected...");
              }
              else {
                e.printStackTrace();
              }
              System.exit(0);

            }
          }
          out.writeChars(result);
          out.flush();
          this.sendPlayerUpdates();
          if (observe) {
            this.gameManager.observe();
          }


        } catch (Exception e) {
          if (e instanceof SocketException) {
            System.out.print(usernames.get(i)+" has disconnected...");
          }
          else if (e instanceof EOFException) {
            System.out.print(usernames.get(i)+" has disconnected...");
          }
            else {
              e.printStackTrace();
          }
          System.exit(0);

        }
      }
    }
     if (!this.gameManager.levelEnd) {
       playAdversaryRound();
       if (observe) {
         this.gameManager.observe();
       }
     }

  }

  /**
   * Disconnects all of the clients from the server, and prints a message that the game is over.
   * @throws IOException if there is an error with the inputs/outputs
   */
  public void disconnectAll() throws IOException {
    for (Socket client:clients) {
      client.close();
    }
    System.out.print("=====The game is over.=====" +"\n");
  }

  /**
   * Sends the JSON object to the player clients as a string.
   * @param jsonObject a JSON object : one of endgame, endlevel, leaderboard in startOneGame
   * @throws IOException if there is an error with the inputs/outputs
   */
  public void sendPlayersJson(JSONObject jsonObject) throws IOException {
    for (int i=0;i<clients.size();i++) {
      Socket client = clients.get(i);
      DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
              client.getOutputStream()));
      try {
        out.writeChars(jsonObject.toString());
      //  System.out.print(jsonObject.toString());
        out.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Moves all of the ghosts and zombies in one round.
   * @throws IOException if there is an error with the inputs/outputs
   * @throws JSONException if there is an error with the JSON
   */
  public void playAdversaryRound() throws IOException, JSONException {
    this.gameManager.updateAdversaries();
    ArrayList<Zombie> zombies = this.gameManager.getZombies();
    ArrayList<Ghost> ghosts = this.gameManager.getGhosts();
    for (int i=0;i< zombies.size();i++) {
      Zombie zombie = zombies.get(i);
      if (!this.gameManager.levelEnd) {
        this.gameManager.adversaryMove(zombie);
        this.sendPlayerUpdates();
      }
    }
    for (int i=0;i<ghosts.size();i++) {
      Ghost ghost = ghosts.get(i);
      if (!this.gameManager.levelEnd) {
        this.gameManager.adversaryMove(ghost);
        this.sendPlayerUpdates();
      }
    }
  }

  /**
   * Generates the end level JSON object that will be sent to the clients.
   * @return the end level JSON
   * @throws JSONException if there is an error with the JSON
   */
  public JSONObject generateEndLevel() throws JSONException {
    JSONObject result = new JSONObject();
    result.put("type", "end-level");
    String keyPlayer = this.gameManager.keyPlayer;
    if (keyPlayer.equals("")) {
      result.put("key", JSONObject.NULL);
    } else {
      String keyName = usernames.get(Integer.parseInt(keyPlayer));
      result.put("key", keyName);
    }

    ArrayList<String> expelledPlayers = this.gameManager.expelledPlayers;
    ArrayList<String> exitedPlayers =this.gameManager.exitedPlayers;

    JSONArray expelled = new JSONArray();
    JSONArray exited = new JSONArray();
    for (String s:exitedPlayers) {
      exited.put(s);
    }
    for(String s:expelledPlayers) {
      expelled.put(s);
    }
    result.put("exits", exited);
    result.put("ejects", expelled);


    return result;
  }

  //HashMap<String, Integer> playerKeyCount = this.gameManager.playerKeyCount;
  //HashMap<String, Integer> playerExitCount = this.gameManager.playerExitCount;

  /**
   * Transforms the given string representing a JSON array into the move position.
   * @param json a string representing a JSON array
   * @return the move Position
   * @throws JSONException if there is an error with the inputs/outputs
   */
  public Position transformMove(String json) throws JSONException {
    JSONObject move = new JSONObject(json);
    Object to = move.get("to");
    if (to instanceof JSONArray) {
      JSONArray toArray = (JSONArray) to;
      int x = toArray.getInt(0);
      int y = toArray.getInt(1);
      return new Position(x, y);
    }
    else{
      return new Position(-1, -1);
    }
  }


  /**
   * Requests the username from the client.
   * @param out represents the DataOutputStream
   * @param in represents the DataInputStream
   * @throws Exception if there is an error with the inputs/outputs or the socket
   */
  public void requestUsername(DataOutputStream out, DataInputStream in, Socket socket) throws Exception {
    out.writeChars("name");
    out.flush();
    String username = readName(in);
    if (username.equals(zombieCode)) {
      clients.remove(socket);
      zombieClients.add(socket);
      return;
    } else if (username.equals(ghostCode)) {
      clients.remove(socket);
      ghostClients.add(socket);
    }
    while (true) {
      if (!usernames.contains(username)) {
        break;
      }

      out.writeChars("name");
      out.flush();
      username = readName(in);
    }
    usernames.add(username);
    System.out.print(username +" has registered." +"\n");
  }

  /**
   * Send the start level to the clients.
   * @throws JSONException if there is an error with the JSON
   * @throws IOException if there is an error with the inputs/outputs
   */
  public void sendStartLevel() throws JSONException, IOException {
    JSONObject startLevel = this.gameManager.generateStartLevel();
    for (Socket client:clients) {
      DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
              client.getOutputStream()));
      try {
        out.writeChars(startLevel.toString());
        out.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    for (Socket zombie:zombieClients) {
      JSONObject adversaryLevel = new JSONObject();
      adversaryLevel.put("type", "adversary-level");
      adversaryLevel.put("levels", this.gameManager.jsonLevels);
      DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
              zombie.getOutputStream()));
      try {
        out.writeChars(adversaryLevel.toString());
        out.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Sends updates to the remote adversaries.
   * @throws JSONException if there is an error with the JSON
   * @throws IOException if there is an error with the inputs/outputs
   */
  public void sendAdversaryUpdates() throws JSONException, IOException {
    for (int i=0;i< zombieClients.size();i++) {
      Socket zombie = zombieClients.get(i);
      JSONObject zombieUpdate = this.gameManager.generateAdversaryUpdate(i);
      out = new DataOutputStream(new BufferedOutputStream(
              zombie.getOutputStream()));
      try {
        out.writeChars(zombieUpdate.toString());
        // System.out.print(playerUpdate.toString());
        out.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }


    }

    for (int i=0;i< ghostClients.size();i++) {
      Socket ghost = ghostClients.get(i);
      JSONObject ghostUpdate = this.gameManager.generateAdversaryUpdate(zombieClients.size()+i);
      out = new DataOutputStream(new BufferedOutputStream(
              ghost.getOutputStream()));
      try {
        out.writeChars(ghostUpdate.toString());
        // System.out.print(playerUpdate.toString());
        out.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Sends updates to the user clients.
   * @throws IOException if there is an error with the inputs/outputs
   * @throws JSONException if there is an error with the JSON
   */
  public void sendPlayerUpdates() throws IOException, JSONException {
    for (int i=0;i<clients.size();i++) {
      Socket client = clients.get(i);
      JSONObject playerUpdate = this.gameManager.generatePlayerUpdate(i);
      if (playerUpdate ==null) {
        continue;
      }
      DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
              client.getOutputStream()));
      try {
        out.writeChars(playerUpdate.toString());
       // System.out.print(playerUpdate.toString());
        out.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  /**
   * Reads in the name from the DataInputStream.
   * @param in represents the DataInputStream
   * @return the name read from in
   * @throws Exception if there is an error with the in
   */
  public String readName(DataInputStream in) throws Exception {
    Character curr = in.readChar();
    StringBuilder valid = new StringBuilder();
    while(in.available()>0) {
      valid.append(curr);
      curr = in.readChar();
    }
    valid.append(curr);
    return valid.toString();
  }

  /**
   * Reads the JSON object from in and returns it as a string.
   * @param in represents the DataInputStream
   * @return the string from the JSON object
   * @throws Exception if there is an error with the in or JSON
   */
  public String readJsonObject(DataInputStream in) throws Exception {
    Character curr = in.readChar();
    StringBuilder valid = new StringBuilder();
    while(curr != '\0') {
      valid.append(curr);
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

  /**
   * Generates the Leader Board for the entire game.
   * @return a JSON object representing the Leader Board
   * @throws JSONException if there is an error with the JSON
   */
  public JSONObject generateLeaderBoard() throws JSONException{
    HashMap<String, Integer> playerKeyCount = this.gameManager.playerKeyCount;
    HashMap<String, Integer> playerExitCount = this.gameManager.playerExitCount;
    HashMap<String, Integer> playerEjectCount = this.gameManager.playerEjectCount;
    //The player's score will be keyCount +exitCount -ejectCount.
    JSONObject leaderBoard = new JSONObject();
    leaderBoard.put("type", "leaderboard");


    for (int i=0;i<usernames.size();i++) {
      Integer keyCount =0;
      Integer exitCount=0;
      Integer ejectCount =0;
      Integer totalScore =0;
      String index = String.valueOf(i);
      if (playerKeyCount.containsKey(index)) {
        keyCount = playerKeyCount.get(index);
      }
      if (playerExitCount.containsKey(index)) {
        exitCount = playerExitCount.get(index);
      } else {

      }
      if (playerEjectCount.containsKey(index)) {
        ejectCount = playerEjectCount.get(index);
      } else {

      }
      String username = usernames.get(i);
      if (playerKeyCountSum.containsKey(username)) {
        Integer keyPrev = playerKeyCountSum.get(username);
        playerKeyCountSum.put(username, keyCount +keyPrev);
        totalScore += keyCount+keyPrev;
      } else {
        playerKeyCountSum.put(username, keyCount);
        totalScore +=keyCount;
      }
      if (playerExitCountSum.containsKey(username)) {
        Integer exitPrev = playerExitCountSum.get(username);
        playerExitCountSum.put(username, exitCount +exitPrev);
        totalScore +=exitCount+exitPrev;
      } else {
        playerExitCountSum.put(username, exitCount);
        totalScore +=exitCount;
      }
      if (playerEjectCountSum.containsKey(username)) {
        Integer ejectPrev = playerEjectCountSum.get(username);
        playerEjectCountSum.put(username, ejectCount +ejectPrev);
        totalScore -=ejectCount+ejectPrev;
      } else {
        playerEjectCountSum.put(username, ejectCount);
        totalScore -= ejectCount;
      }

      playerScore.put(username,totalScore);


    }

    ArrayList<Integer> scores = new ArrayList<>(playerScore.values());
    Collections.sort(scores);
    Collections.reverse(scores);

    ArrayList<String> rank = new ArrayList<>();
    for (int i=0;i<scores.size();i++) {
      Set<String> rankTemp = getKeysByValue(playerScore, scores.get(i));
      for (String username:rankTemp) {
        if (!rank.contains(username)) {
          rank.add(username);
        }
      }
    }

    JSONArray playerRanking = new JSONArray();
    for (int i=0;i<rank.size();i++) {
      playerRanking.put(rank.get(i));
    }
    leaderBoard.put("ranking", playerRanking);




/*
    Map.Entry<String, Integer> maxEntry = null;
    for (Map.Entry<String, Integer> entry: playerScore.entrySet())
    {
      if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
      {
        maxEntry = entry;
      }
    }

 */







    return leaderBoard;
  }

  /**
   * Gets a key given its value in the given Map.
   * @param map represents a Map
   * @param value represents the value of the desired key
   * @param <T> represents the key parameter
   * @param <E> represents the value parameter
   * @return the key of the given value
   */
  public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
    Set<T> keys = new HashSet<T>();
    for (Map.Entry<T, E> entry : map.entrySet()) {
      if (Objects.equals(value, entry.getValue())) {
        keys.add(entry.getKey());
      }
    }
    return keys;
  }


  /**
   * This is the main method for the Server that runs the entire game.
   * @param args the given string arguments
   * @throws IOException if there is an error with the inputs/outputs
   * @throws JSONException if there is an error with the JSON
   */
  public static void main(String[] args) throws IOException, JSONException {
    String fileName = "snarl.levels";
    int clientMax = 4;
    int wait = 60;
    int port = 45678;
    String address = "127.0.0.1";
    boolean observe = false;
    for(int i = 0; i < args.length; i++) {
      String argument  = args[i];
      switch(argument) {
        case "--levels":
          fileName = args[i + 1];
          break;
        case "--client":
          clientMax = Integer.parseInt(args[i + 1]);
          break;
        case "--wait":
          wait = Integer.parseInt(args[i + 1]);
          break;
        case "--observe":
          observe = true;
          break;
        case "--address":
          address = args[i + 1];
          break;
        case "--port":
          port = Integer.parseInt(args[i + 1]);
          break;

      }
    }
    SnarlServer snarlServer = new SnarlServer(fileName, clientMax, wait, port, address, observe);


  }

  /**
   * Generates a JSON object representing the welcome message.
   * @return a JSON object representing the welcome message.
   * @throws JSONException if there is an error with the JSON
   */
  public JSONObject generateServerWelcome() throws JSONException {
    JSONObject info = new JSONObject();
    info.put("type", "welcome");
    info.put("info", "Gothrothiml");
    return info;

  }


}
