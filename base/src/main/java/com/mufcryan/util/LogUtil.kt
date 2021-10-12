package com.mufcryan.util

import android.util.Log

object LogUtil {
  var showLog = true
  private const val TAG = "logutil---"
  fun v(msg: String?) {
    v(TAG, msg)
  }

  fun v(tag: String?, msg: String?) {
    if (showLog) {
      Log.v(tag, msg ?: "")
    }
  }

  fun v(tag: String?, msg: String?, tr: Throwable?) {
    if (showLog) {
      Log.v(tag, msg, tr)
    }
  }

  fun d(msg: String?) {
    d(TAG, msg)
  }

  fun d(tag: String?, msg: String?) {
    if (showLog) {
      Log.d(tag, msg ?: "")
    }
  }

  fun d(tag: String?, msg: String?, tr: Throwable?) {
    if (showLog) {
      Log.d(tag, msg, tr)
    }
  }

  fun i(msg: String?) {
    i(TAG, msg)
  }

  fun i(tag: String?, msg: String?) {
    if (showLog) {
      Log.i(tag, msg ?: "")
    }
  }

  fun i(tag: String?, msg: String?, tr: Throwable?) {
    if (showLog) {
      Log.i(tag, msg, tr)
    }
  }

  fun w(msg: String?) {
    w(TAG, msg)
  }

  fun w(tag: String?, msg: String?) {
    if (showLog) {
      Log.w(tag, msg ?: "")
    }
  }

  fun w(tag: String?, msg: String?, tr: Throwable?) {
    if (showLog) {
      Log.w(tag, msg, tr)
    }
  }

  fun e(msg: String?) {
    e(TAG, msg)
  }

  fun e(tag: String?, msg: String?) {
    if (showLog) {
      Log.e(tag, msg ?: "")
    }
  }

  fun e(tag: String?, msg: String?, tr: Throwable?) {
    if (showLog) {
      Log.e(tag, msg, tr)
    }
  }

  private fun formNull(value: String?): String {
    return value ?: "null"
  }

  fun printException(exception: Throwable?) {
    printException(TAG, exception)
  }

  fun printException(tag: String?, exception: Throwable?) {
    if (showLog) {
      val msg = formNull(Log.getStackTraceString(exception))
      Log.w(tag, msg)
    }
  }
}