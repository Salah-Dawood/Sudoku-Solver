package com.salah.sudoku.service;

import com.salah.sudoku.model.SudokuCell;
import com.salah.sudoku.model.SudokuPuzzle;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SudokuSolver {

    private final FileService fileService;

    public SudokuSolver(FileService fileService) {
        this.fileService = fileService;
    }

    public boolean solvePuzzle(SudokuPuzzle puzzle) throws IOException {
        SudokuCell currentCell = getBestCell(puzzle);

        //if no cell is returned means its solved
        if (currentCell == null) {
            fileService.parsePuzzleToTextFile(puzzle);
            return true;
        }

        List<Integer> candidates = getCandidates(puzzle, currentCell);

        //no possible values?? something is wrong or its unsolvable
        if (candidates.isEmpty()) {
            return false;
        }


        for (int candidate : candidates) {

            currentCell.setValue(candidate);

            if (solvePuzzle(puzzle)) {
                return true;
            }

            // This choice led to a dead end
            currentCell.setValue(0);
        }

        // Every candidate failed
        return false;
    }

    public boolean isPossible(SudokuPuzzle puzzle, SudokuCell cell, int value) {

        // row
        for (SudokuCell c : puzzle.getRows().get(cell.getRowIndex()).getCells()) {
            if (c.getValue() == value) {
                return false;
            }
        }

        //column
        for (SudokuCell c : puzzle.getColumns()
                .get(cell.getColumnIndex()).getCells()) {
            if (c.getValue() == value) {
                return false;
            }
        }

        //grid
        int gridIndex = (cell.getRowIndex() / 3) * 3 + (cell.getColumnIndex() / 3);

        for (SudokuCell c : puzzle.getGrids()
                .get(gridIndex).getCells()) {
            if (c.getValue() == value) {
                return false;
            }
        }
        return true;
    }

    public SudokuCell getBestCell(SudokuPuzzle puzzle){
        SudokuCell bestCell = null;
        int leastPossibilities = 10;

        for (SudokuCell cell : puzzle.getCells()) {

            if (cell.getValue() != 0) {
                continue;
            }

            List<Integer> candidates = getCandidates(puzzle, cell);

            if (candidates.isEmpty()) {
                return cell;
            }

            if (candidates.size() < leastPossibilities) {
                leastPossibilities = candidates.size();
                bestCell = cell;
            }

            if (leastPossibilities == 1) {
                break;
            }
        }
        return bestCell;
        }

    public List<Integer> getCandidates(SudokuPuzzle puzzle, SudokuCell cell) {

        List<Integer> candidates = new ArrayList<>();

        for (int val = 1; val <= 9; val++) {
            if (isPossible(puzzle, cell, val)) {
                candidates.add(val);
            }
        }

        return candidates;
    }
}
