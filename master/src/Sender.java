package src;

import static src.MasterManager.PORT;
import static src.MasterManager.IP;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import src.messages.Message;

public class Sender extends Thread {
    private Message m;
    private int id;

    public Sender(Integer id, Message m) {
        this.m = m;
        this.id = id;
    }

    public void run() {
        try {
            InetAddress addr = InetAddress.getByName(IP[id]);
            Socket slave = new Socket(addr, PORT[id]);
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
