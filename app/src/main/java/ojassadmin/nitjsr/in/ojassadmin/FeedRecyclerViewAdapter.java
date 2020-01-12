package ojassadmin.nitjsr.in.ojassadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.MyViewHolder>{
    @NonNull
    ArrayList<FeedBack> list;
    SelectedItem listner;

    public FeedRecyclerViewAdapter(@NonNull ArrayList<FeedBack> list,SelectedItem listner) {
        this.list = list;
        this.listner=listner;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlefeedbackitem,parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.subject.setText(list.get(position).subject);
        holder.content.setText(list.get(position).message);
        holder.name.setText(list.get(position).name);
        holder.email.setText(list.get(position).email);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.ItemSelected(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public interface SelectedItem{
        void ItemSelected(int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,email,content,subject;
        Button button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.authorDisplayName);
            email=itemView.findViewById(R.id.authorEmail);
            content=itemView.findViewById(R.id.feedbackContent);
            subject=itemView.findViewById(R.id.feedbackSubject);
            button=itemView.findViewById(R.id.feedbackDelete);
        }
    }
}
