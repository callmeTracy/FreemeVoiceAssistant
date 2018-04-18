package com.freeme.jsonparse.results;

import com.freeme.jsonparse.object.SearchObject;

public class SearchResults {
    private String domain;

    private String intent;

    private double score;

    private SearchObject object;

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

    public SearchObject getObject() {
        return object;
    }

    public void setObject(SearchObject object) {
        this.object = object;
    }

}
