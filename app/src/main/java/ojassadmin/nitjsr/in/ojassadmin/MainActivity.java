package ojassadmin.nitjsr.in.ojassadmin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import static ojassadmin.nitjsr.in.ojassadmin.Constants.HIDE_OJASS_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.INTENT_PARAM_SEARCH_SRC;
import static ojassadmin.nitjsr.in.ojassadmin.Constants.SHOW_OJASS_ID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnSearch,btnEvents, btnNoti, btnAdmin;
    private ImageView ivQR, ivLogout;
    private boolean isWarningShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isWarningShown = false;

        btnSearch=(Button)findViewById(R.id.search);
        btnEvents=(Button)findViewById(R.id.events);
        btnNoti = findViewById(R.id.noti);
        btnAdmin = findViewById(R.id.admin);
        ivQR = findViewById(R.id.iv_show_qr);
        ivLogout = findViewById(R.id.iv_logout);

        btnEvents.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnNoti.setOnClickListener(this);
        btnAdmin.setOnClickListener(this);
        ivQR.setOnClickListener(this);
        ivLogout.setOnClickListener(this);
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
        } else if (view == ivQR){
            createPopup();
        } else if (view == ivLogout){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Sure to logout?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    logOut();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }
    }

    private void createPopup() {
        final Dialog QRDialog = new Dialog(this);
        QRDialog.setContentView(R.layout.dialog_qr);
        final ImageView ivQR = QRDialog.findViewById(R.id.iv_qr_code);
        QRDialog.show();
        final String qrCode = "https://api.qrserver.com/v1/create-qr-code/?data="+FirebaseAuth.getInstance().getCurrentUser().getUid()+"&size=240x240&margin=10";
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

    private void logOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();
        FirebaseAuth.getInstance().signOut();
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

    @Override
    public void onBackPressed() {
        if (!isWarningShown){
            isWarningShown = true;
            Toast.makeText(this, "Press again to exit.", Toast.LENGTH_SHORT).show();
        } else super.onBackPressed();
    }
}
