import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

import org.json.*;

public class a4 {

  public static int port;
  public static String ipAddress;
  public static String userName;
  Map<String,String> characterMap = new HashMap<>();
  private static List<String> characters = new ArrayList<>();
  private static List<String> nodes = new ArrayList<>();


  public static void main(String[] args) throws SocketException {
    //If given no command line arguments, then initialize values to default values
    if (args.length == 0) {
      port = 8000;
      ipAddress = "127.0.0.1";
      userName = "Glorifrir Flintshoulder";
    }

    //If given one command line argument, initialize port and userName to default values
    // and ipAddress to input given
    if (args.length == 1) {
      ipAddress = args[0];
      port = 8000;
      userName = "Glorifrir Flintshoulder";
    }

    //If given two command line arguments, initialize userName to default value and
    // port and userName to input given
    if (args.length == 2) {
      ipAddress = args[0];
      port = Integer.parseInt(args[1]);
      userName = "Glorifrir Flintshoulder";
    }

    //If given three command line arguments, initialize ipAddress, port and userName to input values
    if (args.length == 3) {
      ipAddress = args[0];
      port = Integer.parseInt(args[1]);
      userName = args[2];
    }
    //Creates a connection to the server
    a4 client = new a4();
  }

  public a4() throws SocketException {
    Socket client;
    BufferedReader inServer;
    DataOutputStream out;
    BufferedReader inUser;
    String sessionId;
    JSONArray nameArray = new JSONArray();


    try {
      //Creates a connection to the server
      client = new Socket(ipAddress, port);
      client.setKeepAlive(true);
      inServer = new BufferedReader(new InputStreamReader(client.getInputStream()));
      inUser = new BufferedReader(new InputStreamReader(System.in));
      out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));

      //Writes a message to the server in bytes
      out.write(userName.getBytes());
      sessionId = inServer.readLine();
      nameArray.put("The server will call me");
      nameArray.put(userName);
      System.out.println(nameArray);

      //read from stdin once for road network
      String roadJson = inUser.readLine();
      String modRoadJson = roadParser(roadJson);
      if (modRoadJson.equals("roads first")) {
        client.close();
      }
      out.write(modRoadJson.getBytes());
      while (client.getKeepAlive()) {
        inUser.readLine();

      }




   /*   JSONTokener newTokener = new JSONTokener(newInput);
      JSONObject newObject = (JSONObject) newTokener.nextValue();
      String newCommand = newObject.getString("command");
      if (newCommand.equals("place")) {
        placeCharacter(newObject.getJSONObject("params"));
      } else if (newCommand.equals("passage-safe?")) {
        query(newObject);
      } else {
        throw new JSONException("bad command");
      }*/


    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }


  }

  public String roadParser(String roadJson) throws JSONException {

    // create a road network
    JSONTokener jsonToken = new JSONTokener(roadJson);
    JSONObject roadObject = (JSONObject) jsonToken.nextValue();
    String command = roadObject.getString("command");
    if (!command.equals("roads")) {
      System.out.print("roads first");
      return "roads first";
    }
    JSONArray roadParams = roadObject.getJSONArray("params");
    for (int i = 0; i < roadParams.length(); ++i) {
      JSONObject param = roadParams.getJSONObject(i);
      String from = param.getString("from");
      String to = param.getString("to");
      if (!nodes.contains(from)) {
        nodes.add(from);
      }
      if (!nodes.contains(to)) {
        nodes.add(to);
      }
    }

    JSONObject modRoadObject = new JSONObject();
    JSONArray townArray = new JSONArray();
    for (int j=0; j<nodes.size();j++) {
      townArray.put(nodes.get(j));
    }
    modRoadObject.put("towns", townArray);
    modRoadObject.put("road", roadParams);


    return modRoadObject.toString();
  }


  public void jsonParser(String json) throws JSONException {






      String characterInput ="";
      JSONTokener characterToken = new JSONTokener(characterInput);
      JSONObject characterObject = (JSONObject) characterToken.nextValue();
      String place = characterObject.getString("command");
      if (!place.equals("place")) {
        System.out.print("Place characters now");
        return;
      }
      JSONObject single = characterObject.getJSONObject("params");

      characterMap.put(single.get("character").toString(), single.get("town").toString());






    }

  public static boolean query (JSONObject mission) throws JSONException {
    String characterName = mission.getString("character");
    String townName = mission.getString("town");
    if (nodes.contains(townName) && characters.contains(characterName)) {
      //call the canReach function here, return true for now
      return true;
    } else {
      throw new JSONException("no such town or character");
    }
  }
}
