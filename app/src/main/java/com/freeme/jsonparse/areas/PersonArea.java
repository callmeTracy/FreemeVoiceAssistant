package com.freeme.jsonparse.areas;

import java.util.List;

import com.freeme.jsonparse.results.PersonResults;

public class PersonArea {

    private String raw_text;

    private String parsed_text;

    private List<PersonResults> results;

    public String getRaw_text() {
        return raw_text;
    }

    public void setRaw_text(String raw_text) {
        this.raw_text = raw_text;
    }

    public String getParsed_text() {
        return parsed_text;
    }

    public void setParsed_text(String parsed_text) {
        this.parsed_text = parsed_text;
    }

    public List<PersonResults> getResults() {
        return results;
    }

    public void setResults(List<PersonResults> results) {
        this.results = results;
    }


}
