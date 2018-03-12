package com.lateral.lateral.activity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lateral.lateral.R;
import com.lateral.lateral.model.Task;
import java.util.ArrayList;

public class TaskRowAdapter extends RecyclerView.Adapter<TaskRowAdapter.ViewHolder> {
    private ArrayList<Task> mTasks;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvTitle;
        TextView tvUsername;
        TextView tvDate;
        TextView tvCurBid;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.taskImage);
            tvTitle = itemView.findViewById(R.id.taskTitleTextView);
            tvUsername = itemView.findViewById(R.id.usernameTextView);
            tvDate = itemView.findViewById(R.id.dateTextView);
            tvCurBid = itemView.findViewById(R.id.bidTextView);
        }
    }

    public TaskRowAdapter(ArrayList<Task> mTasks) {
        //this.context = context;
        this.mTasks = mTasks;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TaskRowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.task_card, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Task task = mTasks.get(position);
        holder.tvTitle.setText(task.getTitle());
        holder.tvUsername.setText(task.getRequestingUserId());
        holder.tvDate.setText((task.getDate()).toString() );
    }

    // Return the size of dataset
    @Override
    public int getItemCount() {
        return mTasks.size();
    }

}
