package com.google.exam.connect;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class TodoService {

    private static final String COLLECTION_NAME = "todos";

    public List<Todo> getAll() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> docs = future.get().getDocuments();

        List<Todo> result = new ArrayList<>();
        for (QueryDocumentSnapshot doc : docs) {
            Todo todo = doc.toObject(Todo.class);
            todo.setId(doc.getId());
            result.add(todo);
        }
        return result;
    }

    public Todo create(Todo todo) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document();
        todo.setId(docRef.getId());
        docRef.set(todo).get();
        return todo;
    }

    public Todo update(String id, Todo todo) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        todo.setId(id);
        db.collection(COLLECTION_NAME).document(id).set(todo).get();
        return todo;
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        db.collection(COLLECTION_NAME).document(id).delete().get();
    }
}
