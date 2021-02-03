import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.net.*;
import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

  private ServerSocket server;
  private DataOutputStream out;
  BufferedReader in;
  List<JsonElement> numJsons = new ArrayList<>();
  JsonReader jsonReader;
  Socket socket;

  public Server() throws IOException {
    server = new ServerSocket();
    server.setSoTimeout(0);
  }

  public void run() {
    Operation result = new Addition();
    try {
      server = new ServerSocket(8000);
      System.out.println("Server started...");
      socket = server.accept();
      System.out.println("Client has been accepted...");
      in = new BufferedReader(
          new InputStreamReader(socket.getInputStream()));
      out = new DataOutputStream(
          new BufferedOutputStream(socket.getOutputStream()));
      jsonReader = new JsonReader(in);

      String line = "";
      while (true) {
        try {
          out.writeChars("Please input JSON value: \n");
          out.flush();
          numJsons.add(JsonParser.parseReader(jsonReader));
        } catch (Exception e) {
          break;
        }
      }


      String complete = "";
      for (int i = 0; i < numJsons.size(); i++) {
        String object = "{ 'object' : ";
        String total = " 'total' : ";
        if (numJsons.size() == 1) {
          // if there is only one NumJSON given, we can just begin and end the string with brackets
          complete += "[ " + object + numJsons.get(i).toString() + total + result
              .jsonMath(numJsons.get(i)) + " }" + " ]";
        } else if (i == 0) {
          // for the first NumJSON given, we ensure it begins with a bracket.
          complete += "[ " + object + numJsons.get(i).toString() + total + result
              .jsonMath(numJsons.get(i)) + " }";
        } else if (i == numJsons.size() - 1) {
          // for the last NumJSON given, we insert a comma prior, and add an ending bracket
          complete += ", " + object + numJsons.get(i).toString() + total + result
              .jsonMath(numJsons.get(i)) + " }" + " ]";
        } else {
          // for all other cases, we can assume there is a prior NumJSON and a following one, so we insert a comma
          complete += ", " + object + numJsons.get(i).toString() + total + result
              .jsonMath(numJsons.get(i)) + " }";
        }
      }
      // Now we output the entire formatted string
      System.out.println(complete);

      ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
      oos.writeObject(complete);
      oos.close();
      server.close();

    } catch(IOException e) {
      System.out.println("IOException was thrown!");

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






