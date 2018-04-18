package com.freeme.jsonparse.results;

import com.freeme.jsonparse.object.AppObject;

public class AppResults {
    private String domain;
    private String intent;
    private float score;
    private int demand;
    private int update;
    private AppObject object;

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

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

    public AppObject getObject() {
        return object;
    }

    public void setObject(AppObject object) {
        this.object = object;
    }


}
