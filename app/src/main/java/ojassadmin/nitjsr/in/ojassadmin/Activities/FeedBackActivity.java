package ojassadmin.nitjsr.in.ojassadmin.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ojassadmin.nitjsr.in.ojassadmin.Models.FeedBack;
import ojassadmin.nitjsr.in.ojassadmin.Adapters.FeedRecyclerViewAdapter;
import ojassadmin.nitjsr.in.ojassadmin.R;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class FeedBackActivity extends AppCompatActivity {
    RecyclerView feedlbacklist;
    FeedRecyclerViewAdapter adapter;
    ArrayList<FeedBack> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        init();

        fetch();
    }

    private void fetch() {
        FirebaseDatabase.getInstance().getReference().child("feedback").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    FeedBack feedBack= new FeedBack(dataSnapshot1.child("name").getValue(String.class),
                                                    dataSnapshot1.child("email").getValue(String.class),
                                                    dataSnapshot1.child("subject").getValue(String.class),
                                                    dataSnapshot1.child("message").getValue(String.class),
                                                    dataSnapshot1.child("timestamp").getValue(String.class));

                    feedBack.key=dataSnapshot1.getKey();
                    list.add(feedBack);
                    Log.d("asdf", "onDataChange: "+feedBack.key);
                }
                Collections.reverse(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void init() {
        list=new ArrayList<>();
        feedlbacklist=findViewById(R.id.feebackRecyclerView);
        feedlbacklist.setLayoutManager(new LinearLayoutManager(this));
        adapter=new FeedRecyclerViewAdapter(list);
        feedlbacklist.setAdapter(adapter);
    }

}
