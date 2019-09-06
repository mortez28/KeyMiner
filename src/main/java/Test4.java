import newDBPediaGraph.MinerMainClass;
import newDBPediaGraph.newGraphLoader;
import newDBPediaGraph.newSummarizeGraph;

import java.io.FileNotFoundException;

public class Test4 {

    public static void main(String args[]) throws FileNotFoundException {



        System.out.println("Test4");

        String name="Book", year="2020";
        newGraphLoader graph = new newGraphLoader(
                "F:\\MorteZa\\Datasets\\Vicky\\datasets\\Merged\\"+name+"\\Merged_Types_"+name+".ttl"
                , "F:\\MorteZa\\Datasets\\Vicky\\datasets\\Merged\\"+name+"\\Merged_"+name+".ttl");
//        DBPediaDataGraph graph = new DBPediaDataGraph(
//                "F:\\MorteZa\\Datasets\\Statistical\\"+year+"\\instance_types_en.ttl"
//                , "F:\\MorteZa\\Datasets\\Statistical\\"+year+"\\"+name+"_data.ttl");

        //summarizeGraph sum=new summarizeGraph(graph,0.3, "F:\\MorteZa\\Datasets\\Statistical\\"+year+"\\Keys_"+name+".txt");

        newSummarizeGraph sum=new newSummarizeGraph(graph,0.8, "F:\\MorteZa\\Datasets\\Statistical\\"+year+"\\Candidate_Keys_"+name+".txt",false);

        MinerMainClass miner=new MinerMainClass(graph,sum,0.8,1);



    }

}





