package com.mufcryan.util;

import static android.content.Context.WINDOW_SERVICE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.NumberFormat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.ColorRes;

import com.mufcryan.BaseApp;
import com.mufcryan.base.R;

/**
 * Created by yuliu207272 on 2015/3/30.
 */
public class DisplayUtil {

    //dp转化成px
    public static int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    //dp转化成px
    public static float dp2PxF(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale;
    }

    public static int px2Dp(Context context, int pixelValue) {
        float density = context.getResources().getDisplayMetrics().density;
        int dipValue = (int) (pixelValue / density + 0.5f);
        return dipValue;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.heightPixels;
        return screenWidth;
    }

    /**
     * 获取真实屏幕高度 包括虚拟按键的高度
     *
     * @param context
     * @return
     */
    public static int getRealScreenHeight(Context context) {
        int height = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display d = windowManager.getDefaultDisplay();
        Point size = new Point();
        d.getRealSize(size);
        height = size.y;
        return height;
    }

    //获取状态栏的高度
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }


    public static void setStatusBarTransparent(Activity mContext) {

        Window window = mContext.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (RomUtils.isColorOS() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return;
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                /** 华为手机单独适配 **/
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static void setStatusBarTransparent(Window window) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (RomUtils.isColorOS() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return;
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                /** 华为手机单独适配 **/
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private static final int TYPE_STATUS_ICON_SUPPORT_MIUI = 0x01;
    private static final int TYPE_STATUS_ICON_SUPPORT_FLYME = 0x02;
    private static final int TYPE_STATUS_ICON_SUPPORT_ZUK = 0x03;
    private static final int TYPE_STATUS_ICON_SUPPORT_EUI = 0x04;
    private static final int TYPE_STATUS_ICON_SUPPORT_M = 0x05;
    private static final int TYPE_STATUS_ICON_OPPO = 0x06;
    private static int mStatusIconSupportType = -1;

    /**
     * 获取系统亮度
     *
     * @return
     */
    public static int getSystemBrightness() {
        int brightnessValue = -1;
        try {
            brightnessValue = Settings.System.getInt(BaseApp.context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
        }
        return brightnessValue;
    }

    /**
     * 是否开启自动亮度调节
     *
     * @param context
     * @return
     */
    public static boolean isAutoBrightness(Context context) {
        boolean autoBrightness = false;
        try {
            autoBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE)
                    == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Exception e) {
        }
        return autoBrightness;
    }

    /**
     * 开启自动亮度调节
     *
     * @param context
     */
    public static void openAutoBrightness(Context context) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    /**
     * 关闭自动亮度调节
     *
     * @param context
     */
    public static void closeAutoBrightness(Context context) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * 保存亮度
     *
     * @param context
     * @param brightnessValue
     */
    public static void saveBrightness(Context context, int brightnessValue) {
        Uri uri = Settings.System.getUriFor("screen_brightness");
        Settings.System.putInt(context.getContentResolver(), "screen_brightness", brightnessValue);
        context.getContentResolver().notifyChange(uri, null);
    }

    public static Rect getViewLocation(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }

    /**
     * MIUI设置状态栏字体及图表样式
     *
     * @param window
     * @param dark
     * @return
     */
    public static boolean miuiSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);
                }
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }


    /**
     * 魅族设置状态字体及图标样式
     *
     * @param window
     * @param dark
     * @return
     */
    public static boolean flymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * zuk设置状态字体及图标样式
     *
     * @param window
     * @param dark
     * @return
     */
    public static boolean zukSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("PRIVATE_FLAG_DARK_STATUS_ICON");
                Field zukFlags = WindowManager.LayoutParams.class.getDeclaredField("privateFlags");
                darkFlag.setAccessible(true);
                zukFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = zukFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                zukFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * 乐视EUI 适配
     *
     * @param window
     * @param dark
     * @return
     */
    public static boolean euiSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("systemUiIconColor");
                darkFlag.setAccessible(true);
                if (dark) {
                    darkFlag.setInt(lp, Color.BLACK);
                } else {
                    darkFlag.setInt(lp, Color.WHITE);
                }
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
                result = false;
            }
        }
        return result;
    }

    private static final int SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = 0x00000010;

    private static boolean oppoSetStatusTextColorLightMode(Window window, boolean dark) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int vis = window.getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (dark) {
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (dark) {
                vis |= SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
            } else {
                vis &= ~SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
            }
        }
        window.getDecorView().setSystemUiVisibility(vis);
        return true;
    }


    /**
     * Android 6.0 以上设置状态栏字体及图表为深色模式
     *
     * @param window
     */
    private static void setStatusBarLight(Window window, boolean light) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (light) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    public static boolean setStatusBarLightMode(Activity context, Window window, boolean light) {

        setStatusBarLight(window, light);

        if (mStatusIconSupportType > 0) {
            switch (mStatusIconSupportType) {
                case TYPE_STATUS_ICON_SUPPORT_MIUI:
                    return miuiSetStatusBarLightMode(window, light);
                case TYPE_STATUS_ICON_SUPPORT_FLYME:
                    return flymeSetStatusBarLightMode(window, light);
                case TYPE_STATUS_ICON_SUPPORT_ZUK:
                    return zukSetStatusBarLightMode(window, light);
                case TYPE_STATUS_ICON_SUPPORT_EUI:
                    return euiSetStatusBarLightMode(window, light);
                case TYPE_STATUS_ICON_OPPO:
                    if (RomUtils.getRomType().getBaseVersion() < 3) {
                        return false;
                    }
                    return oppoSetStatusTextColorLightMode(window, light);
                case TYPE_STATUS_ICON_SUPPORT_M:
                    return true;
                default:
                    return true;
            }
        }

        if (RomUtils.getRomType() == RomUtils.ROM.MIUI) {
            mStatusIconSupportType = TYPE_STATUS_ICON_SUPPORT_MIUI;
            return miuiSetStatusBarLightMode(window, light);
        } else if (RomUtils.isMeizuFlymeOS()) {
            mStatusIconSupportType = TYPE_STATUS_ICON_SUPPORT_FLYME;
            return flymeSetStatusBarLightMode(window, light);
        } else if (RomUtils.getRomType() == RomUtils.ROM.ZUK) {
            mStatusIconSupportType = TYPE_STATUS_ICON_SUPPORT_ZUK;
            return zukSetStatusBarLightMode(window, light);
        } else if (RomUtils.isEmui()) {
            mStatusIconSupportType = TYPE_STATUS_ICON_SUPPORT_EUI;
            return euiSetStatusBarLightMode(window, light);
        } else if (RomUtils.getRomType() == RomUtils.ROM.ColorOS) {
            mStatusIconSupportType = TYPE_STATUS_ICON_OPPO;
            if (RomUtils.getRomType().getBaseVersion() < 3) {
                return false;
            }
            return oppoSetStatusTextColorLightMode(window, light);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mStatusIconSupportType = TYPE_STATUS_ICON_SUPPORT_M;
            return true;
        }

        return false;
    }


    public static boolean setStatusBarLightMode(Window window, boolean light) {

        setStatusBarLight(window, light);

        if (mStatusIconSupportType > 0) {
            switch (mStatusIconSupportType) {
                case TYPE_STATUS_ICON_SUPPORT_MIUI:
                    return miuiSetStatusBarLightMode(window, light);
                case TYPE_STATUS_ICON_SUPPORT_FLYME:
                    return flymeSetStatusBarLightMode(window, light);
                case TYPE_STATUS_ICON_SUPPORT_ZUK:
                    return zukSetStatusBarLightMode(window, light);
                case TYPE_STATUS_ICON_SUPPORT_EUI:
                    return euiSetStatusBarLightMode(window, light);
                case TYPE_STATUS_ICON_OPPO:
                    if (RomUtils.getRomType().getBaseVersion() < 3) {
                        return false;
                    }
                    return oppoSetStatusTextColorLightMode(window, light);
                case TYPE_STATUS_ICON_SUPPORT_M:
                    return true;
                default:
                    return true;
            }
        }

        if (RomUtils.getRomType() == RomUtils.ROM.MIUI) {
            mStatusIconSupportType = TYPE_STATUS_ICON_SUPPORT_MIUI;
            return miuiSetStatusBarLightMode(window, light);
        } else if (RomUtils.isMeizuFlymeOS()) {
            mStatusIconSupportType = TYPE_STATUS_ICON_SUPPORT_FLYME;
            return flymeSetStatusBarLightMode(window, light);
        } else if (RomUtils.getRomType() == RomUtils.ROM.ZUK) {
            mStatusIconSupportType = TYPE_STATUS_ICON_SUPPORT_ZUK;
            return zukSetStatusBarLightMode(window, light);
        } else if (RomUtils.isEmui()) {
            mStatusIconSupportType = TYPE_STATUS_ICON_SUPPORT_EUI;
            return euiSetStatusBarLightMode(window, light);
        } else if (RomUtils.getRomType() == RomUtils.ROM.ColorOS) {
            mStatusIconSupportType = TYPE_STATUS_ICON_OPPO;
            if (RomUtils.getRomType().getBaseVersion() < 3) {
                return false;
            }
            return oppoSetStatusTextColorLightMode(window, light);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mStatusIconSupportType = TYPE_STATUS_ICON_SUPPORT_M;
            return true;
        }

        return false;
    }



    public static boolean isNavigationBarVisible(Context context) {
        if(RomUtils.isMiui()){
            return Settings.Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0) == 0;
        } else {
            int navigationBarIsMin;
            initDeviceInfo();
            if (Build.BRAND.equalsIgnoreCase("VIVO") || Build.BRAND.equalsIgnoreCase("OPPO")) {
                navigationBarIsMin = Settings.Secure.getInt(context.getContentResolver(),
                        mDeviceInfo, 0);
            } else {
                navigationBarIsMin = Settings.Global.getInt(context.getContentResolver(),
                        mDeviceInfo, 0);
            }
            return navigationBarIsMin != 1;
        }
    }
    private static String mDeviceInfo;
    private static void initDeviceInfo() {
        String brand = Build.BRAND;
        if (brand.equalsIgnoreCase("HUAWEI")) {
            mDeviceInfo = "navigationbar_is_min";
        } else if (brand.equalsIgnoreCase("XIAOMI")) {
            mDeviceInfo = "force_fsg_nav_bar";
        } else if (brand.equalsIgnoreCase("VIVO")) {
            mDeviceInfo = "navigation_gesture_on";
        } else if (brand.equalsIgnoreCase("OPPO")) {
            mDeviceInfo = "navigation_gesture_on";
        } else {
            mDeviceInfo = "navigationbar_is_min";
        }
    }

    public static boolean isFlyme() {
        try {
            // Invoke Build.hasSmartBar()
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }

    public static int getNavigationBarHeight(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - display.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    public static void hideNavigationBar(Activity activity) {
        int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;

        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        //之前如果是IMMERSIVE状态，就不用再隐藏了
        if (!isImmersiveModeEnabled) {

            if (Build.VERSION.SDK_INT >= 14) {
                newUiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }

            if (Build.VERSION.SDK_INT >= 16) {
                newUiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
            if (Build.VERSION.SDK_INT >= 18) {
                newUiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }

            activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        }
    }


    /**
     * 显示虚拟按键
     */
    public static void showNavigationBar(Activity activity) {

        int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        //之前如果是IMMERSIVE状态，就显示NavigationBar
        if (isImmersiveModeEnabled) {

            //先取 非 后再 与， 把对应位置的1 置成0，原本为0的还是0

            if (Build.VERSION.SDK_INT >= 14) {
                newUiOptions &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }

            if (Build.VERSION.SDK_INT >= 16) {
                newUiOptions &= ~View.SYSTEM_UI_FLAG_FULLSCREEN;
            }

            if (Build.VERSION.SDK_INT >= 18) {
                newUiOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);

        }
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64PngString(Bitmap bitmap) {
        StringBuffer string = new StringBuffer();
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();

        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
            bStream.flush();
            bStream.close();
            byte[] bytes = bStream.toByteArray();
            string.append(Base64.encodeToString(bytes, Base64.NO_WRAP));
        } catch (IOException e) {
        } finally {
            try {
                if (bStream != null) {
                    bStream.flush();
                    bStream.close();
                }
            } catch (IOException e) {
            }
        }
        return string.toString();
    }

    /**
     * gif转为base64
     *
     * @param gif
     * @return
     */
    public static String gifToBase64PngString(byte[] gif) {
        StringBuffer string = new StringBuffer();
        string.append(Base64.encodeToString(gif, Base64.NO_WRAP));

        return string.toString();
    }


    /**
     * 百分比计算
     *
     * @param num1
     * @param num2
     * @return
     */
    public static int getPercentage(int num1, int num2) {
        // 创建一个数值格式化对象

        NumberFormat numberFormat = NumberFormat.getInstance();

        // 设置精确到小数点后2位

        numberFormat.setMaximumFractionDigits(2);

        numberFormat.setRoundingMode(RoundingMode.HALF_UP); //四舍五入

        float n = (float) num1 / (float) num2 * 100;
        if (n < 1) {
            n = 0;
        } else if (n > 100) {
            n = 100;
        }
        return (int) n;
    }


    public static int getPercentage(long num1, long num2) {
        // 创建一个数值格式化对象

        NumberFormat numberFormat = NumberFormat.getInstance();

        // 设置精确到小数点后2位

        numberFormat.setMaximumFractionDigits(2);

        numberFormat.setRoundingMode(RoundingMode.HALF_UP); //四舍五入

        String result = numberFormat.format((double) num1 / (double) num2 * 100);

        int p = Float.valueOf(result).intValue();
        return p;
    }


    public static SpannableStringBuilder builerSpannable(Context context, String s1, String s2, int color1, int color2, int f1, int f2) {
        StringBuilder sb = new StringBuilder();
        sb.append(s1).append(s2);
        SpannableStringBuilder takebuilder = new SpannableStringBuilder(sb.toString());

        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        if (color1 != -1) {
            ForegroundColorSpan c1 = new ForegroundColorSpan(context.getResources().getColor(color1));
            takebuilder.setSpan(c1, 0, s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (color2 != -1) {
            ForegroundColorSpan c2 = new ForegroundColorSpan(context.getResources().getColor(color2));
            takebuilder.setSpan(c2, s1.length(), sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        if (f1 != -1) {
            AbsoluteSizeSpan font1 = new AbsoluteSizeSpan(context.getResources().getDimensionPixelSize(f1));
            takebuilder.setSpan(font1, 0, s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (f2 != -1) {
            AbsoluteSizeSpan font2 = new AbsoluteSizeSpan(context.getResources().getDimensionPixelSize(f2));
            takebuilder.setSpan(font2, s1.length(), sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

        return takebuilder;

    }

    public static boolean checkViewVisible(View v) {
        Rect rect = new Rect();
        return v.getGlobalVisibleRect(rect);
    }


    public static int[] unDisplayViewSize(View view) {
        int[] size = new int[2];
        int width = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        size[0] = view.getMeasuredWidth();
        size[1] = view.getMeasuredHeight();
        return size;
    }

    /**
     * 得到一个字符串的前几位,一个汉字或日韩文或大写的字母长度为1,其他长度为0.5
     *
     * @param s
     * @param maxChar
     * @return
     */
    public static String getSubString(String s, int maxChar) {
        if (TextUtils.isEmpty(s)) {
            LogUtil.d("buxq", "getSubString: s = null");
            return "";
        }
        if (s.length() <= maxChar) {
            return s;
        }

        double valueLength = 0;
        StringBuilder sb = new StringBuilder();

        //unicode编码范围
        String chinese = "[\u4e00-\u9fa5]";
        String upcaseLetter = "[\u0041-\u005a]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < s.length(); i++) {
            // 获取一个字符
            String temp = s.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为1
                valueLength += 1;
            } else if (temp.matches(upcaseLetter)) {
                //大写的字母长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 0.5;
            }
            if (valueLength <= maxChar) {
                sb.append(temp);
            } else {
                sb.append("...");
                return sb.toString().trim();
            }
        }
        return sb.toString().trim();
    }


    /**
     * 得到一个字符串的长度,一个汉字或日韩文或大写的字母长度为1,其他长度为0.5
     *
     * @return
     */
    public static int getStringLength(String msg) {
        if (TextUtils.isEmpty(msg)) {
            LogUtil.d("buxq", "getSubString: s = null");
            return 0;
        }
        double valueLength = 0;
        //unicode编码范围
        String chinese = "[\u4e00-\u9fa5]";
        String upcaseLetter = "[\u0041-\u005a]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < msg.length(); i++) {
            // 获取一个字符
            String temp = msg.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为1
                valueLength += 1;
            } else if (temp.matches(upcaseLetter)) {
                //大写的字母长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 0.5;
            }
        }
        return (int) (valueLength + 0.5);
    }

    public static void setStatusBarFitKeyBoard(Activity mContext, @ColorRes int color) {
        Window window = mContext.getWindow();
        boolean light = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!MIUISetImmersiveStatusBar(mContext, color)) {
                //0x00002000：View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR的值，这个Flag用于浅色状态栏的时候使状态栏文字变为深色
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | 0x00002000);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(mContext.getResources().getColor(color));
            }
        } else {
            boolean result = setStatusbarBeforeM(mContext, true);
            if (!result) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | 0x00002000);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(mContext.getResources().getColor(R.color.status_bar));
            }
        }
    }

    private static boolean setStatusbarBeforeM(Activity mContext, boolean light) {
        Window window = mContext.getWindow();

        if (RomUtils.getRomType() == RomUtils.ROM.MIUI) {
            mStatusIconSupportType = TYPE_STATUS_ICON_SUPPORT_MIUI;
            return miuiSetStatusBarLightMode(window, light);
        } else if (RomUtils.isMeizuFlymeOS()) {
            mStatusIconSupportType = TYPE_STATUS_ICON_SUPPORT_FLYME;
            return flymeSetStatusBarLightMode(window, light);
        } else if (RomUtils.getRomType() == RomUtils.ROM.ZUK) {
            mStatusIconSupportType = TYPE_STATUS_ICON_SUPPORT_ZUK;
            return zukSetStatusBarLightMode(window, light);
        } else if (RomUtils.isEmui()) {
            mStatusIconSupportType = TYPE_STATUS_ICON_SUPPORT_EUI;
            return euiSetStatusBarLightMode(window, light);
        } else if (RomUtils.getRomType() == RomUtils.ROM.ColorOS) {
            mStatusIconSupportType = TYPE_STATUS_ICON_OPPO;
            if (RomUtils.getRomType().getBaseVersion() < 3) {
                return false;
            }
            return oppoSetStatusTextColorLightMode(window, light);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mStatusIconSupportType = TYPE_STATUS_ICON_SUPPORT_M;
            return true;
        }
        return false;
    }

    public static int compareVersion(String version1, String version2) {
        String result = version1.replaceAll("[^0-9\\.]", "");
        String[] versionArray1 = result.split("\\.");//注意此处为正则匹配，不能用.；
        String[] versionArray2 = version2.split("\\.");
        int idx = 0;
        int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
        int diff = 0;
        while (idx < minLength
                && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//先比较长度
                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//再比较字符
            ++idx;
        }
        //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        return diff;
    }


    public static boolean MIUISetImmersiveStatusBar(Activity context, @ColorRes int color) {
        return MIUISetImmersiveStatusBar(context, context.getWindow(), color);
    }

    public static boolean MIUISetImmersiveStatusBar(Context context, Window window, @ColorRes int color) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class<?> layoutParams;
                try {
                    layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                }catch (ClassNotFoundException e){
                    return false;
                }
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                String version = Build.VERSION.INCREMENTAL;
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(context.getResources().getColor(color));
                if (!TextUtils.isEmpty(version) && compareVersion(version, "7.7.13") >= 0) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


}
