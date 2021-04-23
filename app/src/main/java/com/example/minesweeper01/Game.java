package com.example.minesweeper01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.annotation.SuppressLint;
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

    boolean colorController;
    boolean gameHasStarted;

    ArrayList<Integer> blacklisted;
    int[] minePlacements;
    int[][] coordsToNumber;
    int spacesOpened;
    Button[][] gridButtons;
    ArrayList<Integer> viableTiles;
    ArrayList<Integer> tilesNeedingCheck;
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


        dimensX = 9;
        dimensY = 16;
        numTiles = dimensX * dimensY;
        sizeScaleFactorForWidth = 0.8;
        sizeScaleFactorForHeight = 0.8;
        mapFiller = 0;
        spacesOpened = 0;
        difficulty = getIntent().getIntExtra("difficulty", 1);
        setDifficulty();
        minePlacements = new int[numMines];
        gridButtons = new Button[dimensX][dimensY];
        coordsToNumber = new int[dimensX][dimensY];
        blacklisted = new ArrayList<>();
        viableTiles = new ArrayList<>();
        tilesNeedingCheck = new ArrayList<>();
        tileObjects = new Tile[numTiles];
        colorController = true;
        gameHasStarted = false;
        shovelMode = true;
        flagMode = false;

        grid = findViewById(R.id.tileGrid);
        createGrid();
    }


    public void setDifficulty() {
        if (difficulty == 0) {
            numMines = (int) (dimensX * dimensY * 0.125);
        } else if (difficulty == 1) {
            numMines = (int) (dimensX * dimensY * 0.16);
        } else if (difficulty == 2) {
            numMines = (int) (dimensX * dimensY * 0.2);
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public void createGrid() {
        boolean skinnyEnough = false;
        gridWidth = (int) (screenWidth * sizeScaleFactorForWidth);
        gridHeight = (int) (Math.round((1.0 * gridWidth) * ((1.0 * dimensY) / (1.0 * dimensX))));
        if (gridHeight > (screenHeight * sizeScaleFactorForHeight)) {
            gridHeight = (int) (screenHeight * sizeScaleFactorForHeight);
            gridWidth = (int) (Math.round((1.0 * gridHeight) * ((1.0 * dimensX) / (1.0 * dimensY))));
        }
        if (gridWidth < (screenWidth * 0.7)) {
            skinnyEnough = true;
        }
        ConstraintLayout constraintLayout = findViewById(R.id.constraintL);
        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout.LayoutParams gridParams = new ConstraintLayout.LayoutParams(gridWidth, gridHeight);
        grid.setLayoutParams(gridParams);
        constraintSet.clone(constraintLayout);
        if (skinnyEnough) {
            constraintSet.connect(R.id.tileGrid, ConstraintSet.LEFT, R.id.constraintL, ConstraintSet.LEFT, (int) (gridWidth * (1.0 / dimensX)));
            constraintSet.connect(R.id.tileGrid, ConstraintSet.TOP, R.id.constraintL, ConstraintSet.TOP, 0);
            constraintSet.connect(R.id.tileGrid, ConstraintSet.BOTTOM, R.id.constraintL, ConstraintSet.BOTTOM, 0);
        }
        else {
            constraintSet.connect(R.id.tileGrid, ConstraintSet.LEFT, R.id.constraintL, ConstraintSet.LEFT, 0);
            constraintSet.connect(R.id.tileGrid, ConstraintSet.RIGHT,R.id.constraintL,ConstraintSet.RIGHT, 0);
            constraintSet.connect(R.id.tileGrid, ConstraintSet.BOTTOM, R.id.constraintL, ConstraintSet.BOTTOM, (int) (gridHeight * (0.75 / dimensY)));
        }
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
                b.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        longClick(view, row, column);
                        return true;
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
                if (colorController)
                    b.setBackground(getResources().getDrawable(R.drawable.dark_green_tile));
                else b.setBackground(getResources().getDrawable(R.drawable.light_green_tile));


                b.setTag(mapFiller);
                coordsToNumber[column][row] = mapFiller;
                tileObjects[mapFiller] = new Tile(mapFiller, dimensX);
                tileObjects[mapFiller].setDark(colorController);

                gridButtons[j][i] = b;
                grid.addView(b);
                mapFiller++;
                colorController = !colorController;
            }
        }
    }

    private void longClick(View v, int row, int column) {
        int longClickedTile = Integer.parseInt((v.getTag().toString()));

        if (!gameHasStarted) {
            clickMethod(v, row, column);
        } else {
            if (!tileObjects[longClickedTile].getHasFlag()) {
                if (tileObjects[longClickedTile].getIsCovered()) {
                    if (tileObjects[longClickedTile].isDark()) {
                        gridButtons[column][row].setBackground(getResources().getDrawable(R.drawable.dark_green_flag));
                    } else {
                        gridButtons[column][row].setBackground(getResources().getDrawable(R.drawable.light_green_flag));
                    }
                    tileObjects[longClickedTile].setHasFlag(true);
                }
            } else {
                if (tileObjects[longClickedTile].isDark()) {
                    gridButtons[column][row].setBackground(getResources().getDrawable(R.drawable.dark_green_tile));
                } else {
                    gridButtons[column][row].setBackground(getResources().getDrawable(R.drawable.light_green_tile));
                }
            }
        }
    }


    public void clickMethod(View v, int row, int column) {
        int clickedTile = Integer.parseInt(v.getTag().toString());
        if (!tileObjects[clickedTile].getHasFlag()) {
            if (!gameHasStarted) {
                blacklisted.add(clickedTile);

                if (column > 0 && row > 0) {
                    //Top left
                    blacklisted.add(clickedTile - dimensX - 1);
                }

                if (row > 0) {
                    //Top
                    blacklisted.add(clickedTile - dimensX);
                }

                if (column < dimensX - 1 && row > 0) {
                    //Top right
                    blacklisted.add(clickedTile - dimensX + 1);
                }

                if (column > 0) {
                    //Left
                    blacklisted.add(clickedTile - 1);
                }

                if (column < dimensX - 1) {
                    //Right
                    blacklisted.add(clickedTile + 1);
                }

                if (column > 0 && row < dimensY - 1) {
                    //Bottom left
                    blacklisted.add(clickedTile + dimensX - 1);
                }

                if (column < dimensY - 1) {
                    //Bottom
                    blacklisted.add(clickedTile + dimensX);
                }

                if (column < dimensX - 1 && row < dimensY - 1) {
                    //Bottom right
                    blacklisted.add(clickedTile + dimensX + 1);
                }

                for (int i = 0; i < numTiles; i++) {
                    boolean viableTile = true;
                    for (int value = 0; value < blacklisted.size(); value++) {
                        if (i == blacklisted.get(value)) {
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

            clearOpenTiles(clickedTile, row, column);


        } else {
            if (tileObjects[clickedTile].isDark()) {
                gridButtons[column][row].setBackground(getResources().getDrawable(R.drawable.dark_green_tile));
            } else {
                gridButtons[column][row].setBackground(getResources().getDrawable(R.drawable.light_green_tile));
            }
            tileObjects[clickedTile].setHasFlag(false);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void clearOpenTiles(int selectedTile, int row, int column) {

        if (!tileObjects[selectedTile].hasBeenChecked()) {
            if (tileObjects[selectedTile].getNumSurroundingMines() == 0) {
                gridButtons[column][row].setBackground(getResources().getDrawable(R.drawable.zero));
            } else if (tileObjects[selectedTile].getNumSurroundingMines() == 1) {
                gridButtons[column][row].setBackground(getResources().getDrawable(R.drawable.one));
            } else if (tileObjects[selectedTile].getNumSurroundingMines() == 2) {
                gridButtons[column][row].setBackground(getResources().getDrawable(R.drawable.two));
            } else if (tileObjects[selectedTile].getNumSurroundingMines() == 3) {
                gridButtons[column][row].setBackground(getResources().getDrawable(R.drawable.three));
            } else if (tileObjects[selectedTile].getNumSurroundingMines() == 4) {
                gridButtons[column][row].setBackground(getResources().getDrawable(R.drawable.four));
            } else if (tileObjects[selectedTile].getNumSurroundingMines() == 5) {
                gridButtons[column][row].setBackground(getResources().getDrawable(R.drawable.five));
            } else if (tileObjects[selectedTile].getNumSurroundingMines() == 6) {
                gridButtons[column][row].setBackground(getResources().getDrawable(R.drawable.six));
            } else if (tileObjects[selectedTile].getNumSurroundingMines() == 7) {
                gridButtons[column][row].setBackground(getResources().getDrawable(R.drawable.seven));
            } else if (tileObjects[selectedTile].getNumSurroundingMines() == 8) {
                gridButtons[column][row].setBackground(getResources().getDrawable(R.drawable.eight));
            } else {
                gridButtons[column][row].setBackground(getResources().getDrawable(R.drawable.eight));
            }
            tileObjects[selectedTile].setCovered(false);
            tileObjects[selectedTile].setHasBeenChecked(true);
            for (int i = 0; i < tileObjects[selectedTile].getExistingNeighbors().size(); i++) {
                if (!tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].hasBeenChecked() && tileObjects[selectedTile].getNumSurroundingMines() == 0) {

                    if (tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getNumSurroundingMines() == 0) {
                        gridButtons[tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getColumn()]
                                [tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getRow()].setBackground(getResources().getDrawable(R.drawable.zero));
                        boolean isUnique = true;
                        for (int b = 0; b < tilesNeedingCheck.size(); b++) {
                            if (tilesNeedingCheck.get(b).equals(tileObjects[selectedTile].getExistingNeighbors().get(i))) {
                                isUnique = false;
                                break;
                            }
                        }
                        if (isUnique) {
                            tilesNeedingCheck.add(tileObjects[selectedTile].getExistingNeighbors().get(i));
                        }
                    } else if (tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getNumSurroundingMines() == 1) {
                        gridButtons[tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getColumn()]
                                [tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getRow()].setBackground(getResources().getDrawable(R.drawable.one));
                    } else if (tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getNumSurroundingMines() == 2) {
                        gridButtons[tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getColumn()]
                                [tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getRow()].setBackground(getResources().getDrawable(R.drawable.two));
                    } else if (tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getNumSurroundingMines() == 3) {
                        gridButtons[tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getColumn()]
                                [tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getRow()].setBackground(getResources().getDrawable(R.drawable.three));
                    } else if (tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getNumSurroundingMines() == 4) {
                        gridButtons[tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getColumn()]
                                [tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getRow()].setBackground(getResources().getDrawable(R.drawable.four));
                    } else if (tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getNumSurroundingMines() == 5) {
                        gridButtons[tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getColumn()]
                                [tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getRow()].setBackground(getResources().getDrawable(R.drawable.five));
                    } else if (tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getNumSurroundingMines() == 6) {
                        gridButtons[tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getColumn()]
                                [tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getRow()].setBackground(getResources().getDrawable(R.drawable.six));
                    } else if (tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getNumSurroundingMines() == 7) {
                        gridButtons[tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getColumn()]
                                [tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].getRow()].setBackground(getResources().getDrawable(R.drawable.seven));
                    }
                    tileObjects[tileObjects[selectedTile].getExistingNeighbors().get(i)].setCovered(false);
                }
            }
            if (tilesNeedingCheck.size() > spacesOpened) {
                selectedTile = tilesNeedingCheck.get(spacesOpened);
                spacesOpened++;
                column = tileObjects[selectedTile].getColumn();
                row = tileObjects[selectedTile].getRow();
                if (spacesOpened <= tilesNeedingCheck.size()) {
                    clearOpenTiles(selectedTile, row, column);
                }
                else {
                    spacesOpened = 0;
                    tilesNeedingCheck.clear();
                }
            }
            int score = 0;
            for (int index = 0; index < tileObjects.length; index++)
            {
                if (!tileObjects[index].getIsCovered()) {
                    score++;
                }
            }
            if (score == numTiles - numMines) {
                Toast.makeText(this, "You win. :|", Toast.LENGTH_SHORT).show();
            }
        }
        //Toast.makeText(this, "This tile " + "(" + selectedTile + ")" + " has already already been checked.", Toast.LENGTH_SHORT).show();

    }

    private void scanForSurroundingTiles() {
        for (int i = 0; i < dimensY; i++) {
            for (int j = 0; j < dimensX; j++) {
                int scanMineNumber = coordsToNumber[j][i];
                if (!tileObjects[scanMineNumber].getHasMine()) {
                    if (i > 0 && j > 0) {
                        //Top left
                        if (tileObjects[scanMineNumber - dimensX - 1].getHasMine()) {
                            tileObjects[scanMineNumber].addNumSurroundingMines();
                        } else
                            tileObjects[scanMineNumber].addExistingNeighbors(scanMineNumber - dimensX - 1);
                    }

                    if (i > 0) {
                        //Top
                        if (tileObjects[scanMineNumber - dimensX].getHasMine()) {
                            tileObjects[scanMineNumber].addNumSurroundingMines();
                        } else {
                            tileObjects[scanMineNumber].addExistingNeighbors(scanMineNumber - dimensX);
                            tileObjects[scanMineNumber].addCardinalNeighbor(scanMineNumber - dimensX);
                        }
                    }

                    if (j < dimensX - 1 && i > 0) {
                        //Top right
                        if (tileObjects[scanMineNumber - dimensX + 1].getHasMine()) {
                            tileObjects[scanMineNumber].addNumSurroundingMines();
                        } else
                            tileObjects[scanMineNumber].addExistingNeighbors(scanMineNumber - dimensX + 1);
                    }

                    if (j > 0) {
                        //Left
                        if (tileObjects[scanMineNumber - 1].getHasMine()) {
                            tileObjects[scanMineNumber].addNumSurroundingMines();
                        } else {
                            tileObjects[scanMineNumber].addExistingNeighbors(scanMineNumber - 1);
                            tileObjects[scanMineNumber].addCardinalNeighbor(scanMineNumber - 1);
                        }
                    }

                    if (j < dimensX - 1) {
                        //Right
                        if (tileObjects[scanMineNumber + 1].getHasMine()) {
                            tileObjects[scanMineNumber].addNumSurroundingMines();
                        } else {
                            tileObjects[scanMineNumber].addExistingNeighbors(scanMineNumber + 1);
                            tileObjects[scanMineNumber].addCardinalNeighbor(scanMineNumber + 1);
                        }
                    }

                    if (j > 0 && i < dimensY - 1) {
                        //Bottom left
                        if (tileObjects[scanMineNumber + dimensX - 1].getHasMine()) {
                            tileObjects[scanMineNumber].addNumSurroundingMines();
                        } else
                            tileObjects[scanMineNumber].addExistingNeighbors(scanMineNumber + dimensX - 1);
                    }

                    if (i < dimensY - 1) {
                        //Bottom
                        if (tileObjects[scanMineNumber + dimensX].getHasMine()) {
                            tileObjects[scanMineNumber].addNumSurroundingMines();
                        } else {
                            tileObjects[scanMineNumber].addExistingNeighbors(scanMineNumber + dimensX);
                            tileObjects[scanMineNumber].addCardinalNeighbor(scanMineNumber + dimensX);
                        }
                    }

                    if (j < dimensX - 1 && i < dimensY - 1) {
                        //Bottom right
                        if (tileObjects[scanMineNumber + dimensX + 1].getHasMine()) {
                            tileObjects[scanMineNumber].addNumSurroundingMines();
                        } else
                            tileObjects[scanMineNumber].addExistingNeighbors(scanMineNumber + dimensX + 1);
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
            tileObjects[viableTiles.get(j)].setNumSurroundingMines(9);
        }
    }

    private void gameOver() {

    }

}