package com.freeme.jsonparse.areas;

import java.util.List;

import com.freeme.jsonparse.results.TelephoneResults;

public class TelephoneArea {
    private String parsed_text;
    private String raw_text;
    private List<TelephoneResults> results;

    public String getParsed_text() {
        return parsed_text;
    }

    public void setParsed_text(String parsed_text) {
        this.parsed_text = parsed_text;
    }

    public String getRaw_text() {
        return raw_text;
    }

    public void setRaw_text(String raw_text) {
        this.raw_text = raw_text;
    }

    public List<TelephoneResults> getResults() {
        return results;
    }

    public void setResults(List<TelephoneResults> results) {
        this.results = results;
    }

}