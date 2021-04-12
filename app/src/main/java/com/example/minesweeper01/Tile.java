package com.example.minesweeper01;

import java.util.ArrayList;

public class Tile {

    private boolean hasMine;
    private boolean isCovered;
    private int numSurroundingMines;
    private int tileNumber;
    private boolean hasBeenChecked;

    public Tile(int tileNumber) {
        this.tileNumber = tileNumber;
        hasMine = false;
        isCovered = false;
        hasBeenChecked = false;
        numSurroundingMines = 0;
    }

    public boolean getHasMine() {
        return hasMine;
    }

    public void setHasMine(boolean hasMine) {
        this.hasMine = hasMine;
    }

    public boolean getIsCovered() {
        return isCovered;
    }

    public void setCovered(boolean covered) {
        isCovered = covered;
    }

    public boolean hasBeenChecked() {
        return hasBeenChecked;
    }

    public void setHasBeenChecked(boolean hasBeenChecked) {
        this.hasBeenChecked = hasBeenChecked;
    }

    public int getTileNumber() {
        return tileNumber;
    }

    public int getNumSurroundingMines() {
        return numSurroundingMines;
    }

    public void addNumSurroundingMines() {
        numSurroundingMines++;
    }
}
