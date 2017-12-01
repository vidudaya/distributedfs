package communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import support.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vidudaya on 4/11/16.
 */
public class NeighbourCommunicationManager {

    private final String JOIN = "JOIN";
    private final String JOINOK = "JOINOK";
    private final String LEAVE = "LEAVE";
    private final String LEAVEOK = "LEAVEOK";
    private final String SEARCH = "SER";
    private final String SEARCHOK = "SEROK";
    private final String BLANK = " ";
    private final String UPDATE = "UPDATE";
    private Sender sender;
    private CommonSupport commonSupport;
    private Node node;

    public NeighbourCommunicationManager(Sender sender, Node node) {
        this.sender = sender;
        this.commonSupport = new CommonSupport(node);
        this.node = node;
    }

    public void searchFileInNetwork(String fileName, Node distributorNode) {
        System.out.println(distributorNode.getShell()
                .concat("Searching in network..."));

        String uniqueId = commonSupport.getUniqueId();
        //length SER IP port name file_name hops req_id
        String messageToSend = commonSupport.generateMessageToSend(SEARCH
                , distributorNode.getIp()
                , String.valueOf(distributorNode.getPort())
                , distributorNode.getNodeIdentifier()
                , fileName
                , "1"
                , uniqueId
                , String.valueOf(node.incrementTimestamp()));

        distributorNode.getRequestCache().addToCache(uniqueId);

        for (NeighbourNode neighbour : distributorNode.getRoutingTable().getAllNeighbours()) {
            sender.sendMessage(messageToSend, neighbour.getIp(), neighbour.getPort());
        }
    }

    public void joinWithInitialNeighbours(ArrayList<NeighbourNode> selectedNodes, Node distributorNode) {
        for (NeighbourNode node : selectedNodes) {
            joinWithNeighbour(node, distributorNode);
        }
    }

    public void leaveFromNetwork(ArrayList<NeighbourNode> neighbours, Node distributorNode) {
        for (NeighbourNode neighbour : neighbours) {
            leaveNeighbour(neighbour, distributorNode);
            System.out.println(distributorNode.getShell()
                    .concat("LEAVE message send to " + neighbour.getNodeIdentifier()));
        }
    }

    public void leaveNeighbour(NeighbourNode neighbour, Node node) {
        //length LEAVE IP_address port_no name
        String messageToSend = commonSupport.generateMessageToSend(LEAVE, node.getIp()
                , String.valueOf(node.getPort()), node.getNodeIdentifier());
        sender.sendMessage(messageToSend, neighbour.getIp(), neighbour.getPort());
    }

    public void joinWithNeighbour(NeighbourNode neighbour, Node node) {
        //length JOIN IP_address port_no name
        String message = JOIN.concat(BLANK).concat(node.getIp()).concat(BLANK).concat(String.valueOf(node.getPort()))
                .concat(BLANK).concat(node.getNodeIdentifier()).concat(BLANK).concat(String.valueOf(node.incrementTimestamp()));
        int length = message.length() + 5;
        String messageToSend = commonSupport.getFormattedNumber(length, 4).concat(BLANK).concat(message);
        sender.sendMessage(messageToSend, neighbour.getIp(), neighbour.getPort());
    }

    public void forwardSearchMessageToRoutingTable(String msg, Node distributorNode) {
        for (NeighbourNode neighbour : distributorNode.getRoutingTable().getAllNeighbours()) {
            sender.sendMessage(msg, neighbour.getIp(), neighbour.getPort());
        }
    }

