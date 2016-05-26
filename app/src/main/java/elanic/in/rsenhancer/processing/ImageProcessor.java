package elanic.in.rsenhancer.processing;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * Created by Jay Rambhia on 5/20/16.
 */
public interface ImageProcessor {

    void reset();
    void initialize(int width, int height);
    Bitmap getBitmap();
    void release();

    void process(@NonNull Bitmap bitmap);

    void setBrightness(float factor);
    void setContrast(float factor);

    Bitmap equalizeHistogram(@NonNull Bitmap bitmap);
}
