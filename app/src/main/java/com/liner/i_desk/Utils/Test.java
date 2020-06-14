package com.liner.i_desk.Utils;

import com.google.firebase.database.DatabaseReference;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.Firebase.UserObject;
import com.liner.utils.TextUtils;
import com.liner.utils.Time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void createTestRequest(int count) {
        for (int i = 0; i < count; i++) {
            final RequestObject newRequest = new RequestObject();
            newRequest.setRequestID(TextUtils.getUniqueString());
            newRequest.setRequestType(randomEnum(RequestObject.RequestType.class));
            newRequest.setRequestPriority(randomEnum(RequestObject.RequestPriority.class));
            newRequest.setRequestStatus(RequestObject.RequestStatus.PENDING);
            newRequest.setRequestTitle(randomWord(5) + " - " + i);
            newRequest.setRequestText(randomWord(125));
            newRequest.setRequestUserDeviceText(randomWord(35));
            newRequest.setRequestMessages(new HashMap<String, String>());
            newRequest.setRequestFiles(new HashMap<String, String>());
            newRequest.setRequestCreatedAt(Time.getTime());
            newRequest.setRequestDeadlineAt(Time.getTime() + TimeUnit.HOURS.toMillis(i));
            if(i == 2)
                newRequest.setRequestCreatorID(Firebase.getUserUID());
            else
                newRequest.setRequestCreatorID(TextUtils.getUniqueString());

            newRequest.setRequestCreatorLastOnlineTime(Time.getTime());
            FirebaseValue.getUser(Firebase.getUserUID(), new FirebaseValue.ValueListener() {
                @Override
                public void onSuccess(Object object, DatabaseReference databaseReference) {
                    UserObject userObject = (UserObject) object;
                    newRequest.setRequestCreatorName(userObject.getUserName());
                    newRequest.setRequestCreatorPhotoURL(userObject.getUserProfilePhotoURL());
                    if (userObject.getUserRequests() == null)
                        userObject.setUserRequests(new ArrayList<String>());
                    userObject.getUserRequests().add(newRequest.getRequestID());
                    Objects.requireNonNull(Firebase.getRequestsDatabase()).child(newRequest.getRequestID()).setValue(newRequest);
                    FirebaseValue.setUser(Firebase.getUserUID(), userObject);
                }

                @Override
                public void onFail(String errorMessage) {

                }
            });
        }
    }


    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = new Random().nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public static String randomWord(int length) {
        Random random = new Random();
        StringBuilder word = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            word.append((char) ('a' + random.nextInt(26)));
        }

        return word.toString();
    }
}
