package src;

import static src.SlaveManager.ID;
import static src.SlaveManager.PORT;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener extends Thread {

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT[ID]);
            System.out.println("server " + ID.toString() + " listening on port " + PORT[ID].toString());
            while (true) {
                Socket master = serverSocket.accept();
                SlaveManager SlaveManager = new SlaveManager(master);
                SlaveManager.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}