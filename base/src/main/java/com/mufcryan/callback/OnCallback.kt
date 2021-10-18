package com.mufcryan.callback

interface OnCallback<T> {
    fun onCallback(param: T)
    fun onCancel(){}
}