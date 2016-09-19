package com.kredx.controller;

import com.kredx.index.Index;
import com.kredx.index.IndexBaseLine;
import com.kredx.objects.Document;
import com.kredx.objects.LoadTestResult;
import com.kredx.objects.SearchQueryList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Vibhor on 18/09/16.
 */
@Controller
@RequestMapping("/")
public class SearchController {
    @Autowired
    Index index;

    @Autowired
    IndexBaseLine indexBaseLine;

    ExecutorService executor = Executors.newSingleThreadExecutor();

    boolean testingInProgress = false;

    @PreDestroy
    private void destroy(){
        executor.shutdown();
        executor.shutdownNow();
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST,  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<Document> query(@RequestBody SearchQueryList queryTerms) {
        List<Document> result = null;

        result = index.search(queryTerms.getQueryTerms());

        return result;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchPage(HttpServletRequest request) {
        return "search";
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public void test() {
        if(!testingInProgress) {
            testingInProgress = true;
            if (!index.testingInProgress) {
                executor.submit(() -> {
                    index.testIndex();
                    testingInProgress = false;
                });
            }
        }
    }

    @RequestMapping(value = "/testResult", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<LoadTestResult> testResult() {
        return index.loadTestResult;
    }


    @RequestMapping(value = "/search-baseline", method = RequestMethod.POST,  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<Document> queryBaseline(@RequestBody SearchQueryList queryTerms) {
        List<Document> result = null;
        result = indexBaseLine.search(queryTerms.getQueryTerms());
        return result;
    }

    @RequestMapping(value = "/test-baseline", method = RequestMethod.GET)
    @ResponseBody
    public void testBaseline() {
        if(!testingInProgress) {
            testingInProgress = true;
            if (!index.testingInProgress) {
                executor.submit(() -> {
                    indexBaseLine.testIndex();
                    testingInProgress = false;
                });
            }
        }
    }

    @RequestMapping(value = "/testResult-baseline", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<LoadTestResult> testResultBaseline() {
        return indexBaseLine.loadTestResult;
    }
}
