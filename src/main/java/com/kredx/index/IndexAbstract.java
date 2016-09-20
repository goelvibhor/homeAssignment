package com.kredx.index;

import com.kredx.objects.Document;
import com.kredx.objects.LoadTestResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.*;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Vibhor on 17/09/16.
 */
public abstract class IndexAbstract {

    public List<Document> documents;
    public HashMap<String, ArrayList<Integer>> idf;
//    public List<String> testQueries;
    public HashSet<String> wordList;
    public List<LoadTestResult> loadTestResult;
    public boolean testingInProgress = false;
//    private Stemmer stemmer = new Stemmer();

    protected int minWordLength = 2;
    protected int numberOfDocs = 568454;
    protected int indexSize = 10000;
    protected int randLimit = numberOfDocs/indexSize;
    protected int testQueriesSize;
    protected int maxQuerySize = 10;

    protected int maxResultCount = 20;
    protected int testingThreadCount = 10;
    protected int loadTestResultCount = 20;

    abstract void buildIndex();
    abstract List<Document> search(List<String> queries);

    public void buildIdfMap(){
        for (int i = 0; i < documents.size(); i++ ) {
            Document document = documents.get(i);
            HashSet<String> words = getStemmedWords(document.summary, null);
            words = getStemmedWords(document.text, words);

            for (String word: words) {
                if(!idf.containsKey(word)){
                    idf.put(word, new ArrayList<>());
                }
                idf.get(word).add(i);
            }
        }
    }

    public HashSet<String> getStemmedWords(String input, HashSet<String> words){
        if(words == null) words = new HashSet<>();
        if(input != null && input.trim().length() > 0) {
            input = input.trim().replaceAll("<[^>]*>", "");
            input = input.replaceAll("[,.!\"':]+", " ");
            input = input.replaceAll("[^$A-Za-z0-9 ]", "");
            Stemmer stemmer = new Stemmer();
            for (String word : input.split(" ")) {
                String tmp = word.toLowerCase().trim();
//                tmp = tmp.replaceAll("^[^$a-z0-9\\s]+|[^a-z0-9\\s]+$", "");
                stemmer.add(tmp.toCharArray(), tmp.length());
                stemmer.stem();
                tmp = stemmer.toString();
                if(tmp.length() > minWordLength){
                    words.add(tmp);
                }
            }
        }
        return words;
    }

    public void readFile(String fileName) {
        String line = null;
        InputStream inputStream = getClass().getResourceAsStream(fileName);
        Document document = new Document();
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
//        random.setSeed(12345);
        String lastId = "";
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("product/productId:")) {
                    document = new Document();
                    line = line.replaceFirst("product/productId:", "");
                    document.productId = line.trim();

                } else if (line.startsWith("review/userId:")) {
                    line = line.replaceFirst("review/userId:", "");
                    document.userId = line.trim();

                } else if (line.startsWith("review/profileName:")) {
                    line = line.replaceFirst("review/profileName:", "");
                    document.profileName = line.trim();

                } else if (line.startsWith("review/helpfulness:")) {
                    line = line.replaceFirst("review/helpfulness:", "");
                    document.helpfulness = line.trim();

                } else if (line.startsWith("review/score:")) {
                    line = line.replaceFirst("review/score:", "");
                    document.score = 0d;
                    try {
                        document.score = Double.parseDouble(line.trim());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (line.startsWith("review/time:")) {
                    line = line.replaceFirst("review/time:", "");
                    document.time = line.trim();
                } else if (line.startsWith("review/summary:")) {
                    line = line.replaceFirst("review/summary:", "");
                    document.summary = line.trim();
                } else if (line.startsWith("review/text:")) {
                    line = line.replaceFirst("review/text:", "");
                    document.text = line.trim();
                } else if (line.trim().length() == 0) {
                    if (random.nextInt(randLimit) == 0 && !document.toString().equals(lastId) && documents.size() < indexSize) {
                        documents.add(document);
                        lastId = document.toString();
                    }

                    wordList = getStemmedWords(document.summary, wordList);
                    wordList = getStemmedWords(document.text, wordList);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void testIndex(){

        if(documents == null || documents.size() <= 0 || idf == null || idf.size() <= 0 || wordList == null || wordList.size() <= 0){
            return;
        }

        testingInProgress = true;

        List<String> testQueryWordList = new ArrayList<>(wordList);
        Collections.shuffle(testQueryWordList);

        Random random = new Random();
        random.setSeed(System.currentTimeMillis());

        int testQueriesPerThread = testQueriesSize/testingThreadCount;
        List<ArrayList<String>> testQueries = new ArrayList<>(testingThreadCount);

        int endIndex = 0;
        int startIndex = 0;

        for(int j = 0; j < testingThreadCount; j++) {
            ArrayList<String> tmpQueries = new ArrayList<>(testQueriesPerThread);
            for (int i = 0; i < testQueriesPerThread; i++) {
                endIndex = Math.min(startIndex + random.nextInt(maxQuerySize) + 1, testQueryWordList.size());
                tmpQueries.add(StringUtils.join(testQueryWordList.subList(startIndex, endIndex), " "));
                startIndex = endIndex;
                if (startIndex == testQueryWordList.size()) {
                    startIndex = 0;
                }
            }
            testQueries.add(tmpQueries);
        }

        List<Callable<List<Long>>> tasks = new ArrayList<>();
        for(int i = 0; i < testingThreadCount; i++){
            final ArrayList<String> queryList = testQueries.get(i);
            Callable<List<Long>> task = () -> {
                return runTest(queryList);
            };
            tasks.add(task);
        }

        ExecutorService executor = Executors.newFixedThreadPool(testingThreadCount);
        List<List<Long>> latency = new ArrayList<>();
        try {
            executor.invokeAll(tasks).stream().forEach(future -> {
                try {
                    latency.add(future.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        executor.shutdownNow();

        Long sumLatency = 0l;
        Integer count = 0;
        double throughput = 0d;
        for(int i = 0; i < latency.size(); i++){
            Long tmpSum = 0l;
            List<Long> tmpLatency = latency.get(i);
            for(int j = 0; j< tmpLatency.size(); j++){
                tmpSum += tmpLatency.get(j);
            }
            sumLatency += tmpSum;
            count += tmpLatency.size();
            throughput += (1000d * 1000d * 1000d * tmpLatency.size())/tmpSum;
        }

        double latencyNano = ((double)sumLatency)/count;
        loadTestResult.add(new LoadTestResult(count, testingThreadCount, throughput, latencyNano, latencyNano/1000000, "testing complete"));
        if(loadTestResult.size() > loadTestResultCount){
            loadTestResult.remove(0);
        }
        System.out.println("testing done");
        testingInProgress = false;
    }

    private List<Long> runTest(ArrayList<String> queryList){
        List<Long> result = new ArrayList<>(queryList.size());
        Long startTime;
        for (String query: queryList) {
            startTime = System.nanoTime();
            try {
                search(Arrays.asList(query.split(" ")));
            } catch (Exception e){
                e.printStackTrace();
            }
            result.add(System.nanoTime() - startTime);
        }
        return result;
    }
}