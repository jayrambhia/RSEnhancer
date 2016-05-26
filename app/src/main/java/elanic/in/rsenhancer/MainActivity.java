package elanic.in.rsenhancer;

import android.animation.Animator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import elanic.in.rsenhancer.processing.ImageProcessingApi;
import elanic.in.rsenhancer.processing.ProcessingUtils;
import elanic.in.rsenhancer.processing.RSImageProcessingApi;
import elanic.in.rsenhancer.processing.RSImageProcessor;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_READ_PERMISSION = 22;
    private static final int REQUEST_GALLERY = 21;
    private static final String TAG = "MainActivity";

    private Bitmap mBitmap;

    @Bind(R.id.image_button) Button loadImageButton;
    @Bind(R.id.control_group) ViewGroup controlGroup;

    @Bind(R.id.brightness_button) TouchRevealImageView brightnessButton;
    @Bind(R.id.contrast_button) TouchRevealImageView contrastButton;
    @Bind(R.id.auto_en_button) ToggleRotateImageView autoEnhanceButton;

    @Bind(R.id.imageview) ImageView mImageView;
    @Bind(R.id.seekbar_seeker) ImageSeekbar bSeekbar;

    private int brightnessProgress = 20;
    private int contrastProgress = 20;
    private float brightness;
    private float contrast;

    private ImageProcessingApi imageProcessingApi;
    private PublishSubject<Boolean> seekbarSubject;

    private static final int MODE_BRIGHTNESS = 1;
    private static final int MODE_CONTRAST = 2;
    private static final int MODE_NONE = -1;
    private int mode = MODE_NONE;

    private boolean isAutoEnhanceApplied = false;
    private boolean isProcessing = false;

    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        imageProcessingApi = new RSImageProcessingApi(new RSImageProcessor(RenderScript.create(this)));

        seekbarSubject = PublishSubject.create();

        bSeekbar.setCallback(new ImageSeekbar.Callback() {
            @Override
            public void onProgressChanged(int progress, boolean fromUser) {
                Log.i(TAG, "progress changed: " + progress);

                int min = bSeekbar.getStart();
                int max = bSeekbar.getEnd();

                Log.i(TAG, "min: " + min + ", max: " + max);
                float factor = ProcessingUtils.getNormalizedProgress(progress, max, min, 0.25f, -0.25f);

                Log.i(TAG, "factor: " + factor);

                if (mode == MODE_BRIGHTNESS) {
                    brightness = factor;
                    brightnessProgress = progress;
                } else if (mode == MODE_CONTRAST) {
                    contrast = factor;
                    contrastProgress = progress;
                } else {
                    return;
                }

                seekbarSubject.onNext(fromUser);

            }

            @Override
            public void onResetClicked() {
                if (mode == MODE_BRIGHTNESS) {
                    brightness = 0;
                } else if (mode == MODE_CONTRAST) {
                    contrast = 0;
                } else {
                    return;
                }

                seekbarSubject.onNext(true);
            }
        });

        seekbarSubject.debounce(60, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean process) {
                        if (mBitmap != null && process) {
                            processImage(brightness, contrast);
                        }
                    }
                });

        computeSeekbarVisibility();
        controlGroup.setVisibility(View.GONE);
        loadImageButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (seekbarSubject != null) {
            seekbarSubject.onCompleted();
        }

        if (imageProcessingApi != null) {
            imageProcessingApi.release();
        }
    }

    @OnClick(R.id.image_button)
    public void onImageButtonClicked() {
        startGalleryIntent();
    }

    @OnClick(R.id.auto_en_button)
    public void onAutoEnhance() {

        if (isProcessing) {
            return;
        }

        mode = MODE_NONE;

        computeSeekbarVisibility();

        if (!isAutoEnhanceApplied) {
            autoEnhance();
            autoEnhanceButton.setActive(false, true);
            return;

        }

        autoEnhanceButton.setActive(true, true);

        if (filePath != null) {
            loadNewImage(filePath);
        }

    }

    @OnClick(R.id.brightness_button)
    public void onBrightnessClicked() {
        if (isProcessing) {
            return;
        }

        mode = MODE_BRIGHTNESS;
        computeSeekbarVisibility();
        brightnessButton.setActive(true, true);
    }

    @OnClick(R.id.contrast_button)
    public void onContrastClicked() {
        if (isProcessing) {
            return;
        }

        mode = MODE_CONTRAST;
        computeSeekbarVisibility();
        contrastButton.setActive(true, true);
    }

    private void computeSeekbarVisibility() {

        if (mode == MODE_BRIGHTNESS) {
            showSeekbar(true);
            bSeekbar.setProgress(brightnessProgress);
            bSeekbar.setImage(R.drawable.ic_brightness_low_grey_600_24dp);
            contrastButton.setActive(false, false);
        } else if (mode == MODE_CONTRAST) {
            showSeekbar(true);
            bSeekbar.setProgress(contrastProgress);
            bSeekbar.setImage(R.drawable.ic_tonality_grey_600_24dp);
            brightnessButton.setActive(false, false);
        } else {
            hideSeekbar(true);
            brightnessButton.setActive(false, false);
            contrastButton.setActive(false, false);
        }
    }

    private void resetSeekbar() {
        brightnessProgress = 20;
        contrastProgress = 20;
    }

    private void loadNewImage(String filePath) {
        isProcessing = true;
        Log.i(TAG, "load image: " + filePath);
        this.filePath = filePath;
        mBitmap = BitmapFactory.decodeFile(filePath);
        Log.i(TAG, "bitmap: " + mBitmap.getWidth() + " " + mBitmap.getHeight());

        int maxP = Math.max(mBitmap.getWidth(), mBitmap.getHeight());
        float scale1280 = (float)maxP / 1280;

        mBitmap = Bitmap.createScaledBitmap(mBitmap, (int)(mBitmap.getWidth()/scale1280),
                (int)(mBitmap.getHeight()/scale1280), true);
        mImageView.setImageBitmap(mBitmap);

        imageProcessingApi.reset();
        imageProcessingApi.initialize(mBitmap.getWidth(), mBitmap.getHeight());

        if (controlGroup.getVisibility() != View.VISIBLE) {
            CustomAnimationUtils.slide(controlGroup, 300, 0, 300);
            controlGroup.setVisibility(View.VISIBLE);
        }

        loadImageButton.setVisibility(View.GONE);

        isAutoEnhanceApplied = false;

        resetSeekbar();

        isProcessing = false;
    }

    private void startGalleryIntent() {

        if (!hasGalleryPermission()) {
            askForGalleryPermission();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private boolean hasGalleryPermission() {
        return ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void askForGalleryPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_READ_PERMISSION);
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent resultIntent) {
        super.onActivityResult(requestCode, responseCode, resultIntent);

        if (responseCode == RESULT_OK) {
            String absPath = BitmapUtils.getFilePathFromUri(this, resultIntent.getData());
            loadNewImage(absPath);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_READ_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGalleryIntent();
                return;
            }
        }

        Toast.makeText(this, "Gallery permission not granted", Toast.LENGTH_SHORT).show();
    }

    private void processImage(float brightness, float contrast) {
        imageProcessingApi.setBrightness(brightness);
        imageProcessingApi.setContrast(contrast);
        Observable<Bitmap> observable = imageProcessingApi.process(mBitmap);
        observable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        Log.i(TAG, "image processed");
                        mImageView.setImageBitmap(bitmap);
                    }
                });
    }

    private void autoEnhance() {
        if (mBitmap == null) {
            return;
        }

        isProcessing = true;

        Observable<Bitmap> observable = imageProcessingApi.equalizeHist(mBitmap);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        long t1 = System.currentTimeMillis();

                        mBitmap = bitmap;
                        mImageView.setImageBitmap(bitmap);
                        isAutoEnhanceApplied = true;

                        resetSeekbar();

                        long t2 = System.currentTimeMillis();

                        isProcessing = false;

                        Log.e(TAG, "time taken to set bitmap: " + (t2 - t1));
                    }
                });
    }

    private void showSeekbar(boolean animate) {
        if (bSeekbar.getVisibility() != View.VISIBLE) {
            bSeekbar.setVisibility(View.VISIBLE);
            if (animate) {
                CustomAnimationUtils.slide(bSeekbar, 400, 0, 300);
            }
        }
    }

    private void hideSeekbar(boolean animate) {
        if (bSeekbar.getVisibility() != View.VISIBLE) {
            return;
        }

        if (!animate) {
            bSeekbar.setVisibility(View.GONE);
            return;
        }

        Animator animator = CustomAnimationUtils.slide(bSeekbar, 0, 400, 300);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bSeekbar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
