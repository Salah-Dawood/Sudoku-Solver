package com.salah.sudoku.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SudokuRow {

    private List<SudokuCell> cells = new ArrayList<>();

    public void addCell(SudokuCell cell){
        cells.add(cell);
    }

    @Override
    public String toString(){
        return "Row:" + cells;
    }

}
