/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.lateral.lateral.R;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.PhotoGallery;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.TaskStatus;
import com.lateral.lateral.service.implementation.DefaultBidService;
import com.lateral.lateral.service.implementation.DefaultUserService;
import com.lateral.lateral.widget.PhotoImageView;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
// TODO: BUG: Title overflows on task card if too long

/**
 * Adapter for the TaskRow RecyclerView
 */
public class TaskRowAdapter extends RecyclerView.Adapter<TaskRowAdapter.ViewHolder> {
    private ArrayList<Task> mTasks;
    private ArrayList<Bid> mBids;
    private Context context;


    /**
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        PhotoImageView imageView;
        TextView tvTitle;
        TextView tvUsername;
        TextView tvDate;
        TextView tvCurBid;
        TextView tvNewBids;
        TextView tvCurrentBidTitle;
        GridLayout tvTaskStatus;

        /**
         * Constructor for the ViewHolder
         *
         * @param itemView View for the Item
         */
        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.taskImage);
            tvTitle = itemView.findViewById(R.id.taskTitleTextView);
            tvUsername = itemView.findViewById(R.id.usernameTextView);
            tvDate = itemView.findViewById(R.id.dateTextView);
            tvCurBid = itemView.findViewById(R.id.bidTextView);
            tvNewBids = itemView.findViewById(R.id.newBidTextView);
            tvTaskStatus = itemView.findViewById(R.id.bidStatusIndicator);
            tvCurrentBidTitle = itemView.findViewById(R.id.currentBidTitleText);
        }

    }

    /**
     * Constructor for the TaskRowAdapter
     *
     * @param mTasks List of tasks to set
     */
    public TaskRowAdapter(ArrayList<Task> mTasks, Context context, ArrayList<Bid> mBids) {
        //this.context = context;
        this.mTasks = mTasks;
        this.context = context;
        this.mBids = mBids;
    }


    /**
     * Create new views (invoked by the layout manager)
     *
     * @param parent   Parent view
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
     *
     * @param holder   The ViewHolder for the Item
     * @param position The position of the Item in the List
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d("ADAPTER", Integer.toString(position));
        // need to fill more properly once big/user objects implemented on elastic search
        final Task task = mTasks.get(position);
        BigDecimal bid;
        String bidText;
        DateFormat df;
        PhotoGallery gallery;
        int bidsNotViewed;
        String bidsNotViewedString;
        boolean displayBidsNotViewed = false;

        holder.tvTitle.setText(task.getTitle());
        //holder.tvUsername.setText(context.getString(R.string.username_display, (defaultUserService.getUserByID(task.getRequestingUserId())).getUsername()));
        holder.tvUsername.setText(context.getString(R.string.username_display, task.getRequestingUserUsername()));
        //TODO error handle
        //Bid bid = defaultBidService.getLowestBid(task.getId());
        bid = task.getLowestBidValue();
        bidText = "No Bids";
        if (bid != null) {
            //bidText = String.valueOf(bid.getAmount());
            bidText = String.valueOf(bid);
        }
        holder.tvCurBid.setText(bidText);
        //holder.tvDate.setText((task.getDate()).toString() );

        /*
        Sets the task status color accordingly (colors are default, can substitute our own later)
         */
        // get task status -> get color from status -> set background color
        holder.tvTaskStatus.setBackgroundColor(getColorValueFromTaskStatus(task.getStatus()));

        df = new SimpleDateFormat("MMM dd yyyy", Locale.CANADA);
        holder.tvDate.setText(df.format(task.getDate()));

        /*
        deals with displaying new bids indicator on task card
         */
        if (context instanceof RequestedTasksViewActivity) {
            bidsNotViewed = task.getBidsNotViewed();
            bidsNotViewedString = String.valueOf(bidsNotViewed);

            if (bidsNotViewed > 1) {
                bidsNotViewedString += " new bids!";
                displayBidsNotViewed = true;
            } else if (bidsNotViewed == 1) {
                bidsNotViewedString += " new bid!";
                displayBidsNotViewed = true;
            }

            if (displayBidsNotViewed) {
                //Log.d("BNVS", bidsNotViewedString);
                holder.tvNewBids.setText(bidsNotViewedString);
            }
        } else if (context instanceof AssignedAndBiddedTasksViewActivity) {
            String leadingBidText;
            String outBidString;
            //Log.d("ADAPTER", "mybid = " + mBids.get(position).getAmount() + "leading = " + bid);
            if ((mBids.get(position).getAmount()).equals(bid)) {
                leadingBidText = "Your Leading Bid:";
                outBidString = "";

            } else {
                leadingBidText = "Leading Bid:";
                outBidString = "You've Been Outbid!";
                //holder.tvNewBids.setText(outBidString);
            }
            holder.tvNewBids.setText(outBidString);
            holder.tvCurrentBidTitle.setText(leadingBidText);
        }


        gallery = task.getPhotoGallery();
        holder.imageView.setImage(gallery.get(0));
    }

    /**
     * Return the size of the dataset
     *
     * @return List of tasks
     */
    @Override
    public int getItemCount() {
        return mTasks.size();
    }


    private int getColorValueFromTaskStatus(TaskStatus status) {
        switch (status) {
            case Requested:
                return ContextCompat.getColor(context, R.color.statusRequested);
            case Bidded:
                return ContextCompat.getColor(context, R.color.statusBidded);
            case Assigned:
                return ContextCompat.getColor(context, R.color.statusAssigned);
            case Done:
                return ContextCompat.getColor(context, R.color.statusDone);
            default:
                return Color.BLACK;
        }
    }
}
