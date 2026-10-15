package com.salah.sudoku.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class SudokuPuzzle {
    private String name;
    private List<SudokuCell> cells = new ArrayList<>();
    private List<SudokuGrid> grids = new ArrayList<>();
    private List<SudokuRow> rows = new ArrayList<>();
    private List<SudokuColumn> columns = new ArrayList<>();

    public SudokuPuzzle(String name) {
        this.name = name;
        for (int i = 0; i < 9; i++) {
            this.rows.add(new SudokuRow());
            this.columns.add(new SudokuColumn());
            this.grids.add(new SudokuGrid());
        }
    }

    public void addCell(SudokuCell cell,int rowIndex,int columnIndex){
        cells.add(cell);
        rows.get(rowIndex).addCell(cell);
        columns.get(columnIndex).addCell(cell);

        int gridIndex = (rowIndex / 3) * 3 + (columnIndex / 3);

        grids.get(gridIndex).addCell(cell);
    }

    @Override
    public String toString(){
        return "Grids:" + grids + "\n" +
                "Rows:" + rows + "\n" +
                "Column:" + columns;
    }


}