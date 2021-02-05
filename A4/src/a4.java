import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.json.*;

public class a4 {

  public static int port;
  public static String ipAddress;
  public static String userName;
  private static Map<String,String> characterMap = new HashMap<>();
  private static List<String> queryList = new ArrayList<>();
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
          System.out.print(error.toString());
        }
        String modRoadJson = roadParser(roadJson);
        if (modRoadJson.equals("roads first")) {
          client.close();
        }
        //send the create request to the server
        out.write(modRoadJson.getBytes());


      try {
        while (true) {
          String singleRequest = inUser.readLine();
          int result = jsonParser(singleRequest);

        //  System.out.print(result);
          //once a query request is put in or stdin closes, send a batch request to the server
          if (result == 1) {
            //send the batch to the server
            out.write(newBatchRequest().getBytes());
            String response = inServer.readLine();
            JSONTokener jsonToken = new JSONTokener(response);
            JSONObject responseObject = (JSONObject) jsonToken.nextValue();
            boolean responseBoolean = responseObject.getBoolean("response");
            String responseArrayString = createBooleanResponse(responseBoolean);
            System.out.print(responseArrayString);
            characterMap = new HashMap<>();
            queryList = new ArrayList<>();
            characters = new ArrayList<>();

            //the query request is not valid
          } else if (result ==2) {
            client.close();
            break;
          }

        }
      } catch (Exception e) {
        out.write(newBatchRequest().getBytes());
        String response = inServer.readLine();
        JSONTokener jsonToken = new JSONTokener(response);
        JSONObject responseObject = (JSONObject) jsonToken.nextValue();
        boolean responseBoolean = responseObject.getBoolean("response");
        String responseArrayString = createBooleanResponse(responseBoolean);
        System.out.print(responseArrayString);
        client.close();


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
          characters.add(characterName);
          characterMap.put(characterName, townName);
          return 0;
        } else {
          System.out.print(responseInvalidPlacement(requestParams));
          return 2;
        }
      } else if (commandType.equals("passage-safe?")) {
        if (characters.contains(characterName) && nodes.contains(townName)) {
          queryList.add(characterName);
          queryList.add(townName);
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
    String command = request.get("command").toString();
    if (command.equals("roads") || command.equals("place") || command.equals("passage-safe?")) {
      switch (command) {
        case "roads":
          if (request.get("params") instanceof JSONArray) {
            return true;
          } else {
            return false;
          }
        case "place":
        case "passage-safe?":
          if (request.get("params") instanceof JSONObject) {
            return true;
          } else {
            return false;
          }

        default:return false;
      }

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
  public static String newBatchRequest() throws JSONException {
    JSONArray placeArray = new JSONArray();
    for (String s:characterMap.keySet()) {
      JSONObject object = new JSONObject();
      object.put("name",s);
      object.put("town",characterMap.get(s));
      placeArray.put(object);
    }
    JSONObject batchObject = new JSONObject();
    batchObject.put("characters", placeArray);
    JSONObject queryObject = new JSONObject();
    queryObject.put("character", queryList.get(0));
    queryObject.put("destination", queryList.get(1));
    batchObject.put("query", queryObject);


    return batchObject.toString();
  }


  //create a new json array for the boolean response
  public static String createBooleanResponse(boolean response ) throws JSONException {
    JSONArray booleanArray = new JSONArray();
    booleanArray.put("the response for");
    JSONObject queryObject = new JSONObject();
    queryObject.put("character", queryList.get(0));
    queryObject.put("destination", queryList.get(1));
    booleanArray.put(queryObject);
    booleanArray.put("is");
    booleanArray.put(response);


    return booleanArray.toString();
  }

}
