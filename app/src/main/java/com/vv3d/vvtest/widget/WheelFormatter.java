package com.vv3d.vvtest.widget;

import androidx.annotation.NonNull;

public interface WheelFormatter {

    /**
     * 格式化滚轮条目显示文本
     *
     * @param item 滚轮条目的内容
     * @return 格式化后最终显示的文本
     */
    String formatItem(@NonNull Object item);

}
