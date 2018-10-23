package com.bignerdranch.android.geoquiz;

// Model
import java.util.Arrays;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;
import android.widget.Toast;
import android.app.Activity;
import android.widget.Button;
import java.util.Collections;
import android.content.Intent;
import android.graphics.Color;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mp1, mp2;
    Context context = this;
    private TextView mScore;
    private Button mPrevButton;
    private Button mTrueButton;
    private Button mCheatButton;
    private Button mResetButton;
    private Button mFalseButton;
    private ActionBar mActionBar;
    private TextView mQuestionNum;
    private ImageButton mNextButton;
    private ProgressBar mProgressBar;
    private TextView mQuestionTextView;
    private Button mResultSummaryButton;
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final String TAG = "MainActivity";
    private ArrayList<Integer> mAnsweredQuestions = new ArrayList<>();
    private static final String KEY_INDEX = "com.bignerdranch.android.geoquiz.index";
    private static final String EXTRA_SCORE = "com.bignerdranch.android.geoquiz.score";
    private static final String KEY_ANSWERED = "com.bignerdranch.android.geoquiz.answered";
    private static final String KEY_CHEATBANK = "com.bignerdranch.android.geoquiz.cheatarray";
    private static final String KEY_CHEATER_ARRAY = "com.bignerdranch.android.geoquiz.cheater";
    private static final String EXTRA_CHEAT_BANK = "com.bignerdranch.android.geoquiz.cheat_BANK";
    private static final String KEY_OVERALLSCORE = "com.bignerdranch.android.geoquiz.overallScore";
    private static final String KEY_CHEAT_ATTEMPTED = "com.bignerdranch.android.geoquiz.cheat_attempted";
    private static final String EXTRA_TOTAL_QUESTIONS = "com.bignerdranch.android.geoquiz.total_questions";
    private static final String EXTRA_CHEAT_ATTEMPTED = "com.bignerdranch.android.geoquiz.cheat_attempted";
    private static final String EXTRA_QUESTIONS_ANSWERED = "com.bignerdranch.android.geoquiz.questions_answered";

    private Question[] mQuestionBank = new Question[]
    {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private boolean[] mIsCheater = new boolean[mQuestionBank.length];
    private int mCheatBank = 3;  // Used to keep track of the user's cheating times (maximum = 3 times to cheat)
    private int mCheatAttempted = 0;
    private int mCurrentIndex = 0;
    private int mCurrentScore = 0;

    // For result summary purpose
    private int mTotalQuestion = mQuestionBank.length;
    int percentage = (int) (((double)mCurrentScore/mQuestionBank.length)*100);

    @Override
    protected void onCreate(Bundle savedInstanceState) // read back
    {
        Arrays.fill(mIsCheater,false); // Filling up the entire array that the user is not cheating
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set content of main page using the Res under layout and the activity main file
        Log.d(TAG, "onCreate is called!");

        // Setting the color of the action bar
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#021028")));

        if(savedInstanceState != null)
        {
            mCheatBank = savedInstanceState.getInt(KEY_CHEATBANK);
            mCheatAttempted = savedInstanceState.getInt(KEY_CHEAT_ATTEMPTED);
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBooleanArray(KEY_CHEATER_ARRAY);
            mAnsweredQuestions = savedInstanceState.getIntegerArrayList(KEY_ANSWERED);
            mCurrentScore = savedInstanceState.getInt(KEY_OVERALLSCORE, 0);
        }

        // Shuffle the questions in the array
        Collections.shuffle(Arrays.asList(mQuestionBank));
        mProgressBar = findViewById(R.id.progress);


        mTrueButton = (Button) findViewById(R.id.true_button); // to link your widget with your code
        mTrueButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                Toast mytoast;
//                mytoast = Toast.makeText(MainActivity.this, // where u want to show the toast
//                                R.string.correct_toast, // which string to show
//                                Toast.LENGTH_SHORT);
//                mytoast.setGravity(Gravity.TOP,120,100); // Gravity.TOP = constant value / static
//                mytoast.show(); // how long u want to show the toast; short = a few secs, long = longer
                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false); // Stopping user from clicking the button again
                mAnsweredQuestions.add(mCurrentIndex);
                checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button); // R.java file
        mFalseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                Toast.makeText(MainActivity.this,
//                                R.string.incorrect_toast,
//                                Toast.LENGTH_SHORT).show();
                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);
                mAnsweredQuestions.add(mCurrentIndex);
                checkAnswer(false);
            }
        });

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
//                mIsCheater = false;
                showQuestionNum();
                updateQuestion();
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
//                mIsCheater = false;
                updateQuestion();
                showQuestionNum();
                // Restart the score when they start over
//                if(mCurrentIndex == 0) {
//                    mCurrentScore = 0;
//                }
            }
        });

        mPrevButton = (Button) findViewById(R.id.previous_button);
        mPrevButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                prevQuestion();
                showQuestionNum();
            }
        });

        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Start CheatActivity
//                Intent intent = new Intent(MainActivity.this, CheatActivity.class);
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();  // ?????????
                Intent intent = CheatActivity.newIntent(MainActivity.this, answerIsTrue);
                intent.putExtra(EXTRA_CHEAT_BANK, mCheatBank);
                if (mCheatBank > 0)
                {
                    startActivityForResult(intent, REQUEST_CODE_CHEAT);
                }
                else
                {
                    toStringCheat();
                }
            }
        });

        mResultSummaryButton = (Button)findViewById(R.id.resultSummary_button);
        mResultSummaryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                int percentage = (int) (((double)mCurrentScore/mQuestionBank.length)*100);

                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
