package com.example.minesweeper01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Game extends AppCompatActivity {

    int difficulty;
    int dimens;
    int numMines;

    int map[][];
    int mapFiller;

    boolean colorController;

    View tileArray[][];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        difficulty = getIntent().getIntExtra("difficulty",1);
        setDifficulty();

        fillMap();
        colorController = true;

        setColors();
    }

    public void setDifficulty() {
        if (difficulty == 0) {
            numMines = 10;
        } else if (difficulty == 1) {
            numMines = 15;
        } else if (difficulty == 2) {
            numMines = 18;
        }
    }

    public void fillMap() {
        dimens = 10;
        mapFiller = 1;

        map = new int[dimens][dimens];

        for (int i = 0; i < dimens; i++) {
            for (int j = 0; j < dimens; j++) {
                map[i][j] = mapFiller;
                mapFiller++;
            }
        }
    }

    public void setColors() {
        for (int i = 0; i < dimens; i++) {
            for (int j = 0; j < dimens; j++) {
                if (colorController) tileArray[i][j].setBackgroundDrawable(getResources().getDrawable(R.drawable.dark_green_tile));
                colorController = !colorController;
            }
        }
    }

    public void checkForMine()
    {

    }
}