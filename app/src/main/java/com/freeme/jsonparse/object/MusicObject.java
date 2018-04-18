package com.freeme.jsonparse.object;

import java.util.List;

public class MusicObject {
    private List<String> byartist;

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    private String singer;
    private String song;

    public List<String> getByartist() {
        return byartist;
    }

    public void setByartist(List<String> byartist) {
        this.byartist = byartist;
    }



}
