package com.kredx.index;

import com.kredx.objects.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by Vibhor on 16/09/16.
 */

@Component
public class Index extends IndexAbstract{
    @Autowired
    private Environment environment;

    @PostConstruct
    private void init(){
        super.minWordLength = Integer.parseInt(environment.getRequiredProperty("minWordLength"));
        super.numberOfDocs = Integer.parseInt(environment.getRequiredProperty("numberOfDocs"));
        super.indexSize = Integer.parseInt(environment.getRequiredProperty("indexSize"));
        super.randLimit = super.numberOfDocs/super.indexSize;
        fileName = environment.getProperty("fileName");
        super.maxQuerySize = Integer.parseInt(environment.getRequiredProperty("maxQuerySize"));
        super.testQueriesSize = Integer.parseInt(environment.getRequiredProperty("testQueriesSize"));
        super.maxResultCount = Integer.parseInt(environment.getRequiredProperty("maxResultCount"));
        super.testingThreadCount = Integer.parseInt(environment.getRequiredProperty("testingThreadCount"));
        buildIndex();
//        testIndex();
    }

    String fileName;


    public void buildIndex(){
        documents = new ArrayList<>(indexSize);
        idf = new HashMap<>();
        wordList = new HashSet<>(10000);
        loadTestResult = new ArrayList<>();
        readFile(fileName);

        Collections.sort(documents, new Comparator<Document>(){
            public int compare(Document o1, Document o2)
            {
                return o2.score.compareTo(o1.score);
            }
        });

        buildIdfMap();
    }

    public List<Document> search(List<String> queries){
        HashSet<String> queryWords = null;

        List<Document> result = new ArrayList<>(maxResultCount);

        if(queries.size() > 0){
            if(queries.size() > maxQuerySize) {
                queries = queries.subList(0, maxQuerySize);
            }

            for(String query : queries){
                queryWords = getStemmedWords(query, queryWords);
            }
        }

        if(queryWords.size() > 0){
            List<Integer> indexList = new ArrayList<Integer>(queryWords.size());
            List<Integer> cumulativeCountList = new ArrayList<Integer>(queryWords.size());
            ArrayList<ArrayList<Integer>> documentList = new ArrayList<ArrayList<Integer>>(queryWords.size());
            ArrayList<ArrayList<Integer>> resultDocumentList = new ArrayList<ArrayList<Integer>>(queryWords.size());
            for(String query: queryWords){
                if(idf.containsKey(query)){
                    documentList.add(idf.get(query));
                    indexList.add(0);
                    cumulativeCountList.add(0);
                    resultDocumentList.add(new ArrayList<Integer>());
                }
            }

            //Assuming there are no complex terms in search

            int querySize = indexList.size();
            long iterationCount = 0;
            int processQuerySize = 0;
            if(querySize > 0){
                while(true){
                    iterationCount++;
                    int minDocIndex = documents.size() + 1;
                    int score = 0;
                    for(int i = 0; i < indexList.size(); i++){
                        minDocIndex = Math.min(minDocIndex, documentList.get(i).get(indexList.get(i)));
                    }

                    for(int i = 0; i < indexList.size(); i++){
                        if(minDocIndex == documentList.get(i).get(indexList.get(i))){
                            if(indexList.get(i) + 1 < documentList.get(i).size()) {
                                indexList.set(i, indexList.get(i) + 1);
                            }else {
                                indexList.remove(i);
                                documentList.remove(i);
                                i--;
                            }
                            score++;
                        }
                    }

                    if(score > processQuerySize) resultDocumentList.get(score - 1).add(minDocIndex);
                    if(resultDocumentList.get(querySize - 1).size() == maxResultCount || documentList.size() == 0
                            || cumulativeCountList.get(indexList.size() - 1) >= maxResultCount){
                        break;
                    }

                    if(iterationCount % (2*maxResultCount) == 0){
                        int cumulativeScore = 0;
                        for(int i = querySize - 1; i >= processQuerySize; i--){
                            cumulativeScore += resultDocumentList.get(i).size();
                            cumulativeCountList.set(i,cumulativeScore);

                            if(cumulativeScore >= maxResultCount){
                                processQuerySize = i;
                                break;
                            }
                        }
                    }
                }

                int count = 0;
                while(result.size() < maxResultCount && querySize > 0){
                    if(resultDocumentList.get(querySize - 1).size() > count){
                        result.add(documents.get(resultDocumentList.get(querySize - 1).get(count)));
                        count++;
                    } else{
                        count = 0;
                        querySize--;
                    }
                }
            }
        }

        return result;
    }

    public static void main(String[] arg){
        Index index = new Index();
//        index.buildIndex("/fineFoods.txt");
    }
}
