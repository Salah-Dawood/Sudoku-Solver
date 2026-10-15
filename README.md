# Sudoku Solver

A Spring Boot REST API that solves Sudoku puzzles from uploaded text files, with a small static web page for using it from the browser.

## Features

- Upload a puzzle as a `.txt` file and get back a solved grid
- Backtracking solver that always fills the cell with the fewest candidates first
- Input validation: allowed characters, 9×9 shape, and conflicting given digits
- Solutions are saved to disk and can be listed, viewed, and downloaded
- Single-page front end, no build step

## Tech stack

- Java 17+
- Spring Boot 3 (Web, Validation)
- Lombok

## Puzzle format

Nine lines of nine characters. Use `0`, `.` or `-` for empty cells. Whitespace and blank lines are ignored.

```
530070000
600195000
098000060
800060003
400803001
700020006
060000280
000419005
000080079
```

## Web page

Open `http://localhost:8080/`, paste a puzzle or load a `.txt` file, name it, and press **Solve puzzle**. Saved solutions are listed at the bottom of the page.

## API

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/upload` | Solve a puzzle. Multipart form with `name` (2–12 characters) and `file` (the puzzle `.txt`). Returns `201` with the solution file name in the body and a `Location` header. |
| `GET` | `/api/get-solved` | List the names of all solved files. Returns `404` if nothing has been solved yet. |
| `GET` | `/api/get-solved/{fileName}` | Download a solution as a plain text attachment. Returns `404` if it does not exist. |

Invalid input and unsolvable puzzles return `400` with a message describing the problem.


## How it works

1. The upload is saved to `puzzles/` as `<name>_<original file name>`.
2. The file is validated and parsed into a puzzle made of 81 cells, shared between its rows, columns, and 3×3 grids.
3. Given digits are checked for conflicts.
4. The solver picks the empty cell with the fewest possible values, tries each in turn, and backtracks on dead ends.
5. The finished grid is written to `solved/` as `<name>_<original file name>.solution.txt`.

## Project structure

The project adopts a standard Layered Architecture split into distinct logical tiers: Controllers (web/routing layer), Services (business logic layer)
