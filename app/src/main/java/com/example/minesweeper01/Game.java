package com.example.minesweeper01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

public class Game extends AppCompatActivity {

    GridLayout grid;

    int difficulty;
    int dimensX;
    int dimensY;
    int numMines;

    Display display;
    DisplayMetrics displayMetrics;
    int screenWidth;
    int screenHeight;
    int gridWidth;
    int gridHeight;

    int mapFiller;

    boolean colorController;
    boolean gameIsOn;

    int[] blacklisted;
    int[] minePlacements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        display = getWindowManager().getDefaultDisplay();
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;


        dimensX = 10;
        dimensY = 14;

        grid = findViewById(R.id.tileGrid);
        createGrid();

        difficulty = getIntent().getIntExtra("difficulty",1);
        setDifficulty();

        colorController = true;

        setColors();

        blacklisted = new int[25];
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


    public void setColors() {
        for (int i = 0; i < dimensX; i++) {
            for (int j = 0; j < dimensX; j++) {
                colorController = !colorController;
            }
        }
    }

    public void createGrid() {
        gridWidth = (int)(screenWidth * 0.9);
        gridHeight = (int)(Math.round((1.0 * gridWidth) * ((1.0 * dimensY) / (1.0 * dimensX))));
        ConstraintLayout constraintLayout = findViewById(R.id.constraintL);
        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout.LayoutParams gridParams = new ConstraintLayout.LayoutParams(gridWidth,gridHeight);
        grid.setLayoutParams(gridParams);
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.tileGrid,ConstraintSet.TOP,R.id.constraintL,ConstraintSet.TOP,0);
        constraintSet.connect(R.id.tileGrid,ConstraintSet.LEFT,R.id.constraintL,ConstraintSet.LEFT,0);
        constraintSet.connect(R.id.tileGrid,ConstraintSet.BOTTOM,R.id.constraintL,ConstraintSet.BOTTOM,0);
        constraintSet.connect(R.id.tileGrid,ConstraintSet.RIGHT,R.id.constraintL,ConstraintSet.RIGHT,0);
        constraintSet.applyTo(constraintLayout);
        grid.setColumnCount((int)dimensX);
        grid.setRowCount((int)dimensY);
        for (int i = 0; i < dimensY; i++) {
            for (int j = 0; j < dimensX; j++) {
                final int row = i;
                final int column = j;

                Button b = new Button(this);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickMethod(view, row, column);
                    }
                });

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(column);
                params.height = (gridHeight / dimensY);
                params.width = (gridWidth / dimensX);
                b.setLayoutParams(params);

                if (column == 0 && row % 2 == 1) colorController = false;
                if (column == 0 && row % 2 == 0) colorController = true;
                if (colorController) b.setBackground(getResources().getDrawable(R.drawable.dark_green_tile));
                else b.setBackground(getResources().getDrawable(R.drawable.light_green_tile));

                colorController = !colorController;
                grid.addView(b);
            }
        }
    }

    public void clickMethod(View v, int row, int column) {
        if (!gameIsOn) {
            gameIsOn = true;
        }
    }

}