package ojassadmin.nitjsr.in.ojassadmin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_ACCESS_LEVEL;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_ADMIN;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.HIDE_OJASS_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_FLAG;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_SRC;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SEARCH_FLAG_QR;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SHOW_OJASS_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.eventNames;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnSearch,btnEvents, btnNoti, btnAdmin, btnAddUser, btnDbInfo,viewfeedback,sendFeeds;
    private ImageView ivQR, ivLogout;
    private boolean isWarningShown;
    private SharedPrefManager sharedPref;
    private FirebaseUser user;
    private ProgressDialog pDialog;
    int pflag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //startActivity(new Intent(this, FeedsActivity.class));

        init();
        setListeners();
        pDialog.show();
        //getEventHash();
        updateAccess();
        storeEvents();
    }

    private void init() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        sharedPref = new SharedPrefManager(this);


        isWarningShown = false;
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading");
        btnSearch=(Button)findViewById(R.id.search);
        btnEvents=(Button)findViewById(R.id.events);
        btnNoti = findViewById(R.id.noti);
        btnAdmin = findViewById(R.id.admin);
        btnAddUser = findViewById(R.id.add_user);
        btnDbInfo = findViewById(R.id.dbInfo);
        ivQR = findViewById(R.id.iv_show_qr);
        ivLogout = findViewById(R.id.iv_logout);
        viewfeedback=findViewById(R.id.feedbackPage);
        sendFeeds = findViewById(R.id.btn_feeds);
    }
    private void setListeners(){
        btnEvents.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnNoti.setOnClickListener(this);
        btnAdmin.setOnClickListener(this);
        btnAddUser.setOnClickListener(this);
        btnDbInfo.setOnClickListener(this);
        ivQR.setOnClickListener(this);
        ivLogout.setOnClickListener(this);
        viewfeedback.setOnClickListener(this);
        sendFeeds.setOnClickListener(this);
    }
    private void storeEvents() {
        eventNames.clear();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Branches");
        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equalsIgnoreCase("National College Film Festival"))
                        continue;
                    boolean z = false;
                    for (int i = 0; i < eventNames.size(); i++) {
                        if (eventNames.get(i).equals(ds.getKey())) {
                            z = true;
                            break;
                        }
                    }
                    if (!z) {
                        eventNames.add(ds.getKey());
                    }
                }
                if(pflag==1){
                    pDialog.dismiss();
                }
                pflag=1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateAccess() {
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference(FIREBASE_REF_ADMIN).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int accessLevel = Integer.parseInt(dataSnapshot.child(FIREBASE_REF_ACCESS_LEVEL).getValue().toString());
                sharedPref.setAccessLevel(accessLevel);
                if (sharedPref.getAccessLevel() < 3){ //Event Manager
                    btnEvents.setVisibility(View.VISIBLE);
                    btnNoti.setVisibility(View.VISIBLE);
                }
                if (sharedPref.getAccessLevel() < 2){ //Update Access
                    btnAdmin.setVisibility(View.VISIBLE);
                    btnAddUser.setVisibility(View.VISIBLE);
                }
                if (sharedPref.getAccessLevel() == 0) btnDbInfo.setVisibility(View.VISIBLE);

                if(pflag==1){
                    pDialog.dismiss();
                }
                pflag=1;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        isWarningShown = false;
        if (view.getId()==R.id.search) {
            Intent intent=new Intent(MainActivity.this, SearchActivity.class);
            intent.putExtra(INTENT_PARAM_SEARCH_SRC, SHOW_OJASS_ID);
            startActivity(intent);
        } else if (view.getId()==R.id.events) {
            Intent intent=new Intent(MainActivity.this, EventsActivity.class);
            startActivity(intent);
        } else if (view == btnNoti){
            Intent intent=new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        } else if (view == btnAdmin){
            Intent intent=new Intent(MainActivity.this, SearchActivity.class);
            intent.putExtra(INTENT_PARAM_SEARCH_SRC, HIDE_OJASS_ID);
            startActivity(intent);
        } else if (view == btnAddUser){
            Intent intent = new Intent(this, AddUserActivity.class);
            startActivity(intent);
        } else if (view == ivQR){
            try {
                createPopup();
            } catch (Exception e){

            }
        } else if (view == ivLogout){
            moveToProfilePage();
        } else if (view == btnDbInfo){
            startActivity(new Intent(this, DBInfoActivity.class));
        }else if(view == viewfeedback){
            startActivity(new Intent(MainActivity.this,FeedBackActivity.class));
        }else if(view == sendFeeds){
            startActivity(new Intent(MainActivity.this,FeedsActivity.class));
        }
    }

    private void moveToProfilePage() {
        Intent intent = new Intent(this, AdminDetailsActivity.class);
        intent.putExtra(INTENT_PARAM_SEARCH_ID, user.getUid());
        intent.putExtra(INTENT_PARAM_SEARCH_FLAG, SEARCH_FLAG_QR);
        startActivity(intent);
    }

    private void createPopup() {
        final Dialog QRDialog = new Dialog(this);
        QRDialog.setContentView(R.layout.dialog_qr);
        final ImageView ivQR = QRDialog.findViewById(R.id.iv_qr_code);
        QRDialog.show();
        final String qrCode = "https://api.qrserver.com/v1/create-qr-code/?data="+user.getUid()+"&size=240x240&margin=10";
        Picasso.with(this).load(qrCode).fit().networkPolicy(NetworkPolicy.OFFLINE).into(ivQR, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(MainActivity.this).load(qrCode).fit().into(ivQR);
                }
            });
    }

    @Override
    public void onBackPressed() {
        if (!isWarningShown){
            isWarningShown = true;
            Toast.makeText(this, "Press again to exit.", Toast.LENGTH_SHORT).show();
        } else super.onBackPressed();
    }

    public void getEventHash(){
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events");
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Log.d("LookEVENT",
                                "Event Hash: " + data.getKey() +
                                    " Branch name: "+ data.child("branch").getValue() +
                                    " Event Name : " + data.child("name").getValue());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
