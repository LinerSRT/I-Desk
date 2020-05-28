package com.liner.i_desk.Utils.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.arthurivanets.bottomsheets.BaseBottomSheet;
import com.arthurivanets.bottomsheets.config.Config;
import com.liner.i_desk.R;
import com.liner.i_desk.Utils.ColorUtils;

@SuppressLint("ViewConstructor")
public class SelectionBottomSheetDialog extends BaseBottomSheet {
    private TextView selectionDialogTitle;
    private TextView selectionDialogText;
    private RadioButton selection1;
    private RadioButton selection2;
    private RadioGroup selectionRadioGroup;

    public SelectionBottomSheetDialog(@NonNull Activity hostActivity) {
        super(hostActivity, new Config.Builder(hostActivity)
                .sheetBackgroundColor(ColorUtils.getThemeColor(hostActivity, R.attr.backgroundColor))
                .dismissOnTouchOutside(false)
                .build());

    }

    @NonNull
    @Override
    public final View onCreateSheetContentView(@NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.selection_dialog_layout, this, false);
        selectionDialogTitle = view.findViewById(R.id.selectionDialogTitle);
        selectionDialogText = view.findViewById(R.id.selectionDialogText);
        selection1 = view.findViewById(R.id.selectionDialog1);
        selection2 = view.findViewById(R.id.selectionDialog2);
        selectionRadioGroup = view.findViewById(R.id.selectionRadioGroup);
        return view;
    }


    public void setDialogTitle(String dialogTitle) {
        selectionDialogTitle.setText(dialogTitle);
    }

    public void setDialogText(String dialogText) {
        selectionDialogText.setText(dialogText);
    }



    public void setListener(final ISeledtionDialogListener listener) {
        selectionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selection1ID = selection1.getId();
                int selection2ID = selection2.getId();
                listener.onSelected((i == selection1ID) ? 0:1, i, (i == selection1ID) ? selection1.getText().toString():selection2.getText().toString());
            }
        });
    }

    public void create(){
        show(true);
    }

    public interface ISeledtionDialogListener{
        void onSelected(int id, int viewID, String text);
    }
}