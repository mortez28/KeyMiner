package newDBPediaGraph;

import Infra.RelationshipEdge;
import newDataTypes.entityNode;
import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.rdf.model.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

public class newGraphLoader {

    private HashMap<Integer, entityNode> nodeMap;
    private HashMap<Integer, HashSet<entityNode>> nodeMapWithType;
    private Graph<entityNode, RelationshipEdge> dataGraph;

    public newGraphLoader(String nodeTypesFilePath, String dataGraphFilePath) throws FileNotFoundException {

        dataGraph = new DefaultDirectedGraph<>(RelationshipEdge.class);
        nodeMap=new HashMap<>();
        nodeMapWithType=new HashMap<>();

        loadNodeMap(nodeTypesFilePath);

        loadGraph(dataGraphFilePath);

        setNodeMapWithType();
    }

    private void setNodeMapWithType()
    {
        for (entityNode node:nodeMap.values()) {
            for (String type:node.getTypes()) {
                if(!nodeMapWithType.containsKey(type.hashCode()))
                    nodeMapWithType.put(type.hashCode(),new HashSet<entityNode>());
                nodeMapWithType.get(type.hashCode()).add(node);
            }
        }
    }

    private void loadNodeMap(String nodeTypesFilePath) {

        if (nodeTypesFilePath == null || nodeTypesFilePath.length() == 0) {
            System.out.println("No Input Node Types File Path!");
            return;
        }
        System.out.println("Start Loading DBPedia Node Map...");

        Model model = ModelFactory.createDefaultModel();
        System.out.println("Loading Node Types...");

        Path input= Paths.get(nodeTypesFilePath);
        model.read(input.toUri().toString());

        StmtIterator typeTriples = model.listStatements();

        while (typeTriples.hasNext()) {

            Statement stmt = typeTriples.nextStatement();
            String subject = stmt.getSubject().getURI();

            if (subject.length() > 28) {
                subject = subject.substring(28).toLowerCase();
            }

            String object = stmt.getObject().asResource().getLocalName().toLowerCase();
            int nodeId = subject.hashCode();

            if (!nodeMap.containsKey(nodeId)) {
                entityNode node=new entityNode(subject);
                node.addType(object);
                nodeMap.put(nodeId, node);
                this.dataGraph.addVertex(node);

            } else {
                nodeMap.get(nodeId).addType(object);
            }

        }

        System.out.println("Done Loading DBPedia Node Map!!!");
        System.out.println("DBPedia NodesMap Size: " + nodeMap.size());


    }


    private void loadGraph(String dataGraphFilePath) throws FileNotFoundException {

        if (dataGraphFilePath == null || dataGraphFilePath.length() == 0) {
            System.out.println("No Input Graph Data File Path!");
            return;
        }

        Model model = ModelFactory.createDefaultModel();
        System.out.println("Loading DBPedia Graph...");

        //model.read(dataGraphFilePath);

        Path input= Paths.get(dataGraphFilePath);
        model.read(input.toUri().toString());

        StmtIterator dataTriples = model.listStatements();

        int loseCount = 0;
        int loopCount = 0;

        while (dataTriples.hasNext()) {

            Statement stmt = dataTriples.nextStatement();
            String subject = stmt.getSubject().getURI();

            if (subject.length() > 28) {
                subject = subject.substring(28);
            }

            String predicate = stmt.getPredicate().getLocalName().toLowerCase();
            RDFNode object = stmt.getObject();
            String objectString;

            try {
                if (object.isLiteral()) {
                    objectString = object.asLiteral().getString().toLowerCase();
                } else {
                    objectString = object.asResource().getLocalName().toLowerCase();
                }
            } catch (DatatypeFormatException e) {
                System.out.println("Invalid DataType Skipped!");
                e.printStackTrace();
                continue;
            }

            int subjectNodeId = subject.toLowerCase().hashCode();
            int objectNodeId = objectString.hashCode();

            if (!nodeMap.containsKey(subjectNodeId)
                    || (!object.isLiteral() && !nodeMap.containsKey(objectNodeId))) {
                loseCount++;
                continue;
            }

            entityNode currentSubject = nodeMap.get(subjectNodeId);

            if (!object.isLiteral()) {

                if (subjectNodeId == objectNodeId) {
                    loopCount++;
                    continue;
                }
                entityNode currentObject = nodeMap.get(objectNodeId);
                dataGraph.addEdge(currentSubject, currentObject, new RelationshipEdge(predicate));

                currentSubject.addEntityNeighbor(predicate,currentObject);
            }
            else if (object.isLiteral())
            {
                currentSubject.addLiteralProperty(predicate,objectString);
            }
            else
            {
                loseCount++;
                continue;
            }
        }

        System.out.println(loopCount + "Loops!");
        System.out.println(loseCount + "Loses!");
        System.out.println("Number of Nodes: " + dataGraph.vertexSet().size());
        System.out.println("Number of Edges: " + dataGraph.edgeSet().size());
        System.out.println("Done Loading DBPedia Graph!!!");

    }

    public Graph<entityNode, RelationshipEdge> getDataGraph() { return dataGraph; }

    public HashMap<Integer, entityNode> getnodeMap() { return nodeMap; }

    public HashMap<Integer, HashSet<entityNode>> getNodeMapWithType() {
        return nodeMapWithType;
    }
}
