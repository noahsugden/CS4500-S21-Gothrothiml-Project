import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ZombieClient extends AdversaryClient {
    Socket client;
    BufferedReader inUser;
    DataOutputStream out;
    DataInputStream in;
    String zombieCode = "1334624065";


    public ZombieClient(String address, int port) throws Exception {
        super(address,port);
        try {
            client = new Socket(address, port);

            in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            inUser = new BufferedReader(new InputStreamReader(System.in));
            out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));

            client.setKeepAlive(true);
            System.out.println("Connected to server...");
        } catch (Exception e) {
            if (e instanceof SocketException) {
                System.out.print("The server has disconnected");
            } else {
                e.printStackTrace();
            }
        }
        try {
            //reads the welcome json from the server
            String welcomeJson = readJsonObject(in);
            JSONObject welcome = new JSONObject(welcomeJson);
            String info = welcome.getString("info");
            System.out.print("Welcome!"+"\n");
            System.out.print(info +"\n");
            String next = readJsonObject(in);
            while (next.equals("name")) {
                out.writeChars(zombieCode);
                out.flush();
                next = readJsonObject(in);
            }
            respond(next);
        }


        catch (Exception e) {
            if (e instanceof SocketException) {
                System.out.print("The server has disconnected");
            } else {
                e.printStackTrace();
            }
        }
    }






    public static void main(String[] args) throws Exception {
        int port = 45678;
        String address = "127.0.0.1";
        for(int i = 0; i < args.length; i++) {
            String argument  = args[i];
            switch(argument) {
                case "--address":
                    address = args[i + 1];
                    break;
                case "--port":
                    port = Integer.parseInt(args[i + 1]);
                    break;
            }
        }
        ZombieClient client = new ZombieClient(address, port);

    }
}
