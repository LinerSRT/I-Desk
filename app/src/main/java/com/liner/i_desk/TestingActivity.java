package com.liner.i_desk;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.liner.views.BaseDialog;
import com.liner.views.BaseDialogBuilder;


public class TestingActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        String[] test = new String[]{"One", "Two", "Three"};
        final BaseDialog baseDialog = new BaseDialogBuilder(this)
                .setSelectionList(test)
                .setSelectionListener(new BaseDialog.BaseDialogSelectionListener() {
                    @Override
                    public void onItemClick(int position) {
                        Toast.makeText(TestingActivity.this, "You selected "+position, Toast.LENGTH_SHORT).show();
                    }
                })
                .setDialogType(BaseDialogBuilder.Type.SINGLE_CHOOSE)
                .setDialogTitle("Title")
                .setDialogText("Select one")
                .build();









    }


}
