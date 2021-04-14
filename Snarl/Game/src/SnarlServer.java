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
          throws UnknownHostException, JSONException {
    InetAddress address1 = InetAddress.getByName(address);
    SocketAddress endpoint = new InetSocketAddress(address1, port);
    this.fileName = fileName;
    this.clientMax = clientMax;
    this.wait = wait;
    this.observe = observe;
    startServer(endpoint);
    acceptClients();
    System.out.println("Clients have been accepted...");
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
    Socket socket;
    DataOutputStream out;
    DataInputStream in;
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
      System.out.println("Time passed is greater than wait time!");

    } catch (IOException e) {
      e.printStackTrace();
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
    System.out.print(username);
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
      // System.out.print(valid.toString()+"\n");
      if (valid.toString().equals("name")) {
        return "name";
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

  public static void main(String[] args) throws UnknownHostException, JSONException {
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

  public JSONObject generateServerWelcome() throws JSONException {
    JSONObject info = new JSONObject();
    info.put("type", "welcome");
    info.put("info", "Gothrothiml");
    return info;

  }


}
