package com.example.minesweeper01;

import java.util.ArrayList;

public class Tile {

    private boolean hasMine;
    private boolean isCovered;
    private int numSurroundingMines;
    private int dimensX;
    private int tileNumber;
    private int column;
    private int row;
    private boolean hasBeenChecked;
    private ArrayList<Integer> existingNeighbors;
    private ArrayList<Integer> cardinalNeighbors;
    private boolean hasFlag;
    private boolean isLight;

    public Tile(int tileNumber, int dimensX) {
        this.dimensX = dimensX;
        this.tileNumber = tileNumber;
        column = tileNumber % dimensX;
        row = tileNumber / dimensX;
        hasMine = false;
        isCovered = false;
        hasBeenChecked = false;
        numSurroundingMines = 0;
        existingNeighbors = new ArrayList<>();
        cardinalNeighbors = new ArrayList<>();
        hasFlag = false;
        isLight = false;
    }

    public void setLight(boolean light) {
        isLight = light;
    }

    public boolean isLight() {
        return isLight;
    }

    public void addExistingNeighbors(int neighborID) {
        existingNeighbors.add(neighborID);
    }

    public ArrayList<Integer> getExistingNeighbors() {
        return existingNeighbors;
    }

    public void addCardinalNeighbor(int neighborID) {
        cardinalNeighbors.add(neighborID);
    }

    public ArrayList<Integer> getCardinalNeighbors() {
        return cardinalNeighbors;
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

    public void setNumSurroundingMines(int numSurroundingMines) {
        this.numSurroundingMines = numSurroundingMines;
    }

    public void addNumSurroundingMines() {
        numSurroundingMines++;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

}
