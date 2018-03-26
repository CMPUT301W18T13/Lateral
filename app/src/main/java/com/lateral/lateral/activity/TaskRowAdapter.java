/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.app.Activity;
import android.app.Application;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.lateral.lateral.R;
import com.lateral.lateral.dialog.UserInfoDialog;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.TaskStatus;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.implementation.DefaultBidService;
import com.lateral.lateral.service.implementation.DefaultUserService;

import java.util.ArrayList;

/**
 * Adapter for the TaskRow RecyclerView
 */
public class TaskRowAdapter extends RecyclerView.Adapter<TaskRowAdapter.ViewHolder> {
    private ArrayList<Task> mTasks;
    DefaultUserService defaultUserService = new DefaultUserService();
    DefaultBidService defaultBidService = new DefaultBidService();
    Context context;


    /**
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView tvTitle;
        TextView tvUsername;
        TextView tvDate;
        TextView tvCurBid;
        GridLayout tvTaskStatus;

        /**
         * Constructor for the ViewHolder
         * @param itemView View for the Item
         */
        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.taskImage);
            tvTitle = itemView.findViewById(R.id.taskTitleTextView);
            tvUsername = itemView.findViewById(R.id.usernameTextView);
            tvDate = itemView.findViewById(R.id.dateTextView);
            tvCurBid = itemView.findViewById(R.id.bidTextView);
            tvTaskStatus = itemView.findViewById(R.id.bidStatusIndicator);
        }

    }

    /**
     * Constructor for the TaskRowAdapter
     * @param mTasks List of tasks to set
     */
    public TaskRowAdapter(ArrayList<Task> mTasks, Context context) {
        //this.context = context;
        this.mTasks = mTasks;
        this.context = context;
    }


    /**
     * Create new views (invoked by the layout manager)
     * @param parent Parent view
     * @param viewType Type of view
     * @return The created ViewHolder
     */
    @Override
    public TaskRowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.task_card, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     * @param holder The ViewHolder for the Item
     * @param position The position of the Item in the List
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // need to fill more properly once big/user objects implemented on elastic search
        final Task task = mTasks.get(position);
        holder.tvTitle.setText(task.getTitle());
        //TODO error handle
        holder.tvUsername.setText(context.getString(R.string.username_display, (defaultUserService.getUserByID(task.getRequestingUserId())).getUsername()));
        holder.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = UserInfoDialog.newInstance(task.getRequestingUserId());
                newFragment.show(((Activity)context).getFragmentManager(), "dialog");
            }
        });
        //TODO error handle
        Bid bid = defaultBidService.getLowestBid(task.getId());
        String bidText = "No Bids";
        if (bid != null) {
            bidText = String.valueOf(bid.getAmount());
        }
        holder.tvCurBid.setText(bidText);
        holder.tvDate.setText((task.getDate()).toString() );

        /*
        Sets the task status color accordingly (colors are default, can substitute our own later)
         */
        // get task status -> get color from status -> set background color
        holder.tvTaskStatus.setBackgroundColor(getColorValueFromTaskStatus(task.getStatus()));

    }

    /**
     * Return the size of the dataset
     * @return List of tasks
     */
    @Override
    public int getItemCount() {
        return mTasks.size();
    }


    public int getColorValueFromTaskStatus(TaskStatus status) {

        int statusColor = Color.BLACK;      // default in case no status match

        switch (status) {
            case Requested:
                statusColor = Color.GREEN;
                break;

            case Bidded:
                statusColor = Color.YELLOW;
                break;

            case Done:
                statusColor = Color.RED;
                break;

            case Assigned:
                statusColor = Color.BLUE;
                break;
        }
        return statusColor;
    }

}
