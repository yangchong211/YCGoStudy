package com.yc.checktool.utils;

import java.lang.reflect.Field;

public final class XposedUtils {

    private static final String XPOSED_HELPERS = "de.robv.android.xposed.XposedHelpers";
    private static final String XPOSED_BRIDGE = "de.robv.android.xposed.XposedBridge";


    /**
     * 通过检查是否已经加载了XP类来检测
     * @return
     */
    @Deprecated
    public boolean isXposedExists() {
        try {
            Object xpHelperObj = ClassLoader.getSystemClassLoader().loadClass(XPOSED_HELPERS).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        try {
            Object xpBridgeObj = ClassLoader.getSystemClassLoader().loadClass(XPOSED_BRIDGE).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 通过主动抛出异常，检查堆栈信息来判断是否存在XP框架
     * @return
     */
    public static boolean isXposedExistByThrow() {
        try {
            throw new Exception("gg");
        } catch (Throwable e) {
            return isXposedExists(e);
        }
    }

    /**
     * 判断是否有Xposed环境
     * @param thr                                   异常
     * @return
     */
    public static boolean isXposedExists(Throwable thr) {
        StackTraceElement[] stackTraces = thr.getStackTrace();
        for (StackTraceElement stackTrace : stackTraces) {
            final String clazzName = stackTrace.getClassName();
            if (clazzName != null && clazzName.contains(XPOSED_BRIDGE)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 尝试关闭XP框架
     * 先通过isXposedExistByThrow判断有没有XP框架
     * 有的话先hookXP框架的全局变量disableHooks
     * <p>
     * 漏洞在，如果XP框架先hook了isXposedExistByThrow的返回值，那么后续就没法走了
     * 现在直接先hookXP框架的全局变量disableHooks
     *
     * @return 是否关闭成功的结果
     */
    public static boolean tryShutdownXposed() {
        Field xpdisabledHooks = null;
        try {
            xpdisabledHooks = ClassLoader.getSystemClassLoader().loadClass(XPOSED_BRIDGE).getDeclaredField("disableHooks");
            xpdisabledHooks.setAccessible(true);
            xpdisabledHooks.set(null, Boolean.TRUE);
            return true;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }


}
