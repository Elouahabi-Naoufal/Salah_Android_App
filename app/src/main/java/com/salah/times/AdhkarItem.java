package com.salah.times;

public class AdhkarItem {
    private int id;
    private String text;
    private String type; // "morning" or "evening"
    private int position;

    public AdhkarItem(int id, String text, String type, int position) {
        this.id = id;
        this.text = text;
        this.type = type;
        this.position = position;
    }

    // Getters
    public int getId() { return id; }
    public String getText() { return text; }
    public String getType() { return type; }
    public int getPosition() { return position; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setText(String text) { this.text = text; }
    public void setType(String type) { this.type = type; }
    public void setPosition(int position) { this.position = position; }
}