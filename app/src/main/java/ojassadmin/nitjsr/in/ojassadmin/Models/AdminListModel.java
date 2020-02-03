package ojassadmin.nitjsr.in.ojassadmin.Models;

public class AdminListModel {
    public String name;
    public String email;
    public String regID;
    public String uid;
    public long no_access;

    public AdminListModel(){}
    public AdminListModel(String name, String email, String regID, String uid) {
        this.name = name;
        this.email = email;
        this.regID = regID;
        this.uid = uid;
    }

    public long getNo_access() {
        return no_access;
    }

    public void setNo_access(long no_access) {
        this.no_access = no_access;
    }
}
