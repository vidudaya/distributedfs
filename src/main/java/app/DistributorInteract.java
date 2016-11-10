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
    private final String OFF_LOG = "OFFLOG";
    // increase the log messages
    private final String ON_LOG = "ONLOG";
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
                } else if (RT.toLowerCase().equals(command)) {
                    distributorNode.getRoutingTable().printRoutingTable();
                } else if (LEAVE.toLowerCase().equals(command.toLowerCase())) {
                    distributorNode.leaveNetwork();
                } else if (command.contains(SEARCH.toLowerCase())) {
                    String tokens[] = command.split(" ");
                    if (SEARCH.toLowerCase().equals(tokens[0].toLowerCase().trim())) {
                        String fileNameToSearch = tokens[1];
                        distributorNode.searchFile(fileNameToSearch);
                    }
                } else if (OFF_LOG.toLowerCase().equals(command)) {
                    distributorNode.setIsDebugMode(false);
                    System.out.println(distributorNode.isDebugMode());
                } else if (ON_LOG.toLowerCase().equals(command)) {
                    distributorNode.setIsDebugMode(true);
                }
            }
        }
    }
}
