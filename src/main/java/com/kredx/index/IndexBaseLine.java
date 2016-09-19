package com.kredx.index;

import com.kredx.objects.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by Vibhor on 17/09/16.
 */
@Component
public class IndexBaseLine extends IndexAbstract{
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

        buildIdfMap();
    }

    public List<Document> search(List<String> queries){
        HashSet<String> queryWords = null;
        List<Document> result = new ArrayList<Document>(maxResultCount);

        if(queries.size() > 0){
            if(queries.size() > maxQuerySize) {
                queries = queries.subList(0, maxQuerySize);
            }

            for(String query : queries){
                queryWords = getStemmedWords(query, queryWords);
            }
        }

        if(queryWords.size() > 0) {
            HashMap<Integer, DocumentScore> documentScoreMap = new HashMap<Integer, DocumentScore>();
            for (String query : queryWords) {
                if (idf.containsKey(query)) {
                    ArrayList<Integer> tmpDocs = idf.get(query);
                    for (Integer doc : tmpDocs) {
                        if (!documentScoreMap.containsKey(doc)) {
                            documentScoreMap.put(doc, new DocumentScore(0, documents.get(doc).score, doc));
                        }
                        documentScoreMap.get(doc).searchScore++;
                    }
                }
            }

            if(documentScoreMap.size() > 0){
                List<DocumentScore> documentScores = new ArrayList<DocumentScore>(documentScoreMap.values());
                Collections.sort(documentScores, new Comparator<DocumentScore>(){
                    public int compare(DocumentScore o1, DocumentScore o2)
                    {
                        return o2.searchScore == o1.searchScore ? o2.documentScore.compareTo(o1.documentScore) :
                        o2.searchScore.compareTo(o1.searchScore);
                    }
                });

                for(int i = 0; i < maxResultCount && i < documentScores.size(); i++){
                    result.add(documents.get(documentScores.get(i).id));
                }
            }
        }

        return result;
    }

    private class DocumentScore{
        public Integer searchScore;
        public Double documentScore;
        public Integer id;

        public DocumentScore(Integer searchScore, Double documentScore, Integer id) {
            this.searchScore = searchScore;
            this.documentScore = documentScore;
            this.id = id;
        }

        public DocumentScore() {
        }
    }
}
