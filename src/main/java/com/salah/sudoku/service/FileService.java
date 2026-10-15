package com.salah.sudoku.service;

import com.salah.sudoku.model.SudokuPuzzle;
import com.salah.sudoku.model.request.SudokuPuzzleRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    private final String PUZZLES_DIR = "puzzles/";
    private final String SOLUTION_DIR = "solved/";

    public String uploadPuzzle(SudokuPuzzleRequest request){
        try {
            Path uploadPath = Paths.get(PUZZLES_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            MultipartFile file = request.getFile();

            String originalFileName = file.getOriginalFilename();


            String fileName = request.getName() + "_" + originalFileName;

            Path filePath = uploadPath.resolve(fileName);

            file.transferTo(filePath);

            return filePath.toString();

        } catch (IOException e) {
            throw new RuntimeException("Could not save file", e);
        }
    }

    public void parsePuzzleToTextFile(SudokuPuzzle puzzle) throws IOException {
        StringBuilder text = new StringBuilder();
        try {
            Path uploadPath = Paths.get(SOLUTION_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    int value = puzzle.getRows()
                            .get(i)
                            .getCells()
                            .get(j)
                            .getValue();

                    text.append(value);
                }

                text.append(System.lineSeparator());
            }
            String fileName = getSolutionFileName(puzzle.getName());
            Files.writeString(Path.of(SOLUTION_DIR + fileName), text.toString());
        } catch (IOException e) {
            throw new RuntimeException("Could not save file", e);
        }
    }

    public static String getSolutionFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isEmpty()) {
            return originalFileName;
        }

        String normalized = originalFileName.replace("\\", "/");

        //removing dir from file name
        int firstSlash = normalized.indexOf('/');
        if (firstSlash != -1 && firstSlash < normalized.length() - 1) {
            normalized = normalized.substring(firstSlash + 1);
        }

        int dotIndex = normalized.lastIndexOf('.');
        if (dotIndex != -1) {
            String baseName = normalized.substring(0, dotIndex);
            String extension = normalized.substring(dotIndex);
            return baseName + ".solution" + extension;
        } else {
            return normalized + ".solution.txt";
        }
    }

}
