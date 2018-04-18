package com.freeme.jsonparse.results;

import com.freeme.jsonparse.object.MessageObject;

public class MessageResults {
    private String domain;
    private String intent;
    private double score;
    private int demand;
    private int update;
    private MessageObject object;

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public MessageObject getObject() {
        return object;
    }

    public void setObject(MessageObject object) {
        this.object = object;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

}
