package elanic.in.rsenhancer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Jay Rambhia on 5/23/16.
 */
public class CustomAnimationUtils {

    public static Animator slide(@NonNull View view, int fromY, int toY, int time) {
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "translationY", fromY, toY);
        animatorY.setDuration(time);
        animatorY.start();
        return animatorY;
    }
}
