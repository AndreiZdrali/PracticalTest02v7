package ro.pub.cs.systems.eim.practicaltest02v7;

import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerThread extends Thread {

    private int port;
    private ServerSocket serverSocket = null;

    private final Map<String, Long> alarmMap = new HashMap<>();

    public ServerThread(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            Log.d("ServerThread", "Started on port " + port);

            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                if (socket != null) {
                    new CommunicationThread(this, socket).start();
                }
            }
        } catch (Exception e) {
            Log.e("ServerThread", "Error: " + e.getMessage());
        }
    }

    public synchronized void setAlarm(String ip, int minutes, int seconds) {
        long currentNistTime = getNistTimeSeconds();
        if (currentNistTime == -1) return;

        long targetTime = (currentNistTime / 3600) * 3600 + (minutes * 60L) + seconds;

        if (targetTime < currentNistTime) {
            targetTime += 3600;
        }

        alarmMap.put(ip, targetTime);
        Log.d("ServerThread", "Alarm set for " + ip + " at " + targetTime);
    }

    public synchronized void resetAlarm(String ip) {
        alarmMap.remove(ip);
        Log.d("ServerThread", "Alarm reset for " + ip);
    }

    public synchronized String pollAlarm(String ip) {
        if (!alarmMap.containsKey(ip)) {
            return "none\n";
        }

        long targetTime = alarmMap.get(ip);
        long currentNistTime = getNistTimeSeconds();

        if (currentNistTime == -1) return "inactive\n";

        if (currentNistTime >= targetTime) {
            return "active\n";
        } else {
            return "inactive\n";
        }
    }

    private long getNistTimeSeconds() {
        try (Socket socket = new Socket("time-a-g.nist.gov", 13);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String line = reader.readLine();
            if (line == null || line.isEmpty()) line = reader.readLine();

            if (line != null && line.length() > 25) {
                String timestampStr = line.substring(15, 23);
                String[] parts = timestampStr.split(":");
                int hours = Integer.parseInt(parts[0]);
                int mins = Integer.parseInt(parts[1]);
                int secs = Integer.parseInt(parts[2]);

                Log.d("ServerThread", "NIST returned minute " + mins + " and second " + secs);

                return (hours * 3600L) + (mins * 60L) + secs;
            }
        } catch (Exception e) {
            Log.e("ServerThread", "NIST Error: " + e.getMessage());
        }
        return -1;
    }

    public void stopServer() {
        try {
            interrupt();
            if (serverSocket != null) serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}