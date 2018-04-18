package com.freeme.jsonparse.results;

import com.freeme.jsonparse.object.CalendarObject;

public class CalendarResults {
    private String domain;
    private String intent;
    private double score;
    private int demand;
    private int update;
    private CalendarObject object;

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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
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

    public CalendarObject getObject() {
        return object;
    }

    public void setObject(CalendarObject object) {
        this.object = object;
    }


}
