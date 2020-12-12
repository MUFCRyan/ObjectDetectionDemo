package com.mufcryan.objectdetectiondemo.base;

public class BaseResponse<T> {
    public final static int Success = 100000;
    public final static int ERROR_CODE_NET_ERROR = -1;
    public final static int ERROR_CODE_LOCAL_ERROR = -2;
    public final static int ERROR_CODE_SERVER_ERROR = -3;
    public int status;
    public String msg = "";
    public T data;
    public String request_code = "";

    /**
     * 返回数据成功（客户端已经处理所有的异常，真正能获取到成功的数据）
     */
    public boolean isSuccessful = true;

    public boolean isStatusOk() {
        return status == Success;
    }


    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setIsNetError() {
        status = ERROR_CODE_NET_ERROR;
    }

    public boolean isNetError() {
        return status == -1;
    }

    public void setIsServerError() {
        status = ERROR_CODE_SERVER_ERROR;
    }
    public boolean isServerError() {
        return status == ERROR_CODE_SERVER_ERROR;
    }


    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
