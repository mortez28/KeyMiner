package newDataTypes;

import java.util.HashSet;

public class key {

    private String type;
    private HashSet<String> valueNodes;
    private HashSet<String> variableNodes;

    public key(String type)
    {
        this.type=type;
        valueNodes=new HashSet<>();
        variableNodes=new HashSet<>();
    }

    public void addValueNode(String node)
    {
        this.valueNodes.add(node);
    }

    public void addVariableNode(String node)
    {
        this.variableNodes.add(node);
    }

    public void setValueNodes(HashSet<String> nodes)
    {
        this.valueNodes=nodes;
    }

    public void setVariableNodes(HashSet<String> nodes)
    {
        this.variableNodes=nodes;
    }

    public HashSet<String> getValueNodes() {
        return valueNodes;
    }

    public HashSet<String> getVariableNodes() {
        return variableNodes;
    }

    public String getType() {
        return type;
    }
}
