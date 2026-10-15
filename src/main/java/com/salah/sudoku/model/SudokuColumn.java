package com.salah.sudoku.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SudokuColumn {

    private List<SudokuCell> cells = new ArrayList<>();

    public void addCell(SudokuCell cell){
        cells.add(cell);
    }

    @Override
    public String toString(){
        return "Column:" + cells;
    }
}