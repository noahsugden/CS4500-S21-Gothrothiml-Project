import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public abstract class AdversaryClient {

    Socket client;
    BufferedReader inUser;
    DataOutputStream out;
    DataInputStream in;
    String name = "Zombie";
    ArrayList<Level> levels = new ArrayList<>();

    public AdversaryClient(String address, int port) {

    }



    public String determineJsonObject(String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        return object.getString("type");
    }

    public void respond(String json) throws Exception {
        String type = determineJsonObject(json);
        switch (type) {
            case "adversary-level":
                generateLevel(json);
                break;
            case "adversary-update":
                System.out.println(json);
                break;
        }
    }

    public void generateLevel(String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        JSONArray levelArray  = object.getJSONArray("levels");
        for (int i=0;i<levelArray.length();i++) {
            JSONObject levelObject = levelArray.getJSONObject(i);
            Level currLevel = getLevel(levelObject);
            levels.add(currLevel);
        }

    }



    public Level getLevel(JSONObject level) throws JSONException{
        ArrayList<Room> roomList = new ArrayList<>();
        ArrayList<Hallway> hallwayList = new ArrayList<>();
        JSONObject key;
        JSONObject exit;
        JSONObject temp;
        Position keyPosition = new Position(-1, -1);
        JSONArray rooms = level.getJSONArray("rooms");
        for (int i = 0; i < rooms.length(); i++) {
            Room curr = readRoomObject(rooms.getJSONObject(i));
            roomList.add(curr);
        }
        JSONArray objects = level.getJSONArray("objects");
        if(objects.length() ==1) {
            exit = objects.getJSONObject(0);
        } else {
            temp = objects.getJSONObject(0);
            if (temp.getString("type").equals("exit")) {
                exit = temp;
                key = objects.getJSONObject(1);
                JSONArray keyPos = key.getJSONArray("position");
                keyPosition = new Position(keyPos.getInt(0), keyPos.getInt(1));
            } else {
                key = temp;
                exit = objects.getJSONObject(1);
                JSONArray keyPos = key.getJSONArray("position");
                keyPosition = new Position(keyPos.getInt(0), keyPos.getInt(1));

            }
        }

        JSONArray exitPos = exit.getJSONArray("position");
        Position exitPosition = new Position(exitPos.getInt(0), exitPos.getInt(1));
        JSONArray hallways = level.getJSONArray("hallways");
        for (int i = 0; i < hallways.length(); i++) {
            Hallway curr = readHallwayObject(hallways.getJSONObject(i));
            hallwayList.add(curr);
        }


        Level l = new Level(roomList, hallwayList, keyPosition, exitPosition);

        return l;
    }

    public Room readRoomObject(JSONObject room) throws JSONException {
        ArrayList<Position> nonWallTiles = new ArrayList<>();
        ArrayList<Position> doors = new ArrayList<>();
        JSONArray originArray = room.getJSONArray("origin");
        int x = originArray.getInt(0);
        int y = originArray.getInt(1);
        Position origin = new Position(x, y);
        JSONObject bounds = room.getJSONObject("bounds");
        int rows = bounds.getInt("rows");
        int columns = bounds.getInt("columns");
        JSONArray layout = room.getJSONArray("layout");
        for (int i = 0; i < rows; i++) {
            JSONArray curr = layout.getJSONArray(i);
            for (int j = 0; j < columns; j++) {
                int temp = curr.getInt(j);
                if (temp == 1) {
                    Position tile = new Position(i + x, j + y);
                    nonWallTiles.add(tile);
                } else if (temp == 2) {
                    Position door = new Position(i + x, j + y);
                    doors.add(door);
                }

            }
        }
        Room jsonRoom = new Room(origin, rows, columns, nonWallTiles, doors);
        return jsonRoom;
    }

    public  Hallway readHallwayObject(JSONObject hallway) throws JSONException {
        JSONArray from = hallway.getJSONArray("from");
        JSONArray to = hallway.getJSONArray("to");
        JSONArray waypoints = hallway.getJSONArray("waypoints");

        Position fromPos = new Position(from.getInt(0), from.getInt(1));
        Position toPos = new Position(to.getInt(0), to.getInt(1));
        ArrayList<Position> waypointsList = new ArrayList<>();

        for (int i = 0; i < waypoints.length(); i++) {
            JSONArray currArray = waypoints.getJSONArray(i);
            Position currPos = new Position(currArray.getInt(0), currArray.getInt(1));
            waypointsList.add(currPos);
        }
        Hallway result = new Hallway(fromPos, toPos, waypointsList);
        return result;
    }

    public int[][] generateLayout(JSONArray layout) throws JSONException {
        int x = layout.length();
        int max = 0;
        for (int i=0;i<x;i++) {
            int curr = layout.getJSONArray(i).length();
            if (curr>max) {
                max =curr;
            }
        }
        int[][] ascii = new int[x][max];
        for (int i = 0; i < layout.length(); i++) {
            JSONArray curr = layout.getJSONArray(i);
            for (int j = 0; j < curr.length(); j++) {
                int temp = curr.getInt(j);
                //1 representing a traversable non-door tile
                if (temp == 1) {
                    ascii[i][j] = 2;
                    //2 representing a door
                } else if (temp == 2) {
                    ascii[i][j] = 4;
                    //everything else is a wall tile
                } else {
                    ascii[i][j] =1;
                }
            }
        }
        ascii[2][2] = 3;

        return ascii;
    }


    public void printAscii(int[][] ascii) {
        for (int[] x : ascii)
        {
            for (int y : x)
            {
                switch(y) {
                    case 1:
                        System.out.print(" * ");
                        break;
                    case 2:
                        System.out.print(" . ");
                        break;
                    case 3:
                        char[] nameArray = name.toCharArray();
                        System.out.print(" "+nameArray[0]+" ");
                        break;
                    case 4:
                        System.out.print(" < ");
                        break;
                    case 7:
                        System.out.print(" k ");
                        break;
                    case 8:
                        System.out.print(" $ ");
                        break;
                    case 11:
                        System.out.print(" z ");
                        break;
                    case 12:
                        System.out.print(" g ");
                        break;


                }
            }
            System.out.println();
        }
    }


    public String readJsonObject(DataInputStream in) throws Exception {
        Character curr = in.readChar();

        StringBuilder valid = new StringBuilder();
        while(curr != '\0') {
            valid.append(curr);
            // System.out.print(valid.toString()+"\n");
            switch(valid.toString()) {
                case "name":
                case "move":
                case "Invalid":
                case "OK":
                case "Key":
                case "Eject":
                case "Exit":
                    return valid.toString();
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

}
