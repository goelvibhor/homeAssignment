package com.kredx.objects;

import java.text.DecimalFormat;

/**
 * Created by Vibhor on 16/09/16.
 */
public class Document {

    public String productId;
    public String userId;
    public String profileName;
    public String helpfulness;
    public Double score;
    public String time;
    public String summary;
    public String text;

    @Override
    public String toString() {
        return  "{ product/productId: " + productId + "\n" +
                "  review/userId: " + userId + '\n' +
                "  review/profileName: " + profileName + '\n' +
                "  review/helpfulness: " + helpfulness + '\n' +
                "  review/score: " + new DecimalFormat("0.0").format(score) + '\n' +
                "  review/time: " + time + '\n' +
                "  review/summary: " + summary + '\n' +
                "  review/text: " + text + '}';
    }

    public Document(String productId, String userId, String profileName, String helpfulness, Double score, String time, String summary, String text) {
        this.productId = productId;
        this.userId = userId;
        this.profileName = profileName;
        this.helpfulness = helpfulness;
        this.score = score;
        this.time = time;
        this.summary = summary;
        this.text = text;
    }

    public Document() {
        this.productId = "";
        this.userId = "";
        this.profileName = "";
        this.helpfulness = "";
        this.score = 0d;
        this.time = "";
        this.summary = "";
        this.text = "";
    }
}
