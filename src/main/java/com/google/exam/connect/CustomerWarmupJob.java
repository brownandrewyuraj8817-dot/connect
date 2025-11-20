package com.google.exam.connect;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class CustomerWarmupJob {

    private static final int MAX_CUSTOMERS = 300;

    private final CustomerService customerService;

    public CustomerWarmupJob(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Cứ 5s: nếu chưa đủ 300 khách thì tạo thêm 1
    @Scheduled(fixedRate = 3000)
    public void warmup() throws ExecutionException, InterruptedException {
        List<Customer> all = customerService.getAll();
        int total = all.size();

        if (total >= MAX_CUSTOMERS) {
            System.out.println("[WARMUP] Reached max " + MAX_CUSTOMERS +
                    " customers, skip creating. Current=" + total);
            return;
        }

        Customer c = customerService.createRandomCustomer();
        System.out.println("[WARMUP] Created: " + c.getId() + " - " + c.getName()
                + " | total ~ " + (total + 1));

    }
}
