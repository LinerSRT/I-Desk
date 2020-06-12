package com.liner.views;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;


import java.util.ArrayList;
import java.util.List;

public class RequestCheckListLayoutView extends BaseItem {
    private RecyclerView requestRecycler;
    private List<String> requestCheckList = new ArrayList<>();
    private RequestCheckAdapter requestCheckAdapter;
    private Activity activity;


    public RequestCheckListLayoutView(Context context) {
        super(context);
    }

    public RequestCheckListLayoutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onFindViewById() {
        requestRecycler = findViewById(R.id.requestCheckLayoutRecycler);
        requestCheckAdapter = new RequestCheckAdapter();
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(requestRecycler);
        requestRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        requestRecycler.setAdapter(requestCheckAdapter);
    }


    @Override
    protected void onInflate() {
        inflater.inflate(R.layout.request_check_layout_view, this);
    }

    public void addItem(String item) {
        requestCheckList.add(item);
        requestCheckAdapter.notifyItemInserted(requestCheckList.size() - 1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestRecycler.smoothScrollToPosition(requestCheckList.size() - 1);
            }
        }, 50);
    }


    public List<String> getRequestCheckList() {
        return requestCheckList;
    }

    public class RequestCheckAdapter extends RecyclerView.Adapter<RequestCheckAdapter.RequestCheckHolder> {
        @NonNull
        @Override
        public RequestCheckHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RequestCheckHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.request_check_item_holder, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final RequestCheckHolder holder, int position) {
            final String currentText = requestCheckList.get(position);
            holder.requestCheckView.getRequestCheckText().setText(currentText);
            holder.requestCheckView.showView(AnimationUtils.loadAnimation(getContext(), R.anim.item_in), new OvershootInterpolator());
        }

        @Override
        public int getItemCount() {
            return requestCheckList.size();
        }


        private class RequestCheckHolder extends RecyclerView.ViewHolder {
            private RequestCheckView requestCheckView;
            private int state = 0;

            public RequestCheckHolder(@NonNull final View itemView) {
                super(itemView);
                requestCheckView = itemView.findViewById(R.id.requestCheckView);
                requestCheckView.getRequestCheckText().setEnabled(false);
                requestCheckView.getRequestDeleteButton().setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        if (activity != null) {
                            final BaseDialog baseDialog = new BaseDialogBuilder(activity)
                                    .setDialogText("Вы действительно хотите удалить файл из списка? ")
                                    .setDialogTitle("Удаление")
                                    .setDialogType(BaseDialogBuilder.Type.QUESTION)
                                    .build();
                            baseDialog.setDialogCancel("Отмена", new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    baseDialog.closeDialog();
                                }
                            });
                            baseDialog.setDialogDone("Удалить", new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    baseDialog.closeDialog();
                                    int position = getAdapterPosition();
                                    requestCheckList.remove(position);
                                    notifyItemRemoved(position);
                                }
                            });
                            baseDialog.showDialog();
                        }
                    }
                });
                requestCheckView.getRequestCheckEditButton().setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        if(state == 0) {
                            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                            requestCheckView.getRequestCheckText().setEnabled(true);
                            requestCheckView.getRequestCheckText().setFocusable(true);
                            requestCheckView.getRequestCheckText().requestFocus();
                            requestCheckView.getRequestCheckText().setSelection(requestCheckList.get(getAdapterPosition()).length());
                            requestCheckView.getRequestCheckEditButton().setImageResource(R.drawable.done_icon);
                            state = 1;
                        } else {
                            String newText = requestCheckView.getRequestCheckText().getText().toString();
                            requestCheckList.set(getAdapterPosition(), newText);
                            requestCheckView.getRequestCheckText().setEnabled(false);
                            requestCheckView.getRequestCheckText().setFocusable(false);
                            requestCheckView.getRequestCheckText().clearFocus();
                            requestCheckView.getRequestCheckEditButton().setImageResource(R.drawable.edit_icon);
                            InputMethodManager inputMethodManager =(InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(requestCheckView.getRequestCheckText().getWindowToken(), 0);
                            state = 0;
                        }
                    }
                });
            }
        }
    }

}
