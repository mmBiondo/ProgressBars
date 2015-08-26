package com.example.megan.progressbarexamples.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.megan.progressbarexamples.R;

import java.text.NumberFormat;

/**
 * Created by Megan on 8/19/15.
 */
public class PercentageCircle extends LinearLayout {

    private Paint circlePaint;
    private Paint arcPaint;
    private RectF rectF;
    private Rect bounds;
    private int percentColor;
    private int secondaryColor;
    private float percent = 0;
    private TextView percentText;
    private TextView totalUnitText;

    public PercentageCircle(Context context) {
        super(context);
        init(null);
    }

    public PercentageCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PercentageCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PercentageCircle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.percentage_circle, this, true);

        circlePaint = new Paint();
        arcPaint = new Paint();
        rectF = new RectF();
        bounds = new Rect();

        int percentTextSize;
        int totalUnitTextSize;

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PercentageCircle);
        try {
            percent = a.getColor(R.styleable.PercentageCircle_percent, 0);
            percentColor = a.getColor(R.styleable.PercentageCircle_percentColor, getResources().getColor(R.color.darker_grey));
            secondaryColor = a.getColor(R.styleable.PercentageCircle_secondaryColor, percentColor);
            percentTextSize = a.getDimensionPixelSize(R.styleable.PercentageCircle_percentTextSize, (int) getContext().getResources().getDimension(R.dimen.percent_text_size));
            totalUnitTextSize = a.getDimensionPixelSize(R.styleable.PercentageCircle_totalUnitTextSize, (int) getContext().getResources().getDimension(R.dimen.total_unit_text_size));

        } finally {
            a.recycle();
        }

        if (getBackground() == null) {
            setBackgroundColor(Color.TRANSPARENT);
        }

        if (!isInEditMode()) {
            percentText = (TextView) findViewById(R.id.percent_complete);
            totalUnitText = (TextView) findViewById(R.id.total_unit);
        }

        if (percentText != null) {
            percentText.setTextColor(percentColor);
            percentText.setTextSize(TypedValue.COMPLEX_UNIT_PX, percentTextSize);

        }
        if (totalUnitText != null) {
            totalUnitText.setTextColor(percentColor);
            totalUnitText.setTextSize(TypedValue.COMPLEX_UNIT_PX, totalUnitTextSize);
        }

        // smooths
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(secondaryColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(20);
        // opacity; //
        if (secondaryColor == percentColor) {
            circlePaint.setAlpha(0x80);
        }
        arcPaint.setAntiAlias(true);
        arcPaint.setColor(percentColor);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(20);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        rectF.set(10, 10, canvas.getWidth() - 10, canvas.getHeight() - 10);
        canvas.drawOval(rectF, circlePaint);

        //Find arc
        float start = -90; //0 is at 3 o'clock
        float arcLength = ((percent * 360) / 100);

        canvas.drawArc(rectF, start, arcLength, false, arcPaint);

        if (!isInEditMode()) {
            fixFontPadding(percentText);
            fixFontPadding(totalUnitText);
        }
    }

    public void setFraction(Float numerator, Float denominator) {

        percent = (numerator / denominator) * 100;

        NumberFormat format = NumberFormat.getIntegerInstance();

        percentText.setText(format.format(numerator));
        totalUnitText.setText("/" + format.format(denominator));

        invalidate();
    }

    public void setPercent(Float percent) {
        this.percent = percent;
        NumberFormat format = NumberFormat.getIntegerInstance();
        percentText.setText(format.format(percent));
        totalUnitText.setText("%");
        invalidate();
    }

    private void fixFontPadding(TextView textView) {

        textView.setIncludeFontPadding(false); //remove the font padding

        textView.getPaint().getTextBounds(textView.getText().toString(), 0, textView.getText().length(), bounds);

        Paint.FontMetrics fm = textView.getPaint().getFontMetrics();
        int fontPadding  = (int) (fm.ascent - bounds.top - 0.5);

        textView.setPadding(0, fontPadding, 0, fontPadding);
        textView.setGravity(textView.getGravity() | Gravity.TOP); //make sure that the gravity is set to the top
    }

}
