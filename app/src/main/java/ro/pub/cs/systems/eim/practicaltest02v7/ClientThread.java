package ro.pub.cs.systems.eim.practicaltest02v7;

import android.util.Log;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {

    private final String address;
    private final int port;
    private final String action;
    private final String minute;
    private final String second;
    private Socket socket;

    public ClientThread(String address, int port, String action, String minute, String second) {
        this.address = address;
        this.port = port;
        this.action = action;
        this.minute = minute;
        this.second = second;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            if (action.equals("set")) {
                writer.println("set " + minute + "," + second);
            } else if (action.equals("reset")) {
                writer.println("reset");
            } else if (action.equals("poll")) {
                writer.println("poll");
            } else {
                Log.e("ClientThread", "Invaild command: " + action);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                final String text = line;
                Log.d("ClientThread", "Response from server: " + line);
            }

            socket.close();
        } catch (final Exception e) {
            Log.e("CommThread", "Error: " + e.getMessage());
        }
        finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}