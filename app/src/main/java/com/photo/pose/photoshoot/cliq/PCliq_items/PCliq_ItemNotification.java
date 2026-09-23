package com.photo.pose.photoshoot.cliq.PCliq_items;

public class PCliq_ItemNotification {
    private int id;
    private String title, message, image, url, postId;
    private boolean isRead;

    public PCliq_ItemNotification(int id, String title, String message, String image, String url, String postId, boolean isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.image = image;
        this.url = url;
        this.postId = postId;
        this.isRead = isRead;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getImage() { return image; }
    public String getUrl() { return url; }
    public String getPostId() { return postId; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
