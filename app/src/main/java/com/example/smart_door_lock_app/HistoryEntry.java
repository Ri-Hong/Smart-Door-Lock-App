package com.example.smart_door_lock_app;

public class HistoryEntry {
    int Image;
    String Name;
    String Time;

    public HistoryEntry(int image, String name, String time) {
        Image = image;
        Name = name;
        Time = time;
    }

    //Getter methods
    public int getImage() {
        return Image;
    }

    public String getName() {
        return Name;
    }

    public String getTime() {
        return Time;
    }

}
