package ojassadmin.nitjsr.in.ojassadmin.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ojassadmin.nitjsr.in.ojassadmin.Adapters.AdminListAdapter;
import ojassadmin.nitjsr.in.ojassadmin.Models.AdminListModel;
import ojassadmin.nitjsr.in.ojassadmin.R;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.FIREBASE_REF_ACCESS;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.FIREBASE_REF_ADMIN;

public class AdminListActivity extends AppCompatActivity {
    public RecyclerView rView;
    public List<AdminListModel> list = new ArrayList<>();
    public AdminListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);
        init();
        initializeRecyclerView();
        getData();
    }

    private void initializeRecyclerView() {
        rView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AdminListAdapter(this,list);
        rView.setAdapter(mAdapter);

    }
    private void getData(){
        FirebaseDatabase.getInstance().getReference(FIREBASE_REF_ADMIN).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        AdminListModel data = new AdminListModel(ds.child("name").getValue(String.class)
                                                              ,ds.child("email").getValue(String.class)
                                                             ,ds.child("regID").getValue(String.class)
                                                             ,ds.getKey());
                        data.setNo_access(ds.child(FIREBASE_REF_ACCESS).getChildrenCount());
                        if(data.uid.compareTo(FirebaseAuth.getInstance().getUid())!=0)
                            list.add(data);
                    }
                    Collections.sort(list, new AdminListSorter());
                    mAdapter.notifyDataSetChanged();

                }catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void init() {
        rView = findViewById(R.id.admin_list_recyclerview);
    }
    public class AdminListSorter implements Comparator<AdminListModel> {
        @Override
        public int compare(AdminListModel o1, AdminListModel o2) {
            return (int) (o2.getNo_access()-o1.getNo_access());
        }
    }
}
