package app;

import support.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by vidudaya on 4/11/16.
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
    private Node distributorNode;
    private BufferedReader br;

    public DistributorInteract(Node distributorNode) {
        this.distributorNode = distributorNode;
        this.br = new BufferedReader(new InputStreamReader(System.in));
    }

    public void listenForUserRequests() throws IOException {
        while (true) {
            String command;
            if (br.ready()) {
                command = br.readLine().trim().toLowerCase();

                if (SHOW.toLowerCase().equals(command)) {
                    distributorNode.getTextStore().printFileList();
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
                    System.out.println("'help'\t:\tHelp menu");
                    System.out.print("\n" + distributorNode.getShell());
                }
            }
        }
    }
}
