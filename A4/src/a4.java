import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import org.json.*;

public class a4 {
public static int port;
public static String ipAddress;
public static String userName;
  public static void main(String[] args) {
    //If given no command line arguments, then initialize values to default values
    if(args.length == 0) {
      port = 8000;
      ipAddress = "127.0.0.1";
      userName = "Glorifrir Flintshoulder";
    }

    //If given one command line argument, initialize port and userName to default values
    // and ipAddress to input given
    if(args.length == 1) {
      ipAddress = args[0];
      port = 8000;
      userName = "Glorifrir Flintshoulder";
    }

    //If given two command line arguments, initialize userName to default value and
    // port and userName to input given
    if(args.length == 2) {
      ipAddress = args[0];
      port = Integer.parseInt(args[1]);
      userName = "Glorifrir Flintshoulder";
    }

    //If given three command line arguments, initialize ipAddress, port and userName to input values
    if(args.length == 3) {
      ipAddress = args[0];
      port = Integer.parseInt(args[1]);
      userName = args[2];
    }
    //Creates a connection to the server
    a4 client = new a4();
  }

  public a4() {
    Socket client;
    BufferedReader inServer;
    DataOutputStream out;
    BufferedReader inUser;
    String sessionId;
    JSONArray sessionArray = new JSONArray();

    try{
      //Creates a connection to the server
      client = new Socket(ipAddress, port);
      inServer = new BufferedReader(new InputStreamReader(client.getInputStream()));
      inUser = new BufferedReader(new InputStreamReader(System.in));
      out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));

      //Writes a message to the server in bytes
      out.write(userName.getBytes());
      sessionId = inServer.readLine();
      sessionArray.put("The server will call me");
      sessionArray.put(sessionId);
      System.out.println(sessionArray);



    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }


  }

}
