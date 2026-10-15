package com.salah.sudoku.model;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class SudokuCell {

    private int value;
    private final int rowIndex;
    private final int columnIndex;

    public SudokuCell(int value, int rowIndex, int columnIndex) {
        this.value = value;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
    }

    @Override
    public String toString(){
        return String.valueOf(value);
    }
}
