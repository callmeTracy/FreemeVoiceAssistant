package com.freeme.jsonparse.results;

import com.freeme.jsonparse.object.MusicObject;

public class MusicResults {
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

    public String getParser() {
        return parser;
    }

    public void setParser(String parser) {
        this.parser = parser;
    }

    public MusicObject getObject() {
        return object;
    }

    public void setObject(MusicObject object) {
        this.object = object;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    private String domain;
    private String intent;
    private String parser;
    private MusicObject object;
    private double score;



}
