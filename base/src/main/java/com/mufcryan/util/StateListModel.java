package com.mufcryan.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.SparseArray;
import androidx.annotation.StringDef;


/**
 * Created by chenchiyi on 2017/12/5.
 * <p>
 * 该类可自动配置日间夜间颜色及点击效果，减少重复的xml编写
 * button,textview等可以用colorStateList,backgroundColor只能用stateListDrawable
 * 方法默认不可点击，如需设置30%透明度在参数中加true
 * 增加新的color或drawable时在该类最下方的注解中添加
 * 一般只需传入日间颜色作为参数即可，如需自定义日夜间颜色调用getUserDefinedColor等方法
 * <p>
 * 用法如下:
 * layout.setBackgroundDrawable(StateListModel.getStateListDrawable(context,StateListModel.Ylw_1));
 * tvTitle.setTextColor(StateListModel.getColorStatelist(context,StateListModel.Red_1));
 * tvRight.setTextColor(StateListModel.getColorStatelist(context,StateListModel.Blk_1,true));
 */

public final class StateListModel {
    private static final String TAG = StateListModel.class.getSimpleName();

    private static final String RES_COLOR = "color";
    private static final String RES_Drawble = "drawable";

    // 获取控件状态
    private static int pressed = android.R.attr.state_pressed;
    private static int focused = android.R.attr.state_focused;
    private static int selected = android.R.attr.state_selected;
    private static int unabled = -android.R.attr.state_enabled;
    private static int normal = android.R.attr.state_enabled;
    private static int window_focused = android.R.attr.state_window_focused;

    private static int curColorId;
    private static int curColorAlphaId;

    private static int curDrawableId;
    private static int curDrawableAlphaId;

    // 缓存
    private static final SparseArray<StateListDrawable> STATE_LIST_DRAWABLE_SPARSE_ARRAY = new SparseArray<>();
    private static final SparseArray<StateListDrawable> STATE_LIST_DRAWABLE_SPARSE_ARRAY_ALPHA = new SparseArray<>();
    private static final SparseArray<ColorStateList> COLOR_STATE_LIST_SPARSE_ARRAY = new SparseArray<>();
    private static final SparseArray<ColorStateList> COLOR_STATE_LIST_SPARSE_ARRAY_ALPHA = new SparseArray<>();
    private static final HashMap<Drawable, StateListDrawable> NONSTANDARD_DRAWABLE_HASHMAP = new HashMap<>();
    private static final HashMap<Drawable, StateListDrawable> NONSTANDARD_DRAWABLE_HASHMAP_ALPHA = new HashMap<>();


    /**
     * 获取drawable的对应列表（drawble必须命名符合要求）
     *
     * @param mContext
     * @param lightDrawable
     * @return
     */
    public static StateListDrawable getStateListDrawable(Context mContext, @colors String lightDrawable, boolean needAlpha) {
        getCurrentDrawable(mContext, lightDrawable);
        Drawable drawable = null;
        Drawable alphaDrawable = null;
        try {
            drawable = mContext.getResources().getDrawable(curDrawableId);
            alphaDrawable = mContext.getResources().getDrawable(curDrawableAlphaId);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

        StateListDrawable cacheList = getCacheListData(curDrawableId, RES_Drawble, needAlpha);
        if (cacheList != null) {
            return cacheList;
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        if (!needAlpha) {
            stateListDrawable.addState(new int[]{}, drawable);
        } else {
            stateListDrawable.addState(new int[]{pressed}, alphaDrawable);
            stateListDrawable.addState(new int[]{selected}, alphaDrawable);
            stateListDrawable.addState(new int[]{unabled}, alphaDrawable);
            stateListDrawable.addState(new int[]{}, drawable);
        }
        cacheStateListData(mContext, curDrawableId, stateListDrawable, needAlpha);
        return stateListDrawable;
    }

    public static StateListDrawable getStateListDrawable(Drawable normalDrawable, Drawable clickDrawable) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{pressed}, clickDrawable);
        stateListDrawable.addState(new int[]{selected}, clickDrawable);
        stateListDrawable.addState(new int[]{unabled}, clickDrawable);
        stateListDrawable.addState(new int[]{}, normalDrawable);
        return stateListDrawable;
    }

