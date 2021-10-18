package com.mufcryan.base.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.mufcryan.base.R;
import com.mufcryan.util.DisplayUtil;
import com.mufcryan.util.SoftInputUtils;
import com.mufcryan.util.SystemUtil;

public class SearchBar extends FrameLayout {
    private static float sDensity = 1;
    private static float sT7DP = 0;
    private static float sT9DP = 0;
    private View mRootView;
    private View mSearchBarContainer;
    private ImageView mIvSearch;
    private View mFlRightIconContainer;
    private ImageView mIvClear;
    private EditText mEtEdit;
    private TextView mTvCancel;
    private OnClickListener mSearchClickListener;
    private OnClickListener mCancelClickListener;
    private OnClickListener mRightIconClickListener;
    private TextWatcher mTextWatcher;
    private OnTouchListener mTouchListener;
    private Style mStyle = Style.TYPE_2;
    private static int Margin_7dp = 0;
    private static int Margin_46dp = 0;
    private View mViewSearchCover;
    private boolean hasAnim = false;

    public SearchBar(@NonNull Context context) {
        super(context);
        init(null);
    }

    public SearchBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SearchBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public static SearchBar get(Context context){
        return get(context, Style.TYPE_2);
    }
    public static SearchBar get(Context context, Style style){
        SearchBar searchBar = new SearchBar(context);
        searchBar.setStyle(style);
        return searchBar;
    }


