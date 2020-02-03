package ojassadmin.nitjsr.in.ojassadmin.Models;

public class FeedBack {
    public String name,email,subject,message,key,timestamp;

    public FeedBack() {
    }

    public FeedBack(String name, String email, String subject, String message, String timestamp) {
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.timestamp = timestamp;
    }

}
