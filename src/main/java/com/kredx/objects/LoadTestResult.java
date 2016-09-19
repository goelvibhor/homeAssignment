package com.kredx.objects;

/**
 * Created by Vibhor on 19/09/16.
 */
public class LoadTestResult {
    int testQuerySize;
    int noOfThread;
    double throughput;
    double latencyInNanoSec;
    double latencyInMilliSec;
    String message;

    public int getTestQuerySize() {
        return testQuerySize;
    }

    public void setTestQuerySize(int testQuerySize) {
        this.testQuerySize = testQuerySize;
    }

    public int getNoOfThread() {
        return noOfThread;
    }

    public void setNoOfThread(int noOfThread) {
        this.noOfThread = noOfThread;
    }

    public double getThroughput() {
        return throughput;
    }

    public void setThroughput(double throughput) {
        this.throughput = throughput;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getLatencyInNanoSec() {
        return latencyInNanoSec;
    }

    public void setLatencyInNanoSec(double latencyInNanoSec) {
        this.latencyInNanoSec = latencyInNanoSec;
    }

    public double getLatencyInMilliSec() {
        return latencyInMilliSec;
    }

    public void setLatencyInMilliSec(double latencyInMilliSec) {
        this.latencyInMilliSec = latencyInMilliSec;
    }

    public LoadTestResult(int testQuerySize, int noOfThread, double throughput, double latencyInNanoSec, double latencyInMilliSec, String message) {
        this.testQuerySize = testQuerySize;
        this.noOfThread = noOfThread;
        this.throughput = throughput;
        this.latencyInNanoSec = latencyInNanoSec;
        this.latencyInMilliSec = latencyInMilliSec;
        this.message = message;
    }

    public LoadTestResult() {
    }
}
