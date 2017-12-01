package support;

import java.util.*;

/**
 * Created by Samiththa on 4/11/16.
 */
public class RoutingTable {

    private Map<String, NeighbourNode> table;

    public RoutingTable() {
        this.table = new HashMap<String, NeighbourNode>();
    }

    public Map<String, NeighbourNode> getTable() {
        return table;
    }

    public void setTable(Map<String, NeighbourNode> table) {
        this.table = table;
    }

    public void addToRoutingTable(String res) {
        //res : IP_address port_no name
        String s[] = res.trim().split(" ");
        String ipAddress = s[0];
        String nodeName = s[2];
        Integer port;

        try {
            port = Integer.parseInt(s[1]);
            NeighbourNode newNode = new NeighbourNode(ipAddress, port, nodeName);
            table.put(nodeName, newNode);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public boolean addToRoutingTable(NeighbourNode newNode) {
        if (table.containsKey(newNode.getNodeIdentifier())) {
            return false;
        }
        table.put(newNode.getNodeIdentifier(), newNode);
        return true;
    }

    public boolean removeFromRoutingTable(NeighbourNode newNode) {
        if (!table.containsKey(newNode.getNodeIdentifier())) {
            return false;
        }
        table.remove(newNode.getNodeIdentifier());
        return true;
    }

    public ArrayList<NeighbourNode> getAllNeighbours() {
        ArrayList<NeighbourNode> knownNodes = new ArrayList<NeighbourNode>();
        for (NeighbourNode node : table.values()) {
            knownNodes.add(node);
        }
        return knownNodes;
    }

    public ArrayList<NeighbourNode> getRandomNeighbours(int count) {
        ArrayList<NeighbourNode> knownNodes = new ArrayList<NeighbourNode>(table.values());
        ArrayList<NeighbourNode> randoms = new ArrayList<NeighbourNode>();
        Collections.shuffle(knownNodes);
        for (int i = 0; i < count && i < knownNodes.size(); ++i) {
            randoms.add(knownNodes.get(i));
        }
        return randoms;
    }

    public ArrayList<NeighbourNode> addRandomTwo(ArrayList<NeighbourNode> list) {
        ArrayList<NeighbourNode> selectedNodes = new ArrayList<NeighbourNode>();
        if (list == null || list.isEmpty()) {
            return selectedNodes;
        }
        if (list.size() == 1) {
            NeighbourNode node = list.get(0);
            table.put(node.getNodeIdentifier(), node);
            selectedNodes.add(node);
        } else if (list.size() == 2) {
            NeighbourNode node1 = list.get(0);
            table.put(node1.getNodeIdentifier(), node1);
            NeighbourNode node2 = list.get(1);
            table.put(node2.getNodeIdentifier(), node2);
            selectedNodes.add(node1);
            selectedNodes.add(node2);
        } else {
            Random rand = new Random();
            int i = 0;
            Set set = new HashSet<Integer>();
            while (i < 2) {
                Integer t = rand.nextInt(list.size());
                if (!set.contains(t)) {
                    NeighbourNode node = list.get(t);
                    table.put(node.getNodeIdentifier(), node);
                    i++;
                    set.add(t);
                    selectedNodes.add(node);
                }
            }
        }
        return selectedNodes;
    }

    public void printRoutingTable() {
        ArrayList<NeighbourNode> list = getAllNeighbours();
        System.out.println("##################### Routing Table ################################");
        for (NeighbourNode node : list) {
            System.out.println(node.getNodeIdentifier() + "\t" + node.getIp() + "\t" + node.getPort());
        }
        System.out.println("##################### Routing Table End ############################");
    }
}
