package src;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import src.messages.Message;
import src.messages.ShuffleReady;

public class Sender extends Thread {
    private String IP;
    private Message m;
    private Integer port;

    public Sender(String IP, Integer port, Message m) {
        this.IP = IP;
        this.port = port;
        this.m = m;
    }

    public void run() {
        try {
            InetAddress addr = InetAddress.getByName(this.IP);
            Socket slave = new Socket(addr, this.port);
            ObjectOutputStream out = new ObjectOutputStream(slave.getOutputStream());
            out.writeObject(this.m);
            out.close();
            slave.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
