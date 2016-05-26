package elanic.in.rsenhancer;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;


import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jay Rambhia on 3/17/16.
 */
public class ImageSeekbar extends FrameLayout {

    @Bind(R.id.prop_imageview) ImageView propView;
    @Bind(R.id.seekbar) AppCompatSeekBar seekBar;
    @Bind(R.id.reset_button) ImageView resetButton;

    private static final int DEFAULT_START = 0;
    private static final int DEFAULT_END = 10;

    private int start = DEFAULT_START;
    private int end = DEFAULT_END;
    private int seekDefault = DEFAULT_START;

    private Callback callback;

    public ImageSeekbar(Context context) {
        super(context);
        init(context, null);
    }

    public ImageSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ImageSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageSeekbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.image_seekbar_layout, this, true);
        ButterKnife.bind(this);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageSeekbar);

            start = a.getInt(R.styleable.ImageSeekbar_seek_start, DEFAULT_START);
            end = a.getInt(R.styleable.ImageSeekbar_seek_end, DEFAULT_END);
            seekDefault = a.getInt(R.styleable.ImageSeekbar_seek_default, DEFAULT_START);

            propView.setImageResource(a.getResourceId(R.styleable.ImageSeekbar_seek_prop_drawable,
                    R.drawable.ic_brightness_low_grey_600_24dp));

            a.recycle();
        }

        seekBar.setMax(end - start);
        seekBar.setProgress(seekDefault - start);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int offsetProgress = start + progress;
                if (callback != null) {
                    callback.onProgressChanged(offsetProgress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        resetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
                if (callback != null) {
                    callback.onResetClicked();
                }
            }
        });
    }

    public void setProgress(int progress) {
        seekBar.setProgress(progress);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void reset() {
        seekBar.setProgress(seekDefault);
    }

    public interface Callback {
        void onProgressChanged(int progress, boolean fromUser);
        void onResetClicked();
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public void setImage(@DrawableRes int drawableId) {
        propView.setImageResource(drawableId);
    }
}
