package app;

import support.CommonSupport;
import support.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by vidudaya on 4/11/16.
 * <p/>
 * User interact with the node itself
 */
public class DistributorInteract {

    // to view all the files in the local store
    private final String SHOW = "SHOW";
    // to view the routing table
    private final String RT = "RT";
    // to leave the network
    private final String LEAVE = "LEAVE";
    // to search a file - ex : search file_name
    private final String SEARCH = "SEARCH";
    // reduce the log messages
    private final String LOG_OFF = "LOGOFF";
    // increase the log messages
    private final String LOG_ON = "LOGON";
    // help menu for commands
    private final String HELP = "HELP";
    private final String SELECT = "SELECT";
    private final String TIMESTAMP = "TIMESTAMP";
    private Node distributorNode;
    private BufferedReader br;
    private CommonSupport commonSupport;

    public DistributorInteract(Node distributorNode) {
        this.distributorNode = distributorNode;
        this.br = new BufferedReader(new InputStreamReader(System.in));
        this.commonSupport = new CommonSupport(distributorNode);
    }

    public void listenForUserRequests() throws IOException {
        while (true) {
            String command;
            if (br.ready()) {
                command = br.readLine().trim().toLowerCase();

                if (TIMESTAMP.toLowerCase().equals(command)) {
                    System.out.println("Node timestamp : " + distributorNode.getNodeTimestamp());
                    System.out.print("\n" + distributorNode.getShell());
                } else if (SHOW.toLowerCase().equals(command)) {
                    //distributorNode.getTextStore().printFileList();
                    distributorNode.getWall().printFileList();
                    System.out.print("\n" + distributorNode.getShell());
                } else if (RT.toLowerCase().equals(command)) {
                    distributorNode.getRoutingTable().printRoutingTable();
                    System.out.print("\n" + distributorNode.getShell());
                } else if (LEAVE.toLowerCase().equals(command.toLowerCase())) {
                    distributorNode.leaveNetwork();
                    System.out.print("\n" + distributorNode.getShell());
                } else if (command.contains(SEARCH.toLowerCase())) {
                    String tokens[] = command.split(" ");
                    if (SEARCH.toLowerCase().equals(tokens[0].toLowerCase().trim())) {
                        String fileNameToSearch = tokens[1];
                        distributorNode.searchFile(fileNameToSearch);
                    }
                    System.out.print("\n" + distributorNode.getShell());
                } else if (LOG_OFF.toLowerCase().equals(command)) {
                    distributorNode.setIsDebugMode(false);
                    System.out.println(distributorNode.getShell()
                            .concat("logging messages reduced"));
                    System.out.print("\n" + distributorNode.getShell());
                } else if (LOG_ON.toLowerCase().equals(command)) {
                    distributorNode.setIsDebugMode(true);
                    System.out.println(distributorNode.getShell()
                            .concat("logging messages increased"));
                    System.out.print("\n" + distributorNode.getShell());
                } else if (HELP.equalsIgnoreCase(command)) {
                    System.out.println("'show'\t:\twill list down the file name list");
                    System.out.println("'rt'\t:\twill list down the routing table of that node");
                    System.out.println("'search <keyword>'\t:\twill search for a file with the keyword");
                    System.out.println("'logoff\t:\twill turn off the debug mode");
                    System.out.println("'logon'\t:\twill turn on the debug mode");
                    System.out.println("'leave'\t:\twill disconnect the node from the network");
                    System.out.println("'select'\t:\twill allow the user to select a post to expand");
                    System.out.println("'help'\t:\tHelp menu");
                    System.out.print("\n" + distributorNode.getShell());
                } else if (command.startsWith("select")) {
                    String tokens[] = command.split(" ");
                    String postID = tokens[1];
                    if (distributorNode.getWall().getFiles().containsKey(postID)) {
                        System.out.println(distributorNode.getWall().getFiles().get(postID));
                        System.out.println("#############################");
                        System.out.println("to comment use comment <id> <comment>");
                        System.out.println("to rank use rank <id> <rank between 1-5>");
                        System.out.println("#############################");
                    } else {
                        System.out.println("No file found for the given ID");
                    }
                    System.out.print("\n" + distributorNode.getShell());
                } else if (command.startsWith("comment")) {
                    String tokens[] = command.split(" ");
                    String msg = commonSupport.getStringFromArray(Arrays.copyOfRange(tokens, 2, tokens.length));

                    distributorNode.commentOnPostItem(tokens[1], msg);
                    System.out.print("\n" + distributorNode.getShell());
                } else if (command.startsWith("rank")) {
                    String tokens[] = command.split(" ");
                    String postID = tokens[1];
                    String rank = tokens[2];

                    distributorNode.rankPostItem(postID, rank);
                    System.out.print("\n" + distributorNode.getShell());
                }
            }
        }
    }
}
