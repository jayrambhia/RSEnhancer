package elanic.in.rsenhancer;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

/**
 * Created by Jay Rambhia on 5/23/16.
 */
public class TouchRevealImageView extends BottomTextImageView {

    private static final String TAG = "TouchRevealImageView";
    private View backgroundView;


    public TouchRevealImageView(Context context) {
        super(context);
//        init(context, null);
    }

    public TouchRevealImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        init(context, attrs);
    }

    public TouchRevealImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TouchRevealImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
//        init(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(TAG, "l: " + l + ", t: " + t + ", r: " + r + ", b:" + b);

        int width = r - l;
        int height = b - t;

        View child0 = getChildAt(0);
        child0.layout(0, 0, width, height);

        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void init(@NonNull Context context, @Nullable AttributeSet attrs) {

        int backgroundColor = 0x00000000;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TouchRevealImageView);
            if (a != null) {
                backgroundColor = a.getColor(R.styleable.TouchRevealImageView_reveal_color, backgroundColor);
                a.recycle();
            }
        }

        addBackgroundView(context, backgroundColor);

        super.init(context, attrs);
    }

    private void addBackgroundView(@NonNull Context context, int backgroundColor) {
        backgroundView = new View(context);
        backgroundView.setBackgroundColor(backgroundColor);
        backgroundView.setVisibility(View.INVISIBLE);
        addView(backgroundView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void setActive(boolean active, boolean animate) {
        super.setActive(active, animate);

        if (Build.VERSION.SDK_INT < 21) {
            animate = false;
        }

        if (!animate) {
            backgroundView.setVisibility(active ? View.VISIBLE : View.INVISIBLE);
            return;
        }

        int cx = getMeasuredWidth()/2;
        int cy = getMeasuredHeight()/2;
        int startRadius = 0;
        int finalRadius = (int) (getMeasuredWidth()/1.41);

        if (!active) {
            startRadius = finalRadius;
            finalRadius = 0;
        } else {
            backgroundView.setVisibility(View.VISIBLE);
        }

        reveal(backgroundView, cx, cy, startRadius, finalRadius, active ? View.VISIBLE : View.INVISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void reveal(@NonNull final View view, int cx, int cy, int startRadius, int endRadius,
                        final int finalState) {

        Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, startRadius, endRadius);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(finalState);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.start();
    }
}
