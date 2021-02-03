import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.net.*;
import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Server with its corresponding input and output stream.
 */
public class Server extends Thread {

  BufferedReader in;
  List<JsonElement> numJsons = new ArrayList<>();
  JsonReader jsonReader;
  Socket socket;
  private ServerSocket server;
  private DataOutputStream out;

  /**
   * Constructor that represents a Server connected to port 8000
   *
   * @throws IOException if an error happens when connecting with the socket
   */
  public Server() throws IOException {
    server = new ServerSocket();
    server.setSoTimeout(0);
  }

  public static void main(String[] args) {
    // write your code here
    try {
      Thread t = new Server();
      t.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * To create a server that can be accessed by a client via a TCP connection on port 8000.
   */
  public void run() {
    //Only capable of adding JSON values.
    Operation result = new Addition();
    try {
      //Connects the server to port 8000
      server = new ServerSocket(8000);
      System.out.println("Server started...");
      socket = server.accept();
      System.out.println("Client has been accepted...");
      //BufferedReader is initialized
      in = new BufferedReader(
          new InputStreamReader(socket.getInputStream()));
      //OutputStream is initialized
      out = new DataOutputStream(
          new BufferedOutputStream(socket.getOutputStream()));
      jsonReader = new JsonReader(in);

      String line = "";
      while (true) {
        try {
          out.writeChars("Please input JSON value: \n");
          out.flush();
          JsonParser parser = JsonParser.parseReader(jsonReader);
          if(parser.equals("END\n")) {
            break;
          }
          // reads the next available numJson element
          numJsons.add(parser);
        } catch (Exception e) {
          // once EOF is reached, an exception
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
      //Sends the formatted string to the client
      oos.writeObject(complete);

      oos.close();
      server.close();

    } catch (IOException e) {
      System.out.println("IOException was thrown!");

    }

  }
}






