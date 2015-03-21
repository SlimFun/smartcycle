package com.example.yh.activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.example.yh.adapter.EmoViewPagerAdapter;
import com.example.yh.adapter.EmoteAdapter;
import com.example.yh.data.FaceText;
import com.example.yh.smartcycle.R;
import com.example.yh.util.FaceTextUtils;
import com.example.yh.view.EmotionsEditText;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends ActionBarActivity {
    private List<FaceText> emos;
    private ViewPager mPageEmo;
    private EmotionsEditText mEtComment;
    private Button mBtnEmo;
    private LinearLayout mLlBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        findViews();
        setClicks();
    }

    private void setClicks() {
        mBtnEmo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlBottom.setVisibility(View.VISIBLE);
            }
        });
    }

    private void findViews() {
        mPageEmo = (ViewPager) findViewById(R.id.acti_chat_pager_emo);
        mEtComment = (EmotionsEditText) findViewById(R.id.acti_chat_edit_user_comment);
        mBtnEmo = (Button) findViewById(R.id.acti_chat_btn_emo);
        mLlBottom = (LinearLayout) findViewById(R.id.acti_chat_layout_bottom);
        emos = FaceTextUtils.faceTexts;

        List<View> views = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            views.add(getGridView(i));
        }
        mPageEmo.setAdapter(new EmoViewPagerAdapter(views));
    }

    private View getGridView(int i) {
        View view = View.inflate(ChatActivity.this, R.layout.include_emo_gridview, null);
        GridView gridView = (GridView) view.findViewById(R.id.acti_chat_emo_gridview);
        List<FaceText> list = new ArrayList<>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        }else {
            list.addAll(emos.subList(21, emos.size()));
        }
        final EmoteAdapter gridAdapter = new EmoteAdapter(ChatActivity.this, list);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.text;
                if (mEtComment != null && !TextUtils.isEmpty(key)) {
                    int start = mEtComment.getSelectionStart();
                    CharSequence content = mEtComment.getText().insert(start, key);
                    mEtComment.setText(content);
                    CharSequence info = mEtComment.getText();
                    if (info instanceof Spannable) {
                        Spannable spanText = (Spannable) info;
                        Selection.setSelection(spanText, start + key.length());
                    }
                }
            }
        });
        return view;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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
