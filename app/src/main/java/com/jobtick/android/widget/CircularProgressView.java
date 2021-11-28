package com.jobtick.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.jobtick.android.R;

public class CircularProgressView extends View {

    private Context mContext;
    private Paint mPaint;
    private int mProgress = 0;
    private static int MAX_PROGRESS = 100;
    /** * radian */
    private int mAngle;
    /** * Middle text */
    private String mText;
    /** * Cylindrical color */
    private int outRoundColor;
    /** * Color of inner circle */
    private int inRoundColor;
    /** * Line width */
    private int roundWidth;
    private int style;
    /*** Font color*/
    private int textColor;
    /** * font size */
    private float textSize;
    /** * Whether the font is bold */
    private boolean isBold;
    /** * Progress bar color */
    private int progressBarColor;

    public CircularProgressView(Context context) {
        this(context, null);
    }
    public CircularProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CircularProgressView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    @TargetApi(21)
    public CircularProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init(attrs);
    }

    /**
     * Resolve custom properties
     * @param attrs
     */
    public void init(AttributeSet attrs) {
        mPaint = new Paint();
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        outRoundColor = typedArray.getColor(R.styleable.CircleProgressBar_outCircleColor, getResources().getColor(R.color.colorPrimary));
        inRoundColor = typedArray.getColor(R.styleable.CircleProgressBar_inCircleColor, getResources().getColor(R.color.colorPrimaryDark));
        progressBarColor = typedArray.getColor(R.styleable.CircleProgressBar_progressColor, getResources().getColor(R.color.colorAccent));
        isBold = typedArray.getBoolean(R.styleable.CircleProgressBar_textBold, false);
        textColor = typedArray.getColor(R.styleable.CircleProgressBar_textColor, Color.BLACK);
        roundWidth = typedArray.getDimensionPixelOffset(R.styleable.CircleProgressBar_lineWidth, 20);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) { /** * Extra circle */
        super.onDraw(canvas);
        int center = getWidth() / 2;//Center of a circle
        int radius = (center - roundWidth / 2);// radius

        //mPaint.setColor(outRoundColor); //Cylindrical color
        mPaint.setStrokeWidth(roundWidth); //Line width
        mPaint.setStyle(Paint.Style.STROKE); //Hollow circle
        mPaint.setAntiAlias(true); //Eliminating sawtooth
        //canvas.drawCircle(center, center, radius, mPaint); //Inner circle

        mPaint.setColor(inRoundColor);
        radius = radius - roundWidth;
        canvas.drawCircle(center, center, radius, mPaint); //Drawing progress is an arc

        mPaint.setColor(progressBarColor);
        RectF rectF = new RectF(center - radius, center - radius, center + radius, center + radius);//Circumscribed rectangle of arc range
        canvas.drawArc(rectF, -90, mAngle, false, mPaint);

        canvas.save(); //Save previously drawn before panning the canvas

        // Drawing the ball at the end of the progress and rotating the canvas
        mPaint.setStyle(Paint.Style.FILL);
        // Move canvas coordinate origin to center
        canvas.translate(center, center);
        // Rotation is the same as progress, because progress starts at - 90 degrees, so - 90 degrees
        canvas.rotate(mAngle - 90);
        // Similarly, starting from the center of the circle, directly translate the origin to the position where you want to draw the ball
        canvas.translate(radius, 0);
        //canvas.drawCircle(0, 0, roundWidth, mPaint);

        // Restore canvas coordinates after drawing
        canvas.restore();

        // Draw text to translate coordinates to center
        canvas.translate(center, center);
        mPaint.setStrokeWidth(0);
        mPaint.setColor(textColor);
        if (isBold) { //Bold font
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        } if (!TextUtils.isEmpty(mText)) {
            // Dynamically set the text length as circle radius and calculate the font size
            float textLength = mText.length();
            textSize = radius / textLength;
            mPaint.setTextSize(textSize);
            // Draw text in the middle
            float textWidth = mPaint.measureText(mText);
            canvas.drawText(mText, -textWidth / 2, textSize / 2, mPaint);
        }


    }


    public int getProgress() {
        return mProgress;
    }

    /**
     * Setting progress
     * @return
     */
    public void setProgress(int p) {
        if (p > MAX_PROGRESS) {
            mProgress = MAX_PROGRESS;
            mAngle = 360;
        } else {
            mProgress = p;
            mAngle = 360 * p / MAX_PROGRESS;
        }
        //Update canvas
        invalidate();
    }

    public String getText() {
        return mText;
    }

    /**
     * Set text
     * @param mText
     */
    public void setText(String mText) {
        this.mText = mText;
        invalidate();
    }

}