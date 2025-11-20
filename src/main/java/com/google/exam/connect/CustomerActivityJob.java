package com.google.exam.connect;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
public class CustomerActivityJob {

    private static final int MIN_CUSTOMERS_FOR_ACTIVITY = 20;
    private static final int MAX_UPDATES = 5;
    private static final int MAX_DELETES = 5;
    private static final int MAX_SEARCHES = 5;

    private final CustomerService customerService;
    private final Random random = new Random();

    private int updateCount = 0;
    private int deleteCount = 0;
    private int searchCount = 0;

    public CustomerActivityJob(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Cứ 5s chạy 1 lần
    @Scheduled(fixedRate = 5000, initialDelay = 3000)
    public void activity() throws ExecutionException, InterruptedException {
        System.out.println("\n=== ACTIVITY JOB (5s) START ===");

        List<Customer> all = customerService.getAll();
        int total = all.size();
        System.out.println("[ACTIVITY] Current total customers: " + total);

        if (total < MIN_CUSTOMERS_FOR_ACTIVITY) {
            System.out.println("[ACTIVITY] total < " + MIN_CUSTOMERS_FOR_ACTIVITY +
                    " => waiting for more customers.");
            System.out.println("=== ACTIVITY JOB (5s) END ===\n");
            return;
        }

        // DELETE: tối đa 5 lần
        if (deleteCount < MAX_DELETES && !all.isEmpty()) {
            Customer toDelete = all.get(random.nextInt(all.size()));
            customerService.deleteById(toDelete.getId());
            deleteCount++;
            System.out.println("[ACTIVITY] Deleted: " + toDelete.getId() +
                    " | deleteCount=" + deleteCount + "/" + MAX_DELETES);

            Map<String, Object> meta = new HashMap<>();
            meta.put("name", toDelete.getName());
        } else {
            System.out.println("[ACTIVITY] Delete quota reached or no customers to delete.");
        }

        // refresh list
        all = customerService.getAll();
        total = all.size();

        if (!all.isEmpty()) {
            // UPDATE: tối đa 5 lần
            if (updateCount < MAX_UPDATES) {
                Customer toUpdate = all.get(random.nextInt(all.size()));
                toUpdate.setName(toUpdate.getName() + " (auto-update)");
                customerService.updateCustomer(toUpdate);
                updateCount++;
                System.out.println("[ACTIVITY] Updated: " + toUpdate.getId() +
                        " | updateCount=" + updateCount + "/" + MAX_UPDATES);

                Map<String, Object> meta = new HashMap<>();
                meta.put("name", toUpdate.getName());
            } else {
                System.out.println("[ACTIVITY] Update quota reached.");
            }

            // SEARCH: tối đa 5 lần
            if (searchCount < MAX_SEARCHES) {
                Customer sample = all.get(random.nextInt(all.size()));
                String[] parts = sample.getName().split(" ");
                String keyword = parts[0];

                List<Customer> searchResult = customerService.searchByNameContains(keyword);
                searchCount++;
                System.out.println("[ACTIVITY] Search keyword='" + keyword +
                        "', found=" + searchResult.size() +
                        " | searchCount=" + searchCount + "/" + MAX_SEARCHES);

                Map<String, Object> meta = new HashMap<>();
                meta.put("keyword", keyword);
                meta.put("resultCount", searchResult.size());
            } else {
                System.out.println("[ACTIVITY] Search quota reached.");
            }
        }


        System.out.println("=== ACTIVITY JOB (5s) END ===\n");
    }
}
