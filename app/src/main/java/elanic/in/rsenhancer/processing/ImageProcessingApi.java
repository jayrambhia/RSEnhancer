package elanic.in.rsenhancer.processing;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import rx.Observable;

/**
 * Created by Jay Rambhia on 5/20/16.
 */
public interface ImageProcessingApi {

    void initialize(int width, int height);

    void setBrightness(float factor);
    void setContrast(float factor);

    Observable<Bitmap> process(@NonNull Bitmap bitmap);
    Observable<Bitmap> equalizeHist(@NonNull Bitmap bitmap);

    void reset();
    void release();
}
