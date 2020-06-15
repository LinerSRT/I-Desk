package com.liner.i_desk;

import android.os.Bundle;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.liner.utils.PickerFileFilter;
import com.liner.views.BaseDialog;
import com.liner.views.BaseDialogBuilder;
import com.liner.views.MediaPicker.FilePickerFragment;

import java.io.File;


public class TestingActivity extends AppCompatActivity {


    private SeekBar testSSSS1;
    private BaseDialog baseDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        baseDialog = new BaseDialogBuilder(this)
                .setDialogTitle("Выберите фото")
                .setDialogType(BaseDialogBuilder.Type.IMAGE_CHOOSE)
                .setFilePickListener(new BaseDialog.BaseDialogFilePickListener() {
                    @Override
                    public void onFileSelected(File file) {

                    }
                })
                .build();

        baseDialog.showDialog();
        testSSSS1 = findViewById(R.id.testSSSS1);
        testSSSS1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });







    }


}
