package newDataTypes;

import Infra.candidateKey;

import java.util.*;

public class candidateNodeForAKey extends entityBase {

    private HashSet<candidateKey> allNeighborsInTheKey;
    private String type;
    private HashMap<String,Boolean> uniqueValues;
    private ArrayList<String> allValues;

    public candidateNodeForAKey(String name, String type)
    {
        super(name);

        this.allNeighborsInTheKey =new HashSet<>();
        this.type=type;
        this.uniqueValues=new HashMap<>();
        this.allValues=new ArrayList<>();
    }

    public void setType(String type)
    {
        this.type=type;
    }

    public HashSet<candidateKey> getAllNeighborsInTheKey() {
        return allNeighborsInTheKey;
    }

    public void setJustLiteralNeighborsToInvestigate(HashSet<candidateKey> candidates)
    {
        for (candidateKey key:candidates) {
            if(key.getValue().equals("*"))
            {
                this.allNeighborsInTheKey.add(key);
            }
        }
    }

    public int getNeighborCount()
    {
        return this.allNeighborsInTheKey.size();
    }

    public void setAllNeighborsToInvestigate(HashSet<candidateKey> candidates)
    {
        for (candidateKey key:candidates) {
            this.allNeighborsInTheKey.add(key);
        }
    }

    public String getType()
    {
        return type;
    }


    public void createLattice(int maximumSizeOfTheKey)
    {
        List<String> allNames=new ArrayList<>();
        for (candidateKey key: allNeighborsInTheKey) {
            if(key.getValue().equals("*"))
                allNames.add(key.getPredicate());
            else
                allNames.add(key.getPredicate() + "@" + key.getValue());
        }
        List<Set<String>> level=new ArrayList<>();
        List<Set<String>> accumulateLevel=new ArrayList<>();
        for (String name:allNames) {
            Set<String> temp=new HashSet<>();
            temp.add(name);
            level.add(temp);
            accumulateLevel.add(temp);
        }
        int levelCount=1;
        while (true)
        {
            if(++levelCount>maximumSizeOfTheKey)
                break;
            List<Set<String>> newLevel=new ArrayList<>();
            for (int i=0;i<level.size();i++)
            {
                for (int j=i+1;j<level.size();j++)
                {
                    Set<String> temp=new HashSet<>();
                    temp.addAll(level.get(i));
                    temp.addAll(level.get(j));

                    /*boolean exist=false;
                    for (Set<String> t:newLevel) {
                        if(t.equals(temp)) {
                            exist = true;
                            break;
                        }
                    }
                    if(!exist)
                        newLevel.add(temp);*/

                    if(temp.size()==levelCount && !newLevel.contains(temp))
                        newLevel.add(temp);

                }
            }
            if(newLevel.isEmpty())
                break;
            else
            {
                accumulateLevel.addAll(newLevel);
                level.clear();
                level.addAll(newLevel);
                //System.out.println(newLevel.get(0).size() + " -- " + newLevel.size());
            }
        }
        for (Set<String> t:accumulateLevel) {
            String[] tempArray = Arrays.copyOf(t.toArray(), t.size(),String[].class);
            Arrays.sort(tempArray);
            String key= tempArray[0];
            for (int i=1;i<t.size();i++)
            {
                key+="#"+tempArray[i];
            }
            uniqueValues.put(key,false);
            allValues.add(key);
        }
    }

    public String getNextValueInLattice()
    {
        if(allValues.size()>0)
            return allValues.remove(0);
        else
            return "";
    }
}
