import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SnarlServer {
  ServerSocket server;
  ArrayList<Socket> clients;
  String fileName;
  int clientMax;
  int wait;
  boolean observe;
  ArrayList<String> usernames = new ArrayList<>();
  GameManager gameManager;
  Socket socket;
  DataOutputStream out;
  DataInputStream in;


  /**
   * Constructor for a snarlserver
   * @param fileName
   * @param clientMax
   * @param wait
   * @param port
   * @param address
   * @param observe
   * @throws UnknownHostException
   * @throws JSONException
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
    this.gameManager = new GameManager(fileName, usernames);
    sendStartLevel();
    sendPlayerUpdates();
    while (true) {
      playOneRound();
    }
  }

  /**
   * Starts a server based on an endpoint
   * @param endpoint the ip address and the port
   * @throws JSONException
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
   * accepts the clients based on the ClientMax number
   * @throws JSONException
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
          //request for a username from the client
          requestUsername(out,in);
        } catch(Exception e) {
          e.printStackTrace();
        }
        clients.add(socket);
        server.setSoTimeout(this.wait * 1000);
        if(clients.size() == this.clientMax) {
          break;
        }
      }
    } catch(SocketTimeoutException e) {
      System.out.println("Time passed is greater than wait time!" +"\n");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void playOneRound() throws IOException, JSONException {
    for (int i=0;i<clients.size();i++) {
      Socket client = clients.get(i);
      DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
              client.getOutputStream()));
      try {
        out.writeChars("move");
        out.flush();
        String response = readJsonObject(in);
        Position move = transformMove(response);
        String result = this.gameManager.performOneMove(i, move);
        while (result.equals("Invalid")) {
          out.writeChars("move");
          out.flush();
          response = readJsonObject(in);
          move = transformMove(response);
          result = this.gameManager.performOneMove(i, move);
          out.writeChars(result);
          out.flush();
        }
        out.writeChars(result);
        out.flush();
        this.sendPlayerUpdates();
        if (this.gameManager.levelEnd) {
          JSONObject endLevel = generateEndLevel();
          out.writeChars(endLevel.toString());
          out.flush();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }


    }
    playAdversaryRound();

  }

  public void playAdversaryRound() throws IOException, JSONException {
    this.gameManager.updateAdversaries();
    ArrayList<Zombie> zombies = this.gameManager.getZombies();
    ArrayList<Ghost> ghosts = this.gameManager.getGhosts();
    for (int i=0;i< zombies.size();i++) {
      Zombie zombie = zombies.get(i);
      this.gameManager.adversaryMove(zombie);
      this.sendPlayerUpdates();
    }
    for (int i=0;i<ghosts.size();i++) {
      Ghost ghost = ghosts.get(i);
      this.gameManager.adversaryMove(ghost);
      this.sendPlayerUpdates();
    }
  }

  public JSONObject generateEndLevel() throws JSONException {
    JSONObject result = new JSONObject();
    result.put("type", "end-level");
    String keyPlayer = this.gameManager.keyPlayer;
    String keyName = usernames.get(Integer.parseInt(keyPlayer));
    result.put("key", keyName);
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
   * requests for username from the client
   * @param out
   * @param in
   * @throws Exception
   */
  public void requestUsername(DataOutputStream out, DataInputStream in) throws Exception {
    out.writeChars("name");
    out.flush();
    String username = readName(in);
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
  }

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
        System.out.print(playerUpdate.toString());
        out.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }



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




  public static void main(String[] args) throws IOException, JSONException {
    String fileName = "snarl.levels";
    int clientMax = 1;
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
   *
   * @return
   * @throws JSONException
   */
  public JSONObject generateServerWelcome() throws JSONException {
    JSONObject info = new JSONObject();
    info.put("type", "welcome");
    info.put("info", "Gothrothiml");
    return info;

  }


}
