package com.example.minesweeper01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class Game extends AppCompatActivity {

    GridLayout grid;

    int difficulty;
    int dimensX;
    int dimensY;
    int numTiles;
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

    int[] blacklisted;
    int[] minePlacements;
    int[][] imaginaryGrid;
    Button[][] gridButtons;
    ArrayList<Integer> viableTiles;
    Tile[] tileObjects;

    //Booleans for shovel and flag mode
    boolean shovelMode;
    boolean flagMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        display = getWindowManager().getDefaultDisplay();
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;


        dimensX = 11;
        dimensY = 18;
        numTiles = dimensX * dimensY;
        sizeScaleFactorForWidth = 0.8;
        sizeScaleFactorForHeight = 0.8;
        mapFiller = 1;
        numMines = 10;
        minePlacements = new int[10];
        gridButtons = new Button[dimensX][dimensY];
        imaginaryGrid = new int[dimensX][dimensY];
        blacklisted = new int[9];
        viableTiles = new ArrayList<>();
        tileObjects = new Tile[numTiles];
        blacklistedController = 0;
        shovelMode = true;
        flagMode = false;

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
                imaginaryGrid[column][row] = mapFiller;
                tileObjects[mapFiller - 1] = new Tile(mapFiller);

                colorController = !colorController;
                gridButtons[j][i] = b;
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

                for (int i = 1; i < numTiles; i++) {
                    boolean viableTile = true;
                    for (int value : blacklisted) {
                        if (value == i) {
                            viableTile = false;
                            break;
                        }
                    }
                    if (viableTile) {
                        viableTiles.add(i);
                    }
                }

                generateMines();
                scanForSurroundingTiles();

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
                if (tileObjects[Integer.parseInt(v.getTag().toString()) - 1].getHasMine()) {
                    Toast.makeText(this, "Mine!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, tileObjects[Integer.parseInt(v.getTag().toString()) - 1].getNumSurroundingMines(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        //clearOpenTiles(Integer.parseInt(v.getTag().toString()));
    }

    private void clearOpenTiles(int selectedTile) {
        boolean openSpaceExists = true;

        while (openSpaceExists) {
            if (!tileObjects[selectedTile].hasBeenChecked()) {
                if (tileObjects[selectedTile].getNumSurroundingMines() == 0)
                {

                }
                else if (tileObjects[selectedTile].getNumSurroundingMines() == 1) {

                }
                else if (tileObjects[selectedTile].getNumSurroundingMines() == 2) {

                }
                else if (tileObjects[selectedTile].getNumSurroundingMines() == 3) {

                }
                else if (tileObjects[selectedTile].getNumSurroundingMines() == 4) {

                }
                else if (tileObjects[selectedTile].getNumSurroundingMines() == 5) {

                }
                else if (tileObjects[selectedTile].getNumSurroundingMines() == 6) {

                }
                else if (tileObjects[selectedTile].getNumSurroundingMines() == 7) {

                }
                else if (tileObjects[selectedTile].getNumSurroundingMines() == 8) {

                }
            }
        }
    }

    private void scanForSurroundingTiles() {
        for (int i = 0; i < dimensY; i++) {
            for (int j = 0; j < dimensX; j++) {
                int scanMineNumber = (i * dimensX) + j;
                if (!tileObjects[scanMineNumber].getHasMine()) {
                    if (j > 0) {
                        if (i > 0) {
                            if (tileObjects[scanMineNumber - dimensX - 1].getHasMine()) {
                                tileObjects[scanMineNumber].addNumSurroundingMines();
                            }
                        }

                        if (i < dimensY - 1) {
                            if (tileObjects[scanMineNumber + dimensX - 1].getHasMine()) {
                                tileObjects[scanMineNumber].addNumSurroundingMines();
                            }
                        }

                        if (tileObjects[scanMineNumber - 1].getHasMine()) {
                            tileObjects[scanMineNumber].addNumSurroundingMines();
                        }
                    }

                    if (i < dimensY - 1)
                    {
                        if (j < dimensX - 1)
                        {
                            if (tileObjects[scanMineNumber + dimensX + 1].getHasMine()) {
                                tileObjects[scanMineNumber].addNumSurroundingMines();
                            }
                        }
                        if (tileObjects[scanMineNumber + dimensX].getHasMine()) {
                            tileObjects[scanMineNumber].addNumSurroundingMines();
                        }
                    }

                    if (i > 0) {
                        if (j < dimensX - 1) {
                            if (tileObjects[scanMineNumber - dimensX + 1].getHasMine()) {
                                tileObjects[scanMineNumber].addNumSurroundingMines();
                            }
                        }

                        if (tileObjects[scanMineNumber - dimensX].getHasMine()) {
                            tileObjects[scanMineNumber].addNumSurroundingMines();
                        }
                    }

                    if (j < dimensX - 1) {
                        if (tileObjects[scanMineNumber + 1].getHasMine()) {
                            tileObjects[scanMineNumber].addNumSurroundingMines();
                        }
                    }
                }
            }
        }
    }

    private void generateMines() {
        Collections.shuffle(viableTiles);
        for (int j = 0; j < numMines; j++) {
            minePlacements[j] = viableTiles.get(j);
            tileObjects[viableTiles.get(j)].setHasMine(true);
        }
    }

    private void gameOver() {

    }

    private void setFlagMode()
    {
        flagMode = true;
        shovelMode = false;
    }

    private void setShovelMode()
    {
        flagMode = false;
        shovelMode = true;
    }

}