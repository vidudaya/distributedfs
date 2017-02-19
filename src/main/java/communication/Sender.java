package communication;

import support.Node;

import java.io.IOException;
import java.net.*;

/**
 * Created by Samiththa on 4/11/16.
 */
public class Sender {

    private DatagramSocket socket;
    private DatagramPacket datagramPacket;
    private Node distributor;

    public Sender(Node distributor) {
        this.distributor = distributor;
    }

    public void sendMessage(String msg, String receiverIp, int receiverPort) {
        if (distributor.isDebugMode()) {
            System.out.println("Message to Send : " + msg);
        }
        try {
            InetAddress receiverAddress = InetAddress.getByName(receiverIp);
            byte[] buffer = msg.getBytes();
            socket = new DatagramSocket();
            datagramPacket = new DatagramPacket(buffer, buffer.length, receiverAddress, receiverPort);
            socket.send(datagramPacket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

}
