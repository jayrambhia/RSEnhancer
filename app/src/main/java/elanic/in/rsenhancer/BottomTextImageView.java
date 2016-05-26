package elanic.in.rsenhancer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Jay Rambhia on 5/23/16.
 */
public class BottomTextImageView extends ViewGroup {

    protected ImageView imageView;
    protected TextView titleView;

    private int imageSize = 48; // dp
    private float density = 1f;

    private boolean active = false;

    public BottomTextImageView(Context context) {
        super(context);
        init(context, null);
    }

    public BottomTextImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BottomTextImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BottomTextImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @CallSuper
    protected void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        density = context.getResources().getDisplayMetrics().density;

        imageSize = (int)(density * imageSize);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BottomTextImageView);
            if (a != null) {
                imageSize = a.getDimensionPixelSize(R.styleable.BottomTextImageView_image_size, imageSize);
                a.recycle();
            }
        }

        addImageView(context, attrs);
        addTitleView(context, attrs);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setActive(!active, true);
            }
        });

        setClipToPadding(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        View child1 = imageView;
        int c1w = child1.getMeasuredWidth();
        int c1h = child1.getMeasuredHeight();

        View child2 = titleView;
        int c2w = child2.getMeasuredWidth();
        int c2h = child2.getMeasuredHeight();

        int width = Math.max(c1w, c2w) + getPaddingLeft() + getPaddingTop();
        int height = c1h + c2h + getPaddingTop() + getPaddingBottom();

        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (width > originalWidth) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }

        int originalHeight = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (height > originalHeight || heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;

        View child0 = getChildAt(0);
        child0.layout(0, 0, width, height);

        View child1 = imageView;

        l = getPaddingLeft();
        t = getPaddingTop();

        int w1 = child1.getMeasuredWidth();

        child1.layout((width - w1)/2, t, (width + w1)/2, t + child1.getMeasuredHeight());

        t += child1.getMeasuredHeight();

        View child2 = titleView;
        int w2 = child2.getMeasuredWidth();

        child2.layout((width - w2)/2, t, (width + w2)/2, t + child2.getMeasuredHeight());
    }

    private void addImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        imageView = new ImageView(context, attrs);
        imageView.setBackgroundColor(0x00000000);
        LayoutParams params = new LayoutParams(imageSize, imageSize);

        imageView.setScaleType(ImageView.ScaleType.CENTER);

        addView(imageView, params);
    }

    private void addTitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        titleView = new TextView(context, attrs);
        titleView.setGravity(Gravity.CENTER);
        titleView.setBackgroundColor(0x00000000);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(titleView, params);
    }

    @CallSuper
    public void setActive(boolean active, boolean animate) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
}
