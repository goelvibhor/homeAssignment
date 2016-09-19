# Home Assignment
 A search engine to search gourmet food reviews data and return the top K (=20) reviews that have the highest overlap with the input query.

### Details:
  - [GitHub Link](https://github.com/goelvibhor/homeAssignment)- https://github.com/goelvibhor/homeAssignment
  - **Deployment** - pending will update by tomorrow (some issue with activating account)
  - **search** - <baseUrl>/search,  page to put queries and get result
  - **Load test optimal index** - <baseUrl>/test, run test on optimised index. Get Call
  - **Load test baseline index** - <baseUrl>/test-baseline, run test on baseline index. Get call
  - **Test Result optimal index** - <baseUrl>/testResult, get result of last 1o test run on optimised index. Get call
  - **TestResult of baseline index** -  <baseUrl>/testResult-baseline, get result of last 1o test run on baseline index
  
### Description:

##### Search Page
Enter up to 10 search query term and pick the index either optimised or baseline and hit search button. Result will be shown there only.

##### Load Testing 
   Queries of random length are generated randomly on the runtime using all the words in the given dataset. Then search is performed using the specified number of threads. Latency is calculated using elapsed time. Throughput is calculated by adding 1/latency(in sec) of all the threads.

###### Performance numbers
 - Baseline Index: Average over 20 runs. 
    - Using 10 threads: 
        - **Throughput** : 49746.3135 request/sec
        - **Latency** : 0.2110372 milli sec
 - Improved Index: 
    - Using 10 threads: 
        - **Throughput** : 126852.3883 request/sec
        - **Latency** : 0.07955055 milli sec
 - Performance improvement:
     - **Throughput** : increased to 2.5 times the basic (150% above the baseline)
     - **Latency** : reduced to 0.37 times the basic (60 % improvement)
###### Improvements
There are several imporvment that are made specific to the problem, such as
   - while indexing sort the documents based on the their scores (decs order)
   - In IDF store them in sorted order (with documents having higher score first).
   - Given maximum number of terms in search query is 10, maximum score possible could be 10 and can be partitioned on the basis of score, i.e for each score maintain an array of resulting documents.
   - Also given that only top k elements need to be returned, we dont need to evaluate score for all the documents. We can stop calculating scores when we can be sure that we have found required no of documents. For this there are certain conditions placed:
        - If there are enough elements in max score possible partition.
        - If there are enough elements in max score possible and above partitions in that iteration.
    - There is no need to find intersection of documents, it can be done be counting the occurance of minimum element (same as merging large number of files in merge sort) as the documents are stored in sorted order in IDF.
    - There is no need to sort the documents are result will be sorted on 2 criteria
        - score by intersection with query terms.
        - score present in document(sorted at the time of indexing).


##### To Run
Please download the data file "finefood.txt" from [here](https://drive.google.com/file/d/0B8_VSW2-5XmpSTNlZXV4cVdLRUE/view) and place it in location: ***"repo-location"***/src/main/resources/***"finefood.txt"***. Now build and deploy on tomcat or anyother server.
##### Configurations
 - minWordLength = 2 : ignore string less than equal to this length
 - numberOfDocs=568454 : number of documents in the sample (used in the calculations)
 - indexSize=100000 : size of index (approximate, used probabilty)
 - fileName=/finefoods.txt : data file name to provide data. Should be present in the resources 
 - maxQuerySize=10 : maximium number of tokens in the query
 - testQueriesSize=100000 : test Query size
 - maxResultCount=20 : top K results. K is specified here
 - testingThreadCount=10 : number of threads to test this.


things left to do:
   - aws deployment
   - pretty print json in html
 
