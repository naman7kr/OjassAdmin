package ojassadmin.nitjsr.in.ojassadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_ACCESS_LEVEL;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_ADMIN;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_BRANCH;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_COLLEGE;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_COLLEGE_REG_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_EMAIL;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_EVENTS;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_EVENT_NAME;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_EVENT_PARTICIPANTS;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_EVENT_RESULT;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_KIT;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_MOBILE;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_NAME;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_OJASS_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_PAID_AMOUNT;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_PHOTO;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_RECEIVED_BY;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_REMARK;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_TSHIRT;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_TSHIRT_SIZE;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_USERS;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_EVENT_HASH;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_EVENT_NAME;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_FLAG;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_SRC;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SEARCH_FLAG_EMAIL;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SEARCH_FLAG_OJ_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SEARCH_FLAG_QR;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SRC_EVENT;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SRC_SEARCH;

public class UsersDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText userName,userInstitute,userRegNo,userPaymentDetails,userRemarks,userOjassID, userBranch;
    private TextView userEmail,userMobile, userHash;
    private CheckBox tShirtCheckBox,kitCheckBox;
    private Spinner sizeSpinner;
    private Button btnRegister, btnEditProfile, btnAddUser;
    private ProgressDialog pd;
    private boolean isSizeSelected;
    private int accessLevel = 3;
    private String userHashID;
    private boolean trackTshirt, trackKit, trackPayment;
    private FirebaseUser user;
    private DatabaseReference userDataRef, eventReference, adminRef;
    private static final String NOT_REGISTERED = "Not Registered";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_details);

        userDataRef = FirebaseDatabase.getInstance().getReference(FIREBASE_REF_USERS);
        eventReference = FirebaseDatabase.getInstance().getReference("tempEvent");
        adminRef = FirebaseDatabase.getInstance().getReference(FIREBASE_REF_ADMIN);
        user = FirebaseAuth.getInstance().getCurrentUser();

        trackTshirt = true;
        trackKit = true;
        trackPayment = true;

        pd = new ProgressDialog(this);
        pd.setMessage("Fetching User...");
        pd.setTitle("Please Wait");
        pd.setCancelable(false);

        isSizeSelected=false;

        userName=(EditText)findViewById(R.id.edit_text_user_name);
        userName.setEnabled(false);
        userInstitute=(EditText)findViewById(R.id.edit_text_user_institute);
        userInstitute.setEnabled(false);
        userRegNo=(EditText)findViewById(R.id.edit_text_user_reg_no);
        userRegNo.setEnabled(false);
        userPaymentDetails=(EditText)findViewById(R.id.edit_text_paid_amt);
        userRemarks=(EditText)findViewById(R.id.edit_text_user_remark);
        userBranch = findViewById(R.id.edit_text_branch);
        userOjassID=(EditText)findViewById(R.id.edit_text_user_ojass_id);
        userOjassID.setEnabled(false);

        userEmail=(TextView)findViewById(R.id.text_view_user_email);
        userMobile=(TextView)findViewById(R.id.text_view_user_phone);
        userHash = findViewById(R.id.tv_user_hash);

        tShirtCheckBox=(CheckBox)findViewById(R.id.checkbox_tshirt);
        kitCheckBox=(CheckBox)findViewById(R.id.checkbox_kit);

        sizeSpinner=(Spinner)findViewById(R.id.sp_tshirt_size);

        btnRegister=(Button)findViewById(R.id.btn_register_user);
        btnEditProfile=(Button)findViewById(R.id.btn_edit_profile);
        btnEditProfile.setVisibility(View.GONE);
        btnAddUser=(Button)findViewById(R.id.add_user_to_event);

        btnRegister.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);
        btnAddUser.setOnClickListener(this);
        findViewById(R.id.ib_edit_profile).setOnClickListener(this);

        Intent intent = getIntent();
        final String ID = intent.getStringExtra(INTENT_PARAM_SEARCH_ID);
        final int SEARCH_FLAG = intent.getIntExtra(INTENT_PARAM_SEARCH_FLAG, 0);
        final int SEARCH_SRC = intent.getIntExtra(INTENT_PARAM_SEARCH_SRC, 0);

        searchUser(SEARCH_FLAG, ID);
        handleButtonVisibility(SEARCH_SRC);
        prohibitEdit();
    }

    private void handleButtonVisibility(int search_src) {
        if (search_src == SRC_EVENT)
            btnAddUser.setVisibility(View.VISIBLE);
        else if (new SharedPrefManager(this).getAccessLevel() < 2)
            findViewById(R.id.ib_edit_profile).setVisibility(View.VISIBLE);
    }

    private void prohibitEdit() {
        userOjassID.setEnabled(false);
        userName.setEnabled(false);
        userInstitute.setEnabled(false);
        userRegNo.setEnabled(false);
        userBranch.setEnabled(false);
        userPaymentDetails.setEnabled(false);
        userRemarks.setEnabled(false);
        kitCheckBox.setEnabled(false);
        tShirtCheckBox.setEnabled(false);
        sizeSpinner.setEnabled(false);
    }

    private void searchUser(int search_flag, String id) {
        pd.show();
        switch (search_flag){
            case SEARCH_FLAG_OJ_ID :
                userDataRef.orderByChild(FIREBASE_REF_OJASS_ID).equalTo("OJ"+id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null){
                            for (DataSnapshot child : dataSnapshot.getChildren()){
                                userHashID = child.getKey();
                                fillData(child);
                            }
                        } else showError();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showError();
                    }
                });
                break;
            case SEARCH_FLAG_EMAIL :
                userDataRef.orderByChild(FIREBASE_REF_EMAIL).equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot){
                        if (dataSnapshot.getValue() != null){
                            for (DataSnapshot child : dataSnapshot.getChildren()){
                                userHashID = child.getKey();
                                fillData(child);
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
                userDataRef.child(userHashID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) fillData(dataSnapshot);
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

    private void showError() {
        if (pd.isShowing()) pd.dismiss();
        Toast.makeText(this, "User Not Found!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void fillData(DataSnapshot dataSnapshot) {
        Picasso.with(this).load(dataSnapshot.child(FIREBASE_REF_PHOTO).getValue().toString()).fit().into((ImageView)findViewById(R.id.iv_user_image));
        if (dataSnapshot.child(FIREBASE_REF_OJASS_ID).exists()) userOjassID.setText(dataSnapshot.child(FIREBASE_REF_OJASS_ID).getValue().toString());
        else userOjassID.setText(NOT_REGISTERED);
        userHash.setText(userHashID);
        userName.setText(dataSnapshot.child(FIREBASE_REF_NAME).getValue().toString());
        userEmail.setText(dataSnapshot.child(FIREBASE_REF_EMAIL).getValue().toString());
        userMobile.setText(dataSnapshot.child(FIREBASE_REF_MOBILE).getValue().toString());
        userInstitute.setText(dataSnapshot.child(FIREBASE_REF_COLLEGE).getValue().toString());
        userRegNo.setText(dataSnapshot.child(FIREBASE_REF_COLLEGE_REG_ID).getValue().toString());
        userBranch.setText(dataSnapshot.child(FIREBASE_REF_BRANCH).getValue().toString());
        if (dataSnapshot.child(FIREBASE_REF_PAID_AMOUNT).exists()){
            trackPayment = false;
            userPaymentDetails.setText(dataSnapshot.child(FIREBASE_REF_PAID_AMOUNT).getValue().toString());
        }
        if (dataSnapshot.child(FIREBASE_REF_REMARK).exists()) userRemarks.setText(dataSnapshot.child(FIREBASE_REF_REMARK).getValue().toString());
        if (dataSnapshot.child(FIREBASE_REF_TSHIRT).exists()){
            trackTshirt = false;
            tShirtCheckBox.setChecked(true);
        }
        if (dataSnapshot.child(FIREBASE_REF_KIT).exists()){
            trackKit = false;
            kitCheckBox.setChecked(true);
        }
        setTshirtSize(dataSnapshot.child(FIREBASE_REF_TSHIRT_SIZE).getValue().toString());
        if (pd.isShowing()) pd.dismiss();
    }

    private void setTshirtSize(String s) {
        for (int i = 0 ; i < getResources().getStringArray(R.array.tshirt_size).length; i++)
            if (s.equals(getResources().getStringArray(R.array.tshirt_size)[i].split(" ")[0])) sizeSpinner.setSelection(i);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btn_edit_profile) {
            sendValueToServer();
        } else if (view.getId()==R.id.add_user_to_event) {
            final String EVENT_HASH = getIntent().getStringExtra(INTENT_PARAM_EVENT_HASH);
            final String EVENT_SELECTED = getIntent().getStringExtra(INTENT_PARAM_EVENT_NAME);
            userDataRef.child(userHashID).child(FIREBASE_REF_EVENTS).child(EVENT_HASH).child(FIREBASE_REF_EVENT_NAME).setValue(EVENT_SELECTED);
            userDataRef.child(userHashID).child(FIREBASE_REF_EVENTS).child(EVENT_HASH).child(FIREBASE_REF_EVENT_RESULT).setValue("TBA");
            eventReference.child(EVENT_HASH).child(FIREBASE_REF_EVENT_PARTICIPANTS).child(userHashID).child(FIREBASE_REF_NAME).setValue(userName.getText().toString());
            eventReference.child(EVENT_HASH).child(FIREBASE_REF_EVENT_PARTICIPANTS).child(userHashID).child(FIREBASE_REF_OJASS_ID).setValue(userOjassID.getText().toString());
            Toast.makeText(UsersDetailsActivity.this, "Participant added", Toast.LENGTH_SHORT).show();
            finish();
        } else if (view.getId() == R.id.ib_edit_profile){
            enableEdit();
        }
    }

    private void sendValueToServer() {
        DatabaseReference hashRef = userDataRef.child(userHashID);
        if (!userOjassID.getText().toString().equals("OJ")) hashRef.child(FIREBASE_REF_OJASS_ID).setValue(userOjassID.getText().toString());
        hashRef.child(FIREBASE_REF_NAME).setValue(userName.getText().toString());
        hashRef.child(FIREBASE_REF_COLLEGE).setValue(userInstitute.getText().toString());
        hashRef.child(FIREBASE_REF_COLLEGE_REG_ID).setValue(userRegNo.getText().toString());
        hashRef.child(FIREBASE_REF_BRANCH).setValue(userBranch.getText().toString());
        hashRef.child(FIREBASE_REF_TSHIRT_SIZE).setValue(sizeSpinner.getSelectedItem().toString().split(" ")[0]);
        if (!TextUtils.isEmpty(userPaymentDetails.getText().toString())) hashRef.child(FIREBASE_REF_PAID_AMOUNT).setValue(userPaymentDetails.getText().toString());
        if (!TextUtils.isEmpty(userRemarks.getText().toString())) hashRef.child(FIREBASE_REF_REMARK).setValue(userRemarks.getText().toString());
        if (trackTshirt && tShirtCheckBox.isChecked()) hashRef.child(FIREBASE_REF_TSHIRT).setValue(user.getEmail());
        if (trackKit && kitCheckBox.isChecked()) hashRef.child(FIREBASE_REF_KIT).setValue(user.getEmail());
        if (trackPayment && !TextUtils.isEmpty(userPaymentDetails.getText())) hashRef.child(FIREBASE_REF_RECEIVED_BY).setValue(user.getEmail());
        Toast.makeText(this, "Values updated!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void enableEdit() {
        findViewById(R.id.ib_edit_profile).setVisibility(View.GONE);
        btnEditProfile.setVisibility(View.VISIBLE);

        if (userOjassID.getText().toString().equals(NOT_REGISTERED)) userOjassID.setText("OJ");
        userName.setEnabled(true);
        userInstitute.setEnabled(true);
        userRegNo.setEnabled(true);
        userBranch.setEnabled(true);
        userRemarks.setEnabled(true);
        kitCheckBox.setEnabled(true);
        tShirtCheckBox.setEnabled(true);
        sizeSpinner.setEnabled(true);
        userOjassID.setEnabled(true);
        userPaymentDetails.setEnabled(true);
    }
}
