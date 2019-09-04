package newDataTypes;

public class entityNeighbor {

    private String predicate;
    private entityNode neighbor;

    public entityNeighbor(String predicate, entityNode neighbor)
    {
        this.predicate=predicate;
        this.neighbor = neighbor;
    }

    public entityNode getNeighbor() {
        return neighbor;
    }

    public String getPredicate() {
        return predicate;
    }
}
