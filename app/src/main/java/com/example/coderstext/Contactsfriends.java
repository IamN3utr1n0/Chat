package com.example.coderstext;

public class Contactsfriends {
    public String name, status, image;
    public Contactsfriends()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Contactsfriends(String name, String status, String image) {
        this.name = name;
        this.status = status;
        this.image = image;
    }
}