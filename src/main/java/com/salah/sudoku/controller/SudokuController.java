package com.salah.sudoku.controller;

import com.salah.sudoku.model.request.SudokuPuzzleRequest;
import com.salah.sudoku.service.SudokuService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SudokuController {
    private final SudokuService sudokuService;

    public SudokuController(SudokuService sudokuService) {
        this.sudokuService = sudokuService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> puzzle(@ModelAttribute @Valid SudokuPuzzleRequest request) throws IOException {
        System.out.println("Recieved request");
        return sudokuService.puzzleRunner(request);
    }

    @GetMapping("/get-solved")
    public ResponseEntity<List<String>> getAllSolved(){
        return sudokuService.getAllSolved();
    }

    @GetMapping("/get-solved/{fileName}")
    public ResponseEntity<?> getSolutionFile(@PathVariable String fileName){
        Resource fileResource = sudokuService.loadSolvedFile(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }
}
