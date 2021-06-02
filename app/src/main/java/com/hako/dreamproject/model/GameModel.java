package com.hako.dreamproject.model;

public class GameModel {


    String id;
    String name;
    String playing;
    String image;
    String type;
    String url;
    String rotation;
    String roomId;

    public GameModel(String id, String name, String playing, String image, String type, String url, String rotation, String roomId) {
        this.id = id;
        this.name = name;
        this.playing = playing;
        this.image = image;
        this.type = type;
        this.url = url;
        this.rotation = rotation;
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getRotation() {
        return rotation;
    }
    public void setRotation(String rotation) {
        this.rotation = rotation;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPlaying() {
        return playing;
    }
    public void setPlaying(String playing) {
        this.playing = playing;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
}
