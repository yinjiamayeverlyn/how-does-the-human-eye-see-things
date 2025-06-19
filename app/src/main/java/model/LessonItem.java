package model;

public class LessonItem {
    private String title;
    private String description;
    private int imageResId;  // drawable image resource

    public LessonItem(String title, String description, int imageResId) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
}