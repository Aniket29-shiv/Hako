package com.hako.dreamproject.model;

public class Online {
    public Online() {

    }

    String sonline;
    String time;
    String score;
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Online(String online, String time, String score, String status) {
        this.sonline = online;
        this.time = time;
        this.score = score;
        this.status = status;
    }

    public String getOnline() {
        return sonline;
    }

    public void setOnline(String online) {
        this.sonline = online;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
