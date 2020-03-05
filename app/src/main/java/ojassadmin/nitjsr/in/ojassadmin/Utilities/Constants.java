package ojassadmin.nitjsr.in.ojassadmin.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
/**
 * Created by Ravi on 2/5/2018.
 */

public class Constants {
    public static final String FIREBASE_REF_USERS = "Users";
    public static final String FIREBASE_REF_ADMIN = "Admins";
    public static final String FIREBASE_REF_EMAIL = "email";
    public static final String FIREBASE_REF_USERNAME = "username";
    public static final String FIREBASE_REF_NAME = "name";
    public static final String FIREBASE_REF_PHOTO = "photoUrl";
    public static final String FIREBASE_REF_MOBILE = "mobile";
    public static final String FIREBASE_REF_COLLEGE = "college";
    public static final String FIREBASE_REF_COLLEGE_REG_ID = "regID";
    public static final String FIREBASE_REF_ACCESS = "access";
    public static final String FIREBASE_REF_BRANCH = "branch";
    public static final String FIREBASE_REF_TSHIRT_SIZE = "tshirtSize";
    public static final String FIREBASE_REF_OJASS_ID = "ojassID";
    public static final String FIREBASE_REF_ACCESS_LEVEL = "accessLevel";
    public static final String FIREBASE_REF_TSHIRT = "isTshirt";
    public static final String FIREBASE_REF_KIT = "isKit";
    public static final String FIREBASE_REF_PAID_AMOUNT = "paidAmount";
    public static final String FIREBASE_REF_RECEIVED_BY = "receivedBy";
    public static final String FIREBASE_REF_REMARK = "remark";
    public static final String FIREBASE_REF_EVENTS = "Events";
    public static final String FIREBASE_REF_EVENT_NAME = "name";
    public static final String FIREBASE_REF_EVENT_RESULT = "result";
    public static final String FIREBASE_REF_EVENT_BRANCH = "branch";
    public static final String FIREBASE_REF_EVENT_TIME = "time";
    public static final String FIREBASE_REF_EVENT_PARTICIPANTS = "participants";

    public static final String FIREBASE_REF_NOTIFICATIONS = "Notifications";

    public static final String FIREBASE_REF_POSTERIMAGES = "PosterImages";
    public static final String FIREBASE_REF_IMG_SRC = "img_url";
    public static final String FIREBASE_REF_IMG_CLICK = "click_url";

    public static final String INTENT_PARAM_SEARCH_ID = "searchId";

    public static final String INTENT_PARAM_SEARCH_SRC = "searchSrc";
    public static final int SRC_SEARCH = 100;
    public static final int SRC_EVENT = 101;
    public static final boolean SHOW_OJASS_ID = true;
    public static final boolean HIDE_OJASS_ID = false;

    public static final String INTENT_PARAM_EVENT_HASH = "eventHash";
    public static final String INTENT_PARAM_SEARCH_FLAG = "searchFlag";
    public static final String INTENT_PARAM_EVENT_NAME = "eventName";
    public static final String INTENT_PARAM_EVENT_BRANCH = "eventBranch";
    public static final String INTENT_PARAM_IS_SOURCE_NEW_USER = "intentParamIsSrcNewUser";
    public static final String INTENT_SOURCE_ = "intentParamSource";

    public static final int SEARCH_FLAG_OJ_ID = 1;
    public static final int SEARCH_FLAG_EMAIL = 2;
    public static final int SEARCH_FLAG_QR = 3;
    public static final int NO_OF_BUTTONS = 8;
    public static final String USER_DUMMY_IMAGE = "https://firebasestorage.googleapis.com/v0/b/ojass18-1cb0d.appspot.com/o/CoreTeamImages%2Fuserdp.png?alt=media&token=15f889fa-6f14-4e23-a259-001e3d017b50";

    public static final ArrayList<String> eventNames = new ArrayList<>();
    public static final HashMap<String, ArrayList<String>> SubEventsMap = new HashMap<>();
    public static final HashMap<String,String> eventHash = new HashMap<>();


    public static final String[] TSHIRT_SIZE = new String[]{
            "XS",
            "S",
            "M",
            "L",
            "XL",
            "XXL"
    };





}
