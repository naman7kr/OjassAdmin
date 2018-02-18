package ojassadmin.nitjsr.in.ojassadmin;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UsersDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout relativeLayout;

    private EditText userName,userInstitute,userRegNo,userPaymentDetails,userRemarks,userOjassID=null;
    private TextView userEmail,userMobile;
    private CheckBox tShirtCheckBox,kitCheckBox,paymentCheckBox;
    private Spinner sizeSpinner;
    private Button btnRegister,btnEditProfile,btnAddUser;
    private String sizeList[]={"Select size","XS","S","M","L","XL","XXL"};
    private String tshirtSize;
    private boolean isSizeSelected;
    private int accessLevel=3;
    private int FLAG=0;
    private String eventKey=null;
    private String eventSelected=null;

    private Bundle bundle;
    private String ID;
    private String Number;

    private boolean isPayment;

    private DatabaseReference userDataRef= FirebaseDatabase.getInstance().getReference("Users");
    private String userHashID=null;
    private DatabaseReference userParticipatedEventsReference=FirebaseDatabase.getInstance().getReference("Users");
    private DatabaseReference eventReference=FirebaseDatabase.getInstance().getReference("tempEvent");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_details);

        relativeLayout=(RelativeLayout)findViewById(R.id.user_details_layout);

        isPayment=true;
        isSizeSelected=false;

        userName=(EditText)findViewById(R.id.edit_text_user_name);
        userName.setEnabled(false);
        userInstitute=(EditText)findViewById(R.id.edit_text_user_institute);
        userInstitute.setEnabled(false);
        userRegNo=(EditText)findViewById(R.id.edit_text_user_reg_no);
        userRegNo.setEnabled(false);
        userPaymentDetails=(EditText)findViewById(R.id.edit_text_user_payment);
        userRemarks=(EditText)findViewById(R.id.edit_text_user_remark);
        userOjassID=(EditText)findViewById(R.id.edit_text_user_ojass_id);
        userOjassID.setEnabled(false);

        userEmail=(TextView)findViewById(R.id.text_view_user_email);
        userMobile=(TextView)findViewById(R.id.text_view_user_phone);

        tShirtCheckBox=(CheckBox)findViewById(R.id.checkbox_tshirt);
        kitCheckBox=(CheckBox)findViewById(R.id.checkbox_kit);
        paymentCheckBox=(CheckBox)findViewById(R.id.checkbox_payment);

        sizeSpinner=(Spinner)findViewById(R.id.t_shirt_size_spinner);

        btnRegister=(Button)findViewById(R.id.btn_register_user);
        btnEditProfile=(Button)findViewById(R.id.btn_edit_profile);
        btnEditProfile.setVisibility(View.GONE);
        btnAddUser=(Button)findViewById(R.id.add_user_to_event);
        btnAddUser.setVisibility(View.GONE);

        tShirtCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (tShirtCheckBox.isChecked())
                {
                    sizeSpinner.setEnabled(false);
                }
                else if (!tShirtCheckBox.isChecked())
                {
                    sizeSpinner.setEnabled(true);
                }
            }
        });

        bundle=getIntent().getExtras();
        ID=bundle.getString("ID");
        Number=bundle.getString("Number");
        FLAG=bundle.getInt("FLAG");
        eventKey=bundle.getString("eventKey");
        eventSelected=bundle.getString("eventSelected");



        if (Number.equals("1"))
        {
            userDataRef.orderByChild("ojassID").equalTo(ID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child: dataSnapshot.getChildren())
                    {
                        userHashID=child.getKey();
                        userName.setText(child.child("name").getValue().toString());
                        userEmail.setText(child.child("email").getValue().toString());
                        userMobile.setText(child.child("mobile").getValue().toString());
                        userInstitute.setText(child.child("college").getValue().toString());
                        userRegNo.setText(child.child("regID").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        if (Number.equals("2"))
        {
            userDataRef.orderByChild("email").equalTo(ID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    for (DataSnapshot child: dataSnapshot.getChildren())
                    {
                        userHashID=child.getKey();
                        userName.setText(child.child("name").getValue().toString());
                        //userEmail.setText(child.child("email").getValue().toString());
                        //userMobile.setText(child.child("mobile").getValue().toString());
                        //userInstitute.setText(child.child("college").getValue().toString());
                        //userRegNo.setText(child.child("regID").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        if (Number.equals("3"))
        {
            userHashID=ID;
            userDataRef.child(ID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userName.setText(dataSnapshot.child("name").getValue().toString());
                    userEmail.setText(dataSnapshot.child("email").getValue().toString());
                    userMobile.setText(dataSnapshot.child("mobile").getValue().toString());
                    userInstitute.setText(dataSnapshot.child("college").getValue().toString());
                    userRegNo.setText(dataSnapshot.child("regID").getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        ArrayAdapter<String > adapter=new ArrayAdapter<String>(UsersDetailsActivity.this,
                android.R.layout.simple_list_item_1,sizeList);
        sizeSpinner.setAdapter(adapter);

        sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position>0)
                {
                    tshirtSize=parent.getItemAtPosition(position).toString();
                    isSizeSelected=true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       // isEnabled(); //isEnabled for checking access level
        isEventSelected();
        userStatus();

        btnRegister.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);
        btnAddUser.setOnClickListener(this);

    }

    private void userStatus() {
        userDataRef.child(userHashID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isPaid();
                if (dataSnapshot.child("tsirtSize").child("isGiven").exists())
                {
                    tShirtCheckBox.setChecked(true);
                    tShirtCheckBox.setEnabled(false);
                    sizeSpinner.setEnabled(false);
                }
                if (dataSnapshot.child("kit").exists())
                {
                    kitCheckBox.setChecked(true);
                    kitCheckBox.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void isEventSelected() {
        if (FLAG==1)
        {
            btnAddUser.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.GONE);
            btnEditProfile.setVisibility(View.GONE);
            userName.setEnabled(false);
            userInstitute.setEnabled(false);
            userRegNo.setEnabled(false);
            userPaymentDetails.setEnabled(false);
            tShirtCheckBox.setChecked(true);
            tShirtCheckBox.setEnabled(false);
            sizeSpinner.setEnabled(false);
            kitCheckBox.setChecked(true);
            kitCheckBox.setEnabled(false);
            paymentCheckBox.setChecked(true);
            paymentCheckBox.setEnabled(false);
            userPaymentDetails.setEnabled(false);
            userRemarks.setEnabled(false);
            userOjassID.setEnabled(false);
            isPaid();
        }
    }

    private void isPaid() {
        userDataRef.child(userHashID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("payment").exists())
                {
                    paymentCheckBox.setChecked(true);
                    paymentCheckBox.setEnabled(false);
                    String paidAmount=dataSnapshot.child("payment").child("paidAmount").getValue().toString();
                    userPaymentDetails.setText(paidAmount);
                    userPaymentDetails.setTextColor(Color.GREEN);
                    userPaymentDetails.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void isEnabled() {
        if (accessLevel==1)
        {
            btnEditProfile.setVisibility(View.VISIBLE);
            userName.setEnabled(true);
            userInstitute.setEnabled(true);
            userRegNo.setEnabled(true);
            userPaymentDetails.setEnabled(true);
        }
        if (accessLevel==2 || accessLevel==3)
        {

            btnEditProfile.setVisibility(View.GONE);
            userName.setEnabled(false);
            userInstitute.setEnabled(false);
            userRegNo.setEnabled(false);
            userPaymentDetails.setEnabled(false);
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btn_register_user)
        {
            String paidAmount=userPaymentDetails.getText().toString();
            if (paymentCheckBox.isChecked() && !TextUtils.isEmpty(paidAmount))
            {
                userDataRef.child(userHashID).child("payment").child("isPaid").setValue("True");
                userDataRef.child(userHashID).child("payment").child("paidAmount").setValue(paidAmount);
                Toast.makeText(this, "Paid", Toast.LENGTH_SHORT).show();
                isPaid();
            }
            else
            {
                Snackbar snackbar = Snackbar.make(relativeLayout, "Payment details can't be empty", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            if (tShirtCheckBox.isChecked() && isSizeSelected)
            {
                userDataRef.child(userHashID).child("tsirtSize").child("size").setValue(tshirtSize);
                userDataRef.child(userHashID).child("tsirtSize").child("isGiven").setValue("True");
            }
            if (kitCheckBox.isChecked())
            {
                userDataRef.child(userHashID).child("kit").setValue("True");
            }
            String remarks=userRemarks.getText().toString();
            if (!TextUtils.isEmpty(remarks))
            {
                userDataRef.child(userHashID).child("remarks").setValue(remarks);
            }
        }
        if (view.getId()==R.id.btn_edit_profile)
        {
            btnAddUser.setVisibility(View.GONE);
            userName.setEnabled(true);
            userInstitute.setEnabled(true);
            userRegNo.setEnabled(true);
            userRemarks.setEnabled(true);
        }
        if (view.getId()==R.id.add_user_to_event)
        {
            userParticipatedEventsReference.child(userHashID).child("events").child(eventKey).child("participatedEvent").setValue(eventSelected);
            userParticipatedEventsReference.child(userHashID).child("events").child(eventKey).child("eventResult").setValue("Not declared");
            //eventReference.child(eventKey).child("Participants").child(userHashID).child("email").setValue(userEmail);
            //Toast.makeText(UsersDetailsActivity.this, "Participant added", Toast.LENGTH_SHORT).show();

        }
    }
}
