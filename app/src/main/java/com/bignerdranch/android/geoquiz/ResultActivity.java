package com.bignerdranch.android.geoquiz;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.graphics.Color;
import android.widget.TextView;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private static final String EXTRA_SCORE = "com.bignerdranch.android.geoquiz.score";
    private static final String EXTRA_TOTAL_QUESTIONS = "com.bignerdranch.android.geoquiz.total_questions";
    private static final String EXTRA_CHEAT_ATTEMPTED = "com.bignerdranch.android.geoquiz.cheat_attempted";
    private static final String EXTRA_QUESTIONS_ANSWERED = "com.bignerdranch.android.geoquiz.questions_answered";

    private int totalQs;
    private int qAnswered;
    private int scoreObtained;
    private int cheatAttempted;
    private Button mReturnButton;
    private ActionBar mActionBar;
    private int questionsAnswered;
    private TextView mTotalScoreTextView;
    private TextView mResultSummaryTextView;
    private TextView mCheatAttemptedTextView;
    private TextView mQuestionAnsweredTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // changing the color of the action bar
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#021028")));

        if(savedInstanceState != null)
        {
            scoreObtained = savedInstanceState.getInt(EXTRA_SCORE);
            totalQs = savedInstanceState.getInt(EXTRA_TOTAL_QUESTIONS);
            qAnswered = savedInstanceState.getInt(EXTRA_QUESTIONS_ANSWERED);
            cheatAttempted = savedInstanceState.getInt(EXTRA_CHEAT_ATTEMPTED);
        }

        mResultSummaryTextView = (TextView)findViewById(R.id.resultSummary_text_view);
        mQuestionAnsweredTextView = (TextView)findViewById(R.id.totalQuestionAnswered_text_view) ;
        mTotalScoreTextView = (TextView) findViewById(R.id.totalScore_text_view);
        mCheatAttemptedTextView = (TextView)findViewById(R.id.totalCheat_text_view);

        scoreObtained = getIntent().getIntExtra(EXTRA_SCORE, 0);
        totalQs = getIntent().getIntExtra(EXTRA_TOTAL_QUESTIONS, 0);
        qAnswered = getIntent().getIntExtra(EXTRA_QUESTIONS_ANSWERED, 0);
        cheatAttempted = getIntent().getIntExtra(EXTRA_CHEAT_ATTEMPTED,0);

        mQuestionAnsweredTextView.setText(qAnswered + " / " + totalQs);
        mCheatAttemptedTextView.setText(cheatAttempted + " cheat attempted");
        mTotalScoreTextView.setText( "Score: "+ scoreObtained +"%");

        // Getting values from intent that are store in the bundle
        int score = getIntent().getIntExtra("CORRECT_COUNT", 0);

        // Save and retrieve data; getSharedPreferences returns a SharedPreference instance pointing to the file that contains the values of the preferences
        // MODE_PRIVATE = file can only be accessed using calling application
        SharedPreferences settings = getSharedPreferences("GeoQuiz", Context.MODE_PRIVATE);
        int tS = settings.getInt("Total Score: ", 0);
        tS += score;

        // Update total score
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("TotalScore", tS);
        editor.commit();

        // Return to question interface
        Button mReturnButton = (Button)findViewById(R.id.return_button);
        mReturnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) // override the button
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(EXTRA_SCORE, scoreObtained);
        savedInstanceState.putInt(EXTRA_TOTAL_QUESTIONS, totalQs);
        savedInstanceState.putInt(EXTRA_QUESTIONS_ANSWERED, qAnswered);
        savedInstanceState.putInt(EXTRA_CHEAT_ATTEMPTED, cheatAttempted);
    }
}