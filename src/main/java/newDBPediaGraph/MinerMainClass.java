package newDBPediaGraph;

import Infra.candidateKey;
import newDataTypes.candidateNodeForAKey;
import newDataTypes.entityNode;
import newDataTypes.key;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class MinerMainClass {

    private HashMap<String,HashSet<candidateKey>> candidateKeys=new HashMap<>();
    private newGraphLoader dataGraph;

    private HashSet<String> analyzedTypes;

    private HashMap<String, key> allMinedKeys=new HashMap<>();

    private int idToAssignEntities=0;

    public MinerMainClass(newGraphLoader dataGraph, newSummarizeGraph summary, double minimumCoverage, double minimumUniqueness)
    {
        this.candidateKeys=summary.getCandidateKeys();
        this.dataGraph=dataGraph;
        this.analyzedTypes=new HashSet<>();

        mineJustLiteralKeys(minimumCoverage, minimumUniqueness);
        mineAllKeys(minimumCoverage,minimumUniqueness);
    }

    private void mineJustLiteralKeys(double minimumCoverage, double minimumUniqueness)
    {
        String candidateType;
        while (!(candidateType=findACandidateTypeWithJustLiterals()).equals(""))
        {
            candidateNodeForAKey node=new candidateNodeForAKey(candidateType,candidateType);
            node.setJustLiteralNeighborsToInvestigate(candidateKeys.get(candidateType));

            node.createLattice();

            String next="";
            boolean foundKey=false;
            while (!(next=node.getNextValueInLattice()).equals(""))
            {
                int count=0,uniques=0,induced=0,optimized=0;
                HashSet<String> literals = new HashSet<>(Arrays.asList(next.split("#")));
                count=dataGraph.getNodeMapWithType().get(candidateType.hashCode()).size();
                HashMap<Integer,HashSet<Integer>> uniqueEntities=new HashMap<>();
                for (entityNode entity:dataGraph.getNodeMapWithType().get(candidateType.hashCode())) {
                    if(entity.isInducedInGeneral(literals))
                    {
                        induced++;
                        if(entity.isUnique(literals)) {
                            uniques++;
                            optimized++;
                            continue;
                        }
                        int key=entity.getValue(literals).hashCode();
                        if(!uniqueEntities.containsKey(key))
                            uniqueEntities.put(key,new HashSet<Integer>());
                        uniqueEntities.get(key).add(entity.getHashCode());
                    }
                }

                for (Integer key:uniqueEntities.keySet()) {
                    if(uniqueEntities.get(key).size()==1)
                    {
                        idToAssignEntities++;
                        for (Integer hashCode:uniqueEntities.get(key)) {
                            dataGraph.getnodeMap().get(hashCode).setUniqueness(literals);
                            dataGraph.getnodeMap().get(hashCode).setEntityID(idToAssignEntities);
                            uniques++;
                        }
                    }
                    else
                    {
                        idToAssignEntities++;
                        for (Integer hashCode:uniqueEntities.get(key)) {
                            dataGraph.getnodeMap().get(hashCode).setEntityID(idToAssignEntities);
                        }
                    }
                }
                if(((double)induced/(double)count)<minimumCoverage)
                {
                    /*
                    System.out.println("Not Supports!!!!");
                    for (String v:literals) {
                        System.out.print(v + " - ");
                    }
                    System.out.println(((double)induced/(double)count));
                    System.out.println("# Optimezed: " + optimized);
                    */
                    continue;
                }

                if(uniques==induced)
                {
                    System.out.println("-----------------------------------------------------------------------------");
                    System.out.println("Bingo!!!!!");
                    System.out.println(((double)uniques/(double)induced) + " ------ "+ ((double)induced/(double)count));
                    System.out.println("Total: " + count + "  -  Induced: " + induced + "  -  Uniques: " + uniques);
                    System.out.println(candidateType);
                    for (String v:literals) {
                        System.out.print(v + " - ");
                    }
                    System.out.println("\n# Optimezed: " + optimized);
                    foundKey=true;

                    key k=new key(candidateType);
                    k.setValueNodes(literals);
                    allMinedKeys.put(candidateType,k);

                    break;
                }
                else if(((double)uniques/(double)induced)>minimumUniqueness)
                {
                    System.out.println("-----------------------------------------------------------------------------");
                    System.out.println("meet the threshold...");
                    System.out.println(((double)uniques/(double)induced) + " ------ "+ ((double)induced/(double)count));
                    System.out.println("Total: " + count + "  -  Induced: " + induced + "  -  Uniques: " + uniques);
                    System.out.println(candidateType);
                    for (String v:literals) {
                        System.out.print(v + " - ");
                    }
                    System.out.println("\n# Optimezed: " + optimized);
                    foundKey=true;

                    key k=new key(candidateType);
                    k.setValueNodes(literals);
                    allMinedKeys.put(candidateType,k);

                    break;
                }
                /*else
                {
                    System.out.println("Not Working");
                    for (String v:literals) {
                        System.out.print(v + " - ");
                    }
                    System.out.println(((double)uniques/(double)induced) + " ------ "+ ((double)induced/(double)count));
                    System.out.println("\n# Optimezed: " + optimized);
                }*/
            }
            if(!foundKey)
            {
                System.out.println("-----------------------------------------------------------------------------");
                System.out.println("Not found :(");
                System.out.println(candidateType);
            }
        }
    }

    private void mineAllKeys(double minimumCoverage, double minimumUniqueness)
    {
        String candidateType;
        while (!(candidateType=findACandidateTypeInGeneral()).equals(""))
        {
            candidateNodeForAKey node=new candidateNodeForAKey(candidateType,candidateType);
            node.setAllNeighborsToInvestigate(candidateKeys.get(candidateType));

            node.createLattice();

            String next="";
            boolean foundKey=false;
            while (!(next=node.getNextValueInLattice()).equals(""))
            {
                int count=0,uniques=0,induced=0,optimized=0;
                HashSet<String> neighbors = new HashSet<>(Arrays.asList(next.split("#")));
                count=dataGraph.getNodeMapWithType().get(candidateType.hashCode()).size();
                HashMap<Integer,HashSet<Integer>> uniqueEntities=new HashMap<>();
                for (entityNode entity:dataGraph.getNodeMapWithType().get(candidateType.hashCode())) {
                    if(entity.isInducedInGeneral(neighbors))
                    {
                        induced++;
                        if(entity.isUnique(neighbors)) {
                            uniques++;
                            optimized++;
                            continue;
                        }
                        int key=entity.getValue(neighbors).hashCode();
                        if(!uniqueEntities.containsKey(key))
                            uniqueEntities.put(key,new HashSet<Integer>());
                        uniqueEntities.get(key).add(entity.getHashCode());
                    }
                }
                for (Integer key:uniqueEntities.keySet()) {
                    if(uniqueEntities.get(key).size()==1)
                    {
                        for (Integer hashCode:uniqueEntities.get(key)) {
                            dataGraph.getnodeMap().get(hashCode).setUniqueness(neighbors);
                            uniques++;
                        }
                    }
                }
                if(((double)induced/(double)count)<minimumCoverage)
                {
                    /*
                    System.out.println("Not Supports!!!!");
                    for (String v:literals) {
                        System.out.print(v + " - ");
                    }
                    System.out.println(((double)induced/(double)count));
                    System.out.println("# Optimezed: " + optimized);
                    */
                    continue;
                }

                if(uniques==induced)
                {
                    System.out.println("-----------------------------------------------------------------------------");
                    System.out.println("Bingo!!!!!");
                    System.out.println(((double)uniques/(double)induced) + " ------ "+ ((double)induced/(double)count));
                    System.out.println("Total: " + count + "  -  Induced: " + induced + "  -  Uniques: " + uniques);
                    System.out.println(candidateType);
                    for (String v:neighbors) {
                        System.out.print(v + " - ");
                    }
                    System.out.println("\n# Optimezed: " + optimized);
                    foundKey=true;

                    key k=new key(candidateType);
                    k.setValueNodes(neighbors);
                    allMinedKeys.put(candidateType,k);

                    setEntityIDToAllEntities(uniqueEntities);

                    break;
                }
                else if(((double)uniques/(double)induced)>minimumUniqueness)
                {
                    System.out.println("-----------------------------------------------------------------------------");
                    System.out.println("meet the threshold...");
                    System.out.println(((double)uniques/(double)induced) + " ------ "+ ((double)induced/(double)count));
                    System.out.println("Total: " + count + "  -  Induced: " + induced + "  -  Uniques: " + uniques);
                    System.out.println(candidateType);
                    for (String v:neighbors) {
                        System.out.print(v + " - ");
                    }
                    System.out.println("\n# Optimezed: " + optimized);
                    foundKey=true;

                    key k=new key(candidateType);
                    k.setValueNodes(neighbors);
                    allMinedKeys.put(candidateType,k);

                    setEntityIDToAllEntities(uniqueEntities);

                    break;
                }
                /*else
                {
                    System.out.println("Not Working");
                    for (String v:literals) {
                        System.out.print(v + " - ");
                    }
                    System.out.println(((double)uniques/(double)induced) + " ------ "+ ((double)induced/(double)count));
                    System.out.println("\n# Optimezed: " + optimized);
                }*/
            }
            if(!foundKey)
            {
                System.out.println("-----------------------------------------------------------------------------");
                System.out.println("Not found :(");
                System.out.println(candidateType);
            }
        }
    }

    private void setEntityIDToAllEntities(HashMap<Integer,HashSet<Integer>> uniqueEntities)
    {
        for (Integer key:uniqueEntities.keySet()) {
            idToAssignEntities++;
            for (Integer hashCode:uniqueEntities.get(key)) {
                dataGraph.getnodeMap().get(hashCode).setEntityID(idToAssignEntities);
            }
        }
    }

    private String findACandidateTypeWithJustLiterals()
    {
        for (String type:candidateKeys.keySet()) {
            if(analyzedTypes.contains(type))
                continue;
            boolean isGoodCandid=true;
            for (candidateKey cand:candidateKeys.get(type)) {
                if(!cand.getValue().equals("*")) {
                    isGoodCandid=false;
                    break;
                }
            }
            if(isGoodCandid)
            {
                analyzedTypes.add(type);
                return type;
            }
        }
        return "";
    }

    private String findACandidateTypeInGeneral()
    {
        for (String type:candidateKeys.keySet()) {
            if(analyzedTypes.contains(type))
                continue;
            boolean isGoodCandid=true;
            for (candidateKey cand:candidateKeys.get(type)) {
                if(!cand.getValue().equals("*") && !allMinedKeys.containsKey(cand.getValue())) {
                    isGoodCandid=false;
                    break;
                }
            }
            if(isGoodCandid)
            {
                analyzedTypes.add(type);
                return type;
            }
        }
        return "";
    }
}