    /**
     * 获取color的对应列表
     *
     * @param mContext
     * @param lightColor
     * @param needAlpha
     * @return
     */
    public static ColorStateList getColorStatelist(Context mContext, @colors String lightColor, boolean needAlpha) {
        getCurrentColor(mContext, lightColor);

        int color = 0;
        int colorAlpha = 0;
        try {
            color = mContext.getResources().getColor(curColorId);
            colorAlpha = mContext.getResources().getColor(curColorAlphaId);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        ColorStateList cacheList = getCacheListData(curColorId, RES_COLOR, needAlpha);
        if (cacheList != null) {
            return cacheList;
        }
        // pressed, focused, enabled, focused, unable, normal
        int[] colors = new int[4];
        int[][] states = getColorConfig(needAlpha);
        if (needAlpha) {
            colors[0] = colorAlpha;
            colors[1] = colorAlpha;
            colors[2] = colorAlpha;
            colors[3] = color;
        } else {
            colors[0] = color;
        }

        ColorStateList colorStateList = new ColorStateList(states, colors);
        cacheStateListData(mContext, curColorId, colorStateList, needAlpha);
        return colorStateList;
    }

    /**
     * 默认配置无点击效果
     *
     * @param mContext
     * @param lightColor
     * @return
     */
    public static ColorStateList getColorStatelist(Context mContext, @colors String lightColor) {
        return getColorStatelist(mContext, lightColor, false);
    }

    /**
     * 默认配置无点击效果
     *
     * @param mContext
     * @param lightDrawable
     * @return
     */
    public static StateListDrawable getStateListDrawable(Context mContext, @colors String lightDrawable) {
        return getStateListDrawable(mContext, lightDrawable, false);
    }

    private static int[][] getColorConfig(boolean needAlpha) {
        if (needAlpha) {
            int[][] states = new int[4][];
            states[0] = new int[]{pressed};
            states[1] = new int[]{selected};
            states[2] = new int[]{unabled};
            states[3] = new int[]{};
            return states;
        } else {
            int[][] states = new int[1][];
            states[0] = new int[]{};
            return states;
        }
    }

    /**
     * 获取对应主题的颜色
     *
     * @param mContext
     * @param lightColor 日间模式下的颜色
     */
    private static void getCurrentColor(Context mContext, String lightColor) {
        StringBuilder curColor = new StringBuilder();
        curColor.append(lightColor);
        curColorId = getResId(mContext, curColor.toString(), RES_COLOR);
        String curColorAlpha = curColor.append("_alpha_30").toString();
        curColorAlphaId = getResId(mContext, curColorAlpha, RES_COLOR);
    }

    /**
     * 获取对应主题下的drawable
     *
     * @param mContext
     * @param lightDrawable
     */
    private static void getCurrentDrawable(Context mContext, String lightDrawable) {
        StringBuilder curDrawable = new StringBuilder();
        curDrawable.append(lightDrawable);
        curDrawableId = getResId(mContext, curDrawable.toString(), RES_Drawble);
        String curDrawableAlpha = curDrawable.append("_alpha_30").toString();
        curDrawableAlphaId = getResId(mContext, curDrawableAlpha, RES_Drawble);
    }

    /**
     * 自定义日间与夜间颜色值
     *
     * @param mContext
     * @param lightColor
     * @param nightColor
     */
    public static void getUserDefinedColor(Context mContext, @colors String lightColor, @colors String nightColor, boolean needAlpha) {
        getColorStatelist(mContext, lightColor, needAlpha);

    }

    /**
     * 自定义日间与夜间的drawable
     *
     * @param mContext
     * @param lightDrawable
     * @param nightDrawable
     */
    public static StateListDrawable getUserDefinedDrawable(Context mContext, int lightDrawable, int nightDrawable, boolean needAlpha) {
        Drawable d = mContext.getResources().getDrawable(lightDrawable);
        return getUserDefinedDrawable(d, needAlpha);

    }

    /**
     * 自定义独立的drawable
     *
     * @param mContext
     * @param curDrawable
     * @param needAlpha
     * @return
     */
    public static StateListDrawable getUserDefinedDrawable(Context mContext, int curDrawable, boolean needAlpha) {
        Drawable d = mContext.getResources().getDrawable(curDrawable);
        return getUserDefinedDrawable(d, needAlpha);
    }

    /**
     * 自定义Drawable
     *
     * @param mDrawable
     * @param needAlpha
     * @return
     */
    public static StateListDrawable getUserDefinedDrawable(Drawable mDrawable, boolean needAlpha) {
        Drawable curDrawable = mDrawable;
        Drawable alphaDrawable = null;
        try {
            alphaDrawable = mDrawable.getConstantState().newDrawable().mutate();
            alphaDrawable.setAlpha(77);
        } catch (NullPointerException e) {
            e.printStackTrace();
            alphaDrawable = mDrawable;
        }

        StateListDrawable cacheList = getCacheListDrawable(curDrawable, needAlpha);
        if (cacheList != null) {
            return cacheList;
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        if (!needAlpha) {
            stateListDrawable.addState(new int[]{}, curDrawable);
        } else {
            stateListDrawable.addState(new int[]{pressed}, alphaDrawable);
            stateListDrawable.addState(new int[]{selected}, alphaDrawable);
            stateListDrawable.addState(new int[]{unabled}, alphaDrawable);
            stateListDrawable.addState(new int[]{}, curDrawable);
        }
        cacheDrawable(curDrawable, stateListDrawable, needAlpha);
        return stateListDrawable;
    }

    public static StateListDrawable getAlpha30Drawable(Drawable mDrawable, boolean needAlpha) {
        Drawable curDrawable = mDrawable;
        Drawable alphaDrawable = null;
        try {
            alphaDrawable = mDrawable.getConstantState().newDrawable().mutate();
            alphaDrawable.setAlpha(178);
        } catch (NullPointerException e) {
            e.printStackTrace();
            alphaDrawable = mDrawable;
        }

        StateListDrawable cacheList = getCacheListDrawable(curDrawable, needAlpha);
        if (cacheList != null) {
            return cacheList;
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        if (!needAlpha) {
            stateListDrawable.addState(new int[]{}, curDrawable);
        } else {
            stateListDrawable.addState(new int[]{pressed}, alphaDrawable);
            stateListDrawable.addState(new int[]{selected}, alphaDrawable);
            stateListDrawable.addState(new int[]{unabled}, alphaDrawable);
            stateListDrawable.addState(new int[]{}, curDrawable);
        }
        cacheDrawable(curDrawable, stateListDrawable, needAlpha);
        return stateListDrawable;
    }


    /**
     * 获取资源文件ID
     *
     * @param context
     * @param resName
     * @param defType
     * @return
     */
    public static int getResId(Context context, String resName, String defType) {
        try {
            return context.getResources().getIdentifier(resName, defType, context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 缓存List数据
     *
     * @param mContext
     * @param lightResourcesId
     * @param cacheList
     * @param <T>
     */
    private static <T> void cacheStateListData(final Context mContext, final int lightResourcesId, final T cacheList, final boolean needAlpha) {
        if (lightResourcesId == 0) {
            return;
        }
        if (cacheList instanceof ColorStateList) {
            if (needAlpha) {
                COLOR_STATE_LIST_SPARSE_ARRAY_ALPHA.put(lightResourcesId, (ColorStateList) cacheList);
            } else {
                COLOR_STATE_LIST_SPARSE_ARRAY.put(lightResourcesId, (ColorStateList) cacheList);
            }
        } else if (cacheList instanceof StateListDrawable) {
            if (needAlpha) {
                STATE_LIST_DRAWABLE_SPARSE_ARRAY_ALPHA.put(lightResourcesId, (StateListDrawable) cacheList);
            } else {
                STATE_LIST_DRAWABLE_SPARSE_ARRAY.put(lightResourcesId, (StateListDrawable) cacheList);
            }
        }
    }

    /**
     * 缓存Drawable数据
     *
     * @param curDrawable
     * @param stateListDrawable
     * @param needAlpha
     */
    private static void cacheDrawable(final Drawable curDrawable, final StateListDrawable stateListDrawable, final boolean needAlpha) {
        if (curDrawable == null) {
            return;
        }
        if (needAlpha) {
            NONSTANDARD_DRAWABLE_HASHMAP_ALPHA.put(curDrawable, stateListDrawable);
        } else {
            NONSTANDARD_DRAWABLE_HASHMAP.put(curDrawable, stateListDrawable);
        }
    }

    /**
     * 取缓存数据
     *
     * @param lightResourcesId
     * @param resType
     * @param <T>
     * @return
     */
    private static <T> T getCacheListData(final Integer lightResourcesId, String resType, boolean needAlpha) {
        switch (resType) {
            case RES_COLOR:
                if (needAlpha) {
                    return (T) COLOR_STATE_LIST_SPARSE_ARRAY_ALPHA.get(lightResourcesId);
                } else {
                    return (T) COLOR_STATE_LIST_SPARSE_ARRAY.get(lightResourcesId);
                }
            case RES_Drawble:
                if (needAlpha) {
                    return (T) STATE_LIST_DRAWABLE_SPARSE_ARRAY_ALPHA.get(lightResourcesId);
                } else {
                    return (T) STATE_LIST_DRAWABLE_SPARSE_ARRAY.get(lightResourcesId);
                }
            default:
                return null;
        }
    }

    /**
     * 取缓存数据
     *
     * @param curDrawable
     * @param needAlpha
     * @return
     */
    private static StateListDrawable getCacheListDrawable(final Drawable curDrawable, boolean needAlpha) {
        if (needAlpha) {
            return NONSTANDARD_DRAWABLE_HASHMAP.get(curDrawable);
        } else {
            return NONSTANDARD_DRAWABLE_HASHMAP_ALPHA.get(curDrawable);
        }
    }

    /**
     * 生成圆角矩形的背景（长度单位都是dp）
     *
     * @param mContext
     * @param solidColor
     * @param strokeColor
     * @param strokeWidth
     * @param cornerRadius
     * @param needAlpha
     * @return
     */
    public static Drawable setRectangleBackground(Context mContext,
                                                  @colors String solidColor,
                                                  @colors String strokeColor,
                                                  float strokeWidth,
                                                  float cornerRadius,
                                                  boolean needAlpha) {
        getCurrentColor(mContext, solidColor);
        int solidColorId = curColorId;
        getCurrentColor(mContext, strokeColor);
        int strokeColorId = curColorId;
        int strokeWidthPx = DisplayUtil.dp2Px(mContext, strokeWidth);
        int cornerRadiusPx = DisplayUtil.dp2Px(mContext, cornerRadius);

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(mContext.getResources().getColor(solidColorId));
        gradientDrawable.setStroke(strokeWidthPx, mContext.getResources().getColor(strokeColorId));
        gradientDrawable.setCornerRadius(cornerRadiusPx);

        Drawable drawable = getUserDefinedDrawable(gradientDrawable, needAlpha);
        return drawable;
    }

    /**
     * 生成圆角矩形的背景
     *
     * @param mContext
     * @param solidColor
     * @param cornerRadius
     * @param needAlpha
     * @return
     */
    public static Drawable setRectangleBackground(Context mContext,
                                                  @colors String solidColor,
                                                  int cornerRadius,
                                                  boolean needAlpha) {
        getCurrentColor(mContext, solidColor);
        int solidColorId = curColorId;
        int cornerRadiusPx = DisplayUtil.dp2Px(mContext, cornerRadius);

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(mContext.getResources().getColor(solidColorId));
        gradientDrawable.setCornerRadius(cornerRadiusPx);

        Drawable drawable = getUserDefinedDrawable(gradientDrawable, needAlpha);
        return drawable;
    }


    public static final String Ylw_1 = "Ylw_1";
    public static final String Ylw_2 = "Ylw_2";
    public static final String Ylw_3 = "Ylw_3";
    public static final String Red_1 = "Red_1";
    public static final String Red_2 = "Red_2";
    public static final String Red_3 = "Red_3";
    public static final String Blu_1 = "Blu_1";
    public static final String Blu_2 = "Blu_2";
    public static final String Blk_1 = "Blk_1";
    public static final String Blk_2 = "Blk_2";
    public static final String Blk_3 = "Blk_3";
    public static final String Blk_4 = "Blk_4";
    public static final String Blk_5 = "Blk_5";
    public static final String Blk_6 = "Blk_6";
    public static final String Blk_7 = "Blk_7";
    public static final String Blk_8 = "Blk_8";
    public static final String Blk_9 = "Blk_9";
    public static final String Blk_10 = "Blk_10";
    public static final String Blk_11 = "Blk_11";
    public static final String Blk_12 = "Blk_12";
    public static final String Blk_13 = "Blk_13";
    public static final String white = "white";


    @StringDef({
            Ylw_1, Ylw_2, Ylw_3, Red_1, Red_2, Red_3, Blu_1, Blu_2,
            Blk_1, Blk_2, Blk_3, Blk_4, Blk_5, Blk_6, Blk_7, Blk_8,
            Blk_9, Blk_10, Blk_11, Blk_12, Blk_13, white
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface colors {
    }

}
