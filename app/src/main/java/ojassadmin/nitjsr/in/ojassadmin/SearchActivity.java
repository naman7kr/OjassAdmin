package ojassadmin.nitjsr.in.ojassadmin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mOjassId,mEmail,mQRCode;
    private IntentIntegrator integrator;

    private DatabaseReference userDataRef= FirebaseDatabase.getInstance().getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mOjassId=(Button)findViewById(R.id.search_by_ojass_id);
        mEmail=(Button)findViewById(R.id.search_by_email);
        mQRCode=(Button)findViewById(R.id.search_by_qr_code);

        mOjassId.setOnClickListener(this);
        mEmail.setOnClickListener(this);
        mQRCode.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.search_by_ojass_id)
        {
            Dialog dialog=new Dialog(this);
            dialog.setContentView(R.layout.ojass_id_dialog);
            dialog.show();

            EditText editText=(EditText)findViewById(R.id.ojass_id);
            Button button=(Button)findViewById(R.id.search_btn_ojass);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        if (view.getId()==R.id.search_by_email)
        {
            Dialog dialog=new Dialog(this);
            dialog.setContentView(R.layout.email_id_dialog);
            dialog.show();

            EditText editText=(EditText)findViewById(R.id.email_id);
            Button button=(Button)findViewById(R.id.search_btn_email);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        if (view.getId()==R.id.search_by_qr_code)
        {
            integrator.initiateScan();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Searching user");
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
            else
            {
                progressDialog.dismiss();
                ID=result.getContents();
                userDataRef.child(ID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userName.setText(dataSnapshot.child("name").getValue().toString());
                        userEmail.setText(dataSnapshot.child("email").getValue().toString());
                        userMobile.setText(dataSnapshot.child("mobile").getValue().toString());
                        layoutItemsGiven.setVisibility(View.VISIBLE);
                        layoutOthers.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
