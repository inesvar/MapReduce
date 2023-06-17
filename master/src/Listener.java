package src;

import static src.Manager.MASTER_PORT;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener extends Thread {

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(MASTER_PORT);
            System.out.println("master server listening on port " + MASTER_PORT.toString());
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("master received a message :");
                Manager process = new Manager(socket);
                process.start();
            }
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }
}