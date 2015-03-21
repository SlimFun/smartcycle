package com.example.yh.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class LoginActivity extends Activity {

    public static final int LOGIN = 333;
    public static final int SUCCESS = 400;
    public static final int ERROR = 500;

    private TextView mBtnBack;
    private EditText mEtAccountNum;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private Button mBtnRegister;
    private ProgressBar mPbLogin;
    private Context context;

    private User user;
    private static String loginUrl = Constants.BASE_URL + "LoginServlet?username=%s&password=%s";
    private RequestQueue mRequestQueue = RequestManager.getRequestQueue();
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getParent().getActionBar().hide();
        context = getApplicationContext();
         User.init(context);
        user = User.getInstance();

        setContentView(R.layout.activity_login);
        findViews();
        setClick();
    }

    private void setClick() {
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(ERROR);
                LoginActivity.this.finish();
            }
        });
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(mEtAccountNum.getText())){
                    Toast.makeText(LoginActivity.this,"请输入账号",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(mEtPassword.getText())){
                    Toast.makeText(LoginActivity.this,"请输入密码",Toast.LENGTH_SHORT).show();
                    return;
                }
                loginLoading(true);
                mEtAccountNum.setEnabled(false);
                mEtPassword.setEnabled(false);
                login();
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent,RegisterActivity.REGISTER);
            }
        });
    }

    private void login() {
        String username = mEtAccountNum.getText().toString();
        String password = mEtPassword.getText().toString();
        String url = String.format(loginUrl,username,password);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
         new Response.Listener<JSONObject>() {
         @Override
            public void onResponse(JSONObject jsonObject) {
                loginLoading(false);
                int status = jsonObject.optInt("status");
                String info = jsonObject.optString("info");
                if(status == 0){
                    try{
                        jsonObject = jsonObject.getJSONObject("user");

                        String userId = jsonObject.optString("id");
                        String attrs = jsonObject.optString("attrs");
                        String sex = jsonObject.optString("sex");
                        String username = jsonObject.optString("username");
                        String faceUrl = jsonObject.optString("face");
                        String birthday = jsonObject.optString("birthday");

                        Log.e(TAG, username);
                        Log.e(TAG, userId);
                        Log.e(TAG, attrs);
                        Log.e(TAG, sex);



                        user.setUsername(username);
                        user.setAttrs(attrs);
                        user.setId(userId);
                        user.setLogin(true);
                        user.setSex(sex);
                        user.setFaceUrl(faceUrl);
  Toast.makeText(LoginActivity.this,faceUrl,Toast.LENGTH_SHORT).show();
                        setResult(SUCCESS);
                        LoginActivity.this.finish();
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                }else{
                    Toast.makeText(LoginActivity.this,info,Toast.LENGTH_SHORT).show();
                    mEtAccountNum.setEnabled(true);
                    mEtPassword.setEnabled(true);
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                 setResult(ERROR);
            }
        });
        mRequestQueue.add(jsonObjectRequest);

    }

    private void loginLoading(boolean isLoading) {
        if(isLoading){
            mBtnLogin.setClickable(false);
            mBtnLogin.setText("");
            mPbLogin.setVisibility(View.VISIBLE);
        }else{
            mBtnLogin.setClickable(true);
            mBtnLogin.setText("登录");
            mPbLogin.setVisibility(View.GONE);
        }
    }

    private void findViews() {
        mBtnBack = (TextView) findViewById(R.id.acti_login_back_btn);
        mEtAccountNum = (EditText) findViewById(R.id.acti_login_edittext_accountnum);
        mEtPassword = (EditText) findViewById(R.id.acti_login_edittext_pwd);
        mBtnLogin = (Button) findViewById(R.id.acti_login_login_btn);
        mBtnRegister = (Button) findViewById(R.id.acti_login_register_btn);
        mPbLogin = (ProgressBar) findViewById(R.id.acti_login_login_pb);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_login, menu);
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
