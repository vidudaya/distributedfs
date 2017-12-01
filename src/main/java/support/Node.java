package support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final String UPDATE = "UPDATE";
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
    private boolean isDebugMode;
    private String shell;
    private Wall wall;
    private Integer nodeTimestamp;

    public Node(String ip, Integer port, String nodeIdentifier) {
        this.ip = ip;
        this.port = port;
        this.nodeIdentifier = nodeIdentifier;
        this.sender = new Sender(this);
        this.commonSupport = new CommonSupport(this);
        this.routingTable = new RoutingTable();
        this.requestCache = new RequestCache();
        this.neighbourCommunicationManager = new NeighbourCommunicationManager(sender, this);
        try {
            this.textStore = new TextStore(this.commonSupport.getRandomFileList());
            this.wall = new Wall(this.commonSupport.getRandomFilePostMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.bsCommunicationManager = new BSCommunicationManager(this);
        this.fileSearch = new FileSearch(this);
        this.isDebugMode = true;
        this.shell = nodeIdentifier.concat(" > ");
        this.nodeTimestamp = 1;
    }

    public void registerNodeWithBS(String serverIP, int serverPort) {
        String message = REGISTER.concat(BLANK).concat(ip).concat(BLANK).concat(String.valueOf(port))
                .concat(BLANK).concat(nodeIdentifier);
        int length = message.length() + PAD + 1;
        String formattedLength = commonSupport.getFormattedNumber(length, PAD);
        String messageToSend = formattedLength.concat(BLANK).concat(message);
        if (isDebugMode) {
            System.out.println("messageToSend : " + messageToSend);
        }
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
        if (isDebugMode) {
            System.out.println("UNREG : " + messageToSend);
        }
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

    public boolean isDebugMode() {
        return isDebugMode;
    }

    public void setIsDebugMode(boolean isDebugMode) {
        this.isDebugMode = isDebugMode;
    }

    public String getShell() {
        return shell;
    }

    public Wall getWall() {
        return wall;
    }

    public void setWall(Wall wall) {
        this.wall = wall;
    }

    public void commentOnPostItem(String id, String msg) {
        Comment comment = new Comment(msg, this, incrementTimestamp());
        for (FilePost fp : wall.getFiles().values()) {
            if (fp.getId().equals(id)) {
                fp.addComment(comment);
                distributeTheUpdate(id);
                return;
            } else {
                for (Comment com : fp.getComments()) {
                    // recursively select the matching comment
                    Comment comSelected = com.getCommentWithId(id);
                    if (comSelected != null) {
                        comSelected.addComment(comment);
                        distributeTheUpdate(fp.getId());
                        return;
                    }
                }
            }
        }
        System.out.println("Comment failed - No match for the entered id");
    }

    public void rankPostItem(String id, String rating) {
        for (FilePost fp : wall.getFiles().values()) {
            if (fp.getId().equals(id)) {
                fp.addRank(Integer.valueOf(rating), this.nodeIdentifier, incrementTimestamp());
                distributeTheUpdate(id);
                return;
            } else {
                for (Comment com : fp.getComments()) {
                    // recursively select the matching comment
                    Comment comSelected = com.getCommentWithId(id);
                    if (comSelected != null) {
                        comSelected.addRank(Integer.valueOf(rating), this.nodeIdentifier, incrementTimestamp());
                        distributeTheUpdate(fp.getId());
                        return;
                    }
                }
            }
        }
        System.out.println("Ratings failed - No match for the entered id");
    }

    /**
     * gossiping the update
     *
     * @param id
     */
    public void distributeTheUpdate(String id) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonVal = mapper.writeValueAsString(wall.getFiles().get(id));
            String uniqueId = commonSupport.getUniqueId();
            getRequestCache().addToCache(uniqueId);
            String messageToSend = commonSupport.generateMessageToSend(UPDATE
                    , getIp()
                    , String.valueOf(getPort())
                    , getNodeIdentifier()
                    , String.valueOf(1)
                    , String.valueOf(incrementTimestamp())
                    , uniqueId
                    , ("[[").concat(jsonVal).concat("]]"));
            for (NeighbourNode neighbour : getRoutingTable().getRandomNeighbours(2)) {
                sender.sendMessage(messageToSend, neighbour.getIp(), neighbour.getPort());
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public Integer getNodeTimestamp() {
        return nodeTimestamp;
    }

    public void setNodeTimestamp(Integer timestamp) {
        this.nodeTimestamp = Math.max(nodeTimestamp, timestamp) + 1;
    }

    public Integer incrementTimestamp() {
        return nodeTimestamp++;
    }
}
