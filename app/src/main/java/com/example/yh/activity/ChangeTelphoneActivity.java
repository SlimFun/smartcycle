package com.example.yh.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.yh.smartcycle.R;

public class ChangeTelphoneActivity extends ActionBarActivity {

    public static final int SUCC = 444;
    private static final int FL = 555;

    private ImageView mIvBack;
    private ImageView mIvOk;
    private EditText mEtTelphone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_telphone);
        findViews();
        initViews();
        setClicks();
    }

    private void initViews() {
        mEtTelphone.setText(getIntent().getStringExtra("telphone"));
    }

    private void setClicks() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(FL);
                finish();
            }
        });
        mIvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telphone = mEtTelphone.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("telphone", telphone);
                setResult(SUCC, intent);
                finish();
            }
        });
    }

    private void findViews() {
        mEtTelphone = (EditText) findViewById(R.id.acti_change_telphone_et);
        mIvBack = (ImageView) findViewById(R.id.acti_change_telphone_back);
        mIvOk = (ImageView) findViewById(R.id.acti_change_telphone_OK);
    }

    @Override
    public void onBackPressed() {
        setResult(FL);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_telphone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
