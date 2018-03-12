package com.lateral.lateral.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lateral.lateral.R;
import com.lateral.lateral.model.Task;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

// Last thing I started before bed
/*
    * then implement query searching (basic query)
        * ask tyler to explain how to use base service for searching
*/

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    //private ArrayList<Task> mTasks;
    private List<Task> mTasks;

    private Context context;

    public MyAdapter(Context context, List<Task> mTasks) {
        this.context = context;
        this.mTasks = mTasks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.task_card, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Task task = mTasks.get(position);
        holder.tvTitle.setText(task.getTitle());
        //Log.d("this should say task1", task.getTitle());
        holder.tvUsername.setText(task.getRequestingUserId());
        holder.tvDate.setText((task.getDate()).toString() );
        //holder.tvCurBid.setText(task.getTitle());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("TASK SELECTED", "Task" + (mTasks.get(position)).getTitle());
//            }
//        });



    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvTitle, tvUsername, tvDate, tvCurBid;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.taskImage);
            tvTitle = itemView.findViewById(R.id.taskTitleTextView);
            tvUsername = itemView.findViewById(R.id.usernameTextView);
            tvDate = itemView.findViewById(R.id.dateTextView);
            tvCurBid = itemView.findViewById(R.id.bidTextView);
        }
    }



}
