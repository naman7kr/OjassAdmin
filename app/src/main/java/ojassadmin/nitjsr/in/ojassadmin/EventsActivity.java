package ojassadmin.nitjsr.in.ojassadmin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;

import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_EVENT_HASH;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_EVENT_NAME;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_FLAG;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_SRC;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SEARCH_FLAG_EMAIL;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SEARCH_FLAG_OJ_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SEARCH_FLAG_QR;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SRC_EVENT;

public class EventsActivity extends AppCompatActivity implements View.OnClickListener{

    private Spinner spinner;
    private String[] arrEventList;
    HashMap<String,String> hashMap=new HashMap<String, String>();
    private Button btnEmailID,btnOjassID,btnQRCode;
    private IntentIntegrator integrator;
    private String eventKey,eventSelected;
    private int isEventSelected=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        btnOjassID=(Button)findViewById(R.id.search_by_ojass_id_event);
        btnEmailID=(Button)findViewById(R.id.search_by_email_event);
        btnQRCode=(Button)findViewById(R.id.search_by_qr_code_event);

        integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);

        spinner=(Spinner)findViewById(R.id.spinner_add_event);

        DatabaseReference eventReference= FirebaseDatabase.getInstance().getReference("tempEvent");
        eventReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrEventList = new String[(int) dataSnapshot.getChildrenCount()];
                int currIndex = 0;
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    hashMap.put(dataSnapshot1.child("eventNAme").getValue().toString(),dataSnapshot1.getKey());
                    arrEventList[currIndex] = dataSnapshot1.child("eventNAme").getValue().toString();
                    currIndex++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(EventsActivity.this,
                        android.R.layout.simple_list_item_1, arrEventList);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==0)
                {
                    isEventSelected=0;
                }
                if (position>0)
                {
                    isEventSelected=1;
                    eventSelected=parent.getItemAtPosition(position).toString();
                    eventKey=hashMap.get(eventSelected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnOjassID.setOnClickListener(this);
        btnEmailID.setOnClickListener(this);
        btnQRCode.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(EventsActivity.this, "Searching failed", Toast.LENGTH_LONG).show();
            } else {
                openUserDetail(result.getContents(), SEARCH_FLAG_QR);
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.search_by_ojass_id_event) {
            if(isEventSelected==0) {
                Toast.makeText(this, "Select any event", Toast.LENGTH_SHORT).show();
            } else {
                String ojassID = ((EditText)findViewById(R.id.et_event_oj_id)).getText().toString().trim();
                if (!TextUtils.isEmpty(ojassID)) openUserDetail(ojassID, SEARCH_FLAG_OJ_ID);
                else Toast.makeText(this, "Field can't be left blank!", Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId()==R.id.search_by_email_event) {
            if (isEventSelected==0) {
                Toast.makeText(this, "Select any event", Toast.LENGTH_SHORT).show();
            } else {
                String emailID = ((EditText)findViewById(R.id.et_event_email_id)).getText().toString().trim();
                if (!TextUtils.isEmpty(emailID)) openUserDetail(emailID, SEARCH_FLAG_EMAIL);
                else Toast.makeText(this, "Field can't be left blank!", Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId()==R.id.search_by_qr_code_event) {
            if(isEventSelected==0) Toast.makeText(this, "Select any event", Toast.LENGTH_SHORT).show();
            else integrator.initiateScan();
        }
    }

    private void openUserDetail(String ID, int FLAG){
        Intent intent=new Intent(EventsActivity.this,UsersDetailsActivity.class);
        intent.putExtra(INTENT_PARAM_SEARCH_FLAG, FLAG);
        intent.putExtra(INTENT_PARAM_SEARCH_ID, ID);
        intent.putExtra(INTENT_PARAM_SEARCH_SRC, SRC_EVENT);
        intent.putExtra(INTENT_PARAM_EVENT_NAME, eventSelected);
        intent.putExtra(INTENT_PARAM_EVENT_HASH, eventKey);
        startActivity(intent);
    }
}
