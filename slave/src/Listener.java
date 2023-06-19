package src;

import static src.Manager.ID;
import static src.Manager.PORT;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.io.FileReader;
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
                Manager manager = new Manager(master);
                manager.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}