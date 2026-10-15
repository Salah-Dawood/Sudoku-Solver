package com.salah.sudoku.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SudokuPuzzleRequest {
    @NotEmpty(message = "enter name please")
    @Size(min = 2,max = 12,message = "between 2 and 12 characters please")
    private String name;

    @NotNull(message = "null file??")
    private MultipartFile file;
}
