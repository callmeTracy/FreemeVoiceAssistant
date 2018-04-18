package com.freeme.jsonparse.results;

import com.freeme.jsonparse.object.TelephoneObject;

public class TelephoneResults {
    private int demand;
    private String domain;
    private String intent;
    private TelephoneObject object;
    private double score;
    private int update;

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

    public TelephoneObject getObject() {
        return object;
    }

    public void setObject(TelephoneObject object) {
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
