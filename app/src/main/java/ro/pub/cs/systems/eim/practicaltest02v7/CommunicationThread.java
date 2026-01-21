package ro.pub.cs.systems.eim.practicaltest02v7;

import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String clientIp = socket.getInetAddress().getHostAddress();
            Log.d("CommThread", "Connection from: " + clientIp);

            String line = reader.readLine();
            if (line == null || line.isEmpty()) return;

            if (line.startsWith("set")) {
                Log.d("CommThread", "Client requested set");
                try {
                    String data = line.substring(4).trim();
                    String[] parts = data.split(",");
                    int minutes = Integer.parseInt(parts[0].trim());
                    int seconds = Integer.parseInt(parts[1].trim());

                    serverThread.setAlarm(clientIp, minutes, seconds);
                } catch (Exception e) {
                    Log.e("CommThread", "Invalid set format: " + line);
                }

            } else if (line.trim().equals("reset")) {
                Log.d("CommThread", "Client requested reset");

                serverThread.resetAlarm(clientIp);

            } else if (line.trim().equals("poll")) {
                Log.d("CommThread", "Client requested poll");

                String result = serverThread.pollAlarm(clientIp);
                writer.println(result);
            }

        } catch (Exception e) {
            Log.e("CommThread", "Error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}