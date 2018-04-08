/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.activity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lateral.lateral.R;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.TaskStatus;
import com.lateral.lateral.service.BidService;
import com.lateral.lateral.service.TaskService;
import com.lateral.lateral.service.implementation.DefaultBidService;
import com.lateral.lateral.service.implementation.DefaultTaskService;

import java.math.BigDecimal;
import java.util.ArrayList;

//TODO: use recycler view
//TODO: set unaccepted bids to not accepted and don't delete them

/**
 * Class to work the Bid List into the List/RecyclerView
 */
public class BidRowAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Bid> bids;
    private Task task;
    private static LayoutInflater inflater = null;
    private Activity bidListActivity;
    private String usernameFormat;
    private String amountFormat;

    /**
     * Constructor for the adapter
     * @param context The current context
     * @param bids The list of bids to add
     * @param task the task to watch
     * @param bidListActivity The activity to use this in
     */
    public BidRowAdapter(Context context, ArrayList<Bid> bids, Task task, Activity bidListActivity) {

        this.context = context;
        this.bids = bids;
        this.task = task;
        this.bidListActivity = bidListActivity;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Gets the count of all the bids in the list
     * @return The count of all the bids
     */
    @Override
    public int getCount() {
        return bids.size();
    }

    /**
     * Gets an item at the specified position
     * @param position Position in the list
     * @return The item at the position
     */
    @Override
    public Object getItem(int position) {
        return bids.get(position);
    }

    /**
     * Gets the position of the item in the list
     * @param position The position of the item
     * @return The position of the item in the list
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Returns the view used to represent an item
     * @param position The position of the item in the list
     * @param convertView The view to display the info in
     * @param parent The parent view for the adapter
     * @return The fully populated view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Bid bid = bids.get(position);
        View vi = convertView;
        if (vi == null) vi = inflater.inflate(R.layout.bid_card, null);
        TextView usernameTextView = vi.findViewById(R.id.username_tv);
        TextView amountTextView = vi.findViewById(R.id.amount_tv);

        //prevent the resizing of TextViews
        //usernameTextView.setMaxWidth(usernameTextView.getWidth());
        //amountTextView.setMaxWidth(usernameTextView.getWidth());

        String username = bid.getBidder().getUsername();
        String formattedUsername = String.format(usernameFormat, username);
        usernameTextView.setText(formattedUsername);

        String amount = bid.getAmount().toString();
        String formattedAmount = String.format(amountFormat, amount);
        amountTextView.setText(formattedAmount);

        Button acceptButton = vi.findViewById(R.id.accept_btn);
        Button declineButton = vi.findViewById(R.id.decline_btn);

        acceptButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                task.setAssignedBidId(bid.getId());
                task.setStatus(TaskStatus.Assigned);
                TaskService taskService = new DefaultTaskService();
                try{
                    taskService.update(task);
                } catch(Exception e){
                    Toast errorToast = Toast.makeText(context,
                            "Failed to update task", Toast.LENGTH_SHORT);
                    errorToast.show();
                }
                bidListActivity.setResult(Activity.RESULT_OK);
                ((Activity)context).finish();
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                bids.remove(bid);
                BidService bidService = new DefaultBidService();
                bidService.delete(bid.getId());

                if (bids.size() == 0){
                    task.setStatus(TaskStatus.Requested);
                    TaskService taskService = new DefaultTaskService();
                    taskService.update(task);
                }
                notifyDataSetChanged();
            }
        });

        return vi;
    }

    /**
     * Sets the displayed username according to a certain format
     * @param usernameFormat the format to display the username
     */
    public void setUsernameFormat(String usernameFormat){ this.usernameFormat = usernameFormat; }

    /**
     * Sets the displayed amount according to a certain format
     * @param amountFormat the format to display the amount
     */
    public void setAmountFormat(String amountFormat){
        this.amountFormat = amountFormat;
    }
}
