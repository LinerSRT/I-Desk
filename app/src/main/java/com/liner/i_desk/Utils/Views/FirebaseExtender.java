package com.liner.i_desk.Utils.Views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class FirebaseExtender <T>{
    private Query query;
    private List<T> items;
    private List<String> keys;

    public FirebaseExtender(Query query) {
        this(query, new ArrayList<T>(), new ArrayList<String>());
    }


    public FirebaseExtender(Query query, List<T> items, List<String> keys) {
        this.query = query;
        this.items = items;
        this.keys = keys;
        query.addChildEventListener(queryListener);
    }

    private ChildEventListener queryListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String key = dataSnapshot.getKey();
            if(!keys.contains(key)){
                T item = getObject(dataSnapshot);
                int position;
                if(s == null){
                    items.add(item);
                    keys.add(key);
                    position = 0;
                } else {
                    int previousIndex = key.indexOf(s);
                    int nextIndex = previousIndex + 1;
                    if(nextIndex == items.size()){
                        items.add(item);
                        keys.add(key);
                    } else {
                        items.add(nextIndex, item);
                        keys.add(nextIndex, key);
                    }
                    position = nextIndex;
                }
                onItemAdded(key, item, position);
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String key = dataSnapshot.getKey();
            if(keys.contains(key)){
                int index = keys.indexOf(key);
                T oldItem = items.get(index);
                T newItem = getObject(dataSnapshot);
                items.set(index, newItem);
                onItemChanged(key, oldItem, newItem, index);
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
            if(keys.contains(key)){
                int index = key.indexOf(key);
                T item = items.get(index);
                keys.remove(index);
                items.remove(index);
                onItemRemoved(key, item, index);
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
            onItemMoved(key, item, index, newPosition);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    public abstract void onItemAdded(String key, T item, int pos);
    public abstract void onItemChanged(String key, T oldItem, T newItem, int pos);
    public abstract void onItemRemoved(String key, T item, int pos);
    public abstract void onItemMoved(String key, T item, int pos, int newPos);


    public void destroy(){
        query.removeEventListener(queryListener);
    }

    public List<T> getItems(){
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

    protected T getObject(DataSnapshot dataSnapshot){
        return dataSnapshot.getValue(getGenericClass());
    }

    private Class<T> getGenericClass() {
        return (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
