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
    double sizeScaleFactorForWidth;
    double sizeScaleFactorForHeight;
    
    int mapFiller;
    int blacklistedController;

    boolean colorController;
    boolean gameHasStarted;
    boolean shovelBeingUsed;

    int[] blacklisted;
    int[] minePlacements;
    int[][] imaginaryGrid;
    Button[][] gridButtons;
    int[] viableTiles;

    //Booleans for shovel and flag mode
    boolean shovelMode = true;
    boolean flagMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        display = getWindowManager().getDefaultDisplay();
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;


        dimensX = 5;
        dimensY = 5;
        sizeScaleFactorForWidth = 0.9;
        sizeScaleFactorForHeight = 0.8;
        mapFiller = 1;
        numMines = 10;
        minePlacements = new int[numMines];
        gridButtons = new Button[dimensX][dimensY];
        imaginaryGrid = new int[dimensX][dimensY];
        blacklisted = new int[9];
        viableTiles = new int[dimensX * dimensY - blacklisted.length];
        blacklistedController = 0;

        grid = findViewById(R.id.tileGrid);
        createGrid();

        difficulty = getIntent().getIntExtra("difficulty",1);
        setDifficulty();

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


    public void setColors() {
        for (int i = 0; i < dimensX; i++) {
            for (int j = 0; j < dimensX; j++) {
                colorController = !colorController;
            }
        }
    }


    public void createGrid() {
        gridWidth = (int)(screenWidth * sizeScaleFactorForWidth);
        gridHeight = (int)(Math.round((1.0 * gridWidth) * ((1.0 * dimensY) / (1.0 * dimensX))));
        if (gridHeight > (screenHeight * sizeScaleFactorForHeight))
        {
            gridHeight = (int)(screenHeight * sizeScaleFactorForHeight);
            gridWidth = (int)(Math.round((1.0 * gridHeight) * ((1.0 * dimensX) / (1.0 * dimensY))));
        }
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
        grid.setColumnCount(dimensX);
        grid.setRowCount(dimensY);
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

                b.setTag(mapFiller);
                imaginaryGrid[row][column] = mapFiller;

                colorController = !colorController;
                gridButtons[i][j] = b;
                grid.addView(b);
                mapFiller++;
            }
        }
    }


    public void clickMethod(View v, int row, int column) {
        int clickedTile = Integer.parseInt(v.getTag().toString());
        if (!gameHasStarted) {
            if (row > 0 && column > 0 && row < (dimensX - 1) && column < (dimensY - 1)) {
                for (int i = 0; i < dimensX; i++) {
                    for (int j = 0; j < dimensY; j++) {
                        if (2 > Math.abs((imaginaryGrid[i][j]) - clickedTile) || 2 > Math.abs((imaginaryGrid[i][j]) - (clickedTile - dimensX)) || 2 > Math.abs((imaginaryGrid[i][j]) - (clickedTile + dimensX))) {
                            blacklisted[blacklistedController] = imaginaryGrid[i][j];
                            blacklistedController++;
                        }
                    }
                }

                int iterationTracker = 0;
                for (int i = 1; i < dimensX * dimensY; i++) {
                    boolean viableTile = true;
                    for (int value : blacklisted) {
                        if (value == i) {
                            viableTile = false;
                            break;
                        }
                    }
                    if (viableTile) {
                        viableTiles[iterationTracker] = i;
                        iterationTracker++;
                    }

                }

                for (int i = 0; i < minePlacements.length; i++) {
                    int key = (int)(Math.random() * viableTiles.length);
                    minePlacements[i] = viableTiles[key];
                }

                gameHasStarted = true;
            }
        }
        else {
            if (flagMode == true)
            {
                //adding flag code here
            }
            else
            {
                for (int minePlacement : minePlacements) {
                    if (Integer.parseInt(v.getTag().toString()) == minePlacement) {
                        Toast.makeText(this, "Mine!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        }
    }


    public void gameOver() {

    }

    public void setFlagMode()
    {
        flagMode = true;
        shovelMode = false;
    }

    public void setShovelMode()
    {
        flagMode = false;
        shovelMode = true;
    }
}