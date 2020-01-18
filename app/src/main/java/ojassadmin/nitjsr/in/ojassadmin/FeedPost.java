package ojassadmin.nitjsr.in.ojassadmin;

import android.widget.LinearLayout;

import java.util.ArrayList;

public class FeedPost {

    public String timestamp;
    public String event;
    public String subEvent;
    public String content;
    public String imageURL;
    public ArrayList<Likes> likes;
    public ArrayList<Comments> comments;

    public FeedPost(){

    }

    public FeedPost(String timestamp, String content,String event,  String imageURL, String subEvent,
                    ArrayList<Likes> mlikes,ArrayList<Comments> mcomments) {
        this.timestamp = timestamp;
        this.event = event;
        this.subEvent = subEvent;
        this.content = content;
        this.imageURL = imageURL;

        likes=new ArrayList<>();
        comments=new ArrayList<>();
        this.likes=mlikes;
        this.comments=mcomments;

    }

    public void setLikes(ArrayList<Likes> likes) {
        this.likes = likes;
    }

    public void setComments(ArrayList<Comments> comments) {
        this.comments = comments;
    }

    public ArrayList<Likes> getLikes() {
        return likes;
    }

    public ArrayList<Comments> getComments() {
        return comments;
    }

    public String getEvent() {
        return event;
    }

    public String getSubEvent() {
        return subEvent;
    }

    public String getContent() {
        return content;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setSubEvent(String subEvent) {
        this.subEvent = subEvent;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}
