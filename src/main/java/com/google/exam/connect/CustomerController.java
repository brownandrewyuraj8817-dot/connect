package com.google.exam.connect;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAll() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<Customer> create(@RequestBody Customer input)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.create(input));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@PathVariable String id, @RequestBody Customer input)
            throws ExecutionException, InterruptedException {
        input.setId(id);
        return ResponseEntity.ok(service.updateCustomer(input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id)
            throws ExecutionException, InterruptedException {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
