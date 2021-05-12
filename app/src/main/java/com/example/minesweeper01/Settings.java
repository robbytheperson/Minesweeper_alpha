package com.example.minesweeper01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SpinnerAdapter;

public class Settings extends AppCompatActivity {

    RadioGroup difficultyButtons;
    int difficulty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //difficulty = getIntent().getIntExtra("difficulty", 1);
        difficultyButtons = findViewById(R.id.difficultyButtonsSettings);
        difficultyButtons.setOnCheckedChangeListener(difficultyListener);

        switch (difficulty) {
            case 0:
                difficultyButtons.check(R.id.easyButtSettings);
                break;
            case 1:
                difficultyButtons.check(R.id.medButtSettings);
                break;
            case 2:
                difficultyButtons.check(R.id.hardButtSettings);
        }
    }

    private RadioGroup.OnCheckedChangeListener difficultyListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.easyButtSettings:
                    difficulty = 0;
                    break;
                case R.id.medButtSettings:
                    difficulty = 1;
                    break;
                case R.id.hardButtSettings:
                    difficulty = 2;
            }
        }
    };


}