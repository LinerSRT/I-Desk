package com.liner.i_desk.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
public abstract class FirebaseListener<T> {
    private String TAG = "FirebaseListener";
    private Query query;
    private List<T> items;
    private List<String> keys;
    private HashMap<String, DatabaseReference> referenceHashMap;
    private ChildEventListener queryListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String key = dataSnapshot.getKey();
            if (!referenceHashMap.containsKey(key))
                referenceHashMap.put(key, dataSnapshot.getRef());
            if (!keys.contains(key)) {
                T item = getObject(dataSnapshot);
                int position;
                if (s == null) {
                    items.add(item);
                    keys.add(key);
                    position = 0;
                } else {
                    int previousIndex = key.indexOf(s);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == items.size()) {
                        items.add(item);
                        keys.add(key);
                    } else {
                        items.add(nextIndex, item);
                        keys.add(nextIndex, key);
                    }
                    position = nextIndex;
                }
                Log.i(TAG, "New item added. Key={" + key + "}, position={" + position + "}");
                onItemAdded(key, item, position, referenceHashMap.get(key));
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String key = dataSnapshot.getKey();
            if (referenceHashMap.containsKey(key)) {
                referenceHashMap.remove(key);
                referenceHashMap.put(key, dataSnapshot.getRef());
            }
            if (keys.contains(key)) {
                int index = keys.indexOf(key);
                T newItem = getObject(dataSnapshot);
                items.set(index, newItem);
                onItemChanged(key, newItem, index, referenceHashMap.get(key));
                Log.i(TAG, "Item changed. Key={" + key + "}, position={" + index + "}");
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
            if (referenceHashMap.containsKey(key)) {
                referenceHashMap.remove(key);
            }
            if (keys.contains(key)) {
                int index = Objects.requireNonNull(keys).indexOf(key);
                T item = items.get(index);
                keys.remove(index);
                items.remove(index);
                onItemRemoved(key, item, index, referenceHashMap.get(key));
            }


        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String key = dataSnapshot.getKey();
            int index = keys.indexOf(key);
            T item = getObject(dataSnapshot);
            items.remove(index);
            keys.remove(index);
            int newPosition;
            if (s == null) {
                items.add(0, item);
                keys.add(0, key);
                newPosition = 0;
            } else {
                int previousIndex = keys.indexOf(s);
                int nextIndex = previousIndex + 1;
                if (nextIndex == keys.size()) {
                    items.add(item);
                    keys.add(key);
                } else {
                    items.add(nextIndex, item);
                    keys.add(nextIndex, key);
                }
                newPosition = nextIndex;
            }
            Log.i(TAG, "Item moved. Key={" + key + "}, position={" + index + "}");
            onItemMoved(key, item, index, newPosition);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public FirebaseListener(String referencePath) {
        this(FirebaseDatabase.getInstance().getReference(referencePath).limitToLast(100));
    }

    public FirebaseListener(Query query) {
        this(query, new ArrayList<T>(), new ArrayList<String>());
    }

    public FirebaseListener(Query query, List<T> items, List<String> keys) {
        this.query = query;
        this.items = items;
        this.keys = keys;
        this.referenceHashMap = new HashMap<>();
    }

    public void destroy() {
        query.removeEventListener(queryListener);
    }

    public void start() {
        query.addChildEventListener(queryListener);
    }

    public HashMap<String, DatabaseReference> getReferenceHashMap() {
        return referenceHashMap;
    }

    public List<T> getItems() {
        return items;
    }

    public List<String> getKeys() {
        return keys;
    }

    public int getPositionForItem(T item) {
        return items != null && items.size() > 0 ? items.indexOf(item) : -1;
    }

    public boolean contains(T item) {
        return items != null && items.contains(item);
    }

    public abstract void onItemAdded(String key, T item, int pos, DatabaseReference reference);

    public abstract void onItemChanged(String key, T item, int pos, DatabaseReference reference);

    public abstract void onItemRemoved(String key, T item, int pos, DatabaseReference reference);

    public abstract void onItemMoved(String key, T item, int pos, int newPos);

    private T getObject(DataSnapshot dataSnapshot) {
        try {
            return dataSnapshot.getValue(getGenericClass());
        } catch (DatabaseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Class<T> getGenericClass() {
        return (Class<T>) ((ParameterizedType) Objects.requireNonNull(this.getClass().getGenericSuperclass())).getActualTypeArguments()[0];
    }
}
