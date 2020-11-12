package com.frm.safe_pin_pad_library;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class PinPadActivity extends Activity {

    private static final String TAG = "PinPad";

    public static final int GET_PIN_CODE = 0;
    public static final int SET_PIN_CODE = 1;
    public static final int CLEAR_PIN_CODE = 2;
    public static final int CONFIRM_PIN_CODE = 3;

    public static final int RESULT_ERROR = 2;

    public static final String DIGITS = "DIGITS";
    public static final String WORDS = "WORDS";
    public static final String COLORS = "COLORS";
    public static final String FIGURES = "FIGURES";
    public static final String TEXTURES = "TEXTURES";

    private int type_ = GET_PIN_CODE;

    private String currentPinType_ = DIGITS;

    private List<Integer> currentPin_;
    private List<String> currentDigitPin_;
    private List<String> currentWordPin_;
    private List<String> currentColorPin_;
    private List<String> currentFigurePin_;
    private List<String> currentTexturePin_;

    private PinPadView pinPadView_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pin_pad);

        pinPadView_ = (PinPadView) findViewById(R.id.pin_pad_view);

        currentPin_ = new ArrayList<>();

        pinPadView_.clear();

        Bundle b = getIntent().getExtras();
        if(b != null) {
            type_ = b.getInt("type");

            switch (type_) {
                case GET_PIN_CODE:
                    if(PinPadSettings.isPinExist(getApplicationContext())) {
                        pinPadView_.setPromptText("Enter pin code");
                        currentPinType_ = PinPadSettings.getPinType(getApplicationContext());
                        pinPadView_.setPlaceOrder(PinPadView.RANDOM_ORDER);
                    }
                    else {
                        Intent intent = new Intent(PinPadActivity.this, PinPadActivity.this.getCallingActivity().getClass());
                        intent.putExtra("info", "Pin not set yet");
                        setResult(RESULT_ERROR, intent);
                        finish();
                    }
                    break;

                case CLEAR_PIN_CODE:
                    PinPadSettings.setPin(getApplicationContext(), "");
                    PinPadSettings.setPinType(getApplicationContext(), "");
                    PinPadSettings.setLastPinTimestamp(getApplicationContext(), 0);
                    type_ = SET_PIN_CODE;
                case SET_PIN_CODE:
                    pinPadView_.setPromptText("Set pin code");
                    pinPadView_.setPlaceOrder(PinPadView.BASIC_ORDER);
                    break;

                case CONFIRM_PIN_CODE:
                    pinPadView_.setPlaceOrder(PinPadView.SHIFT_ORDER);
                    break;
            }
        }

        pinPadView_.setOnSubmitListener(new PinPadView.OnSubmitListener() {
            @Override
            public void onCompleted(List<Integer> pin) {

                Intent intent = new Intent(PinPadActivity.this, PinPadActivity.this.getCallingActivity().getClass());
                String pinAsString;

                switch (type_)
                {
                    case GET_PIN_CODE:
                        pinAsString = constructPin(pin);
                        if(pinAsString.equals(PinPadSettings.getPin(getApplicationContext()))) {

                            PinPadSettings.setLastPinTimestamp(getApplicationContext(), System.currentTimeMillis() / 1000);
                            intent.putExtra("pin", pinAsString);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            pinPadView_.vibratePhone();
                            type_ = GET_PIN_CODE;
                            pinPadView_.clear();
                            pinPadView_.setPromptText("Enter pin code");
                        }
                        break;

                    case SET_PIN_CODE:
                        type_ = CONFIRM_PIN_CODE;
                        currentPin_ = new ArrayList<>(pin);
                        currentDigitPin_ = new ArrayList<>();
                        currentWordPin_ = new ArrayList<>();
                        currentColorPin_ = new ArrayList<>();
                        currentFigurePin_ = new ArrayList<>();
                        currentTexturePin_ = new ArrayList<>();

                        String[] digits = pinPadView_.getDigits();
                        String[] words = pinPadView_.getWords();
                        String[] colors = pinPadView_.getColors();
                        String[] figures = pinPadView_.getFigures();
                        String[] textures = pinPadView_.getTextures();
                        for (int i = 0; i < pin.size(); i++) {
                            currentDigitPin_.add(digits[pin.get(i)]);
                            currentWordPin_.add(words[pin.get(i)]);
                            currentColorPin_.add(colors[pin.get(i)]);
                            currentFigurePin_.add(figures[pin.get(i)]);
                            currentTexturePin_.add(textures[pin.get(i)]);
                        }
                        pinPadView_.clear();
                        pinPadView_.setPlaceOrder(PinPadView.SHIFT_ORDER);
                        pinPadView_.setPromptText("Confirm pin code");
                        break;

                    case CONFIRM_PIN_CODE:
                        if(confirmSetPin(pin)) {
                            pinAsString = constructPin(pin);
                            intent.putExtra("pin", pinAsString);
                            intent.putExtra("pin_type", currentPinType_);

                            PinPadSettings.setPin(getApplicationContext(), pinAsString);
                            PinPadSettings.setPinType(getApplicationContext(), currentPinType_);
                            PinPadSettings.setLastPinTimestamp(getApplicationContext(), System.currentTimeMillis() / 1000);

                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            pinPadView_.vibratePhone();
                            type_ = SET_PIN_CODE;
                            currentPin_ = new ArrayList<>();
                            currentDigitPin_ = new ArrayList<>();
                            currentWordPin_ = new ArrayList<>();
                            currentColorPin_ = new ArrayList<>();
                            currentFigurePin_ = new ArrayList<>();
                            currentTexturePin_ = new ArrayList<>();
                            pinPadView_.clear();
                            pinPadView_.setPlaceOrder(PinPadView.BASIC_ORDER);
                            pinPadView_.setPromptText("Set pin code");
                        }
                        break;
                }

            }

            @Override
            public void onIncompleteSubmit(List<Integer> pin) {

            }
        });
    }
    private String constructPin(List<Integer> pin) {

        String[] digits = pinPadView_.getDigits();
        String[] words = pinPadView_.getWords();
        String[] colors = pinPadView_.getColors();
        String[] figures = pinPadView_.getFigures();
        String[] textures = pinPadView_.getTextures();

        String pinAsString = "";
        for (int i = 0; i < pin.size(); i++) {
            switch(currentPinType_)
            {
                case DIGITS:
                    pinAsString += digits[pin.get(i)];
                    break;

                case WORDS:
                    pinAsString += words[pin.get(i)];
                    break;

                case COLORS:
                    pinAsString += colors[pin.get(i)];
                    break;

                case FIGURES:
                    pinAsString += figures[pin.get(i)];
                    break;

                case TEXTURES:
                    pinAsString += textures[pin.get(i)];
            }
        }

        return pinAsString;
    }


    private boolean confirmSetPin(List<Integer> pin) {
        if(currentPin_.size() != pin.size()) {
            return false;
        }

        boolean isEqual = true;
        boolean isDigitsEqual = true;
        boolean isWordsEqual = true;
        boolean isColorsEqual = true;
        boolean isFiguresEqual = true;
        boolean isTexturesEqual = true;
        String[] digits = pinPadView_.getDigits();
        String[] words = pinPadView_.getWords();
        String[] colors = pinPadView_.getColors();
        String[] figures = pinPadView_.getFigures();
        String[] textures = pinPadView_.getTextures();

        Log.i(TAG, "Digits:");
        for (int i = 0; i < pin.size(); i++) {
            Log.i(TAG, " " + digits[pin.get(i)] + " " + currentDigitPin_.get(i));
            if(!digits[pin.get(i)].equals(currentDigitPin_.get(i))){
                isDigitsEqual = false;
            }
        }

        Log.i(TAG, "Words:");
        for (int i = 0; i < pin.size(); i++) {
            Log.i(TAG, " " + words[pin.get(i)] + " " + currentWordPin_.get(i));
            if(!words[pin.get(i)].equals(currentWordPin_.get(i))){
                isWordsEqual = false;
            }
        }

        Log.i(TAG, "Colors:");
        for (int i = 0; i < pin.size(); i++) {
            Log.i(TAG, " " + colors[pin.get(i)] + " " + currentColorPin_.get(i));
            if(!colors[pin.get(i)].equals(currentColorPin_.get(i))){
                isColorsEqual = false;
            }
        }

        Log.i(TAG, "Figures:");
        for (int i = 0; i < pin.size(); i++) {
            Log.i(TAG, " " + figures[pin.get(i)] + " " + currentFigurePin_.get(i));
            if(!figures[pin.get(i)].equals(currentFigurePin_.get(i))){
                isFiguresEqual = false;
            }
        }

        Log.i(TAG, "Textures:");
        for (int i = 0; i < pin.size(); i++) {
            Log.i(TAG, " " + textures[pin.get(i)] + " " + currentTexturePin_.get(i));
            if(!textures[pin.get(i)].equals(currentTexturePin_.get(i))){
                isTexturesEqual = false;
            }
        }

        if(isDigitsEqual)
        {
            currentPinType_ = DIGITS;
            Log.i(TAG, "Type: digits");
        }
        if(isWordsEqual)
        {
            currentPinType_ = WORDS;
            Log.i(TAG, "Type: words");
        }
        if(isColorsEqual)
        {
            currentPinType_ = COLORS;
            Log.i(TAG, "Type: colors");
        }
        if(isFiguresEqual)
        {
            currentPinType_ = FIGURES;
            Log.i(TAG, "Type: figures");
        }
        if(isTexturesEqual)
        {
            currentPinType_ = TEXTURES;
            Log.i(TAG, "Type: textures");
        }

        return isDigitsEqual || isWordsEqual || isColorsEqual || isFiguresEqual || isTexturesEqual;
    }

}
