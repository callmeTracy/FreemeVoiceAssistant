package com.freeme.jsonparse.pojo;

import java.util.List;

public class PersonData {
    private String _id;

    private int _score;

    private String _type;

    private int _visited_num;

    private List<String> height;

    private List<String> name;

    private List<String> sid;

    private List<String> weight;

    private List<String> birthDate;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int get_score() {
        return _score;
    }

    public void set_score(int _score) {
        this._score = _score;
    }

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public int get_visited_num() {
        return _visited_num;
    }

    public void set_visited_num(int _visited_num) {
        this._visited_num = _visited_num;
    }

    public List<String> getHeight() {
        return height;
    }

    public void setHeight(List<String> height) {
        this.height = height;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<String> getSid() {
        return sid;
    }

    public void setSid(List<String> sid) {
        this.sid = sid;
    }

    public List<String> getWeight() {
        return weight;
    }

    public void setWeight(List<String> weight) {
        this.weight = weight;
    }

    public List<String> getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(List<String> birthDate) {
        this.birthDate = birthDate;
    }


}
