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
        ProgressDialog progressDialog=new ProgressDialog(EventsActivity.this);
        progressDialog.setTitle("Searching user");
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(EventsActivity.this, "Searching failed", Toast.LENGTH_LONG).show();
            }
            else
            {
                String ID;
                progressDialog.dismiss();
                if (isEventSelected==1)
                {
                    ID=result.getContents().toString();
                    Intent intent=new Intent(EventsActivity.this,UsersDetailsActivity.class);
                    intent.putExtra("ID",ID);
                    intent.putExtra("Number","3");
                    intent.putExtra("FLAG",1);
                    intent.putExtra("eventKey",eventKey);
                    intent.putExtra("eventSelected",eventSelected);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(EventsActivity.this, "Select an event", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.search_by_ojass_id_event)
        {
            final Dialog dialog=new Dialog(EventsActivity.this);
            dialog.setContentView(R.layout.ojass_id_dialog);
            dialog.show();

            final EditText editText=(EditText)dialog.findViewById(R.id.ojass_id);
            Button button=(Button)dialog.findViewById(R.id.search_btn_ojass);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ojassID=editText.getText().toString().trim();
                    if (!TextUtils.isEmpty(ojassID))
                    {
                        dialog.dismiss();
                        if (isEventSelected==1)
                        {
                            Intent intent=new Intent(EventsActivity.this,UsersDetailsActivity.class);
                            intent.putExtra("ID",ojassID);
                            intent.putExtra("Number","1");
                            intent.putExtra("FLAG",1);
                            intent.putExtra("eventKey",eventKey);
                            intent.putExtra("eventSelected",eventSelected);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(EventsActivity.this, "Select an event", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
        if (view.getId()==R.id.search_by_email_event)
        {
            final Dialog dialog=new Dialog(EventsActivity.this);
            dialog.setContentView(R.layout.email_id_dialog);
            dialog.show();


            final EditText editText=(EditText)dialog.findViewById(R.id.email_id);
            Button button=(Button)dialog.findViewById(R.id.search_btn_email);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String emailID=editText.getText().toString().trim();
                    if (!TextUtils.isEmpty(emailID))
                    {
                        dialog.dismiss();
                        if (isEventSelected==1)
                        {
                            Intent intent=new Intent(EventsActivity.this,UsersDetailsActivity.class);
                            intent.putExtra("Number","2");
                            intent.putExtra("ID",emailID);
                            intent.putExtra("FLAG",1);
                            intent.putExtra("eventKey",eventKey);
                            intent.putExtra("eventSelected",eventSelected);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(EventsActivity.this, "Select an event", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
        if (view.getId()==R.id.search_by_qr_code_event)
        {
            integrator.initiateScan();
        }
    }
}
