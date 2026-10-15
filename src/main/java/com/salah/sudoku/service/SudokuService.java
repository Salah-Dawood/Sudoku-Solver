package com.salah.sudoku.service;

import com.salah.sudoku.exception.InvalidCharacterException;
import com.salah.sudoku.exception.InvalidPuzzleException;
import com.salah.sudoku.exception.SudokuFileNotFoundException;
import com.salah.sudoku.model.SudokuCell;
import com.salah.sudoku.model.SudokuPuzzle;
import com.salah.sudoku.model.request.SudokuPuzzleRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SudokuService {

    private final SudokuSolver sudokuSolver;
    private final FileService fileService;

    private final Path SOLUTION_DIR = Paths.get("solved");

    public SudokuService(SudokuSolver sudokuSolver, FileService fileService) {
        this.sudokuSolver = sudokuSolver;
        this.fileService = fileService;
    }

    public ResponseEntity<?> puzzleRunner(SudokuPuzzleRequest request) throws IOException {
        String path = fileService.uploadPuzzle(request);
        SudokuPuzzle puzzle = parseTextFileToPuzzle(request,Paths.get(path));
        if (sudokuSolver.solvePuzzle(puzzle)) {
            String solutionName = FileService.getSolutionFileName(puzzle.getName());
            URI location = UriComponentsBuilder.fromPath("/api/get-solved/{fileName}")
                    .buildAndExpand(solutionName)
                    .encode()
                    .toUri();
            return ResponseEntity.created(location).body(solutionName);
        } else {
            return new ResponseEntity<>("womp womp",HttpStatus.BAD_REQUEST);
        }
    }

    //    Method to read the puzzle1.txt file and generate sudoku grid
    public SudokuPuzzle parseTextFileToPuzzle(SudokuPuzzleRequest request,Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        lines.removeIf(String::isBlank);
        lines.replaceAll(line -> line.replaceAll("\\s+", ""));
        System.out.println(lines);
        validate(lines);

        SudokuPuzzle puzzle = new SudokuPuzzle(path.toString());
        for (int i = 0; i <= 8;i++){
            for (int j = 0; j <= 8;j++){
                char ch = lines.get(i).charAt(j);
                int value = (ch == '.' || ch == '-') ? 0 : ch - '0';
                puzzle.addCell(new SudokuCell(value,i,j),i,j);
            }
        }

        validateGivens(puzzle);
        System.out.println(showBoard(puzzle));
        return puzzle;
    }




    //    Method to save the generated solution file as puzzle1.solution.txt



    //    Method to print the current game board
    public StringBuilder showBoard(SudokuPuzzle puzzle) {
        StringBuilder board = new StringBuilder();

        board.append("\n");
        board.append("---------Sudoku---------\n");

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board.append(puzzle.getRows().get(i).getCells().get(j))
                        .append(" ");

                if ((j + 1) % 3 == 0 && (j + 1) != 9) {
                    board.append(" | ");
                }
            }

            board.append("\n");

            if ((i + 1) % 3 == 0 && (i + 1) != 9) {
                board.append("------------------------\n");
            }
        }

        return board;
    }

    public void validate(List<String> lines){
        lines.stream()
                .flatMapToInt(String::chars)
                .filter(c -> (c < '0' || c > '9') && c != '.' && c != '-')
                .findFirst()
                .ifPresent(invalidChar -> {
                    throw new InvalidCharacterException("Invalid character found in puzzle file: " + (char) invalidChar);
                });

        if (lines.size() != 9 || lines.stream().anyMatch(line -> line.length() != 9)) {
            throw new InvalidPuzzleException("Puzzle must contain exactly 9 lines of 9 characters");
        }
    }

    private void validateGivens(SudokuPuzzle puzzle) {
        for (SudokuCell cell : puzzle.getCells()) {
            int value = cell.getValue();
            if (value == 0) {
                continue;
            }
            cell.setValue(0);
            boolean valid = sudokuSolver.isPossible(puzzle, cell, value);
            cell.setValue(value);
            if (!valid) {
                throw new InvalidPuzzleException("Conflicting value " + value + " at row "
                        + (cell.getRowIndex() + 1) + ", column " + (cell.getColumnIndex() + 1));
            }
        }
    }

    public Resource loadSolvedFile(String fileName) {
        try {
            Path base = SOLUTION_DIR.toAbsolutePath().normalize();
            Path filePath = base.resolve(fileName).normalize();
            if (!filePath.startsWith(base)) {
                throw new SudokuFileNotFoundException("The solved file '" + fileName + "' could not be found.");
            }
            Resource resource = new UrlResource(filePath.toUri());

            // file is readable
            if (!resource.exists() || !resource.isReadable()) {
                throw new SudokuFileNotFoundException("The solved file '" + fileName + "' could not be found.");
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new SudokuFileNotFoundException("File path resolution failed for file '" + fileName + "'.");
        }
    }

    public ResponseEntity<List<String>> getAllSolved(){
        Path directoryPath = Paths.get(SOLUTION_DIR.toUri());


        if (!Files.exists(directoryPath) || !Files.isDirectory(directoryPath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try (Stream<Path> stream = Files.list(directoryPath)) {

            List<String> fileNames = stream
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(fileNames);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
