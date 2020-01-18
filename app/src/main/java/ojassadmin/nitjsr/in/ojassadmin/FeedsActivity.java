package ojassadmin.nitjsr.in.ojassadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FeedsActivity extends AppCompatActivity {

    FirebaseDatabase mDatabase;
    FirebaseUser mUser;
    EditText mEvent, mSubevent, mBody, mImageURL;
    Button mPublish;
    CardView mLoader;
    LinearLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);
        mDatabase = FirebaseDatabase.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if(mUser == null){
            Toast.makeText(this, "Not Authenticated!", Toast.LENGTH_SHORT).show();
            finish();
        }

        initialize();
    }

    private void initialize(){
        mEvent = findViewById(R.id.event_edit_text);
        mSubevent = findViewById(R.id.subevent_edit_text);
        mBody = findViewById(R.id.body_edit_text);
        mImageURL = findViewById(R.id.imageurl_edit_text);
        mPublish = findViewById(R.id.publish);
        mLoader = findViewById(R.id.loader);
        mMainLayout = findViewById(R.id.main_layout);

        mPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndPublish();
            }
        });
    }

    private void validateAndPublish(){
        if((mEvent.getText().toString()).equals("")){
            Toast.makeText(this, "Please enter event name", Toast.LENGTH_SHORT).show();
            return;
        }

        if((mSubevent.getText().toString()).equals("")){
            Toast.makeText(this, "Please enter sub-event name", Toast.LENGTH_SHORT).show();
            return;
        }

        if((mBody.getText().toString()).equals("")){
            Toast.makeText(this, "Please enter body text", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentTime = System.currentTimeMillis() / 1000 + "";
        String feedID = mUser.getUid() + currentTime;
        FeedPost post = new FeedPost(
                currentTime,
                mBody.getText().toString(),
                mEvent.getText().toString(),
                mImageURL.getText().toString(),
                mSubevent.getText().toString(),
                new ArrayList<Likes>(),
                new ArrayList<Comments>()
        );

        DatabaseReference reference = mDatabase.getReference().child("Feeds");
        String push_key = reference.push().getKey();
        mMainLayout.animate().alpha(0.2f);
        mLoader.setVisibility(View.VISIBLE);
        reference.child(push_key).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(FeedsActivity.this, "Feed created",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

}
