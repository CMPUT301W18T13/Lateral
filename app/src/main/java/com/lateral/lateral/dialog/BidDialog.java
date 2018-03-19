package com.lateral.lateral.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lateral.lateral.R;
import com.lateral.lateral.model.Bid;

import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.Character.isDigit;

//TODO: Implement a better solution to error popup blocking cancel button

public class BidDialog extends Dialog implements android.view.View.OnClickListener {
    private boolean defaultAmountCleared = false;
    private EditText amountPlainText;
    private Bid newBid;

    public BidDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newBid = null;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bid_dialog);
        Button bidButton = findViewById(R.id.bid_btn);
        Button cancelButton = findViewById(R.id.cancel_btn);
        LinearLayout bidDialogLayout = findViewById(R.id.bid_dialog_lo);
        bidButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        bidDialogLayout.setOnClickListener(this);

        amountPlainText = findViewById(R.id.amount_pt);
        amountPlainText.setOnClickListener(this);
        amountPlainText.addTextChangedListener(new TextWatcher() {

            private String oldValue = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                //See if the system change produced the event. If yes, return
                String newValue = amountPlainText.getText().toString();
                if (newValue.equals(oldValue)){
                    return;
                }

                int cursorPosition = amountPlainText.getSelectionStart() - 1;

                char[] charArray = newValue.toCharArray();
                Character[] digits = ArrayUtils.toObject(charArray);
                Character digit;
                for (int i = 0; i < digits.length; i++) {
                    // Determines if the specified character is a digit
                    digit = digits[i];
                    if (!isDigit(digit)) {
                        if (!digit.toString().equals(".")){
                            amountPlainText.setText(oldValue);
                            amountPlainText.setSelection(cursorPosition);
                            return;
                        }
                    }
                }

                oldValue = newValue;

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        });

    }

    @Override
    public void onClick(View view) {
        amountPlainText.setError(null); // a quick fix for error message blocking cancel button
        switch (view.getId()) {
            case R.id.bid_btn:
                createNewBid();
                if (newBid != null) {
                    dismiss();
                }
                break;
            case R.id.cancel_btn:
                dismiss();
                break;
            case R.id.amount_pt:
                if (!defaultAmountCleared){
                    amountPlainText.getText().clear();
                    amountPlainText.setTextColor(Color.parseColor("black"));
                    defaultAmountCleared = true;
                }
            case R.id.bid_dialog_lo:
                Log.i("the message would clear", "the message would clear");
            default:
                break;
        }
    }

    private void createNewBid(){
        if (!bidAmountValid()){
            return;
        }

        BigDecimal amount = new BigDecimal(amountPlainText.getText().toString());
        amount = amount.setScale(2, RoundingMode.CEILING);  // Needed to keep precision
        newBid = new Bid(amount);
    }

    private boolean bidAmountValid(){
        String bidAmount = amountPlainText.getText().toString();
        String[] newValueSplit = bidAmount.split("\\.");
        if (amountPlainText.getText().toString().split("\\.").length == 0) {
            amountPlainText.setError("A single decimal is not a bid");
            return false;
        }

        if (newValueSplit.length <= 2 && newValueSplit.length > 0){

            if (newValueSplit[0].length() > 7){
                amountPlainText.setError("Too many dollar digits");
                return false;
            }

            if (newValueSplit.length == 2) {
                if (newValueSplit[1].length() > 2) {
                    amountPlainText.setError("Too many cent digits");
                    return false;
                }
            }
        } else if (newValueSplit.length > 2) {
            amountPlainText.setError("Too many decimals");
            return false;
        }

        try{
            new BigDecimal(bidAmount);
        } catch (NumberFormatException e){
            amountPlainText.setError("An unknown formatting issue has occurred");
            return false;
        }

        return true;
    }

    public Bid getNewBid(){ return newBid; }
}
