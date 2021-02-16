import javafx.geometry.Pos;
import org.json.*;

import java.util.ArrayList;

public class JsonParser {

    static ArrayList<Room> rooms = new ArrayList<>();
    static ArrayList<Hallway> hallways= new ArrayList<>();
    static Position key;
    static Position exit;

    static Level completelevel;

    static String room1 = "[\n" +
            "  {\n" +
            "    \"name\": \"room\",\n" +
            "    \"upperleft\": [\n" +
            "      0,\n" +
            "      0\n" +
            "    ],\n" +
            "    \"width\": 4,\n" +
            "    \"height\": 4,\n" +
            "    \"tiles\": [\n" +
            "      [\n" +
            "        2,\n" +
            "        2\n" +
            "      ],\n" +
            "      [\n" +
            "        2,\n" +
            "        3\n" +
            "      ]\n" +
            "    ],\n" +
            "    \"doors\": [\n" +
            "      [\n" +
            "        3,\n" +
            "        0\n" +
            "      ]\n" +
            "    ]\n" +
            "  }\n" +
            "]";


    public static String hallway1 = "[\n" +
            "  {\n" +
            "    \"name\": \"room\",\n" +
            "    \"upperleft\": [\n" +
            "      0,\n" +
            "      0\n" +
            "    ],\n" +
            "    \"width\": 4,\n" +
            "    \"height\": 4,\n" +
            "    \"tiles\": [\n" +
            "      [\n" +
            "        2,\n" +
            "        2\n" +
            "      ],\n" +
            "      [\n" +
            "        2,\n" +
            "        3\n" +
            "      ]\n" +
            "    ],\n" +
            "    \"doors\": [\n" +
            "      [\n" +
            "        3,\n" +
            "        0\n" +
            "      ]\n" +
            "    ]\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"room\",\n" +
            "    \"upperleft\": [\n" +
            "      6,\n" +
            "      6\n" +
            "    ],\n" +
            "    \"width\": 4,\n" +
            "    \"height\": 4,\n" +
            "    \"tiles\": [\n" +
            "      [\n" +
            "        8,\n" +
            "        8\n" +
            "      ]\n" +
            "    ],\n" +
            "    \"doors\": [\n" +
            "      [\n" +
            "        8,\n" +
            "        6\n" +
            "      ]\n" +
            "    ]\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"hallway\",\n" +
            "    \"init\": [\n" +
            "      3,\n" +
            "      0\n" +
            "    ],\n" +
            "    \"end\": [\n" +
            "      8,\n" +
            "      6\n" +
            "    ],\n" +
            "    \"waypoints\": [\n" +
            "      [\n" +
            "        8,\n" +
            "        0\n" +
            "      ]\n" +
            "    ]\n" +
            "  }\n" +
            "]";

    public static String keyString = "[  { name:'key',\n" +
            "   position:[2, 2]\n" +
            "  }]";

    public static String exitString = "[ { name: 'exit',\n" +
            "   position:[2, 3]}]";


    public static Level getCompletelevel() {
        return completelevel;
    }

    public static ArrayList<Hallway> getHallways() {
        return hallways;
    }

    public static ArrayList<Room> getRooms() {
        return rooms;
    }

    public static Position getExit() {
        return exit;
    }

    public static Position getKey() {
        return key;
    }

    public static void readRoomObject(JSONObject r) throws JSONException {
        JSONArray upperleftArray = r.getJSONArray("upperleft");
        int UpperX = upperleftArray.getInt(0);
        int UpperY = upperleftArray.getInt(1);
        Position upperLeft = new Position(UpperX,UpperY);
        int width = r.getInt("width");
        int height = r.getInt("height");
        ArrayList<Position> tiles = new ArrayList<>();
        JSONArray tilesArray = r.getJSONArray("tiles");
        for (int i =0; i< tilesArray.length();i++) {
            JSONArray temp = tilesArray.getJSONArray(i);
            Position tempPosition = new Position(temp.getInt(0), temp.getInt(1));
            tiles.add(tempPosition);
        }
        ArrayList<Position> doors = new ArrayList<>();
        JSONArray doorsArray = r.getJSONArray("doors");
        for (int i =0; i< doorsArray.length();i++) {
            JSONArray temp = doorsArray.getJSONArray(i);
            Position tempPosition = new Position(temp.getInt(0), temp.getInt(1));
            doors.add(tempPosition);
        }
        Room singleRoom = new Room(upperLeft,width,height,tiles,doors);
        rooms.add(singleRoom);
    }

    public static void readHallwayObject(JSONObject h) throws JSONException {
       JSONArray initArray = h.getJSONArray("init");
       int initX = initArray.getInt(0);
       int initY = initArray.getInt(1);
       Position init = new Position(initX,initY);
        JSONArray endArray = h.getJSONArray("end");
        int endX = endArray.getInt(0);
        int endY = endArray.getInt(1);
        Position end = new Position(endX,endY);
        ArrayList<Position> waypointArray = new ArrayList<>();
        JSONArray waypoints = h.getJSONArray("waypoints");
        for (int i =0; i< waypoints.length();i++) {
            JSONArray temp = waypoints.getJSONArray(i);
            Position tempPosition = new Position(temp.getInt(0), temp.getInt(1));
            waypointArray.add(tempPosition);
        }
        Hallway singleHallway = new Hallway(init, end, waypointArray);
        hallways.add(singleHallway);
    }

    public static void readKeyObject(JSONObject k) throws JSONException {
        JSONArray keyPos = k.getJSONArray("position");
        int keyX = keyPos.getInt(0);
        int keyY = keyPos.getInt(1);
        Position keyPosition = new Position(keyX,keyY);
        if (key == null) {
            key = keyPosition;
        } else {
            throw new IllegalArgumentException("duplicate keys");
        }
    }

    public static void readExitObject(JSONObject e) throws JSONException {
        JSONArray exitPos = e.getJSONArray("position");
        int exitX = exitPos.getInt(0);
        int exitY = exitPos.getInt(1);
        Position exitPosition = new Position(exitX,exitY);
        if (exit == null) {
            exit = exitPosition;
        } else {
            throw new IllegalArgumentException("duplicate level exits");
        }
    }

     public static void readString(String s) throws JSONException {
        JSONArray array = new JSONArray(s);
        for (int i = 0; i<array.length(); i++) {
           JSONObject curr = array.getJSONObject(i);
           String name = curr.get("name").toString();
           if (name.equals("room")) {
               readRoomObject(curr);
           } else if (name.equals("hallway")) {
               readHallwayObject(curr);
           } else if (name.equals("key")) {
               readKeyObject(curr);
           } else if (name.equals("exit")) {
               readExitObject(curr);
           } else {
               throw new IllegalArgumentException("Object not valid");
           }
        }

        completelevel = new Level(rooms,hallways,key,exit);


     }



}
