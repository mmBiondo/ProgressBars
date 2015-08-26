package com.example.megan.progressbarexamples;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.example.megan.progressbarexamples.widgets.PercentageCircle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PercentageCircle first = (PercentageCircle) findViewById(R.id.first);
        PercentageCircle second = (PercentageCircle) findViewById(R.id.second);
        PercentageCircle third = (PercentageCircle) findViewById(R.id.third);
        PercentageCircle fourth = (PercentageCircle) findViewById(R.id.fourth);

        first.setPercent(75f);
        second.setFraction(1f, 4f);
        third.setPercent(45f);
        fourth.setFraction(3f, 8f);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        final ProgressBar pb = (ProgressBar) findViewById(R.id.vertical_progressbar);

        pb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go(pb);
            }
        });


    }

    private void go(ProgressBar pb){
        ObjectAnimator progressAnimator;
        progressAnimator = ObjectAnimator.ofInt(pb, "progress", pb.getProgress(), pb.getProgress()+10);
        progressAnimator.setDuration(1000);
        progressAnimator.start();
    }

}
