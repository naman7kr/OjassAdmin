package ojassadmin.nitjsr.in.ojassadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText adminMobile,adminBranch,adminRegId;
    private Button mRegister;
    private TextView adminName,adminEmail;

    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    private DatabaseReference rootReference=firebaseDatabase.getReference().child("Admins");

    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private FirebaseUser currentUser=firebaseAuth.getCurrentUser();

    private ProgressDialog progressDialog;

    String Name,Email,userId;

    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        adminName=(TextView)findViewById(R.id.admin_name);
        adminEmail=(TextView)findViewById(R.id.admin_email);

        mRegister=(Button)findViewById(R.id.btn_register);

        adminMobile=(EditText)findViewById(R.id.admin_mobile);
        adminBranch=(EditText)findViewById(R.id.admin_branch);
        adminRegId=(EditText)findViewById(R.id.admin_reg_id);

        progressDialog=new ProgressDialog(this);

        Name=currentUser.getDisplayName();
        Email=currentUser.getEmail();
        userId=currentUser.getUid();

        adminName.setText(Name);
        adminEmail.setText(Email);

        mRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_register)
        {
            registerAdmin();
        }
    }

    private void registerAdmin() {

        progressDialog.setTitle("Hang on");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        String inputMobile=adminMobile.getText().toString().trim();
        String inputBranch=adminBranch.getText().toString().trim();
        String inputRegID=adminRegId.getText().toString().trim();

        if (!TextUtils.isEmpty(inputMobile) && !TextUtils.isEmpty(inputBranch) && !TextUtils.isEmpty(inputRegID))
        {
            rootReference.child(userId).child("Name").setValue(Name);
            rootReference.child(userId).child("Email").setValue(Email);
            rootReference.child(userId).child("Mobile").setValue(inputMobile);
            rootReference.child(userId).child("Branch").setValue(inputBranch);
            rootReference.child(userId).child("Registration Id").setValue(inputRegID);

            sharedPrefManager.setIsRegistered(true);
            sharedPrefManager.setIsLoggedIn(true);

            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            finish();
        }
    }
}
