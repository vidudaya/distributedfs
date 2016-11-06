package app;

import support.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by vidudaya on 4/11/16.
 */
public class DistributorInteract {

    private Node distributorNode;
    private BufferedReader br;
    private final String SHOW = "SHOW";
    private final String RT = "RT";
    private final String LEAVE = "LEAVE";
    private final String SEARCH = "SEARCH";

    public DistributorInteract(Node distributorNode) {
        this.distributorNode = distributorNode;
        this.br = new BufferedReader(new InputStreamReader(System.in));
    }

    public void listenForUserRequests() throws IOException {
        while(true){
            String command;
            if(br.ready()){
                command = br.readLine().trim();

                if(SHOW.equals(command)){
                    distributorNode.getTextStore().printFileList();
                }else if(RT.equals(command)){
                    distributorNode.getRoutingTable().printRoutingTable();
                }else if(LEAVE.toLowerCase().equals(command.toLowerCase())){
                    distributorNode.leaveNetwork();
                }else if(command.contains(SEARCH.toLowerCase())){
                    String tokens[] = command.split(" ");
                    if(SEARCH.toLowerCase().equals(tokens[0].toLowerCase().trim())){
                        String fileNameToSearch = tokens[1];
                        distributorNode.searchFile(fileNameToSearch);
                    }
                }
            }
        }
    }
}
