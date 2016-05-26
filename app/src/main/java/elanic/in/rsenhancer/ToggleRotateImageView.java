package elanic.in.rsenhancer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Jay Rambhia on 5/23/16.
 */
public class ToggleRotateImageView extends BottomTextImageView {

    private static final String TAG = "ToggleRotateImageView";
    @DrawableRes private int activeResId;
    @DrawableRes private int inactiveResId;
    @StringRes private int activeTextResId;
    @StringRes private int inactiveTextResId;

    public ToggleRotateImageView(Context context) {
        super(context);
    }

    public ToggleRotateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleRotateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ToggleRotateImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        super.init(context, attrs);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ToggleRotateImageView);
            if (a != null) {

                activeResId = a.getResourceId(R.styleable.ToggleRotateImageView_src_active, 0);
                inactiveResId = a.getResourceId(R.styleable.ToggleRotateImageView_src_inactive, 0);
                activeTextResId = a.getResourceId(R.styleable.ToggleRotateImageView_title_active, 0);
                inactiveTextResId = a.getResourceId(R.styleable.ToggleRotateImageView_title_inactive, 0);

                a.recycle();
            }
        }

        setActive(true, false);
    }

    @Override
    public void setActive(boolean active, boolean animate) {
        super.setActive(active, animate);

        if (!animate) {
            imageView.setImageResource(active ? activeResId : inactiveResId);
            titleView.setText(active ? activeTextResId : inactiveTextResId);
            return;
        }

        animate(imageView, active? activeResId : inactiveResId,
                active? activeTextResId : inactiveTextResId);
    }

    private void animate(@NonNull ImageView view, @DrawableRes final int finalStateRes,
                         @StringRes final int finalStateTextRes) {

        /*view.setAnimation(null);

        Animation animation = new RotateAnimation(0, 360);
        animation.setRepeatCount(2);
        animation.setDuration(200);

        view.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            int repeatCount = 0;

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                repeatCount++;
                Log.i(TAG, "animation repeat: " + repeatCount);
                if (repeatCount >= 1) {
                    imageView.setImageResource(finalStateRes);
                }
            }
        });*/


        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0, 360);
        animator.setRepeatCount(2);
        animator.setDuration(100);


        animator.addListener(new Animator.AnimatorListener() {
            int repeatCount = 0;

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                repeatCount++;
                Log.i(TAG, "animation repeat: " + repeatCount);
                if (repeatCount > 1) {
                    imageView.setImageResource(finalStateRes);
                    titleView.setText(finalStateTextRes);
                }
            }
        });

        animator.start();
    }
}
