package com.example.minesweeper01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Menu extends AppCompatActivity {

    RadioGroup difficultyButtons;
    int difficulty;

    Button startButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        difficultyButtons = findViewById(R.id.difficultyButtons);
        difficultyButtons.setOnCheckedChangeListener(difficultyListener);

        startButton = findViewById(R.id.start);
    }

    private RadioGroup.OnCheckedChangeListener difficultyListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.easyButt:
                    difficulty = 0;
                    break;
                case R.id.medButt:
                    difficulty = 1;
                    break;
                case R.id.hardButt:
                    difficulty = 2;
            }
        }
    };

    public void start(View v) {
            Intent startGameIntent = new Intent(Menu.this,Game.class);
            startGameIntent.putExtra("difficulty",difficulty);
            startActivity(startGameIntent);
    }
}