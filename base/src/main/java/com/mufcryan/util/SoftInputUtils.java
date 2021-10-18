package com.mufcryan.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by xiaxl 2016.01.18
 */
public class SoftInputUtils {
    private static final String TAG = SoftInputUtils.class.getSimpleName();

    public static interface OnShowSoftInputListener {
        // 弹出之前
        public void onPreShow();

        // 弹出之后
        public void onShown();
    }

    public static interface OnHideSoftInputListener {
        // 隐藏之后
        public void onHid();
    }


    /**
     * 显示输入框
     *
     * @param editText
     * @param listener
     */
    public static void showSoftInput(EditText editText, final OnShowSoftInputListener listener) {
        if (editText == null) {
            return;
        }

        //
        if (listener != null) {
            listener.onPreShow();
        }

        // 获取焦点
        editText.requestFocus();

        //
        ResultReceiver result = new ResultReceiver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onReceiveResult(int r, Bundle data) {
                if (listener != null) {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listener.onShown();
                        }
                    },300);
                }
            }
        };
        // 弹出软键盘
        InputMethodManager inputManager = (InputMethodManager) editText
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0, result);
    }


    /**
     * 隐藏输入框
     *
     * @param editText
     * @param callback
     */
    public static void hideSoftInput(EditText editText, final OnHideSoftInputListener callback) {
        try {
            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0, null);
        }catch (Exception e){

        }

    }

    public static void hideSoftInput(Activity activity, final OnHideSoftInputListener callback) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0, null);
        }catch (Exception e){

        }

    }

    public static boolean isSoftInputActive(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            return imm.isActive();
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
}