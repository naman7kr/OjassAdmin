package ojassadmin.nitjsr.in.ojassadmin.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import ojassadmin.nitjsr.in.ojassadmin.Models.Comments;
import ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants;
import ojassadmin.nitjsr.in.ojassadmin.Models.FeedPost;
import ojassadmin.nitjsr.in.ojassadmin.Models.Likes;
import ojassadmin.nitjsr.in.ojassadmin.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.SubEventsMap;

public class FeedsActivity extends AppCompatActivity {

    FirebaseDatabase mDatabase;
    FirebaseUser mUser;
    EditText mBody;
    Spinner mEvent,mSubevent;
    TextView mSelectImageTextView, mImagePathTextView;
    Button mPublish, mClear;
    CardView mLoader;
    LinearLayout mMainLayout;
    Uri mSelectedImageUri;
    ArrayList<String> eventList,subEventList;
    String mSelectedImageDownloadURL;

    private static int IMAGE_PICK_REQUEST_CODE = 0;
    private static String LOG_TAG = "FeedsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);
        initialize();
        mDatabase = FirebaseDatabase.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        setEventsSpinner();
        setSubEventsSpinner();
        if(mUser == null){
            Toast.makeText(this, "Not Authenticated!", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    private void initialize(){
        mEvent = findViewById(R.id.event_edit_text);
        mSubevent = findViewById(R.id.subevent_edit_text);
        mBody = findViewById(R.id.body_edit_text);
        mSelectImageTextView = findViewById(R.id.select_image_text);
        mPublish = findViewById(R.id.publish);
        mLoader = findViewById(R.id.loader);
        mMainLayout = findViewById(R.id.main_layout);
        mImagePathTextView = findViewById(R.id.imagepath);
        mImagePathTextView.setText("");
        mSelectedImageUri = null;
        mSelectedImageDownloadURL = "";
        mClear = findViewById(R.id.clear);

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImagePathTextView.setText("");
                mSelectedImageUri = null;
            }
        });

        mSelectImageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select an image"),
                        IMAGE_PICK_REQUEST_CODE);
            }
        });
        mPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndPublish();
            }
        });
    }
    private void setEventsSpinner() {
        eventList = new ArrayList<>();
        eventList.clear();
        eventList.addAll(Constants.eventNames);
        eventList.add(0,"Ojass");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.spinner_item, eventList);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        mEvent.setAdapter(arrayAdapter);
        mEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSubEventsSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void setSubEventsSpinner(){
        subEventList = new ArrayList<>();
        subEventList.clear();
        if(mEvent.getSelectedItem().toString().compareTo("Ojass")==0){
            subEventList.add(0,"The annual TechnoManagement fest of NIT Jamshedpur");
        }else {
            subEventList.addAll(SubEventsMap.get(mEvent.getSelectedItem().toString()));
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.spinner_item, subEventList);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        mSubevent.setAdapter(arrayAdapter);
    }
    private void validateAndPublish(){
        if((mEvent.getSelectedItem().toString()).equals("")){
            Toast.makeText(this, "Please enter event name", Toast.LENGTH_SHORT).show();
            return;
        }

        if((mBody.getText().toString()).equals("")){
            Toast.makeText(this, "Please enter body text", Toast.LENGTH_SHORT).show();
            return;
        }

        final String currentTime = System.currentTimeMillis() / 1000 + "";
        mMainLayout.animate().alpha(0.2f);
        mLoader.setVisibility(View.VISIBLE);
        String uploadID = mUser.getUid() + currentTime;

        if(mSelectedImageUri == null){
            final FeedPost post = new FeedPost(
                    currentTime,
                    mBody.getText().toString(),
                    mEvent.getSelectedItem().toString(),
                    mSelectedImageDownloadURL,
                    mSubevent.getSelectedItem().toString(),
                    new ArrayList<Likes>(),
                    new ArrayList<Comments>()
            );

            DatabaseReference reference = mDatabase.getReference().child("Feeds");
            String push_key = reference.push().getKey();
            reference.child(push_key).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(FeedsActivity.this, "Feed created",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FeedsActivity.this, "Error: Could not create Feed",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference storageReference = storage.getReference("uploads/" + uploadID);
            UploadTask uploadTask = storageReference.putFile(mSelectedImageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadURi = task.getResult();
                        String imageURL = downloadURi.toString();
                        mSelectedImageDownloadURL = imageURL;
                        Log.e(LOG_TAG, mSelectedImageDownloadURL);

                        final FeedPost post = new FeedPost(
                                currentTime,
                                mBody.getText().toString(),
                                mEvent.getSelectedItem().toString(),
                                mSelectedImageDownloadURL,
                                mSubevent.getSelectedItem().toString(),
                                new ArrayList<Likes>(),
                                new ArrayList<Comments>()
                        );

                        DatabaseReference reference = mDatabase.getReference().child("Feeds");
                        String push_key = reference.push().getKey();
                        reference.child(push_key).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(FeedsActivity.this, "Feed created",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(FeedsActivity.this, "Error: Could not create Feed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Log.e(LOG_TAG, "Error fetching downloading URL");
                        Toast.makeText(FeedsActivity.this, "Error: Could not upload image",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_PICK_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                mSelectedImageUri = data.getData();
                mImagePathTextView.setText(mSelectedImageUri.toString());
            }
        }
    }
}