    private void init(AttributeSet attributeSet) {
        sDensity = getContext().getResources().getDisplayMetrics().density;
        sT7DP = getContext().getResources().getDimension(R.dimen.T_7) / sDensity;
        sT9DP = getContext().getResources().getDimension(R.dimen.T_9) / sDensity;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_search_bar, null);
        initViewsAndListeners(view);
        initAttrs(attributeSet);
        setContainerPaddingRight(hasAnim);
        addView(view);
    }

    private void initAttrs(AttributeSet attributeSet) {
        if (Margin_7dp <= 0){
            Margin_7dp = DisplayUtil.dp2Px(getContext(), 7);
        }
        if (Margin_46dp <= 0){
            Margin_46dp = DisplayUtil.dp2Px(getContext(), 46);
        }
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.HySearchBar);
        boolean isShowCancel = typedArray.getBoolean(R.styleable.HySearchBar_csb_show_cancel, true);
        if (mTvCancel != null && !isShowCancel){
            setShowCancel(false);
        }
        int style = typedArray.getInt(R.styleable.HySearchBar_search_style, 3);
        Style[] values = Style.values();
        if (0 <= style - 1 && style - 1 < values.length){
            mStyle = values[style - 1];
            updateViewsByStyle(mStyle);
        }
        String hint = typedArray.getString(R.styleable.HySearchBar_search_hint);
        mEtEdit.setHint(hint);
        typedArray.recycle();
    }

    private void initViewsAndListeners(View view) {
        mRootView = view.findViewById(R.id.ll_rootView);
        mSearchBarContainer = view.findViewById(R.id.search_bar_container);
        mViewSearchCover = view.findViewById(R.id.view_search_cover);
        mViewSearchCover.setOnClickListener(v -> {
            if (mSearchClickListener != null && !SystemUtil.isFastDoubleClick()){
                mSearchClickListener.onClick(mIvSearch);
            }
        });

        mIvSearch = view.findViewById(R.id.iv_search_icon);
        mIvSearch.setOnClickListener(v -> {
            if (mSearchClickListener != null && !SystemUtil.isFastDoubleClick()){
                mSearchClickListener.onClick(mIvSearch);
            }
        });

        mFlRightIconContainer = view.findViewById(R.id.fl_right_icon_container);
        mFlRightIconContainer.setOnClickListener(v -> {
            if (mStyle == Style.TYPE_2){
                clearText();
            }
            if (mRightIconClickListener != null){
                mRightIconClickListener.onClick(mFlRightIconContainer);
            }
        });
        mIvClear = view.findViewById(R.id.iv_clear);
        mEtEdit = view.findViewById(R.id.search_bar_edit);
        SpannableString ss = new SpannableString(mEtEdit.getHint());
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint
        mEtEdit.setHint(new SpannedString(ss));
        mEtEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (mTextWatcher != null){
                    mTextWatcher.beforeTextChanged(s, start, count, after);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mTextWatcher != null){
                    mTextWatcher.onTextChanged(s, start, before, count);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mTextWatcher != null){
                    mTextWatcher.afterTextChanged(s);
                }
                if (s != null && s.toString().length() > 0){
                    if (mStyle == Style.TYPE_2){
                        mFlRightIconContainer.setVisibility(VISIBLE);
                        mIvClear.setVisibility(VISIBLE);
                        mEtEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, sT7DP);
                    }
                } else {
                    if (mStyle == Style.TYPE_2){
                        mFlRightIconContainer.setVisibility(GONE);
                        mIvClear.setVisibility(GONE);
                        mEtEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, sT9DP);
                    }
                }
            }
        });
        mEtEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH && mSearchClickListener != null && !SystemUtil.isFastDoubleClick()) {
                mSearchClickListener.onClick(mIvSearch);
                return true;
            }
            return false;
        });
        mEtEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
            private boolean mHasFocus = mEtEdit.hasFocus();
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mHasFocus && !hasFocus){
                    hideSoftInput();
                } else if (!mHasFocus && hasFocus){
                    showSoftInput();
                }
                mHasFocus = hasFocus;
            }
        });
        mEtEdit.setOnTouchListener((v, event) -> {
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                mEtEdit.setFocusable(true);
                mEtEdit.setFocusableInTouchMode(true);
            }
            if (mTouchListener != null){
                return mTouchListener.onTouch(v, event);
            }
            return false;
        });

        mTvCancel = view.findViewById(R.id.tv_cancel);
        mTvCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCancelClickListener != null){
                    mCancelClickListener.onClick(mTvCancel);
                }
            }
        });
    }

    private void updateViewsByStyle(Style style) {
        if (mRootView == null)
            return;
        mFlRightIconContainer.setVisibility(GONE);
        mIvClear.setVisibility(GONE);
        setShowCancel(false);
        switch(style){
            case TYPE_1:
                mRootView.setPadding(mRootView.getPaddingLeft(), 0, mRootView.getPaddingRight(), 0);
                mViewSearchCover.setVisibility(VISIBLE);
                mEtEdit.setFocusable(false);
                mEtEdit.setFocusableInTouchMode(false);
                mFlRightIconContainer.setVisibility(VISIBLE);
                //mIvScan.setVisibility(VISIBLE);
                break;
            case TYPE_2:
                mRootView.setPadding(mRootView.getPaddingLeft(), Margin_7dp, mRootView.getPaddingRight(), Margin_7dp);
                mViewSearchCover.setVisibility(GONE);
                mFlRightIconContainer.setVisibility(VISIBLE);
                if (!TextUtils.isEmpty(mEtEdit.getText())){
                    mIvClear.setVisibility(VISIBLE);
                }
                setShowCancel(true);
                break;
            default :
                break;
        }
    }

    public SearchBar setStyle(Style style){
        mStyle = style;
        updateViewsByStyle(style);
        return this;
    }

    private SearchBar performCancelClick(){
        mTvCancel.performClick();
        return this;
    }

    public SearchBar setOnSearchClickListener(OnClickListener searchClickListener){
        mSearchClickListener = searchClickListener;
        return this;
    }
    public SearchBar performSearchClick(){
        if (mSearchClickListener != null && !SystemUtil.isFastDoubleClick()){
            mSearchClickListener.onClick(mIvSearch);
        }
        return this;
    }

    public SearchBar setOnClearClickListener(OnClickListener clearClickListener){
        if (mStyle == Style.TYPE_1)
            return this;
        mRightIconClickListener = clearClickListener;
        return this;
    }

    public SearchBar setOnScanClickListener(OnClickListener scanClickListener){
        if (mStyle == Style.TYPE_2)
            return this;
        mRightIconClickListener = scanClickListener;
        return this;
    }

    public SearchBar setOnCancelClickListener(OnClickListener cancelClickListener){
        mCancelClickListener = cancelClickListener;
        return this;
    }

    public SearchBar addTextChangedListener(TextWatcher textWatcher){
        mTextWatcher = textWatcher;
        return this;
    }

    public SearchBar setOnEditTextTouchListener(OnTouchListener touchListener){
        mTouchListener = touchListener;
        return this;
    }

    public SearchBar clearText(){
        mEtEdit.setText("");
        return this;
    }

    public SearchBar setText(@StringRes int resId){
        mEtEdit.setText(resId);
        return this;
    }

    public SearchBar setText(CharSequence text){
        mEtEdit.setText(text);
        return this;
    }

    public Editable getText(){
        return mEtEdit.getText();
    }

    public SearchBar setHint(@StringRes int resId){
        mEtEdit.setHint(resId);
        return this;
    }

    public SearchBar setHint(CharSequence hint){
        mEtEdit.setHint(hint);
        return this;
    }

    public SearchBar setSelection(int index){
        mEtEdit.setSelection(index);
        return this;
    }

    public SearchBar setSelectionToLast(){
        mEtEdit.setSelection(mEtEdit.length());
        return this;
    }

    public int getEditTextLength(){
        return mEtEdit.length();
    }

    public boolean isEditTextFocused(){
        return mEtEdit.isFocused();
    }

    public SearchBar requestEditTextFocus(){
        mEtEdit.requestFocus();
        return this;
    }

    public SearchBar clearEditTextFocus(){
        mEtEdit.setFocusable(false);
        return this;
    }

    public SearchBar showSoftInput(){
        requestEditTextFocus();
        SoftInputUtils.showSoftInput(mEtEdit, null);
        return this;
    }

    public SearchBar hideSoftInput(){
        SoftInputUtils.hideSoftInput(mEtEdit, null);
        return this;
    }


    public View getRootView(){
        return mRootView;
    }

    public View getSearchBarContainer() {
        return mSearchBarContainer;
    }

    public ImageView getIvSearch() {
        return mIvSearch;
    }

    public TextView getTvCancel() {
        return mTvCancel;
    }

    public void setShowCancel(boolean isShow){
        if (isShow){
            mTvCancel.setVisibility(View.VISIBLE);
        } else {
            mTvCancel.setVisibility(View.GONE);
        }
        setContainerPaddingRight(hasAnim);
    }

    public void setHasAnim(boolean hasAnim) {
        this.hasAnim = hasAnim;
        setContainerPaddingRight(hasAnim);
    }

    private void setContainerPaddingRight(boolean hasAnim) {
        if(mStyle == Style.TYPE_1){
            return;
        }
        int paddingRight = hasAnim ? 0 : getPaddingRightForAnim();
        mSearchBarContainer.setPadding(mSearchBarContainer.getPaddingLeft(),
                mSearchBarContainer.getPaddingTop(),
                paddingRight,
                mSearchBarContainer.getPaddingBottom());
    }

    public void initAnimMarginTop(int marginTop){
        ViewGroup.MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
        params.setMargins(params.leftMargin, marginTop, params.rightMargin, params.bottomMargin);
        requestLayout();
    }

    public int getPaddingRightForAnim(){
        return mTvCancel.getVisibility() == View.VISIBLE ? Margin_46dp : 0;
    }

    public enum Style{
        TYPE_1,
        TYPE_2,
    }
}