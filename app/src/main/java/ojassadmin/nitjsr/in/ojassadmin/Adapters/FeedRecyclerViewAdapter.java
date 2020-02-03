package ojassadmin.nitjsr.in.ojassadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ojassadmin.nitjsr.in.ojassadmin.Models.FeedBack;
import ojassadmin.nitjsr.in.ojassadmin.R;

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.MyViewHolder>{
    @NonNull
    ArrayList<FeedBack> list;
    SelectedItem listner;
    Context context;

    public FeedRecyclerViewAdapter(@NonNull ArrayList<FeedBack> list) {
        this.list = list;
        this.listner=listner;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlefeedbackitem,parent,false);
        this.context = parent.getContext();
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        try {
            holder.subject.setText(list.get(position).subject);
            holder.content.setText(list.get(position).message);
            holder.name.setText(list.get(position).name);
            holder.email.setText(list.get(position).email);
            holder.reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + list.get(position).email));
                    context.startActivity(i.createChooser(i,"Choose an Email Client"));
                }
            });

            long post_time = Integer.parseInt(list.get(position).timestamp);
            long curr_time = System.currentTimeMillis() / 1000;
            long diff = curr_time - post_time;
            String suffix, prefix;
            if (diff < 5) {
                prefix = "just now";
                suffix = "";
            } else if (diff < 60) {
                suffix = "s ago";
                prefix = diff + "";
            } else if (diff < 3600) {
                suffix = "m ago";
                prefix = diff / 60 + "";
            } else if (diff < 86400) {
                suffix = "hr ago";
                prefix = diff / 3600 + "";
            } else if (diff < 2628003) {
                suffix = "d ago";
                prefix = diff / 86400 + "";
            } else if (diff < 31536000) {
                suffix = "mo ago";
                prefix = diff / 2628003 + "";
            } else {
                suffix = "y ago";
                prefix = diff / 31536000 + "";
            }
            holder.timestamp.setText(prefix + suffix);
        }catch (Exception e){}


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public interface SelectedItem{
        void ItemSelected(int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,email,content,subject,timestamp;
        Button reply;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.authorDisplayName);
            email=itemView.findViewById(R.id.authorEmail);
            content=itemView.findViewById(R.id.feedbackContent);
            subject=itemView.findViewById(R.id.feedbackSubject);
            timestamp = itemView.findViewById(R.id.timestamp);
            reply = itemView.findViewById(R.id.reply);
        }
    }
}
