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
  private static Map<String,String> characterMap = new HashMap<>();
  private static Map<String,String> queryMap = new HashMap<>();
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
      //check if roadJson is a well-formed json
      if (!checkRequest(roadJson)) {
        JSONObject error = new JSONObject();
        error.put("error", "not a request");
        error.put("object", roadJson);
      }
      String modRoadJson = roadParser(roadJson);
      if (modRoadJson.equals("roads first")) {
        client.close();
      }
      //send the create request to the server
      out.write(modRoadJson.getBytes());


      while (client.getKeepAlive()) {
        String singleRequest = inUser.readLine();
        int result = jsonParser(singleRequest);
        //once a query request is put in or stdin closes, send a batch request to the server
        if (result == 1 ) {


        }


      }






    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {

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


  //parse one json request, add the information to the maps
  //return 0  if it is a place request, return 1 if it is a query request, return 2 if it is not valid
  public int jsonParser(String json) throws JSONException {
      JSONTokener requestToken = new JSONTokener(json);
      JSONObject request = (JSONObject) requestToken.nextValue();
      String commandType = request.getString("command");
      JSONObject requestParams = request.getJSONObject("params");
      String townName = requestParams.get("town").toString();
      String characterName = requestParams.get("character").toString();
      if (commandType.equals("place")) {
        if (nodes.contains(townName)) {
          characterMap.put(characterName, townName);
          return 0;
        } else {
          System.out.print(responseInvalidPlacement(requestParams));
        }
      } else if (commandType.equals("passage-safe?")) {
        if (characters.contains(characterName) && nodes.contains(townName)) {
          queryMap.put(characterName,townName);
          return 1;
        } else {
          return 2;
        }
      }
      return 2;

    }


  //check if a json input is a well-formed request
  public static boolean checkRequest(String json) throws JSONException {
    JSONTokener jsonToken = new JSONTokener(json);
    JSONObject request = (JSONObject) jsonToken.nextValue();
    String command = request.getString("command");
    if (command.equals("roads") || command.equals("place") || command.equals("passage-safe?")) {
      return true;
    } else {
      return false;
    }

  }

  //responses to the user when the placement is invalid
  public static String responseInvalidPlacement(JSONObject invalidParams)  {
    JSONArray response = new JSONArray();
    response.put("invalid placement");
    response.put(invalidParams);

    return response.toString();
  }

  //create a new batch request based on the maps
  public static String newBatchRequest(){
    JSONArray placeArray = new JSONArray();
    for (int i =0 ;i<characterMap.size();i++) {


    }

    return "";
  }
}
