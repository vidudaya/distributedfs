package app;

import communication.Receiver;
import support.Node;

import java.io.IOException;

/**
 * Created by vidudaya on 3/11/16.
 */
public class Distributor {

    //java -jar distributedfs-1.0-SNAPSHOT-jar-with-dependencies.jar 127.0.0.1 2000 vid12345 127.0.0.1 20000
    //java -jar distributedfs-1.0-SNAPSHOT-jar-with-dependencies.jar 127.0.0.1 2001 sam12345 127.0.0.1 20000

    private Receiver receiver;

    public static void main(String[] args) {
        new Distributor().setUpDistributor(args);
    }

    public void setUpDistributor(String[] args) {
        //java -jar distributedfs-1.0-SNAPSHOT.jar 127.0.0.1 2000 vid12345 127.0.0.1 20000
        //java -jar distributedfs-1.0-SNAPSHOT.jar 127.0.0.1 2001 sam12345 127.0.0.1 20000
        //java -jar distributedfs-1.0-SNAPSHOT.jar 127.0.0.1 2002 las12345 127.0.0.1 20000
        //java -jar distributedfs-1.0-SNAPSHOT.jar 127.0.0.1 2003 ron12345 127.0.0.1 20000
        //java -jar distributedfs-1.0-SNAPSHOT.jar 127.0.0.1 2004 jim12345 127.0.0.1 20000
        String distributorNodeIp = getNodeIp(args);
        Integer distributorNodePort = getNodePort(args);
        String distributorNodeUsername = getDistributorName(args);
        String serverIP = getBootstrapServerIp(args);
        Integer serverPort = getServerPort(args);

        System.out.println("distributorNodeIp : " + distributorNodeIp + "| distributorNodePort : " + distributorNodePort
                + "| distributorNodeUsername : " + distributorNodeUsername + "| serverIP : " + serverIP
                + "| serverPort : " + serverPort);

        // creating a new Node with [ip|port|name]
        Node node = new Node(distributorNodeIp, distributorNodePort, distributorNodeUsername);
        try {
            // this is for listening and handling the messages from other nodes
            // the current node will listen to it's specified port all the time
            receiver = new Receiver(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
        node.registerNodeWithBS(serverIP, serverPort);
        try {
            // once the node is up and running the users can execute commands on that node
            // this will capture those inputs and handle them
            new DistributorInteract(node).listenForUserRequests();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getNodeIp(String[] args) {
        return args[0].trim();
    }

    public String getBootstrapServerIp(String[] args) {
        return args[3].trim();
    }

    public Integer getNodePort(String[] args) {
        Integer port = 0;
        try {
            port = Integer.parseInt(args[1].trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return port;
    }

    public Integer getServerPort(String[] args) {
        Integer port = 0;
        try {
            port = Integer.parseInt(args[4].trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return port;
    }

    public String getDistributorName(String[] args) {
        return args[2].trim();
    }
}
