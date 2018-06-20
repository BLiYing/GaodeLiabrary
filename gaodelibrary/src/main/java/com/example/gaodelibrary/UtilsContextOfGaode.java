package com.example.gaodelibrary;

import android.content.Context;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 16/12/08
 *     desc  : Utils初始化相关
 *     全局
 * </pre>
 */
public final class UtilsContextOfGaode {

    private static Context context;

    private UtilsContextOfGaode() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        //传递整个app生命周期的上下文，避免内存泄露
        UtilsContextOfGaode.context = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) return context;
        throw new NullPointerException("u should init first");
    }

}