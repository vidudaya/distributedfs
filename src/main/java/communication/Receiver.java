package communication;

import support.Node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by vidudaya on 4/11/16.
 */
public class Receiver extends Thread {
    private final int BUFFER_SIZE = 1024;
    DatagramSocket socket;
    private Node node;

    public Receiver(Node node) throws IOException {
        this.node = node;
        socket = new DatagramSocket(this.node.getPort());
        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] buffer = new byte[BUFFER_SIZE];

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                if (node.isDebugMode()) {
                    System.out.println("Received Message : " + message);
                }
                node.processReceivedMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
