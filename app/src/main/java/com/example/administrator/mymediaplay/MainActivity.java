package com.example.administrator.mymediaplay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button bt_normal,bt_custom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_normal= (Button) findViewById(R.id.bt_normal);
        bt_custom= (Button) findViewById(R.id.bt_custom);

        bt_normal.setOnClickListener(this);
        bt_custom.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId())
        {
            case R.id.bt_normal:
                intent.setClass(this,NormalActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_custom:
                intent.setClass(this,CustomActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
