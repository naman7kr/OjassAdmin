package ojassadmin.nitjsr.in.ojassadmin.Activities;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import ojassadmin.nitjsr.in.ojassadmin.R;
import ojassadmin.nitjsr.in.ojassadmin.Utilities.SharedPrefManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.FIREBASE_REF_ACCESS;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.FIREBASE_REF_ADMIN;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.FIREBASE_REF_BRANCH;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.FIREBASE_REF_COLLEGE_REG_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.FIREBASE_REF_EMAIL;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.FIREBASE_REF_MOBILE;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.FIREBASE_REF_NAME;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.FIREBASE_REF_USERNAME;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.FIREBASE_REF_PHOTO;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.INTENT_PARAM_SEARCH_FLAG;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.INTENT_PARAM_SEARCH_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.NO_OF_BUTTONS;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.SEARCH_FLAG_EMAIL;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.SEARCH_FLAG_QR;

public class AdminDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private SharedPrefManager sharedPref;
    private EditText etName, etEmail, etNumber, etBranch, etReg;
    private Button btnUpdate;
    private ImageButton ibEdit;
    private DatabaseReference adminRef;
    private String userHashID;
    private ProgressDialog pd;
    private ImageView ivImage;
    private FirebaseUser mUser;
    private CheckBox search,events,notification,adminSearch,addUser,dbinfo, viewFeedback,sendFeeds;
    private List<Integer> accessItems = new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_details);
        init();
        initCheckBox();
        adminRef = FirebaseDatabase.getInstance().getReference(FIREBASE_REF_ADMIN);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        pd = new ProgressDialog(this);
        pd.setMessage("Fetching User...");
        pd.setTitle("Please Wait");
        pd.setCancelable(false);

        ibEdit.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        Intent intent = getIntent();
        final String ID = intent.getStringExtra(INTENT_PARAM_SEARCH_ID);
        final int SEARCH_FLAG = intent.getIntExtra(INTENT_PARAM_SEARCH_FLAG, 0);

        if(checkMainAdmin())
            ibEdit.setVisibility(View.VISIBLE);
        else{
            ibEdit.setVisibility(View.GONE);
        }

        prohibitEdit();
        searchUser(SEARCH_FLAG, ID);

    }

    private boolean checkMainAdmin() {
        if(sharedPref.getAdminStatus())
            return true;
        return false;
    }

    private void initCheckBox() {
        search.setChecked(false);
        events.setChecked(false);
        notification.setChecked(false);
        adminSearch.setChecked(false);
        addUser.setChecked(false);
        dbinfo.setChecked(false);
        viewFeedback.setChecked(false);
        sendFeeds.setChecked(false);
    }

    private void init() {
        etName = findViewById(R.id.et_admin_name);
        etEmail = findViewById(R.id.et_admin_email);
        etNumber = findViewById(R.id.et_admin_number);
        etBranch = findViewById(R.id.et_admin_branch);
        etReg = findViewById(R.id.et_admin_reg_id);
        btnUpdate = findViewById(R.id.btn_admin_update);
        ibEdit = findViewById(R.id.ib_admin_edit);
        ivImage = findViewById(R.id.iv_admin_img);
        search = findViewById(R.id.check_user_search);
        events = findViewById(R.id.check_events);
        notification = findViewById(R.id.check_notifications);
        adminSearch = findViewById(R.id.check_admin_search);
        addUser = findViewById(R.id.check_add_user);
        dbinfo = findViewById(R.id.check_db_info);
        viewFeedback = findViewById(R.id.check_viewfeedback);
        sendFeeds = findViewById(R.id.check_sendfeeds);
        sharedPref = new SharedPrefManager(this);

        for(int i=0;i<NO_OF_BUTTONS;i++){
            accessItems.add(i);
        }
    }

    private void checkSelf() {
        if (!TextUtils.isEmpty(userHashID) && userHashID.equals(mUser.getUid())){
            btnUpdate.setText("Log Out");
            btnUpdate.setVisibility(View.VISIBLE);
            ibEdit.setVisibility(View.GONE);
        }
    }

    private void prohibitEdit() {
        etName.setEnabled(false);
        etEmail.setEnabled(false);
        etNumber.setEnabled(false);
        etBranch.setEnabled(false);
        etReg.setEnabled(false);
        btnUpdate.setVisibility(View.GONE);

        search.setEnabled(false);
        events.setEnabled(false);
        notification.setEnabled(false);
        adminSearch.setEnabled(false);
        addUser.setEnabled(false);
        dbinfo.setEnabled(false);
        viewFeedback.setEnabled(false);
        sendFeeds.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        if (view == ibEdit){
            enableField();
        } else if (view == btnUpdate){
            if (btnUpdate.getText().toString().equals("Update")) sendDataToServer();
            else logOut();
        }
    }

    private void logOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();
        FirebaseAuth.getInstance().signOut();
        sharedPref.setIsLoggedIn(false);
        sharedPref.setIsRegistered(false);
        moveToLoginPage();
    }

    private void moveToLoginPage() {
        SharedPrefManager shared = new SharedPrefManager(this);
        shared.setIsLoggedIn(false);
        shared.setIsRegistered(false);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendDataToServer() {
        adminRef.child(userHashID).child(FIREBASE_REF_USERNAME).setValue(etName.getText().toString());
        adminRef.child(userHashID).child(FIREBASE_REF_EMAIL).setValue(etEmail.getText().toString());
        adminRef.child(userHashID).child(FIREBASE_REF_MOBILE).setValue(etNumber.getText().toString());
        adminRef.child(userHashID).child(FIREBASE_REF_BRANCH).setValue(etBranch.getText().toString());
        adminRef.child(userHashID).child(FIREBASE_REF_COLLEGE_REG_ID).setValue(etReg.getText().toString());


        adminRef.child(userHashID).child(FIREBASE_REF_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        int available_access = ds.getValue(Integer.class);
                        CheckBox cb = getMappedCheckbox(available_access);
                        accessItems.remove(new Integer(available_access));
                        if(!cb.isChecked()){
                            //remove value
                            ds.getRef().removeValue();
                        }
                    }
                    for(int i:accessItems){
                        CheckBox cb = getMappedCheckbox(i);
                        if(cb.isChecked()){
                            //add value
                            dataSnapshot.getRef().push().setValue(i);
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Values updated!", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "There was an error", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        finish();
    }
    private CheckBox getMappedCheckbox(int i){
        switch (i){
            case 0:
                return search;
            case 1:
                return events;
            case 2:
                return notification;
            case 3:
                return adminSearch;
            case 4:
                return addUser;
            case 5:
                return dbinfo;
            case 6:
                return viewFeedback;
            case 7:
                return sendFeeds;
        }
        return null;
    }
    private void enableField() {
        etName.setEnabled(true);
        etEmail.setEnabled(true);
        etNumber.setEnabled(true);
        etBranch.setEnabled(true);
        etReg.setEnabled(true);
        btnUpdate.setVisibility(View.VISIBLE);
        ibEdit.setVisibility(View.GONE);

        search.setEnabled(true);
        events.setEnabled(true);
        notification.setEnabled(true);
        adminSearch.setEnabled(true);
        addUser.setEnabled(true);
        dbinfo.setEnabled(true);
        viewFeedback.setEnabled(true);
        sendFeeds.setEnabled(true);
    }

    private void searchUser(int search_flag, String id) {
        pd.show();
        switch (search_flag){
            case SEARCH_FLAG_EMAIL :
                adminRef.orderByChild(FIREBASE_REF_EMAIL).equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot){
                        if (dataSnapshot.getValue() != null){
                            for (DataSnapshot child : dataSnapshot.getChildren()){
                                userHashID = child.getKey();
                                fillData(child);
                                checkSelf();
                            }
                        } else showError();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showError();
                    }
                });
                break;
            case SEARCH_FLAG_QR :

                userHashID = id;
                checkSelf();
                adminRef.child(userHashID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null){
                            fillData(dataSnapshot);
                        }
                        else showError();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showError();
                    }
                });
                break;
        }
    }

    private void fillData(DataSnapshot dataSnapshot) {
        if (pd.isShowing()) pd.dismiss();
        try {
            Log.e("hello",dataSnapshot.child(FIREBASE_REF_NAME).getValue(String.class));
            etName.setText(dataSnapshot.child(FIREBASE_REF_NAME).getValue().toString());
            etNumber.setText(dataSnapshot.child(FIREBASE_REF_MOBILE).getValue().toString());
            etEmail.setText(dataSnapshot.child(FIREBASE_REF_EMAIL).getValue().toString());
            etBranch.setText(dataSnapshot.child(FIREBASE_REF_BRANCH).getValue().toString());
            etReg.setText(dataSnapshot.child(FIREBASE_REF_COLLEGE_REG_ID).getValue().toString());

            for(DataSnapshot ds: dataSnapshot.child(FIREBASE_REF_ACCESS).getChildren()){
                if(ds.exists()){
                    CheckBox cb = getMappedCheckbox(ds.getValue(Integer.class));
                    cb.setChecked(true);
                }
            }
            Picasso.with(this).load(dataSnapshot.child(FIREBASE_REF_PHOTO).getValue().toString()).fit().into(ivImage);
        } catch (Exception e){

        }
    }

    private void showError() {
        if (pd.isShowing()) pd.dismiss();
        Toast.makeText(this, "User Not Found!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
