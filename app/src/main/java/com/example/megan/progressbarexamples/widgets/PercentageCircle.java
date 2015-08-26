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

    private static float DEFAULT_START_ANGLE = -90f;

    private Paint circlePaint;
    private Paint arcPaint;
    private RectF rectF;
    private Rect bounds;
    private int percentColor;
    private int secondaryColor;
    private TextView percentText;
    private TextView totalUnitText;
    private Float totalUnits;
    private Float completedUnits;
    private boolean animated;

    private Float prevActualUnits;
    private Float sweepAngle;
    private Float progressAngle;
    private Float prevAngle;

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
        completedUnits = 0f;


        int percentTextSize;
        int totalUnitTextSize;

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PercentageCircle);
        try {
            animated = a.getBoolean(R.styleable.PercentageCircle_animated, true);
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

        prevAngle = DEFAULT_START_ANGLE;
        sweepAngle = 0f;
        progressAngle = 0f;
        prevActualUnits = 0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        rectF.set(10, 10, canvas.getWidth() - 10, canvas.getHeight() - 10);
        canvas.drawOval(rectF, circlePaint);

        if (!isInEditMode()) {
            fixFontPadding(percentText);
            fixFontPadding(totalUnitText);

            //Animation
            if (animated) {
                canvas.drawArc(rectF, prevAngle, progressAngle, false, arcPaint);

                if (progressAngle < sweepAngle) {
                    progressAngle += 360f / 100f;
                    if (progressAngle >= (getAngleIncrements() * prevActualUnits)) {
                        percentText.setText(String.format("%.0f", prevActualUnits++));
                        if (progressAngle > sweepAngle) {
                            progressAngle = sweepAngle;
                        }
                    }
                    invalidate();
                } else {
                    percentText.setText(String.format("%.0f", completedUnits));
                }

            } else {
                canvas.drawArc(rectF, prevAngle, sweepAngle, false, arcPaint);
                percentText.setText(String.format("%.0f", completedUnits));
            }

        }
    }

    private void updateMeter() {
        if (prevAngle != DEFAULT_START_ANGLE) {
            prevAngle = sweepAngle;
        }

        if (completedUnits == null) {
            sweepAngle = 0f;
        } else {
            sweepAngle = completedUnits / (isPercentage() ? 100f : totalUnits) * 360.0f;
        }

        invalidate();
    }

    public void setFraction(Float numerator, Float denominator) {
        prevActualUnits = 0f;
        totalUnits = denominator;
        completedUnits = numerator;
        if (denominator != null) {
            NumberFormat format = NumberFormat.getIntegerInstance();

            totalUnitText.setText("/" + format.format(denominator));
        } else {
            totalUnitText.setText("");
        }

        updateMeter();
    }

    public void setPercent(Float percent) {
        prevActualUnits = 0f;
        completedUnits = percent;
        totalUnitText.setText("%");
        updateMeter();
    }

    private void fixFontPadding(TextView textView) {

        textView.setIncludeFontPadding(false); //remove the font padding

        textView.getPaint().getTextBounds(textView.getText().toString(), 0, textView.getText().length(), bounds);

        Paint.FontMetrics fm = textView.getPaint().getFontMetrics();
        int fontPadding = (int) (fm.ascent - bounds.top - 0.5);

        textView.setPadding(0, fontPadding, 0, fontPadding);
        textView.setGravity(textView.getGravity() | Gravity.TOP); //make sure that the gravity is set to the top
    }

    private float getAngleIncrements() {
        return 360f / (isPercentage() ? 100f : totalUnits);

    }

    private boolean isPercentage() {
        return totalUnits == null;
    }

}
