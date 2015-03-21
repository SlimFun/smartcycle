package com.example.yh.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yh on 2015/3/1.
 */
public class EmotionsEditText extends EditText {
    public EmotionsEditText(Context context) {
        super(context);
    }

    public EmotionsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmotionsEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!TextUtils.isEmpty(text)) {
            super.setText(replace(text.toString()),type);
        }
        super.setText(text, type);
    }

    private Pattern buildPattern() {
        return Pattern.compile("\\\\[a-z0-9]{3}", Pattern.CASE_INSENSITIVE);
    }

    private CharSequence replace(String s) {
        SpannableString spannableString = new SpannableString(s);
        int start = 0;
        Pattern pattern = buildPattern();
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            String faceText = matcher.group();
            String key = faceText.substring(1);
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),
                    getContext().getResources().getIdentifier(key, "drawable", getContext().getPackageName()), options);
            ImageSpan imageSpan = new ImageSpan(getContext(), bitmap);
            int startIndex = s.indexOf(faceText, start);
            int endIndex = startIndex + faceText.length();
            if (startIndex >= 0) {
                spannableString.setSpan(imageSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            start = endIndex - 1;
        }
        return spannableString;
    }
}
