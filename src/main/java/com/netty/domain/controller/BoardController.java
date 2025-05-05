package com.netty.domain.controller;

import com.netty.domain.entity.Board;
import com.netty.domain.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @Operation(summary = "Create a new board", description = "Creates a new board with the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Board created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Board> createBoard(@RequestBody Board board) {
        return boardService.createBoard(board);
    }

    @Operation(summary = "Get all boards", description = "Retrieves a list of all boards")
    @ApiResponse(responseCode = "200", description = "List of boards retrieved successfully")
    @GetMapping
    public Flux<Board> getAllBoards() {
        return boardService.getAllBoards();
    }

    @Operation(summary = "Get a board by ID", description = "Retrieves a board by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Board found"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @GetMapping("/{id}")
    public Mono<Board> getBoardById(@PathVariable Long id) {
        return boardService.getBoardById(id);
    }

    @Operation(summary = "Update a board", description = "Updates an existing board by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Board updated successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public Mono<Board> updateBoard(@PathVariable Long id, @RequestBody Board board) {
        return boardService.updateBoard(id, board);
    }

    @Operation(summary = "Delete a board", description = "Deletes a board by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Board deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBoard(@PathVariable Long id) {
        return boardService.deleteBoard(id);
    }
}