package com.bignerdranch.android.geoquiz;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.graphics.Color;
import android.content.Context;
import android.widget.TextView;
import android.support.v7.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;

public class CheatActivity extends AppCompatActivity {

    private int mCheatBank;
    private Boolean mIsCheater;
    private ActionBar mActionBar;
    private boolean mAnswerIsTrue;
    private TextView mAnswerTextView;
    private Button mShowAnswerButton;
    private TextView mVersionTextView;
    private TextView mCheatCountTextView;
    public static final String KEY_CHEATER = "cheater";
    private static final String EXTRA_CHEAT_BANK = "com.bignerdranch.android.geoquiz.cheat_BANK";
    private static final String EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown";
    private static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true";

    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        // Setting the color of the action bar
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#021028")));

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mShowAnswerButton = (Button)findViewById(R.id.show_answer_button);
        mCheatBank = getIntent().getIntExtra(EXTRA_CHEAT_BANK, 0);
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        if(savedInstanceState != null)
        {
            setAnswerShownResult(savedInstanceState.getBoolean(KEY_CHEATER, false));
            mIsCheater = savedInstanceState.getBoolean(KEY_CHEATER);
            mAnswerIsTrue = savedInstanceState.getBoolean(EXTRA_ANSWER_IS_TRUE, false);
            mCheatBank = savedInstanceState.getInt(EXTRA_CHEAT_BANK, 0);

            if((mAnswerIsTrue == true) && (mIsCheater == true))
            {
                mAnswerTextView.setText(R.string.true_button);
            }
            else if((mAnswerIsTrue == false) && (mIsCheater == true))
            {
                mAnswerTextView.setText(R.string.false_button);
            }
            else
            {
                mIsCheater = false;
                setAnswerShownResult(false);
            }
        }

        // Counting the remaining cheating time for users to cheat
        mCheatCountTextView = (TextView) findViewById(R.id.cheatBank_text_view);
        mCheatCountTextView.setText(mCheatBank +" cheats remaining");

        mVersionTextView = (TextView) findViewById(R.id.version_text_view);
        mVersionTextView.setText("SDK Version: " + Build.VERSION.SDK_INT);

        mShowAnswerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mAnswerIsTrue)
                {
                    mAnswerTextView.setText(R.string.true_button);
                }
                else
                {
                    mAnswerTextView.setText(R.string.false_button);
                }
                setAnswerShownResult(true);
                mIsCheater = true;
            }
        });
    }

    private void setAnswerShownResult(boolean isAnswerShown)
    {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) // override the button
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(KEY_CHEATER, mIsCheater);
        savedInstanceState.putInt(EXTRA_CHEAT_BANK, mCheatBank);
        savedInstanceState.putBoolean(EXTRA_ANSWER_IS_TRUE, mAnswerIsTrue);
    }
}