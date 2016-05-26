package elanic.in.rsenhancer.processing;

import android.graphics.Bitmap;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.v8.renderscript.Type;
import android.util.Log;

import elanic.in.rsenhancer.ScriptC_AddWeighted;
import elanic.in.rsenhancer.ScriptC_BezierCurve;
import elanic.in.rsenhancer.ScriptC_BrightnessContrastFilter;
import elanic.in.rsenhancer.ScriptC_histEq;

/**
 * Created by Jay Rambhia on 5/20/16.
 */
public class RSImageProcessor implements ImageProcessor {

    private static final String TAG = "RSImageProcessor";

    private int width;
    private int height;

    private RenderScript rs;
    private Allocation inAllocation;
    private Allocation outAllocation;
    private Allocation blurAllocation;

    private Bitmap bitmap;

    private float brightness = 0f;
    private float contrast = 0f;

    private ScriptC_BrightnessContrastFilter brightnessContrastFilter;
    private ScriptC_histEq equalizerScript;
    private ScriptIntrinsicBlur blurScript;
    private ScriptC_AddWeighted addWeightedScript;
    private ScriptC_BezierCurve bezierCurve;

    public RSImageProcessor(RenderScript rs) {
        this.rs = rs;
    }

    @Override
    public void reset() {
        release();
    }

    @Override
    public void initialize(int width, int height) {

        brightnessContrastFilter = new ScriptC_BrightnessContrastFilter(rs);
        equalizerScript = new ScriptC_histEq(rs);
        equalizerScript.set_size(width * height);

        blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        blurScript.setRadius(25);

        addWeightedScript = new ScriptC_AddWeighted(rs);
        bezierCurve = new ScriptC_BezierCurve(rs);


        if (outAllocation != null) {
            outAllocation.destroy();
            outAllocation = null;
        }

        this.height = height;
        this.width = width;

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        outAllocation = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

        Type t = outAllocation.getType();
        inAllocation = Allocation.createTyped(rs, t);
        blurAllocation = Allocation.createTyped(rs, t);
    }

    @Override
    public Bitmap getBitmap() {

        if (outAllocation == null) {
            return null;
        }

        outAllocation.copyTo(bitmap);
        return bitmap;
    }

    @Override
    public void release() {

        if (blurScript != null) {
            blurScript.destroy();
            blurScript = null;
        }

        if (bezierCurve != null) {
            bezierCurve.destroy();
            bezierCurve = null;
        }

        if (addWeightedScript != null) {
            addWeightedScript.destroy();
            addWeightedScript = null;
        }

        if (brightnessContrastFilter != null) {
            brightnessContrastFilter.destroy();
            brightnessContrastFilter = null;
        }

        if (equalizerScript != null) {
            equalizerScript.destroy();
            equalizerScript = null;
        }

        if (inAllocation != null) {
            inAllocation.destroy();
            inAllocation = null;
        }

        if (outAllocation != null) {
            outAllocation.destroy();
            outAllocation = null;
        }

        if (blurAllocation != null) {
            blurAllocation.destroy();
            blurAllocation = null;
        }

        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    @Override
    public void process(@NonNull Bitmap bitmap) {
        inAllocation.copyFrom(bitmap);
        brightnessContrastFilter.set_gIn(inAllocation);
        brightnessContrastFilter.set_gOut(outAllocation);
        brightnessContrastFilter.set_gBrightnessFactor(brightness);
        brightnessContrastFilter.set_gContrastFactor(contrast);
        brightnessContrastFilter.set_gScript(brightnessContrastFilter);
        brightnessContrastFilter.invoke_filter();
    }

    @Override
    public void setBrightness(float factor) {
        this.brightness = factor;
    }

    @Override
    public void setContrast(float factor) {
        this.contrast = factor;
    }

    @Override
    public Bitmap equalizeHistogram(@NonNull Bitmap bitmap) {

        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.e(TAG, "running on main thread");
        } else {
            Log.d(TAG, "running on background thread");
        }

        //Create new bitmap
        Bitmap res = bitmap.copy(bitmap.getConfig(), true);

        inAllocation.copyFrom(res);

//        equalizerScript.forEach_root(inAllocation, outAllocation);
//        equalizerScript.invoke_createRemapArray();
//        equalizerScript.forEach_remaptoRGB(outAllocation, outAllocation);

        blurScript.setInput(inAllocation);
        blurScript.forEach(outAllocation);

        addWeightedScript.forEach_root(inAllocation, outAllocation);

        long t1 = System.currentTimeMillis();
        bezierCurve.forEach_root(inAllocation, outAllocation);
        long t2 = System.currentTimeMillis();
        outAllocation.copyTo(res);
        Log.i(TAG, "bezier time: " + (t2-t1));
        long t3 = System.currentTimeMillis();
        Log.i(TAG, "time taken to copy data: " + (t3 - t2));

        /*equalizerScript.forEach_root(inAllocation, outAllocation);
        equalizerScript.invoke_createRemapArray();
        equalizerScript.forEach_remaptoRGB(outAllocation, inAllocation);

        inAllocation.copyTo(res);*/
        return res;
    }
}
