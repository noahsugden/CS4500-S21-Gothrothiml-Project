import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.json.JSONObject;

public class SnarlServer {
  ServerSocket server;
  ArrayList<Socket> clients;




  public SnarlServer(String fileName, int clientMax, int wait, int port, String address, boolean observe)
      throws UnknownHostException {
    InetAddress address1 = InetAddress.getByName(address);
    SocketAddress endpoint = new InetSocketAddress(address1, port);
    startServer(endpoint, wait, clientMax);



  }

  public void startServer(SocketAddress endpoint, int wait, int clientMax) {
    this.clients = new ArrayList<>();
    Socket socket;
    JSONObject welcomeJson = generateServerWelcome();
    try {
      this.server = new ServerSocket();
      server.bind(endpoint);
      System.out.println("Server has started...");
      try {
        while(true) {
          socket = server.accept();
          DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
              socket.getOutputStream()));
          out.writeChars(welcomeJson.toString());
          out.flush();
          clients.add(socket);
          if(clients.size() == clientMax) {
            break;
          }
          server.setSoTimeout(wait * 1000);
        }
      } catch(SocketTimeoutException e) {
        System.out.println("Time passed is greater than wait time!");

      }

      System.out.println("Client has been accepted...");
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws UnknownHostException {
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

  public JSONObject generateServerWelcome() {
    JSONObject info = new JSONObject();
    info.put("type", "welcome");
    info.put("info", "Gothrothiml");
    return info;

  }
}
