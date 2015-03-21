package com.example.yh.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.yh.smartcycle.R;

/**
 * Created by yh on 2015/2/22.
 */
public class LoadingFooter {
    protected View mLoadingFooter;
    protected TextView mLoadingText;
    protected State mState = State.Idle;
    private ProgressBar mProgress;
    private long mAnimationDuration;
    public static enum State{
        Idle,TheEnd,Loading
    }
    public LoadingFooter(Context context) {
        mLoadingFooter = LayoutInflater.from(context).inflate(R.layout.loading_foot, null);
        mLoadingFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mProgress = (ProgressBar) mLoadingFooter.findViewById(R.id.progressBar);
        mLoadingText = (TextView) mLoadingFooter.findViewById(R.id.textView);
        mAnimationDuration = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
        setState(State.Idle);
    }

    public View getView() {
        return mLoadingFooter;
    }

    public State getState() {
        return mState;
    }

    public void setState(final State state, long delay) {
        mLoadingFooter.postDelayed(new Runnable() {
            @Override
            public void run() {
                setState(state);
            }
        }, delay);
    }

    public void setState(State status) {
        if(mState == status) {
            return;
        }
        mState = status;

        mLoadingFooter.setVisibility(View.VISIBLE);
        switch(status){
            case Loading:
                mLoadingText.setVisibility(View.GONE);
                mProgress.setVisibility(View.VISIBLE);
                break;
            case TheEnd:
//                mLoadingText.setVisibility(View.VISIBLE);
//                mProgress.setVisibility(View.GONE);
                mLoadingFooter.setVisibility(View.GONE);
                break;
            case Idle:
                mProgress.setVisibility(View.GONE);
                mLoadingText.setVisibility(View.VISIBLE);
            default:
                mLoadingFooter.setVisibility(View.GONE);
                break;
        }
    }
}
