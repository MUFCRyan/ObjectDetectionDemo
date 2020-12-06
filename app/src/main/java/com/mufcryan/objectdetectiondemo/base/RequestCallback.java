package com.mufcryan.objectdetectiondemo.base;

/**
 * Created by zhaofengchun on 2018/7/25.
 *
 */

public interface RequestCallback<ResponseBody> {
    void onSuccess(ResponseBody data);
    default void onFailure(int errorCode, String errorText){
        // 此处可添加请求失败时的默认处理代码（一般指显示 Toast），子类若不需要则不调用父类方法即可
    }

    default void onError(Throwable e){
        // 此处可添加请求失败时的默认处理代码（一般指显示 Toast），子类若不需要则不调用父类方法即可
    }
}