//                Bundle b = new Bundle();
                intent.putExtra(EXTRA_QUESTIONS_ANSWERED, mAnsweredQuestions.size());
                intent.putExtra(EXTRA_TOTAL_QUESTIONS, mTotalQuestion);
                intent.putExtra(EXTRA_CHEAT_ATTEMPTED, mCheatAttempted);
                intent.putExtra(EXTRA_SCORE, percentage);
                startActivity(intent);
            }
        });

        mResetButton =  (Button)findViewById(R.id.reset_button);
        mResetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                recreate();
                mTrueButton.setEnabled(true);
                mFalseButton.setEnabled(true);
                mCheatButton.setEnabled(true);
                mAnsweredQuestions.clear();
                mProgressBar.setProgress(0);
                Collections.shuffle(Arrays.asList(mQuestionBank));
                Arrays.fill(mIsCheater,false);
                mCurrentScore = 0;
                mCheatBank = 3;
                mCheatAttempted = 0;
                mCurrentIndex = 0;
                showScore();
                showQuestionNum();
            }
        });
        updateQuestion();

        mp1 = MediaPlayer.create(context, R.raw.welldone);
        mp2 = MediaPlayer.create(context, R.raw.tryagain);

        mQuestionNum = (TextView)findViewById(R.id.questionNum);
        showQuestionNum();

        mScore = (TextView)findViewById(R.id.score);
        showScore();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != Activity.RESULT_OK) // RESULT_OK = operation was successful
        {
            return;
        }
        if(requestCode == REQUEST_CODE_CHEAT)
        {
            if(data == null)
            {
                return;
            }
            mIsCheater[mCurrentIndex] = true;
            mCheatBank --;
            mCheatAttempted ++;
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) // override the button
    {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putInt(KEY_CHEATBANK, mCheatBank);
        savedInstanceState.putInt(KEY_CHEAT_ATTEMPTED, mCheatAttempted);
        savedInstanceState.putInt(KEY_OVERALLSCORE, mCurrentScore);
        savedInstanceState.putBooleanArray(KEY_CHEATER_ARRAY, mIsCheater);
        savedInstanceState.putIntegerArrayList(KEY_ANSWERED, mAnsweredQuestions );
//        savedInstanceState.putBoolean(KEY_CHEATER, mIsCheater);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion()
    {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        if(mAnsweredQuestions.contains(mCurrentIndex))
        {
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        }
        else
        {
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        }
    }

    private void prevQuestion()
    {
        if (mCurrentIndex > 0)
        {
            mCurrentIndex -= 1;
            updateQuestion();
        }
        else
        {
            mCurrentIndex = mQuestionBank.length - 1;
            updateQuestion();
        }
    }

    String alertTitle;
    int correct = 0, incorrect = 0;

    private void checkAnswer(boolean userPressedTrue)
    {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0; // Default toast = 0 = ntg

//        mAnsweredQuestions[mCurrentIndex] = true;

        if (mIsCheater[mCurrentIndex])
        {
            messageResId =R.string.judgement_toast;
        }
        else
        {
            if (userPressedTrue == answerIsTrue)
            {
                messageResId = R.string.correct_toast;
                alertTitle = "Correct!";
                mCurrentScore += 1;
                correct += 1;
                // Play sound from mp3
                try
                {
                    if(mp1.isPlaying())
                    {
                        mp1.stop();
                        mp1.release();
                        mp1 = MediaPlayer.create(context, R.raw.welldone);
                    }   mp1.start();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                Log.d(TAG, mCurrentScore + "");
                showScore();
            }
            else
            {
                messageResId = R.string.incorrect_toast;
                alertTitle = "Wrong...";
                incorrect += 1;
                try
                {
                    if(mp2.isPlaying())
                    {
                        mp2.stop();
                        mp2.release();
                        mp2 = MediaPlayer.create(context, R.raw.tryagain);
                    }   mp2.start();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

            // Create dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(alertTitle);
            builder.setMessage("Answer: " + answerIsTrue);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    if (mAnsweredQuestions.size() == mQuestionBank.length)
                    {
                        // Show result - by returning the context of the entire application (the process of all the activities are running inside of)
                        mResultSummaryButton.performClick();
                    }
                    else
                    {
                        updateQuestion();
                    }
                }
            });
            // Back key doesn't close the dialog, close when user click the dialog button (either using space bar, enter / OK button)
            builder.setCancelable(false);
            builder.show();
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        showProgress();

        if (mAnsweredQuestions.size() == mQuestionBank.length)
        {
            showOverallScore();
        }
    }

    private void showProgress()
    {
        int x = (mAnsweredQuestions.size() * 100) / mQuestionBank.length;
        mProgressBar.setProgress(x);
    }

    int currentPosition = 0;
    private void showQuestionNum()
    {
        int questionNum = (mCurrentIndex + 1);
        currentPosition ++;
        mQuestionNum.setText("Question Number: " + questionNum + "/" + mQuestionBank.length);
    }

    int score = 0;
    private void showScore()
    {
        int percentage = (int) (((double)mCurrentScore/mQuestionBank.length)*100);
        mScore.setText("Score: " + percentage + "%");
        score += percentage;
    }

    private void showOverallScore()
    {
        int percentage = (int) (((double)mCurrentScore/mQuestionBank.length)*100);
        Toast.makeText(this, "Correct: " + mCurrentScore + "\n" + "Incorrect: " + (mTotalQuestion - mCurrentScore) + "\n" + "You got " + percentage + "% correct answers", Toast.LENGTH_LONG).show();
    }

    public void toStringCheat()
    {
        String stringNoCheat = "NO MORE CHEAT!";
        mCheatButton.setEnabled(false);
        Toast.makeText(this, stringNoCheat, Toast.LENGTH_SHORT).show();
    }
}