    public void processReceivedMessage(String msg, Node distributorNode) {
        //length JOIN IP_address port_no name
        //length JOINOK value
        String tokens[] = msg.trim().split(" ");
        String cmd = tokens[1].trim();

        if (JOIN.equals(cmd)) {
            String ip = tokens[2].trim();
            String port = tokens[3].trim();
            String name = tokens[4].trim();
            String timestamp = tokens[5].trim();

            try {
                Integer portNum = Integer.parseInt(port);
                NeighbourNode newNode = new NeighbourNode(ip, portNum, name);
                boolean success = distributorNode.getRoutingTable().addToRoutingTable(newNode);

                Integer timestampInt = Integer.parseInt(timestamp);
                node.setNodeTimestamp(timestampInt);
                if (success) {
                    // send JOINOK with 0
                    String message = JOINOK.concat(BLANK).concat("0").concat(BLANK).concat(String.valueOf(node.incrementTimestamp()));
                    int length = message.length() + 5;
                    String messageToSend = commonSupport.getFormattedNumber(length, 4).concat(BLANK).concat(message);
                    sender.sendMessage(messageToSend, newNode.getIp(), newNode.getPort());
                } else {
                    //send JOINOK with 9999
                    String message = JOINOK.concat(BLANK).concat("9999");
                    int length = message.length() + 5;
                    String messageToSend = commonSupport.getFormattedNumber(length, 4).concat(BLANK).concat(message);
                    sender.sendMessage(messageToSend, newNode.getIp(), newNode.getPort());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (LEAVE.equals(cmd)) {
            //length LEAVE IP_address port_no name
            //length LEAVEOK value
            //0028 LEAVE 64.12.123.190 432 name
            String ip = tokens[2].trim();
            String port = tokens[3].trim();
            String name = tokens[4].trim();

            try {
                Integer portNum = Integer.parseInt(port);
                NeighbourNode node = new NeighbourNode(ip, portNum, name);
                boolean success = distributorNode.getRoutingTable().removeFromRoutingTable(node);

                String messageToSend = "";
                if (success) {
                    System.out.println(distributorNode.getShell()
                            .concat("[ Node " + name + " left the network ]"));
                    messageToSend = commonSupport.generateMessageToSend(LEAVEOK, "0");
                } else {
                    messageToSend = commonSupport.generateMessageToSend(LEAVEOK, "9999");
                }
                sender.sendMessage(messageToSend, node.getIp(), node.getPort());

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        } else if (JOINOK.equals(cmd)) {
            if (distributorNode.isDebugMode()) {
                if ("0".equals(tokens[2])) {
                    String timestamp = tokens[3].trim();
                    Integer timestampInt = Integer.parseInt(timestamp);
                    node.setNodeTimestamp(timestampInt);

                    System.out.println(distributorNode.getShell()
                            .concat("Join Success"));
                } else if ("9999".equals(tokens[2])) {
                    System.out.println("Join Failed");
                } else {
                    System.out.println("Bad JOINOK response");
                }
            }
        } else if (LEAVEOK.equals(cmd)) {
            if (distributorNode.isDebugMode()) {
                if ("0".equals(tokens[2])) {
                    System.out.println(distributorNode.getShell()
                            .concat("LEAVE Success with a Node"));
                } else if ("9999".equals(tokens[2])) {
                    System.out.println("LEAVE Failed with a Node");
                } else {
                    System.out.println("Bad LEAVEOK response");
                }
            }
        } else if (SEARCH.equals(cmd)) {
            //length SER IP port name file_name hops req_id
            String ip = tokens[2].trim();
            String port = tokens[3].trim();
            String name = tokens[4].trim();
            String fileName = tokens[5].trim();
            String hops = tokens[6].trim();
            String reqId = tokens[7].trim();
            String timestamp = tokens[8].trim();

            int hopsCount = 0;
            int portNum = 0;
            try {
                Integer timestampInt = Integer.parseInt(timestamp);
                node.setNodeTimestamp(timestampInt);

                hopsCount = Integer.parseInt(hops) + 1;
                portNum = Integer.parseInt(port);

                // Add the requester to the RT
                NeighbourNode node = new NeighbourNode(ip, portNum, name);
                distributorNode.getRoutingTable().addToRoutingTable(node);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            if (distributorNode.isDebugMode()) {
                System.out.println("Search request for [ " + fileName + " ] from " + name);
            }
            boolean isDuplicateReq = distributorNode.getRequestCache().isPossibleDuplicate(reqId);
            if (!isDuplicateReq) {
                distributorNode.getRequestCache().addToCache(reqId);
                //ArrayList<String> localStore = distributorNode.getTextStore().returnAllPartialMatches(fileName);
                ArrayList<FilePost> fpStore = distributorNode.getWall().returnAllPartialMatches(fileName);

                try {
                    if (fpStore.size() > 0) {
                        //length SEROK no_files IP port name hops filename1 filename2
                        String messageToSend = commonSupport.generateMessageToSend(SEARCHOK
                                , String.valueOf(fpStore.size())
                                , distributorNode.getIp()
                                , String.valueOf(distributorNode.getPort())
                                , distributorNode.getNodeIdentifier()
                                , String.valueOf(hopsCount)
                                , String.valueOf(node.incrementTimestamp())
                                , commonSupport.getCombinedStringOfFilePostMatches(fpStore));

                        if (distributorNode.isDebugMode()) {
                            System.out.println("Local Search OK : [ " + commonSupport.getCombinedStringOfFilePostMatches(fpStore) + " ]");
                        }
                        // reply to the request
                        sender.sendMessage(messageToSend, ip, portNum);
                    } else {
                        if (distributorNode.isDebugMode()) {
                            System.out.println("No local results found");
                        }
                    }

                    if (hopsCount < 10) {
                        // Forward the request to RT
                        // we need to forward the request as flooding, then without anonymity forward the result
                        String messageToSend = commonSupport.generateMessageToSend(SEARCH
                                , ip
                                , port
                                , name
                                , fileName
                                , String.valueOf(hopsCount)
                                , reqId
                                , String.valueOf(node.incrementTimestamp()));
                        forwardSearchMessageToRoutingTable(messageToSend, distributorNode);
                    } else {
                        if (distributorNode.isDebugMode()) {
                            System.out.println("Maximum hops count reached - message dropped");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                if (distributorNode.isDebugMode()) {
                    System.out.println("Duplicate Request");
                }
                // duplicate request - avoid processing
            }
        } else if (SEARCHOK.equals(cmd)) {
            String count = tokens[2].trim();
            String ip = tokens[3].trim();
            String port = tokens[4].trim();
            String name = tokens[5].trim();
            String hops = tokens[6].trim();
            String timestamp = tokens[7].trim();
            int countOfFiles = 0;
            int hopsCount = 0;
            int portNum = 0;
            int pointer = 7;

            try {
                Integer timestampInt = Integer.parseInt(timestamp);
                node.setNodeTimestamp(timestampInt);

                countOfFiles = Integer.parseInt(count);
                hopsCount = Integer.parseInt(hops) + 1;
                portNum = Integer.parseInt(port);

                // add the responder to the routing table
                // RT can get larger by this, since non-neighbours can be added also
                NeighbourNode node = new NeighbourNode(ip, portNum, name);
                distributorNode.getRoutingTable().addToRoutingTable(node);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            System.out.println(("\n").concat(distributorNode.getShell())
                    .concat("[ Total of " + countOfFiles + " matching files found in " + name + " ]"));

            ObjectMapper mapper = new ObjectMapper();
            ArrayList<FilePost> fpList = new ArrayList<FilePost>();
            Matcher m = Pattern.compile("\\[\\[(.*?)\\]\\]").matcher(msg);
            while (m.find()) {
                //System.out.println(m.group(1));
                FilePost post = null;
                try {
                    post = mapper.readValue(m.group(1), FilePost.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fpList.add(post);
                //System.out.println(post);
                //System.out.println("fpList size : " + fpList.size());
            }
            mergePosts(fpList);

            //System.out.print(distributorNode.getShell());
        } else if (UPDATE.equals(cmd)) {
            String ip = tokens[2].trim();
            String port = tokens[3].trim();
            String name = tokens[4].trim();
            String hops = tokens[5].trim();
            String timestamp = tokens[6].trim();
            String reqId = tokens[7].trim();
            int hopsCount = 0;
            int portNum = 0;

            boolean isDuplicateReq = distributorNode.getRequestCache().isPossibleDuplicate(reqId);
            if (!isDuplicateReq) {
                try {
                    Integer timestampInt = Integer.parseInt(timestamp);
                    node.setNodeTimestamp(timestampInt);

                    hopsCount = Integer.parseInt(hops) + 1;
                    portNum = Integer.parseInt(port);

                    // RT can get larger by this, since non-neighbours can be added also
                    NeighbourNode node = new NeighbourNode(ip, portNum, name);
                    distributorNode.getRoutingTable().addToRoutingTable(node);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                ObjectMapper mapper = new ObjectMapper();
                ArrayList<FilePost> fpList = new ArrayList<FilePost>();
                // consider the whole msg
                Matcher m = Pattern.compile("\\[\\[(.*?)\\]\\]").matcher(msg);
                while (m.find()) {
                    FilePost post = null;
                    try {
                        post = mapper.readValue(m.group(1), FilePost.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fpList.add(post);
                }
                mergePosts(fpList);

                if (hopsCount < 10) {
                    try {
                        String jsonVal = mapper.writeValueAsString(fpList.get(0));
                        // Forward the request to RT
                        // we need to forward the request as flooding, then without anonymity forward the result
                        String messageToSend = commonSupport.generateMessageToSend(UPDATE
                                , ip
                                , port
                                , name
                                , String.valueOf(hopsCount)
                                , String.valueOf(node.incrementTimestamp())
                                , reqId
                                , ("[[").concat(jsonVal).concat("]]"));
                        for (NeighbourNode neighbour : node.getRoutingTable().getRandomNeighbours(2)) {
                            sender.sendMessage(messageToSend, neighbour.getIp(), neighbour.getPort());
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
            //System.out.print(distributorNode.getShell());
        }

    }

    /**
     * When SEARCHOK the received entries need to be merged with the Wall
     * Comments and the Ranks should be merged
     * <p/>
     * If new FilePost arrives then can safely add that to the Wall without the need for merging
     *
     * @param fpList
     */
    public void mergePosts(ArrayList<FilePost> fpList) {
        Map<String, FilePost> postsMap = node.getWall().getFiles();
        for (FilePost receivedFp : fpList) {
            if (postsMap.containsKey(receivedFp.getId())) {
                FilePost existingFp = postsMap.get(receivedFp.getId());
                // merge the ranks and comments
                mergeRanks(existingFp, receivedFp);
                mergeComments(existingFp, receivedFp);
            } else {
                // new post
                node.getWall().addToWall(receivedFp, receivedFp.getId());
            }
        }
    }

    /**
     * This method will merge the comments in the FilePost
     * New comments can be added directly
     *
     * @param existingFp
     * @param receivedFp
     */
    public void mergeComments(FilePost existingFp, FilePost receivedFp) {
        List<Comment> existingComments = existingFp.getComments();
        List<Comment> receivedComments = receivedFp.getComments();

        for (Comment receivedComment : receivedComments) {
            if (existingComments.contains(receivedComment)) {
                Comment existingComment = existingComments.get(existingComments.indexOf(receivedComment));

                // since the comment is already in the list, it should be merged
                mergeRanks(existingComment, receivedComment);
                mergeComments(existingComment, receivedComment);
            } else {
                existingComments.add(receivedComment);
            }
        }
    }

    /**
     * This method will merge the comments in a Comment
     * Since the comments can contain comments, the merging should be handled in a recursive manner
     *
     * @param existingComment
     * @param receivedComment
     */
    public void mergeComments(Comment existingComment, Comment receivedComment) {
        if (existingComment.getComments().isEmpty()) {
            if (receivedComment.getComments().isEmpty()) {
                // ignore
            } else {
                existingComment.setComments(receivedComment.getComments());
            }
        } else {
            if (receivedComment.getComments().isEmpty()) {
                // ignore
            } else {
                mergeReplyComments(existingComment, receivedComment);
            }
        }
    }

    /**
     * This method will merge the reply comments recursively
     *
     * @param existingComment
     * @param receivedComment
     */
    private void mergeReplyComments(Comment existingComment, Comment receivedComment) {
        List<Comment> existingReplyComments = existingComment.getComments();
        List<Comment> receivedReplyComments = receivedComment.getComments();

        for (Comment receivedReplyComment : receivedReplyComments) {
            if (existingReplyComments.contains(receivedReplyComment)) {
                Comment existingReplyComment = existingReplyComments.get(existingReplyComments.indexOf(receivedReplyComment));

                mergeRanks(existingReplyComment, receivedReplyComment);
                mergeComments(existingReplyComment, receivedReplyComment);
            } else {
                existingReplyComments.add(receivedReplyComment);
            }
        }
    }

    /**
     * Merge the Ranks of FilePosts
     *
     * @param existingFp
     * @param receivedFp
     */
    private void mergeRanks(FilePost existingFp, FilePost receivedFp) {
        Set<Rank> existingFpRanks = existingFp.getRanks();
        Set<Rank> receivedFpRanks = receivedFp.getRanks();
        existingFpRanks.addAll(receivedFpRanks);
    }

    /**
     * Merge the Ranks of Comments
     *
     * @param existingComment
     * @param receivedComment
     */
    private void mergeRanks(Comment existingComment, Comment receivedComment) {
        Set<Rank> existingCommentRanks = existingComment.getRanks();
        Set<Rank> receivedCommentRanks = receivedComment.getRanks();
        existingCommentRanks.addAll(receivedCommentRanks);
    }
}
