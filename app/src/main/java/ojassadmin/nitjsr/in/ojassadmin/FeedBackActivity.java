package ojassadmin.nitjsr.in.ojassadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class FeedBackActivity extends AppCompatActivity implements FeedRecyclerViewAdapter.SelectedItem {
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
                    FeedBack feedBack=(FeedBack) dataSnapshot1.getValue(FeedBack.class);
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
        adapter=new FeedRecyclerViewAdapter(list,this);
        feedlbacklist.setAdapter(adapter);
    }

    @Override
    public void ItemSelected(int position) {
        FirebaseDatabase.getInstance().getReference().child("feedback").child(list.get(position).key).removeValue();
    }
}
