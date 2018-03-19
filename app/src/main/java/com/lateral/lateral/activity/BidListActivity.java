package com.lateral.lateral.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lateral.lateral.R;
import com.lateral.lateral.model.Bid;
import com.lateral.lateral.model.Task;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.BidService;
import com.lateral.lateral.service.implementation.DefaultBidService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class BidListActivity extends AppCompatActivity {
    public static final String TASK_ID = "com.lateral.lateral.TASK_ID_INTERESTED_IN";

    private ListView bidListView;
    private BidRowAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_list);
        bidListView = (ListView) findViewById(R.id.bid_lv);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        String taskID = intent.getStringExtra(TASK_ID);
        BidService bidService = new DefaultBidService();
        ArrayList<Bid> bids = bidService.getAllBidsByTaskID(taskID); //TODO: load User information using id

        // TODO: Test
        User user = new User("Mordo", "7801234567", "mal@gmail.com", "djfkn");
        //String des = "20 Boogies have taken the Prime Minister and his family hostage in his home. They are armed with fully automatic weapons " +
        //        "You have a green light to use deadly force. Canada future will be in your hands";
        //Task task = new Task("Secure The Hostages", des);
        //task.setRequestingUser(user);
        //BigDecimal num = new BigDecimal(12.12);
        //Bid bid = new Bid(new BigDecimal("51.14").setScale(2, RoundingMode.CEILING));
        //Bid bid2 = new Bid(new BigDecimal(101.10).setScale(2, RoundingMode.CEILING));
        //bid.setBidder(user);
        //bid2.setBidder(user);
        //ArrayList<Bid> bids = new ArrayList<>();
        //bids.add(bid);
        //bids.add(bid2);
        for (Bid bid : bids) {
            bid.setBidder(user);
        }
        // TODO: Test

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
