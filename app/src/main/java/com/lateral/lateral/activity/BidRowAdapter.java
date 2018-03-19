package com.lateral.lateral.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.lateral.lateral.R;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.BidEvent;
import com.lateral.lateral.service.BidService;
import com.lateral.lateral.service.implementation.DefaultBidService;

import java.util.ArrayList;

import static com.lateral.lateral.model.BidEvent.*;

//TODO: use recycler view
//TODO: make disgusting bid_card look nicer

public class BidRowAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Bid> bids;
    private BidEvent bidEvent = NO_EVENT;
    private static LayoutInflater inflater = null;
    private Activity BidListActivtyClass;
    private String usernameFormat;
    private String amountFormat;

    public BidRowAdapter(Context context, ArrayList<Bid> bids, Activity BidListActivtyClass) {
        this.context = context;
        this.bids = bids;
        this.BidListActivtyClass = BidListActivtyClass;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return bids.size();
    }

    @Override
    public Object getItem(int position) {
        return bids.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Bid bid = bids.get(position);
        View vi = convertView;
        if (vi == null) vi = inflater.inflate(R.layout.bid_card, null);
        TextView usernameTextView = vi.findViewById(R.id.username_tv);
        TextView amountTextView = vi.findViewById(R.id.amount_tv);

        //prevent the resizing of TextViews
        usernameTextView.setMaxWidth(usernameTextView.getWidth());
        amountTextView.setMaxWidth(usernameTextView.getWidth());

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
            public void onClick(View v)
            {
                //Delete all other bids which have not been accepted
                for (Bid b : bids){ //TODO uncomment vvv
                    if (!b.getId().equals(bid.getId())){
                        BidService bidService = new DefaultBidService();
                        bidService.delete(b.getId());
                    }
                } //TODO uncomment ^^^

                //send back to myTaskActivity, the bid that has been accepted and its id
                Intent returnIntent = new Intent();
                bidEvent = BID_ACCEPTED;
                returnIntent.putExtra(MyTaskViewActivity.BID_EVENT, BID_ACCEPTED);
                returnIntent.putExtra(MyTaskViewActivity.ACCEPTED_BID_ID, bid.getId());
                BidListActivtyClass.setResult(Activity.RESULT_OK, returnIntent);
                ((Activity)context).finish();
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                bids.remove(bid);
                BidService bidService = new DefaultBidService(); //TODO uncomment
                bidService.delete(bid.getId()); //TODO uncomment
                notifyDataSetChanged();
                bidEvent = BID_DECLINED;
            }
        });

        return vi;
    }

    public void setUsernameFormat(String usernameFormat){ this.usernameFormat = usernameFormat; }

    public void setAmountFormat(String amountFormat){
        this.amountFormat = amountFormat;
    }

    public BidEvent getBidEvent(){return bidEvent;}
}
