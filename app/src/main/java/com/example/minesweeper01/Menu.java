package com.example.minesweeper01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Menu extends AppCompatActivity {

    int difficulty;

    Display display;
    DisplayMetrics displayMetrics;
    int screenWidth;
    int screenHeight;

    Button startButton, helpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        display = getWindowManager().getDefaultDisplay();
        displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        difficulty = 1;
        startButton = findViewById(R.id.start);
        helpButton = findViewById(R.id.helpButton);

        setUserInterfaceSize();
    }

    public static int dpToPx(float dp, Context context) {
        return (int) (dp / context.getResources().getDisplayMetrics().density);
    }

    public static int dpToSp(float dp, Context context) {
        return (int) (dpToPx(dp, context) / context.getResources().getDisplayMetrics().scaledDensity);
    }

    private void setUserInterfaceSize() {
        ConstraintLayout constraintLayout = findViewById(R.id.consL);
        int startButtonWidth = (int)(screenWidth * 0.5);
        int startButtonHeight = (int)(startButtonWidth * (20.0 / 67.0));
        if (startButtonHeight > (int)(screenHeight * 0.25)) {
            startButtonHeight = (int)(screenHeight * 0.25);
            startButtonWidth = (int)(startButtonHeight * (67.0 / 20.0));
        }

        ConstraintLayout.LayoutParams startButtonParams = new ConstraintLayout.LayoutParams(startButtonWidth,startButtonHeight);
        startButton.setLayoutParams(startButtonParams);

        int startTextSize = 18;
        if (screenWidth < 500) {
            startTextSize = 6;
        }
        else if (screenWidth < 750) {
            startTextSize = 8;
        }
        else if (screenWidth > 1450){
            int startButtonSetter = screenWidth;
            while (startButtonSetter > 0) {
                startButtonSetter -= 250;
                startTextSize += 2;
            }
        }
        startButton.setTextSize(startTextSize);

        Toast.makeText(this, String.valueOf(screenWidth), Toast.LENGTH_SHORT).show();


        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        constraintSet.connect(R.id.start,ConstraintSet.TOP,R.id.consL,ConstraintSet.TOP);
        constraintSet.connect(R.id.start,ConstraintSet.LEFT,R.id.consL,ConstraintSet.LEFT);
        constraintSet.connect(R.id.start,ConstraintSet.RIGHT,R.id.consL,ConstraintSet.RIGHT);
        constraintSet.connect(R.id.start,ConstraintSet.BOTTOM,R.id.consL,ConstraintSet.BOTTOM);

        constraintSet.applyTo(constraintLayout);
    }

    public void start(View v) {
            Intent startGameIntent = new Intent(Menu.this,Game.class);
            startGameIntent.putExtra("difficulty", difficulty);
            startActivity(startGameIntent);
    }

    public void helpScreen(View v)
    {
        Intent helpScreenIntent = new Intent(Menu.this, HowToPlay.class);
        helpScreenIntent.putExtra("difficulty", difficulty);
        startActivity(helpScreenIntent);
    }

    public void settingsScreen(View v) {
        Intent settingsScreenIntent = new Intent(Menu.this, Settings.class);
        settingsScreenIntent.putExtra("difficulty", difficulty);
        startActivity(settingsScreenIntent);
    }
    //testing comment


}