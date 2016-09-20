# Home Assignment
 A search engine to search gourmet food reviews data and return the top K (=20) reviews that have the highest overlap with the input query.

### Details:
  - [GitHub Link](https://github.com/goelvibhor/homeAssignment)- https://github.com/goelvibhor/homeAssignment
  - [Deployment](http://ec2-54-179-191-125.ap-southeast-1.compute.amazonaws.com:8080/) - deployment done
  - [Search](http://ec2-54-179-191-125.ap-southeast-1.compute.amazonaws.com:8080/kredx/search)   html page to query. Get call
  - **Search** - *baseUri*/search,  page to put queries and get result. Post call
  - [Load test optimal index](http://ec2-54-179-191-125.ap-southeast-1.compute.amazonaws.com:8080/kredx/test) run test on optimised index. Get Call
  - [Load test baseline index](http://ec2-54-179-191-125.ap-southeast-1.compute.amazonaws.com:8080/kredx/test-baseline)  run test on baseline index. Get call
  - [Test Result optimal index](http://ec2-54-179-191-125.ap-southeast-1.compute.amazonaws.com:8080/kredx/testResult) get result of last 1o test run on optimised index. Get call
  - [TestResult of baseline index](http://ec2-54-179-191-125.ap-southeast-1.compute.amazonaws.com:8080/kredx/testResult-baseline) get result of last 1o test run on baseline index
  
### Description:

##### Search Page
Enter up to 10 search query term and pick the index either optimised or baseline and hit search button. Result will be shown there only.

##### Search Post Call
Post call with search query parameters will return the resulted documents in json format. Sample call
curl -X POST -H "Content-type: application/json" -H "Cache-Control: no-cache" -d '{"queryTerms":["vibhorTest004","vibhorTest005","vibhorTest001"]}' "http://localhost:8080/search"

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
There are several improvement that are made specific to the problem, such as
   - while indexing sort the documents based on the their scores (decs order)
   - In IDF store them in sorted order (with documents having higher score first).
   - Given maximum number of terms in search query is 10, maximum score possible could be 10 and can be partitioned on the basis of score, i.e for each score maintain an array of resulting documents.
   - Also given that only top k elements need to be returned, we don't need to evaluate score for all the documents. We can stop calculating scores when we can be sure that we have found required no of documents. For this there are certain conditions placed:
        - If there are enough elements in max score possible partition.
        - If there are enough elements in max score possible and above partitions in that iteration.
   - There is no need to find intersection of documents, it can be done be counting the occupancies of minimum element (same as merging large number of files in merge sort) as the documents are stored in sorted order in IDF.
   - There is no need to sort the documents are result will be sorted on 2 criteria
        - score by intersection with query terms.
        - score present in document(sorted at the time of indexing).


##### To Run
Please download the data file "finefood.txt" from [here](https://drive.google.com/file/d/0B8_VSW2-5XmpSTNlZXV4cVdLRUE/view) and place it in location: ***repo-location***/src/main/resources/***finefood.txt***. Now build and deploy on tomcat or any other server.
##### Configurations
 - minWordLength = 2 : ignore string less than equal to this length
 - numberOfDocs=568454 : number of documents in the sample (used in the calculations)
 - indexSize=100000 : size of index (approximate, used probability)
 - fileName=/finefoods.txt : data file name to provide data. Should be present in the resources 
 - maxQuerySize=10 : maximum number of tokens in the query
 - testQueriesSize=100000 : test Query size
 - maxResultCount=20 : top K results. K is specified here
 - testingThreadCount=10 : number of threads to test this.

##### Results
 - Baseline: [{"testQuerySize":100000,"noOfThread":10,"throughput":54704.191948487576,"latencyInNanoSec":183082.32482,"latencyInMilliSec":0.18308232482,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":70547.17334765394,"latencyInNanoSec":141928.52315,"latencyInMilliSec":0.14192852314999999,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":53294.601338081244,"latencyInNanoSec":187710.21897,"latencyInMilliSec":0.18771021896999998,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":54994.74289116185,"latencyInNanoSec":182739.12576,"latencyInMilliSec":0.18273912575999998,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":42617.34146668065,"latencyInNanoSec":235475.71069,"latencyInMilliSec":0.23547571069,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":31901.638982842982,"latencyInNanoSec":313782.36327,"latencyInMilliSec":0.31378236327,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":41751.3292482064,"latencyInNanoSec":240031.97918,"latencyInMilliSec":0.24003197918,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":39111.162432485995,"latencyInNanoSec":256278.59412,"latencyInMilliSec":0.25627859412,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":37609.55994984203,"latencyInNanoSec":265929.53422,"latencyInMilliSec":0.26592953422,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":33182.59209597522,"latencyInNanoSec":301508.06305,"latencyInMilliSec":0.30150806305,"message":"testing complete"}]
 - Improvement: [ { "testQuerySize":100000,"noOfThread":10,"throughput":165448.1775001203,"latencyInNanoSec":60509.49174,"latencyInMilliSec":0.06050949174,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":127590.51289006298,"latencyInNanoSec":78514.22608,"latencyInMilliSec":0.07851422608,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":131339.0980935045,"latencyInNanoSec":76169.72732,"latencyInMilliSec":0.07616972732,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":118157.54870795741,"latencyInNanoSec":84672.18088,"latencyInMilliSec":0.08467218088,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":60163.01953117619,"latencyInNanoSec":166224.85747,"latencyInMilliSec":0.16622485747,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":136973.3898125993,"latencyInNanoSec":73036.15016,"latencyInMilliSec":0.07303615016000001,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":121460.46914009984,"latencyInNanoSec":82390.06613,"latencyInMilliSec":0.08239006613000001,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":132181.16893312955,"latencyInNanoSec":75700.42883,"latencyInMilliSec":0.07570042883,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":127766.78794434466,"latencyInNanoSec":78302.86124,"latencyInMilliSec":0.07830286124,"message":"testing complete"},{"testQuerySize":100000,"noOfThread":10,"throughput":141547.2785939771,"latencyInNanoSec":70694.2345,"latencyInMilliSec":0.07069423450000001,"message":"testing complete"}]

##### ScreenShots

![](https://raw.githubusercontent.com/goelvibhor/homeAssignment/master/images/image1.png)
![](https://raw.githubusercontent.com/goelvibhor/homeAssignment/master/images/image2.png)
![](https://raw.githubusercontent.com/goelvibhor/homeAssignment/master/images/image3.png)
![](https://raw.githubusercontent.com/goelvibhor/homeAssignment/master/images/image4.png)
![](https://raw.githubusercontent.com/goelvibhor/homeAssignment/master/images/image5.png)

