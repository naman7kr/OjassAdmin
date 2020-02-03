package ojassadmin.nitjsr.in.ojassadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ojassadmin.nitjsr.in.ojassadmin.Activities.AdminDetailsActivity;
import ojassadmin.nitjsr.in.ojassadmin.Models.AdminListModel;
import ojassadmin.nitjsr.in.ojassadmin.R;

import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.INTENT_PARAM_SEARCH_FLAG;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.INTENT_PARAM_SEARCH_ID;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.SEARCH_FLAG_EMAIL;
import static ojassadmin.nitjsr.in.ojassadmin.Utilities.Constants.SEARCH_FLAG_OJ_ID;

public class AdminListAdapter extends RecyclerView.Adapter<AdminListAdapter.MyHolder> {
    public Context mContext;
    public List<AdminListModel> list;
    public AdminListAdapter(Context mContext, List<AdminListModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adminlist,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final AdminListModel data = list.get(position);
        holder.name.setText("Name : "+ data.name);
        holder.email.setText("Email : "+data.email);
        holder.reg.setText("Registration No : " + data.regID);
        holder.access_no.setText("Total No. of access : "+ data.getNo_access());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AdminDetailsActivity.class);
                intent.putExtra(INTENT_PARAM_SEARCH_ID,data.email);
                intent.putExtra(INTENT_PARAM_SEARCH_FLAG,SEARCH_FLAG_EMAIL);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
    TextView name,email,reg, access_no;
    LinearLayout layout;
     public MyHolder(@NonNull View itemView) {
         super(itemView);
         name = itemView.findViewById(R.id.admin_list_name);
         email = itemView.findViewById(R.id.admin_list_email);
         reg = itemView.findViewById(R.id.admin_list_reg);
         layout = itemView.findViewById(R.id.admin_list_layout);
         access_no = itemView.findViewById(R.id.admin_list_access);
     }
 }
}
