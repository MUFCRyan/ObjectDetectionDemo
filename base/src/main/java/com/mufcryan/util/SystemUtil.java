package com.mufcryan.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.mufcryan.BaseApp;
import com.mufcryan.callback.OnCallback;

/**
 * 系统相关工具
 */

public class SystemUtil {

    private static int IS_ROOT = -1;

    private static long lastClickTime;

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
            LogUtil.printeException(e);
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
            LogUtil.printeException(e);
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
        Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");
        android.provider.Settings.System.putInt(context.getContentResolver(), "screen_brightness", brightnessValue);
        context.getContentResolver().notifyChange(uri, null);
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long div = time - lastClickTime;
        if (div > 0 && div < 800) {
            LogUtil.i("gj", "div:" + div);
            LogUtil.i("gj", "time:" + time);
            LogUtil.i("gj", "lastClickTime:" + lastClickTime);
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 展示软键盘
     */
    public static void showKeyBoard(Context context, View view) {
        if (context == null) {
            return;
        }
        InputMethodManager mInputMethodManager = (InputMethodManager) BaseApp.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        if (mInputMethodManager != null) {
            view.requestFocus();
            mInputMethodManager.showSoftInput(view, 0);
        }
    }

    /**
     * 判断软键盘是否弹出
     */
    public static boolean isShowKeyboard(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        if (imm.hideSoftInputFromWindow(v.getWindowToken(), 0)) {
            imm.showSoftInput(v, 0);
            return true;
            //软键盘已弹出
        } else {
            return false;
            //软键盘未弹出
        }
    }

    /**
     * 隐藏软键盘
     */
    public static void hideKeyBoard(Context context) {
        if (context != null && context instanceof Activity) {
            //软键盘管理
            InputMethodManager mInputMethodManager
                    = (InputMethodManager) BaseApp.context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (mInputMethodManager != null) {
                mInputMethodManager.hideSoftInputFromWindow(((Activity) context).getWindow().getDecorView().getWindowToken(), 0);
            }
        }
    }


    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "没有检测到版本号";
        }
    }

    public static String getAppPackageName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String packageName = info.packageName;
            return packageName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 判断手机是否Root
     *
     * @return
     */
    public static boolean isRoot() {
        if (IS_ROOT == -1) {
            String binPath = "/system/bin/su";
            String xBinPath = "/system/xbin/su";
            if (new File(binPath).exists() && isExecutable(binPath)) {
                IS_ROOT = 1;
                return true;
            }
            if (new File(xBinPath).exists() && isExecutable(xBinPath)) {
                IS_ROOT = 1;
                return true;
            }
            IS_ROOT = 0;
            return false;
        } else {
            return IS_ROOT > 0;
        }
    }

    private static boolean isExecutable(String filePath) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("ls -l " + filePath);
            // 获取返回内容
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String str = in.readLine();
            if (str != null && str.length() >= 4) {
                char flag = str.charAt(3);
                if (flag == 's' || flag == 'x')
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return false;
    }

    public static int getHeapSize() {
        ActivityManager manager = (ActivityManager) BaseApp.context.getSystemService(Context.ACTIVITY_SERVICE);
        int heapSize = manager.getMemoryClass();
        return heapSize;
    }

    /**
     * 获取进程名
     *
     * @param context
     * @param pid
     * @return
     */
    public static String getProcessName(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    public static boolean isActivityExist(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        ComponentName cmpName = intent.resolveActivity(context.getPackageManager());
        boolean flag = false;
        if (cmpName != null) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(50);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * 获取应用列表名
     *
     * @param context
     */
    public static ArrayList<String> getAllAppName(Context context) {
        ArrayList<String> appList = new ArrayList<>();
//        try {
//            PackageManager pm = context.getPackageManager();
//            List<PackageInfo> packages = pm.getInstalledPackages(0);
//            for (PackageInfo packageInfo : packages) {
//                // 判断系统/非系统应用
//                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
//                    appList.add(packageInfo.applicationInfo.loadLabel(pm).toString());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return appList;
    }

    /**
     * 判断应用通知栏是否开启权限
     *
     * @param context
     * @return
     */
    public static boolean isNotificationEnabled(Context context) {
        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                ApplicationInfo appInfo = context.getApplicationInfo();
                String pkg = context.getApplicationContext().getPackageName();
                int uid = appInfo.uid;
                Class appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                int value = (int) opPostNotificationValue.get(Integer.class);
                return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 是否正在充电
     *
     * @return
     */
    public static boolean isCharging() {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = BaseApp.context.registerReceiver(null, ifilter);
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            return isCharging;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 是否正在充电
     *
     * @return
     */
    public static int getPowerConsumption() {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryIntent = BaseApp.context.registerReceiver(null, ifilter);
            int current = batteryIntent.getExtras().getInt("level");// 获得当前电量
            int total = batteryIntent.getExtras().getInt("scale");// 获得总电量
            int percent = current * 100 / total;
            return percent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean isOpenProxy() {
        String proxyHost;
        int proxyPort = 0;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            proxyHost = System.getProperty("http.proxyHost");
            String port = System.getProperty("http.proxyPort");

            if (port != null && port.length() > 0) {
                proxyPort = Integer.parseInt(port);
            }
        } else {
            proxyHost = android.net.Proxy.getHost(BaseApp.context);
            proxyPort = android.net.Proxy.getPort(BaseApp.context);
        }

        if (TextUtils.isEmpty(proxyHost) && proxyPort == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isOpenVPN() {
        try {
            Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
            if (niList != null) {
                for (NetworkInterface intf : Collections.list(niList)) {
                    if (!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                        continue;
                    }

                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
                        return true;
                    }
                }
            } else {
                return false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 前往应用设置页面
     *
     * @param context
     */
    public static void toNotificSettingAct(Context context) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", SystemUtil.getAppPackageName(context));
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
            context.startActivity(intent);
        } catch (Exception e) {
            Uri packageURI = Uri.parse("package:" + SystemUtil.getAppPackageName(context));
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
            context.startActivity(intent);
        }
    }

    public static void copyString(Context context, String copyString) {
        //复制黏贴
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setPrimaryClip(ClipData.newPlainText(null, copyString));
    }

    /**
     * 获取剪切板里内容
     *
     * @param editText
     * @return
     */
    private static Handler sHandler = new Handler();

    public static void getClipBoardContent(EditText editText, boolean isPageJustStart,
                                           OnCallback<String> callback) {
        if (isVersionLowerThanQ()) {
            callback.onCallback(getClipBoardContent(editText.getContext()));
        } else {
            sHandler.postDelayed(() -> {
                editText.setTextIsSelectable(true);
                callback.onCallback(getClipBoardContent(editText.getContext()));
            }, isPageJustStart ? 100 : 0); // 页面刚打开时必须延时后执行，否则可以立即执行
        }
    }

    private static String getClipBoardContent(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
        ClipData primaryClip = clipboardManager.getPrimaryClip();
        String content = null;
        if (primaryClip != null && primaryClip.getItemCount() > 0) {
            ClipData.Item itemAt = primaryClip.getItemAt(0);
            if (itemAt != null && itemAt.getText() != null) {
                content = itemAt.getText().toString();
            }
        }
        return content;
    }

    /**
     * 复制到剪切板
     */
    public static void copyToClipBoard(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));
        }
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
     * 判断当前应用所占内存是否达到阈值
     *
     * @return
     */
    public static boolean getRunningAppProcessInfo() {
        ActivityManager mActivityManager = (ActivityManager) BaseApp.context.getSystemService(Context.ACTIVITY_SERVICE);

        // 获得系统里正在运行的所有进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessesList = mActivityManager
                .getRunningAppProcesses();

        if (runningAppProcessesList == null || runningAppProcessesList.isEmpty()) {
            return false;
        }

        int memSize = 0;
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessesList) {
            // 进程ID号
            int pid = runningAppProcessInfo.pid;
            // 进程名
            String processName = runningAppProcessInfo.processName;
            if (processName.equals(BaseApp.context.getPackageName())) {
                // 占用的内存
                int[] pids = new int[]{pid};
                Debug.MemoryInfo[] memoryInfo = mActivityManager
                        .getProcessMemoryInfo(pids);
                memSize = memoryInfo[0].dalvikPrivateDirty;
                break;
            }
        }
        if (memSize >= 90 * 1024) {
            return true;
        }
        return false;
    }

    //判断当前页面的前后台切换
    public static boolean isRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context
                .ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        // 枚举进程
        if (appProcessInfos != null) {
            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
                if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 获取当前应用程序的包名
     *
     * @param context 上下文对象
     * @return 返回包名
     */
    public static String getAppProcessName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    /**
     * 获取SystemProperties属性值
     *
     * @param context
     * @param key
     * @return
     * @throws IllegalArgumentException
     */
    public static String getSystemPropertiesValue(Context context, String key) throws IllegalArgumentException {
        String ret = "";
        try {
            ClassLoader cl = context.getClassLoader();
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;
            Method get = SystemProperties.getMethod("get", paramTypes);
            Object[] params = new Object[1];
            params[0] = new String(key);
            ret = (String) get.invoke(SystemProperties, params);
        } catch (Exception e) {
            LogUtil.printeException(e);
        }
        return ret;
    }

    public static boolean isVersionLowerThanQ() {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.P;
    }
}


