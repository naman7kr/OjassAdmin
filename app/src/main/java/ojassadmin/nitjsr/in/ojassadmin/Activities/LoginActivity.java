package ojassadmin.nitjsr.in.ojassadmin.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import ojassadmin.nitjsr.in.ojassadmin.R;
import ojassadmin.nitjsr.in.ojassadmin.Utilities.SharedPrefManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.FIREBASE_REF_ACCESS_LEVEL;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.FIREBASE_REF_ADMIN;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.INTENT_PARAM_IS_SOURCE_NEW_USER;


public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "LoginActivity";
    private ProgressDialog pd;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        sharedPrefManager = new SharedPrefManager(this);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            //auto login
            if(sharedPrefManager.isRegistered()){
                moveToMainActivity();
                Toast.makeText(LoginActivity.this, "Welcome to Ojass Indic Erudition Dashboard! ", Toast.LENGTH_LONG).show();
            }else {
                isRegisteredUser(mAuth.getCurrentUser());
            }
        }


        Picasso.with(this).load(R.drawable.login_bg).fit().into((ImageView)findViewById(R.id.login_screen));


        pd = new ProgressDialog(this);
        pd.setTitle("Hang On");
        pd.setMessage("Connecting you to Mothership...");
        pd.setCancelable(false);



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.btn_signIN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            pd.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account);
            } catch (ApiException e){
                Log.d(TAG, "Google Sign in failed. Reason: " + e.getMessage());
                if (pd.isShowing()) pd.dismiss();
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && mAuth.getCurrentUser()!=null){
                            sharedPrefManager.setEmail(account.getEmail());

                            Log.e("LOL", sharedPrefManager.getEmail() + "WTF");
                            Log.e("LOL", mAuth.getCurrentUser().getDisplayName() + "WTF");
                            sharedPrefManager.setIsLoggedIn(true);
                            isRegisteredUser(mAuth.getCurrentUser());
                        } else {
                            if (pd.isShowing()) pd.dismiss();
                            Log.d(TAG,"Authentication failed. Reason: "+ task.getException());
                            Toast.makeText(LoginActivity.this, "LoginActivity Failed. Reason: "+ task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //method to go to main activity
    private void moveToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    //method to go to register activity
    private void moveToRegisterActivity() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    //check if user is already registerd
    private void isRegisteredUser(FirebaseUser user) {
        if(mAuth.getCurrentUser()!=null) {
            final String fName = mAuth.getCurrentUser().getDisplayName().split(" ")[0];

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(FIREBASE_REF_ADMIN).child(mAuth.getCurrentUser().getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.exists()) {
                            sharedPrefManager.setIsRegistered(true);
                            moveToMainActivity();
                            Toast.makeText(LoginActivity.this, "Welcome to Ojass Indic Erudition Dashboard! " + fName, Toast.LENGTH_LONG).show();
                        } else {
                            sharedPrefManager.setIsRegistered(false);
                            moveToRegisterActivity();
                            Toast.makeText(LoginActivity.this, "Hey " + fName + "! Let us know you better.", Toast.LENGTH_LONG).show();
                        }
                        if (pd.isShowing()) pd.dismiss();
                    }catch (Exception e){

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            Toast.makeText(this, "Cannot get user", Toast.LENGTH_SHORT).show();
        }
    }

    //method to sign in
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pd.isShowing()) pd.dismiss();
    }
}
