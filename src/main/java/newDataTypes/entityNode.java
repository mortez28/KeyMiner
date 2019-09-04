package newDataTypes;

import java.util.*;

public class entityNode extends  entityBase {

    private HashSet<String> types;
    private HashSet<literalProperty> allLiterals;
    private List<HashSet<String>> uniqueLiteralCombinations;
    private HashSet<entityNeighbor> allEntityNeighbors;
    private int entityID;

    public entityNode(String name)
    {
        super(name);
        this.allLiterals= new HashSet<>();
        this.types=new HashSet<>();
        this.uniqueLiteralCombinations=new ArrayList<>();
        this.allEntityNeighbors=new HashSet<>();
        this.entityID=-1;
    }

    public void addLiteralProperty(literalProperty lit)
    {
        this.allLiterals.add(lit);
    }

    public void addLiteralProperty(String pred, String obj)
    {
        literalProperty lit=new literalProperty(pred,obj);
        this.allLiterals.add(lit);
    }

    public void addEntityNeighbor(String pred, entityNode neighbor)
    {
        this.allEntityNeighbors.add(new entityNeighbor(pred,neighbor));
    }

    public void addType(String type)
    {
        this.types.add(type);
    }

    public HashSet<literalProperty> getAllLiterals()
    {
        return allLiterals;
    }

    public void setEntityID(int entityID) {
        this.entityID = entityID;
    }

    public int getEntityID() {
        return entityID;
    }

    public HashSet<entityNeighbor> getAllEntityNeighbors() {
        return allEntityNeighbors;
    }

    public boolean isInducedInGeneral(Set<String> literals)
    {
        for (String name:literals) {
            if(!name.contains("@"))
            {
                boolean exist=false;
                for (literalProperty lit:allLiterals) {
                    if(lit.getPredicate().equals(name))
                    {
                        exist=true;
                        break;
                    }
                }
                if(!exist)
                    return false;
            }
            else
            {
                boolean exist=false;
                String []arr=name.split("@");
                for (entityNeighbor node:allEntityNeighbors) {
                    if(node.getPredicate().equals(arr[0])
                            && node.getNeighbor().getTypes().contains(arr[1])
                            && node.getNeighbor().getEntityID()!=-1)
                    {
                        exist=true;
                        break;
                    }
                }
                if(!exist)
                    return false;
            }
        }
        return true;
    }

    public String getValue(Set<String> literals)
    {
        StringBuilder value=new StringBuilder();
        for (String name:literals) {
            if(!name.contains("@"))
            {
                for (literalProperty lit:allLiterals) {
                    if(lit.getPredicate().equals(name))
                    {
                        value.append(lit.getValue());
                        break;
                    }
                }
            }
            else
            {
                String []arr=name.split("@");
                for (entityNeighbor node:allEntityNeighbors) {
                    if(node.getPredicate().equals(arr[0]) && node.getNeighbor().getTypes().contains(arr[1]))
                    {
                        value.append(node.getNeighbor().getEntityID());
                        break;
                    }
                }
            }
        }
        return value.toString();
    }

    public HashSet<String> getTypes()
    {
        return types;
    }

    public void setUniqueness(HashSet<String> parts)
    {
        for (HashSet<String> currentUniques:uniqueLiteralCombinations) {
            if(parts.containsAll(currentUniques))
                return;
        }
        uniqueLiteralCombinations.add(parts);
    }

    public Boolean isUnique(HashSet<String> parts)
    {
//        HashSet<String> parts=new HashSet<>();
//        for (String var:combination.split("#")) {
//            if(!var.equals(""))
//                parts.add(var);
//        }
        for (HashSet<String> currentUniques:uniqueLiteralCombinations) {
            if(parts.containsAll(currentUniques))
                return true;
        }
        return false;
    }
}
