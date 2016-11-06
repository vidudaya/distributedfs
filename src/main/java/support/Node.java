package support;

import communication.BSCommunicationManager;
import communication.NeighbourCommunicationManager;
import communication.Sender;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vidudaya on 3/11/16.
 */
public class Node {

    private final String BLANK = " ";
    private final String REGISTER = "REG";
    private final String UNREG = "UNREG";
    private final int PAD = 4;
    private String ip;
    private Integer port;
    private String nodeIdentifier;
    private Sender sender;
    private BSCommunicationManager bsCommunicationManager;
    private CommonSupport commonSupport;
    private TextStore textStore;
    private RoutingTable routingTable;
    private RequestCache requestCache;
    private NeighbourCommunicationManager neighbourCommunicationManager;
    private String serverIp;
    private Integer serverPort;
    private FileSearch fileSearch;

    public Node(String ip, Integer port, String nodeIdentifier) {
        this.ip = ip;
        this.port = port;
        this.nodeIdentifier = nodeIdentifier;
        this.sender = new Sender();
        this.commonSupport = new CommonSupport();
        this.routingTable = new RoutingTable();
        this.requestCache = new RequestCache();
        this.neighbourCommunicationManager = new NeighbourCommunicationManager(sender);
        try {
            this.textStore = new TextStore(this.commonSupport.getRandomFileList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.bsCommunicationManager = new BSCommunicationManager(this);
        this.fileSearch = new FileSearch(this);
    }

    public void registerNodeWithBS(String serverIP, int serverPort) {
        String message = REGISTER.concat(BLANK).concat(ip).concat(BLANK).concat(String.valueOf(port))
                .concat(BLANK).concat(nodeIdentifier);
        int length = message.length() + PAD + 1;
        String formattedLength = commonSupport.getFormattedNumber(length, PAD);
        String messageToSend = formattedLength.concat(BLANK).concat(message);
        System.out.println("messageToSend : " + messageToSend);
        String response;

        try {
            response = bsCommunicationManager.sendMessageToBS(messageToSend, serverIP, serverPort);
            ArrayList<NeighbourNode> nodeList = bsCommunicationManager.handleBSResponse(response);
            ArrayList<NeighbourNode> selectedNodes = routingTable.addRandomTwo(nodeList);
            neighbourCommunicationManager.joinWithInitialNeighbours(selectedNodes, this);
            this.serverIp = serverIP;
            this.serverPort = serverPort;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processReceivedMessage(String msg) {
        neighbourCommunicationManager.processReceivedMessage(msg, this);
    }

    public void leaveNetwork() throws IOException {
        //0028 UNREG 64.12.123.190 432
        String messageToSend = commonSupport.generateMessageToSend(UNREG
                , this.ip
                , String.valueOf(this.port)
                , this.getNodeIdentifier());
        System.out.println("UNREG : " + messageToSend);
        String response = bsCommunicationManager.sendMessageToBS(messageToSend, serverIp, serverPort).trim();
        if (response != null || !response.isEmpty()) {
            ArrayList<NeighbourNode> nodesInRT = bsCommunicationManager.handleBSResponse(response);
            if (nodesInRT != null) {
                neighbourCommunicationManager.leaveFromNetwork(nodesInRT, this);
            } else {
                System.out.println("Error while leaving BS");
            }
        } else {
            System.out.println("Invalid response from BS for LEAVE request");
        }

    }

    public void searchFile(String fileName) {
        fileSearch.searchForFiles(fileName);
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNodeIdentifier() {
        return nodeIdentifier;
    }

    public void setNodeIdentifier(String nodeIdentifier) {
        this.nodeIdentifier = nodeIdentifier;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public BSCommunicationManager getBsCommunicationManager() {
        return bsCommunicationManager;
    }

    public void setBsCommunicationManager(BSCommunicationManager bsCommunicationManager) {
        this.bsCommunicationManager = bsCommunicationManager;
    }

    public TextStore getTextStore() {
        return textStore;
    }

    public void setTextStore(TextStore textStore) {
        this.textStore = textStore;
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    public void setRoutingTable(RoutingTable routingTable) {
        this.routingTable = routingTable;
    }

    public RequestCache getRequestCache() {
        return requestCache;
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }

    public NeighbourCommunicationManager getNeighbourCommunicationManager() {
        return neighbourCommunicationManager;
    }
}
