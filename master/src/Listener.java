package src;

import static src.Manager.PORT;
import static src.Manager.ID;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener extends Thread {

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT[ID]);
            System.out.println("master server listening on port " + PORT[ID].toString());
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