package communication;

import support.NeighbourNode;
import support.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by vidudaya on 4/11/16.
 */
public class BSCommunicationManager {
    private final String REGOK = "REGOK";
    private final String UNROK = "UNROK";
    PrintWriter writer;
    BufferedReader br;
    Socket socketToServer;
    private Node distributor;

    public BSCommunicationManager(Node distributor) {
        this.distributor = distributor;
    }

    public String sendMessageToBS(String message, String serverIp, int serverPort) throws IOException {
        //String message = "xxxx REG localhost 20000 sam";
        String responseFromBS = "";

        try {
            socketToServer = new Socket(serverIp, serverPort);
            writer = new PrintWriter(socketToServer.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(socketToServer.getInputStream()));
            writer.println(message);

            char[] charBuf = new char[1000];
            br.read(charBuf);
            responseFromBS = "";
            int i = 0;
            char c = '\u0000';
            while (charBuf[i] != c) {
                responseFromBS += charBuf[i];
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
            br.close();
            socketToServer.close();
        }
        //System.out.println("responseFromBS : " + responseFromBS);

        return responseFromBS;
    }

    public ArrayList<NeighbourNode> handleBSResponse(String response) {

        System.out.println("response : " + response);

        String tokens[] = response.split(" ");
        String cmd = "";
        int nodeCount = 0;

        if (tokens.length > 1) {
            cmd = tokens[1].trim();
        }

        if (REGOK.equals(cmd)) {
            //0051 REGOK 2 129.82.123.45 5001 name1 64.12.123.190 34001 name2
            try {
                nodeCount = Integer.parseInt(tokens[2].trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            System.out.println("Communication with the BS is a success and response received with REGOK");
            System.out.println("response : " + response);

            if (nodeCount == 0) {
                System.out.println("First node to join the network through BS");
            } else if (nodeCount == 9996) {
                System.out.println("Error : failed, can’t register. BS full");
            } else if (nodeCount == 9997) {
                System.out.println("Error : failed, registered to another user, try a different IP and port");
            } else if (nodeCount == 9998) {
                System.out.println("Error : failed, already registered to you, unregister first");
            } else if (nodeCount == 9999) {
                System.out.println("Error : failed, there is some error in the command");
            } else {
                System.out.println("Details of " + nodeCount + " nodes received");
                ArrayList<NeighbourNode> nodeList = new ArrayList<NeighbourNode>();
                int cur = 3;
                for (int i = 0; i < nodeCount; ++i) {
                    String ip = tokens[cur++];
                    String port = tokens[cur++];
                    String name = tokens[cur++];
                    Integer portNum;
                    try {
                        portNum = Integer.parseInt(port);
                        NeighbourNode node = new NeighbourNode(ip, portNum, name);
                        nodeList.add(node);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                return nodeList;
            }
        } else if (UNROK.equals(cmd)) {
            //0012 UNROK 0
            System.out.println("Communication with the BS is a success and response received with UNROK");
            System.out.println("response : " + response);
            try {
                int res = Integer.parseInt(tokens[2].trim());
                if (res == 0) {
                    return distributor.getRoutingTable().getAllNeighbours();
                } else {
                    return null;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
