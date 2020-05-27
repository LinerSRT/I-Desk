package com.liner.i_desk.API;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.liner.i_desk.API.Data.User;

public class FirebaseHelper {
    public static void getUserModel(String userID, final IFirebaseHelperListener helperListener){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference currentUserReference = firebaseDatabase.getReference().child("Users").child(userID);
        currentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                helperListener.onSuccess(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                helperListener.onFail(databaseError.getMessage());
            }
        });
    }





    public static void getUserModel(final IFirebaseHelperListener helperListener){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference currentUserReference = firebaseDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        currentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                helperListener.onSuccess(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                helperListener.onFail(databaseError.getMessage());
            }
        });
    }

    public static void setUserModel(String useruID, User userModel, final IFirebaseHelperListener helperListener){
        getUserDatabase(useruID).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(helperListener != null) {
                    if (task.isSuccessful()) {
                        helperListener.onSuccess(task);
                    } else {
                        helperListener.onFail("Failed");
                    }
                }
            }
        });
    }

    public static void setUserValue(String useruID, String key, Object object, final IFirebaseHelperListener helperListener){
        getUserDatabase(useruID).child(key).setValue(object).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    helperListener.onSuccess(task);
                } else {
                    helperListener.onFail("Failed");
                }
            }
        });
    }


    public static void checkUsersDatabaseExistValue(final Object valueObj, final String databaseKey, final IFirebaseDatabaseValueListener listener){
        DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference("Users");
        Query query = usersDatabase.orderByChild(databaseKey).equalTo(String.valueOf(valueObj));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean founded = false;
                for (DataSnapshot item: dataSnapshot.getChildren()) {
                    if(String.valueOf(valueObj).equals(String.valueOf(item.child(databaseKey).getValue())) ){
                        founded = true;
                        break;
                    }
                }
                if(!founded) {
                    listener.onValueNotFound();
                } else {
                    listener.onValueExists();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public interface IFirebaseDatabaseValueListener {
        void onValueExists();
        void onValueNotFound();
    }

    public interface IFirebaseHelperListener <T>{
        void onSuccess(T result);
        void onFail(String reason);
    }

    public static DatabaseReference getUserDatabase(String userID){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        return firebaseDatabase.getReference().child("Users").child(userID);
    }
    public static DatabaseReference getUserDatabase(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        return firebaseDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }


}
