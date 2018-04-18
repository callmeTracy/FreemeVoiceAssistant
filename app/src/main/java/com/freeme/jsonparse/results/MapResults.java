package com.freeme.jsonparse.results;

import com.freeme.jsonparse.object.MapObject;

public class MapResults {

    private String domain;

    private String intent;

    private MapObject object;

    private double score;

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

    public MapObject getObject() {
        return object;
    }

    public void setObject(MapObject object) {
        this.object = object;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }


}
