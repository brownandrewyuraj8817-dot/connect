package com.google.exam.connect;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAll() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<Todo> create(@RequestBody Todo todo)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.create(todo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> update(@PathVariable String id, @RequestBody Todo todo)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.update(id, todo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id)
            throws ExecutionException, InterruptedException {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
