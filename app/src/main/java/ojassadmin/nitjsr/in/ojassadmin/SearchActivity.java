package ojassadmin.nitjsr.in.ojassadmin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
    private Toolbar toolbar;

    private DatabaseReference userDataRef= FirebaseDatabase.getInstance().getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar=(Toolbar)findViewById(R.id.search_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search users");

        mOjassId=(Button)findViewById(R.id.search_by_ojass_id);
        mEmail=(Button)findViewById(R.id.search_by_email);
        mQRCode=(Button)findViewById(R.id.search_by_qr_code);

        integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);

        mOjassId.setOnClickListener(this);
        mEmail.setOnClickListener(this);
        mQRCode.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.search_by_ojass_id)
        {
            final Dialog dialog=new Dialog(SearchActivity.this);
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
                            Intent intent=new Intent(SearchActivity.this,UsersDetailsActivity.class);
                            intent.putExtra("ID",ojassID);
                            intent.putExtra("Number","1");
                            startActivity(intent);
                        }
                    }
            });
        }
        if (view.getId()==R.id.search_by_email)
        {
            final Dialog dialog=new Dialog(SearchActivity.this);
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
                            Intent intent=new Intent(SearchActivity.this,UsersDetailsActivity.class);
                            intent.putExtra("Number","2");
                            intent.putExtra("ID",emailID);
                            startActivity(intent);
                        }
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
                String ID;
                progressDialog.dismiss();
                ID=result.getContents().toString();
                Intent intent=new Intent(this,UsersDetailsActivity.class);
                intent.putExtra("ID",ID);
                intent.putExtra("Number","3");
                startActivity(intent);
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
