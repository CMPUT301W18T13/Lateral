package com.lateral.lateral;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import org.apache.commons.lang3.ArrayUtils;

import static java.lang.Character.isDigit;

//TODO: have the task view activity call this
//TODO: set functionality of buttons

/*Heavy influence from https://stackoverflow.com/a/13342157/5262028*/
public class BidDialog extends Dialog implements android.view.View.OnClickListener {
    private boolean defaultAmountCleared = false;
    private Button bidButton;
    private Button cancel_button;
    private Activity activity;
    private EditText amountPlainText;

    public BidDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bid_dialog);
        bidButton = (Button) findViewById(R.id.bid_btn);
        cancel_button = (Button) findViewById(R.id.cancel_btn);
        bidButton.setOnClickListener(this);
        cancel_button.setOnClickListener(this);

        amountPlainText = findViewById(R.id.amount_pt);
        amountPlainText.setOnClickListener(this);
        amountPlainText.addTextChangedListener(new TextWatcher() {

            private String oldValue = "";
            //private DecimalFormat df = (DecimalFormat)DecimalFormat.getInstance(Locale.CANADA);
            //I beleive there is a event queue
            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c)
            {

                //See if the system change produced the event. If yes, return
                String newValue = amountPlainText.getText().toString();
                if (newValue.equals(oldValue)){
                    return;
                }

                int cursorPosition = amountPlainText.getSelectionStart() - 1;

                String[] newValueSplit = newValue.split("\\.");
                if (newValueSplit.length <= 2 && newValueSplit.length > 0){


                    if (newValueSplit[0].length() > 6){
                        amountPlainText.setText(oldValue);
                        amountPlainText.setSelection(cursorPosition);
                        return;
                    }

                    if (newValueSplit.length == 2) {
                        if (newValueSplit[1].length() > 2) {
                            amountPlainText.setText(oldValue);
                            amountPlainText.setSelection(cursorPosition);
                            return;
                        }
                    }
                } else if (newValueSplit.length > 2) {
                    amountPlainText.setText(oldValue);
                    amountPlainText.setSelection(cursorPosition);
                    return;
                }

                char[] charArray = newValue.toCharArray();
                Character[] digits = ArrayUtils.toObject(charArray);
                Character digit;
                boolean decimal_point_found = false;
                for (int i = 0; i < digits.length; i++) {
                    // Determines if the specified character is a digit
                    digit = digits[i];
                    if (!isDigit(digit)) {
                        if (!digit.toString().equals(".") ||
                                digit.toString().equals(".") && decimal_point_found){
                            amountPlainText.setText(oldValue);
                            amountPlainText.setSelection(cursorPosition);
                            return;
                        } else {
                            decimal_point_found = true;
                        }
                    }
                }

                oldValue = newValue;

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bid_btn:
                dismiss();
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
            default:
                break;
        }
    }

    public void CreateBid(){
        String[] newValueSplit = amountPlainText.getText().toString().split("\\.");
        if (amountPlainText.getText().toString().split("\\.").length < 0) {
            return;
        }


    }


}
