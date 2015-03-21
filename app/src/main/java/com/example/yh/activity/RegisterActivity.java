package com.example.yh.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.yh.data.Constants;
import com.example.yh.data.User;
import com.example.yh.netword.RequestManager;
import com.example.yh.smartcycle.R;

import org.json.JSONObject;

public class RegisterActivity extends Activity {

    private static final String TAG = "RegisterActivity";

    public static final int REGISTER = 222;

    private TextView mTvBackBtn;
    private EditText mEtAccountNum;
    private RadioButton mRbMale;
    private EditText mEtPassword;
    private EditText mEtConfirmPassword;
    private Button mBtRegister;
    private ProgressBar mPbRegister;
    private User mUser;
    private RequestQueue mRequestQueue = RequestManager.getRequestQueue();

    private static String registerUrl = Constants.BASE_URL + "RegisterServlet?username=%s&sex=%s&password=%s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        User.init(getApplicationContext());
        mUser = User.getInstance();
        findViews();
        setClick();
    }

    private void setClick() {
        mTvBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });
        mBtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    private void register() {
        final AlertDialog dialog = ProgressDialog.show(RegisterActivity.this,"提交信息","正在提交");
        dialog.show();
        final String sex = mRbMale.isChecked()?"male":"female";
        final String username = mEtAccountNum.getText().toString();
        String password = mEtPassword.getText().toString();
        String passwordConfirm = mEtConfirmPassword.getText().toString();
        if(TextUtils.isEmpty(username)){
            Toast.makeText(RegisterActivity.this,"请输入账号",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this,"请输入密码",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(passwordConfirm)){
            Toast.makeText(RegisterActivity.this,"确认密码不正确",Toast.LENGTH_SHORT).show();
            mEtPassword.setText("");
            mEtConfirmPassword.setText("");
            dialog.dismiss();
            return;
        }
        String url = String.format(registerUrl, username, sex, password);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url,null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int status = jsonObject.optInt("status");
                String info = jsonObject.optString("info");
    Log.i(TAG,jsonObject.toString());
                if (status == 0) {

                    mUser.setUsername(username);
                    mUser.setSex(sex);
                    mUser.setLogin(true);
                    mUser.setId(jsonObject.optString("userId"));
                    Toast.makeText(RegisterActivity.this, info, Toast.LENGTH_SHORT).show();
                    setResult(LoginActivity.SUCCESS);
                    dialog.dismiss();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this,info,Toast.LENGTH_SHORT).show();
                    setResult(LoginActivity.ERROR);
                    dialog.dismiss();
                    finish();
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(TAG,volleyError.toString());
                dialog.dismiss();
                setResult(LoginActivity.ERROR);
                finish();
            }
        });
        mRequestQueue.add(jsonObjectRequest);
}

    private void findViews() {
        mEtAccountNum = (EditText) findViewById(R.id.acti_register_editText_accountNum);
        mRbMale = (RadioButton) findViewById(R.id.acti_register_male_radioButton);
        mEtPassword = (EditText) findViewById(R.id.acti_register_editText_password);
        mEtConfirmPassword = (EditText) findViewById(R.id.acti_register_editText_password_confirm);
        mTvBackBtn = (TextView) findViewById(R.id.acti_register_back_btn);
        mBtRegister = (Button) findViewById(R.id.acti_register_register_btn);
        mPbRegister = (ProgressBar) findViewById(R.id.acti_register_register_pb);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
