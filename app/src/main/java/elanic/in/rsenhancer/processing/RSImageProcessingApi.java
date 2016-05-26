package elanic.in.rsenhancer.processing;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import rx.Observable;
import rx.functions.Func0;

/**
 * Created by Jay Rambhia on 5/20/16.
 */
public class RSImageProcessingApi implements ImageProcessingApi {

    private static final String TAG = "RSImageProcessingApi";
    private ImageProcessor imageProcessor;

    public RSImageProcessingApi(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
    }

    @Override
    public void initialize(int width, int height) {
        imageProcessor.initialize(width, height);
    }

    @Override
    public void setBrightness(float factor) {
        imageProcessor.setBrightness(factor);
    }

    @Override
    public void setContrast(float factor) {
        imageProcessor.setContrast(factor);
    }

    @Override
    public Observable<Bitmap> process(@NonNull final Bitmap bitmap) {
        return Observable.defer(new Func0<Observable<Bitmap>>() {
            @Override
            public Observable<Bitmap> call() {
                imageProcessor.process(bitmap);
                return Observable.just(imageProcessor.getBitmap());
            }
        });
    }

    @Override
    public Observable<Bitmap> equalizeHist(@NonNull final Bitmap bitmap) {
        return Observable.defer(new Func0<Observable<Bitmap>>() {
            @Override
            public Observable<Bitmap> call() {
                long t1 = System.currentTimeMillis();
                Bitmap result = imageProcessor.equalizeHistogram(bitmap);
                long t2 = System.currentTimeMillis();
                Log.i(TAG, "time taken to process image: " + (t2 - t1));
                return Observable.just(result);
            }
        });
    }

    @Override
    public void reset() {
        imageProcessor.reset();
    }

    @Override
    public void release() {
        imageProcessor.release();
    }
}
