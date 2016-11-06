package support;

/**
 * Created by Samiththa on 4/11/16.
 */
public class NeighbourNode {

    private String ip;
    private Integer port;
    private String nodeIdentifier;

    public NeighbourNode(String ip, Integer port, String nodeIdentifier) {
        this.ip = ip;
        this.port = port;
        this.nodeIdentifier = nodeIdentifier;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getNodeIdentifier() {
        return nodeIdentifier;
    }

    public void setNodeIdentifier(String nodeIdentifier) {
        this.nodeIdentifier = nodeIdentifier;
    }
}
