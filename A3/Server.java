import java.net.*;
import java.io.*;

public class Server extends Thread {
  private ServerSocket serverSocket;

  public Server() throws IOException {
    serverSocket = new ServerSocket(8000);
    serverSocket.setSoTimeout(0);
  }

  public void run() {
    Operation result = new Addition();
    while(true) {
      try {
        Socket server = serverSocket.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
        System.out.println("You are here");
        System.out.println(in.toString());

//                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
//                    out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress()
//                        + "\nGoodbye!");
//                    System.out.println("Hello!");
        server.close();


      } catch(IOException e) {
        System.out.println("IOException was thrown");
        e.printStackTrace();
        break;
      }
    }
  }
  public static void main(String[] args) {
    // write your code here
    System.out.println(args[0]);
    try {
      Thread t = new Server();
      t.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}






