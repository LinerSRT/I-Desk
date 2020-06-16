package com.liner.i_desk.Fragments.Request;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.hsalf.smileyrating.SmileyRating;
import com.liner.i_desk.Firebase.DatabaseListener;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.FirebaseValue;
import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.Firebase.UserObject;
import com.liner.i_desk.R;
import com.liner.i_desk.SmileRatingBar;
import com.liner.views.BaseDialog;
import com.liner.views.BaseDialogBuilder;
import com.liner.views.ExpandLayout;

public class RequestActionsFragment extends Fragment {
    int ratedValue = 3;
    private RequestObject requestObject;
    private UserObject userObject;
    private DatabaseListener databaseListener;
    private ExpandLayout expandRequestRatingBar;
    private SmileRatingBar requestRatingBar;
    private MaterialButton closeRequest;
    private MaterialButton acceptRequest;
    private MaterialButton deleteRequest;
    private BaseDialog infoDialog;

    public RequestActionsFragment(RequestObject requestObject) {
        this.requestObject = requestObject;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_actions, container, false);
        expandRequestRatingBar = view.findViewById(R.id.expandRequestRatingBar);
        requestRatingBar = view.findViewById(R.id.requestRatingBar);
        closeRequest = view.findViewById(R.id.closeRequest);
        acceptRequest = view.findViewById(R.id.acceptRequest);
        deleteRequest = view.findViewById(R.id.deleteRequest);
        if (requestObject.isRequestRated())
            requestRatingBar.setVisibility(View.GONE);

        infoDialog = BaseDialogBuilder.buildFast(getActivity(),
                "Ошибка!",
                "Запрос уже отправлен!",
                "",
                "Понятно",
                BaseDialogBuilder.Type.INFO,
                null, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        infoDialog.closeDialog();
                    }
                });


        databaseListener = new DatabaseListener() {
            @Override
            public void onUserAdded(UserObject object, int position) {
                super.onUserAdded(userObject, position);
                if (object.getUserID().equals(Firebase.getUserUID())) {
                    userObject = object;
                    switch (userObject.getUserType()) {
                        case CLIENT:
                            expandRequestRatingBar.expand();
                            acceptRequest.setVisibility(View.GONE);
                            deleteRequest.setVisibility(View.GONE);
                            break;
                        case SERVICE:
                            expandRequestRatingBar.collapse();
                            if (requestObject.getRequestAcceptorID() != null && requestObject.getRequestAcceptorID().equals(Firebase.getUserUID())) {
                                acceptRequest.setVisibility(View.GONE);
                            } else {
                                if (requestObject.getRequestStatus() == RequestObject.RequestStatus.CLOSED) {
                                    acceptRequest.setVisibility(View.GONE);
                                } else {
                                    acceptRequest.setVisibility(View.VISIBLE);
                                }
                            }
                            deleteRequest.setVisibility(View.GONE);
                            break;
                        case ADMIN:
                            expandRequestRatingBar.expand();
                            acceptRequest.setVisibility(View.VISIBLE);
                            deleteRequest.setVisibility(View.VISIBLE);
                            break;
                    }

                }
            }
        };
        databaseListener.startListening();
        requestRatingBar.setSmileySelectedListener(new SmileRatingBar.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                switch (type) {
                    case TERRIBLE:
                        ratedValue = 1;
                        break;
                    case BAD:
                        ratedValue = 2;
                        break;
                    case OKAY:
                        ratedValue = 3;
                        break;
                    case GOOD:
                        ratedValue = 4;
                        break;
                    case GREAT:
                        ratedValue = 5;
                        break;
                    default:
                        ratedValue = 3;
                }
                requestObject.setRequestRate(ratedValue);
                FirebaseValue.setRequest(requestObject.getRequestID(), requestObject);
            }
        });
        closeRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (userObject.getUserType()) {
                    case ADMIN:
                    case SERVICE:
                        switch (requestObject.getRequestStatus()){
                            case CLOSED:
                                break;
                            case CLOSE_REQUEST:
                                infoDialog.setDialogTitleText("Ошибка!");
                                infoDialog.setDialogTextText("Запрос уже отправлен!");
                                infoDialog.setDialogDone("Ок", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        infoDialog.closeDialog();
                                    }
                                });
                                infoDialog.showDialog();
                                break;
                            case PROCESSING:
                                requestObject.setRequestStatus(RequestObject.RequestStatus.CLOSE_REQUEST);
                                FirebaseValue.setRequest(requestObject.getRequestID(), requestObject);
                                infoDialog.setDialogTitleText("Запрос отправлен");
                                infoDialog.setDialogTextText("Запрос на закрытие заявки отправлен успешно");
                                infoDialog.setDialogDone("Ок", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        infoDialog.closeDialog();
                                    }
                                });
                                infoDialog.showDialog();
                                break;
                            case PENDING:
                                infoDialog.setDialogTitleText("Ошибка!");
                                infoDialog.setDialogTextText("Вы должны принять заявку что бы сделать это!");
                                infoDialog.setDialogDone("Ок", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        infoDialog.closeDialog();
                                    }
                                });
                                infoDialog.showDialog();
                                break;
                        }
                        break;
                    case CLIENT:
                        switch (requestObject.getRequestStatus()){
                            case CLOSED:
                            case CLOSE_REQUEST:
                            case PROCESSING:
                                infoDialog.setDialogTitleText("Закрыто!");
                                infoDialog.setDialogTextText("Вы закрыли свою заявку!");
                                infoDialog.setDialogDone("Ок", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        infoDialog.closeDialog();
                                    }
                                });
                                infoDialog.showDialog();
                                requestObject.setRequestStatus(RequestObject.RequestStatus.CLOSED);
                                requestObject.setRequestRated(true);
                                FirebaseValue.setRequest(requestObject.getRequestID(), requestObject);
                                break;
                            case PENDING:
                                infoDialog.setDialogTitleText("Ошибка!");
                                infoDialog.setDialogTextText("Вашу заявку еще никто не принял!");
                                infoDialog.setDialogDone("Ок", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        infoDialog.closeDialog();
                                    }
                                });
                                infoDialog.showDialog();
                                break;
                        }
                        break;
                }
            }
        });

        acceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestObject.setRequestAcceptorPhotoURL(userObject.getUserProfilePhotoURL());
                requestObject.setRequestAcceptorName(userObject.getUserName());
                requestObject.setRequestAcceptorLastOnlineTime(userObject.getUserLastOnlineAt());
                requestObject.setRequestAcceptorID(userObject.getUserID());
                requestObject.setRequestStatus(RequestObject.RequestStatus.PROCESSING);
                FirebaseValue.setRequest(requestObject.getRequestID(), requestObject);
            }
        });


        if (requestObject.getRequestStatus() == RequestObject.RequestStatus.CLOSED) {
            closeRequest.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (databaseListener != null)
            databaseListener.stopListening();
    }
}
