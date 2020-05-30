package com.liner.i_desk.Utils.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.liner.i_desk.API.Data.Request;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.Animations.ViewAnimator;
import com.liner.i_desk.Utils.ColorUtils;

public class RequestTypeView extends LinearLayout implements View.OnClickListener{
    private Context context;
    private onTypeSelectedListener onTypeSelectedListener;
    public Request.Type type = null;
    private TextView serviceTypeView;
    private TextView consultationTypeView;
    private TextView incedentTypeView;

    public RequestTypeView(Context context) {
        super(context);
        init(context, null);
    }

    public RequestTypeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet){
        this.context = context;
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View view = inflate(getContext(), R.layout.request_type_view_layout, null);
        view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(view);
        serviceTypeView = view.findViewById(R.id.requestViewServiceTypeView);
        consultationTypeView = view.findViewById(R.id.requestViewConsultationTypeView);
        incedentTypeView = view.findViewById(R.id.requestViewIncedentTypeView);
        serviceTypeView.setOnClickListener(this);
        consultationTypeView.setOnClickListener(this);
        incedentTypeView.setOnClickListener(this);
    }


    private void updateView(Request.Type type){
        switch (type){
            case SERVICE:
                serviceTypeView.setBackground(context.getDrawable(R.drawable.round_corner));
                serviceTypeView.setTextColor(context.getResources().getColor(R.color.color_white));
                incedentTypeView.setBackground(context.getDrawable(R.drawable.round_corner_background));
                incedentTypeView.setTextColor(ColorUtils.getThemeColor(context, R.attr.textColor));
                consultationTypeView.setBackground(context.getDrawable(R.drawable.round_corner_background));
                consultationTypeView.setTextColor(ColorUtils.getThemeColor(context, R.attr.textColor));
                break;
            case INCIDENT:
                incedentTypeView.setBackground(context.getDrawable(R.drawable.round_corner));
                incedentTypeView.setTextColor(context.getResources().getColor(R.color.color_white));
                consultationTypeView.setBackground(context.getDrawable(R.drawable.round_corner_background));
                consultationTypeView.setTextColor(ColorUtils.getThemeColor(context, R.attr.textColor));
                serviceTypeView.setBackground(context.getDrawable(R.drawable.round_corner_background));
                serviceTypeView.setTextColor(ColorUtils.getThemeColor(context, R.attr.textColor));
                break;
            case CONSULTATION:
                consultationTypeView.setBackground(context.getDrawable(R.drawable.round_corner));
                consultationTypeView.setTextColor(context.getResources().getColor(R.color.color_white));
                incedentTypeView.setBackground(context.getDrawable(R.drawable.round_corner_background));
                incedentTypeView.setTextColor(ColorUtils.getThemeColor(context, R.attr.textColor));
                serviceTypeView.setBackground(context.getDrawable(R.drawable.round_corner_background));
                serviceTypeView.setTextColor(ColorUtils.getThemeColor(context, R.attr.textColor));
                break;
        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()){
            case R.id.requestViewServiceTypeView:
                type = Request.Type.SERVICE;
                break;
            case R.id.requestViewConsultationTypeView:
                type = Request.Type.CONSULTATION;
                break;
            case R.id.requestViewIncedentTypeView:
                type = Request.Type.INCIDENT;
                break;
        }
        new ViewAnimator(view).animateAction(200, new ViewAnimator.AnimatorListener() {
            @Override
            public void done() {
                if(onTypeSelectedListener != null)
                    onTypeSelectedListener.onSelected(type);
                updateView(type);
            }
        });
    }

    public Request.Type getType() {
        return type;
    }

    public void setOnTypeSelectedListener(RequestTypeView.onTypeSelectedListener onTypeSelectedListener) {
        this.onTypeSelectedListener = onTypeSelectedListener;
    }

    public interface onTypeSelectedListener{
        void onSelected(Request.Type type);
    }
}
