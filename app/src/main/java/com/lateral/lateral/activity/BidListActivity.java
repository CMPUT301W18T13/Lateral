package com.lateral.lateral.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.lateral.lateral.R;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.service.BidService;
import com.lateral.lateral.service.UserService;
import com.lateral.lateral.service.implementation.DefaultBidService;
import com.lateral.lateral.service.implementation.DefaultUserService;

import java.util.ArrayList;

public class BidListActivity extends AppCompatActivity {
    public static final String TASK_ID = "com.lateral.lateral.TASK_ID_INTERESTED_IN";

    private ListView bidListView;
    private BidRowAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_list);
        bidListView = findViewById(R.id.bid_lv);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        String taskID = intent.getStringExtra(TASK_ID);
        DefaultBidService bidService = new DefaultBidService();
        ArrayList<Bid> bids = bidService.getAllBidsByTaskID(taskID);

        UserService userService = new DefaultUserService();
        for (Bid bid: bids){
            bid.setBidder(userService.getById(bid.getBidderId()));
        }

        adapter = new BidRowAdapter(this, bids, BidListActivity.this);
        adapter.setUsernameFormat(getString(R.string.bid_card_username_text_field));
        adapter.setAmountFormat(getString(R.string.bid_card_amount_text_field));
        bidListView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(MyTaskViewActivity.BID_EVENT, adapter.getBidEvent());
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        return true; //returning true produces the onActivityResult event needed
    }

}
