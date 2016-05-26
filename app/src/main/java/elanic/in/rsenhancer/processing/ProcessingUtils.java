package elanic.in.rsenhancer.processing;

/**
 * Created by Jay Rambhia on 5/20/16.
 */
public class ProcessingUtils {

    public static float getNormalizedProgress(int progress, int max, int min, float nMax, float nMin) {
        return (progress - min) * (nMax - nMin) / (float) (max - min) + nMin;
    }
}
