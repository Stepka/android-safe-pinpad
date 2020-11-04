package com.frm.safe_pin_pad_demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.frm.safe_pin_pad_library.PinPadActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PinPad";

    Button setNewPinButton_;
    Button enterPinButton_;
    Button clearPinButton_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setNewPinButton_ = (Button)findViewById(R.id.set_new_pin_button);
        enterPinButton_ = (Button)findViewById(R.id.enter_pin_button);
        clearPinButton_ = (Button)findViewById(R.id.clear_pin_button);

        setNewPinButton_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, PinPadActivity.class);
                intent.putExtra("type", PinPadActivity.SET_PIN_CODE);
                startActivityForResult(intent, PinPadActivity.SET_PIN_CODE);
            }
        });
        enterPinButton_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PinPadActivity.class);
                intent.putExtra("type", PinPadActivity.GET_PIN_CODE);
                startActivityForResult(intent, PinPadActivity.GET_PIN_CODE);
            }
        });
        clearPinButton_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PinPadActivity.class);
                intent.putExtra("type", PinPadActivity.CLEAR_PIN_CODE);
                startActivityForResult(intent, PinPadActivity.CLEAR_PIN_CODE);
            }
        });
    }

    // This method is called when the second activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == PinPadActivity.GET_PIN_CODE) {
            if (resultCode == RESULT_OK) {

                // get String data from Intent
                String pin = data.getStringExtra("pin");

                Log.i(TAG, "Entered pin: " + pin);
            } else {
                Log.i(TAG, "Pin error: " + data.getStringExtra("info"));
            }
        } else if (requestCode == PinPadActivity.SET_PIN_CODE) {
            if (resultCode == RESULT_OK) {

                // get String data from Intent
                String pin = data.getStringExtra("pin");
                String pinType = data.getStringExtra("pin_type");

                Log.i(TAG, "Pin changed ");
                Log.i(TAG, "New pin: " + pin);
                Log.i(TAG, "Pin type: " + pinType);
            }
        }
    }
}
