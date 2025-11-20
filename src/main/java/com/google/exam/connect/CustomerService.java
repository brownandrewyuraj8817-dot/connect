package com.google.exam.connect;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class CustomerService {

    private static final String COLLECTION = "customers";
    private final Faker faker = new Faker();

    private Firestore db() {
        return FirestoreClient.getFirestore();
    }

    // Tạo customer NGẪU NHIÊN (dùng cho warmup)
    public Customer createRandomCustomer() throws ExecutionException, InterruptedException {
        Firestore db = db();
        DocumentReference doc = db.collection(COLLECTION).document();

        Customer c = new Customer();
        c.setId(doc.getId());
        c.setName(faker.name().fullName());
        c.setEmail(faker.internet().emailAddress());
        c.setPhone(faker.phoneNumber().phoneNumber());
        c.setAddress(
                faker.address().streetAddress() + ", " +
                        faker.address().city() + ", " +
                        faker.address().country()
        );

        doc.set(c).get();
        return c;
    }

    // Tạo customer từ payload UI
    public Customer create(Customer input) throws ExecutionException, InterruptedException {
        Firestore db = db();
        DocumentReference doc = db.collection(COLLECTION).document();

        Customer c = new Customer();
        c.setId(doc.getId());
        c.setName(input.getName());
        c.setEmail(input.getEmail());
        c.setPhone(input.getPhone());
        c.setAddress(input.getAddress());

        doc.set(c).get();
        return c;
    }

    // Lấy tất cả customers
    public List<Customer> getAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db().collection(COLLECTION).get();
        List<QueryDocumentSnapshot> docs = future.get().getDocuments();

        List<Customer> result = new ArrayList<>();
        for (QueryDocumentSnapshot doc : docs) {
            Customer c = doc.toObject(Customer.class);
            c.setId(doc.getId());
            result.add(c);
        }
        return result;
    }

    // Update 1 customer
    public Customer updateCustomer(Customer customer) throws ExecutionException, InterruptedException {
        if (customer.getId() == null) {
            throw new IllegalArgumentException("Customer id is required for update");
        }
        db().collection(COLLECTION)
                .document(customer.getId())
                .set(customer)
                .get();
        return customer;
    }

    // Xóa 1 customer
    public void deleteById(String id) throws ExecutionException, InterruptedException {
        db().collection(COLLECTION).document(id).delete().get();
    }

    // Search theo name chứa keyword (lọc trong memory)
    public List<Customer> searchByNameContains(String keyword)
            throws ExecutionException, InterruptedException {
        String lower = keyword.toLowerCase(Locale.ROOT);
        List<Customer> all = getAll();
        List<Customer> result = new ArrayList<>();
        for (Customer c : all) {
            if (c.getName() != null &&
                    c.getName().toLowerCase(Locale.ROOT).contains(lower)) {
                result.add(c);
            }
        }
        return result;
    }
}
