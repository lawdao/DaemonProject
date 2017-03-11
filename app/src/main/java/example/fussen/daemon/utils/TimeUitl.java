package example.fussen.daemon.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Fussen on 2017/2/22.
 */

public class TimeUitl {

    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16时09分00秒"）
     *
     * @param timeSeconds
     * @return
     */
    public static String parse(Long timeSeconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒",
                Locale.getDefault());
        return sdf.format(timeSeconds);

    }
}
