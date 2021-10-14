package com.mufcryan.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.mufcryan.BaseApp;
import com.mufcryan.base.bean.BaseResponse;
import com.mufcryan.util.LogUtil;

import org.jetbrains.annotations.NotNull;

/**
 * Created by zhaofengchun on 2018/7/25.
 */

public abstract class BaseRepository<RequestBody, ResponseBody> {
    public enum DataStrategy {
        LOCAL_GET_THEN_ALWAYS_NET_GET_AND_STORE, // 本地先获取数据，无论获取成功与否，之后都进行网络请求，并将请求回的数据保存到本地
        LOCAL_GET_IF_ERROR_NET_GET_AND_STORE, // 本地优先，没有缓存时才走网络请求，请求成功后存入缓存
        LOCAL_GET_ONLY, // 只从本地获取，不从网络获取
        LOCAL_GET_ONLY_NET_STORE_ONLY, // 只从本地获取，但是获取之前先从网络请求最新数据并存入本地（此过程中可能有数据的比对）
        LOCAL_GET_ONLY_NET_GET_ONLY, // 只从本地获取，但是获取之前先从网络请求最新数据但存入本地（此过程中可能有数据的比对），存本地的逻辑由外部处理
        LOCAL_STORE_ONLY, // 数据只保存到本地，一般用于草稿等本地产生的数据的保存
        LOCAL_STORE_THEN_NET_GET, // 数据先保存到本地，保存成功之后请求网络
        LOCAL_STORE_THEN_NET_GET_AND_STORE, // 数据先保存到本地，保存成功之后请求网络，网络请求成功之后再次存入本地
        NET_GET_AND_STORE_IF_ERROR_LOCAL_GET, // 网络优先，网络请求失败时才拿本地数据，网络请求成功后存入本地
        NET_GET_ONLY, // 只从网络获取，无论成功失败都不存本地
        NET_STORE_ONLY, // 只从网络请求数据，网络数据请求回来只保存，保存成功后通知外部回调
        NET_GET_AND_STORE_ONLY, // 只从网络请求数据，请求回来成功时回调并保存到本地，失败时只执行回调
    }

    /**
     * 不需要自动进行错误回调处理的数据请求方法
     */
    public void processData(final RequestBody requestBody, final RequestCallback<ResponseBody> callBack) {
        switch (getStrategy()) {
            case LOCAL_GET_THEN_ALWAYS_NET_GET_AND_STORE:
                performByLocalGetThenAlwaysNetGetStore(requestBody, callBack);
                break;
            case NET_GET_AND_STORE_IF_ERROR_LOCAL_GET:
                performByNetGetStoreIfErrorLocalGet(requestBody, callBack);
                break;
            case NET_GET_ONLY:
                performByNetOnly(requestBody, callBack);
                break;
            case LOCAL_GET_IF_ERROR_NET_GET_AND_STORE:
                performByLocalGetIfErrorNetGetStore(requestBody, callBack);
                break;
            case LOCAL_GET_ONLY:
                performByLocalOnly(requestBody, callBack);
                break;
            case LOCAL_GET_ONLY_NET_STORE_ONLY:
                performByLocalOnlyNetStore(requestBody, callBack);
                break;
            case LOCAL_GET_ONLY_NET_GET_ONLY:
                performByLocalOnlyNetGet(requestBody, callBack);
                break;
            case NET_STORE_ONLY:
                performByNetStoreOnly(requestBody, callBack);
                break;
            case LOCAL_STORE_ONLY:
                performByLocalStoreOnly(requestBody, callBack);
                break;
            case LOCAL_STORE_THEN_NET_GET:
                performByLocalStoreThenNetGet(requestBody, callBack);
                break;
            case LOCAL_STORE_THEN_NET_GET_AND_STORE:
                performByLocalStoreThenNetGetAndStore(requestBody, callBack);
                break;
            case NET_GET_AND_STORE_ONLY:
                performByNetGetStoreOnly(requestBody, callBack);
                break;
            default:
                break;
        }
    }

    /**
     * 需要自动进行错误回调处理的数据请求
     */
    public void processDataForResponse(final RequestBody requestBody, MutableLiveData<ResponseBody> liveData) {
        RequestCallback<ResponseBody> callBack = new RequestCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                liveData.postValue(data);
            }

            public boolean dealFailure(BaseResponse response) {
                return false;
            }

            @Override
            public void onFailure(int errorCode, String errorText) {
                LogUtil.INSTANCE.e("kami", "onFailure :" + errorCode + " " + errorText);
                // 网络请求成功、但是 status 错误时的回调
                try {
                    // 因为实在无法获取到 ResponseBody 的类型，只能通过强转是否产生转换异常来判断，不产生异常代表 ResponseBody 是
                    // BaseResponse 类型
                    BaseResponse baseResponse = new BaseResponse();
                    baseResponse.isSuccessful = false;
                    baseResponse.setStatus(errorCode);
                    baseResponse.setMsg(errorText);
                    ResponseBody responseBody = (ResponseBody) baseResponse;
                    liveData.postValue((ResponseBody) baseResponse);

                } catch (ClassCastException e) {
                    e.printStackTrace();
                    // ResponseBody 不是 BaseResponse 类型，不做进一步处理
                }
            }

            @Override
            public void onError(Throwable e) {
                if (requestBody != null) {
                    Toast.makeText(BaseApp.context, requestBody.toString() + "\nonError cause :" + e.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(BaseApp.context, "onError cause :" + e.toString(), Toast.LENGTH_LONG).show();
                }
                // 网络请求本身失败的回调
                try {
                    // 因为实在无法获取到 ResponseBody 的类型，只能通过强转是否产生转换异常来判断，不产生异常代表 ResponseBody 是
                    // BaseResponse 类型
                    BaseResponse baseResponse = new BaseResponse();
                    baseResponse.isSuccessful = false;
                    if (isNetworkConnected(BaseApp.context)) {
                        baseResponse.setIsServerError();
                    } else {
                        baseResponse.setIsNetError();
                    }
                    baseResponse.setMsg(e.getMessage());
                    ResponseBody responseBody = (ResponseBody) baseResponse;
                    liveData.postValue((ResponseBody) baseResponse);
                } catch (ClassCastException castException) {
                    // ResponseBody 不是 BaseResponse 类型，不做进一步处理
                }
            }
        };
        processData(requestBody, callBack);
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    private void performByLocalGetThenAlwaysNetGetStore(RequestBody requestBody, RequestCallback<ResponseBody> callBack) {
        RepositoryCallback<ResponseBody> realCallback;
        realCallback = new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                callBack.onSuccess(data);
                getNetData(requestBody, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody data) {
                        callBack.onSuccess(data);
                        saveLocalData(data, new RepositoryCallback<ResponseBody>() {
                        });
                    }

                    @Override
                    public void onFailure(int errorCode, String errorText) {
                        callBack.onFailure(errorCode, errorText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                    }
                });
            }

            @Override
            public void onFailure(int errorCode, String errorText) {
                callBack.onFailure(errorCode, errorText);
                getNetData(requestBody, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody data) {
                        callBack.onSuccess(data);
                        saveLocalData(data, new RepositoryCallback<ResponseBody>() {
                        });
                    }

                    @Override
                    public void onFailure(int errorCode, String errorText) {
                        callBack.onFailure(errorCode, errorText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(e);
                getNetData(requestBody, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody data) {
                        callBack.onSuccess(data);
                        saveLocalData(data, new RepositoryCallback<ResponseBody>() {
                        });
                    }

                    @Override
                    public void onFailure(int errorCode, String errorText) {
                        callBack.onFailure(errorCode, errorText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                    }
                });
            }
        };
        getLocalData(requestBody, realCallback);
    }

    private void performByNetGetStoreIfErrorLocalGet(RequestBody requestBody, RequestCallback<ResponseBody> callBack) {
        RepositoryCallback<ResponseBody> realCallback;
        realCallback = new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                callBack.onSuccess(data);
                saveLocalData(data, new RepositoryCallback<ResponseBody>() {
                });
            }

            @Override
            public void onFailure(@NotNull int errorCode, @NotNull String errorText) {
                getLocalData(requestBody, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody data) {
                        callBack.onSuccess(data);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorText) {
                        callBack.onFailure(errorCode, errorText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                getLocalData(requestBody, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody data) {
                        callBack.onSuccess(data);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorText) {
                        callBack.onFailure(errorCode, errorText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                    }
                });
            }
        };
        getNetData(requestBody, realCallback);
    }

    private void performByNetOnly(RequestBody requestBody, RequestCallback<ResponseBody> callBack) {
        RepositoryCallback<ResponseBody> realCallback;
        realCallback = new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                callBack.onSuccess(data);
            }

            @Override
            public void onFailure(int errorCode, String errorText) {
                callBack.onFailure(errorCode, errorText);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(e);
            }
        };
        getNetData(requestBody, realCallback);
    }

    private void performByLocalGetIfErrorNetGetStore(RequestBody requestBody, RequestCallback<ResponseBody> callBack) {
        RepositoryCallback<ResponseBody> realCallback;
        realCallback = new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                callBack.onSuccess(data);
            }

            @Override
            public void onFailure(int errorCode, String errorText) {
                getNetData(requestBody, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody data) {
                        callBack.onSuccess(data);
                        saveLocalData(data, new RepositoryCallback<ResponseBody>() {
                        });
                    }

                    @Override
                    public void onFailure(int errorCode, String errorText) {
                        callBack.onFailure(errorCode, errorText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                getNetData(requestBody, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody data) {
                        callBack.onSuccess(data);
                        saveLocalData(data, new RepositoryCallback<ResponseBody>() {
                        });
                    }

                    @Override
                    public void onFailure(int errorCode, String errorText) {
                        callBack.onFailure(errorCode, errorText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                    }
                });
            }
        };
        getLocalData(requestBody, realCallback);
    }

    private void performByLocalOnly(RequestBody requestBody, RequestCallback<ResponseBody> callBack) {
        RepositoryCallback<ResponseBody> realCallback;
        realCallback = new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                callBack.onSuccess(data);
            }

            @Override
            public void onFailure(int errorCode, String errorText) {
                callBack.onFailure(errorCode, errorText);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(e);
            }
        };
        getLocalData(requestBody, realCallback);
    }

    private void performByLocalOnlyNetStore(RequestBody requestBody, RequestCallback<ResponseBody> callBack) {
        RepositoryCallback<ResponseBody> realCallback;
        realCallback = new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                saveLocalData(data, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody data) {
                        getLocalData(requestBody, new RepositoryCallback<ResponseBody>() {
                            @Override
                            public void onSuccess(ResponseBody data) {
                                callBack.onSuccess(data);
                            }

                            @Override
                            public void onFailure(int errorCode, String errorText) {
                                callBack.onFailure(errorCode, errorText);
                            }

                            @Override
                            public void onError(Throwable e) {
                                callBack.onError(e);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int errorCode, String errorText) {
                        getLocalData(requestBody, new RepositoryCallback<ResponseBody>() {
                            @Override
                            public void onSuccess(ResponseBody data) {
                                callBack.onSuccess(data);
                            }

                            @Override
                            public void onFailure(int errorCode, String errorText) {
                                callBack.onFailure(errorCode, errorText);
                            }

                            @Override
                            public void onError(Throwable e) {
                                callBack.onError(e);
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(int errorCode, String errorText) {
                getLocalData(requestBody, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody data) {
                        callBack.onSuccess(data);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorText) {
                        callBack.onFailure(errorCode, errorText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                getLocalData(requestBody, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody data) {
                        callBack.onSuccess(data);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorText) {
                        callBack.onFailure(errorCode, errorText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                    }
                });
            }
        };
        getNetData(requestBody, realCallback);
    }

    private void performByLocalOnlyNetGet(RequestBody requestBody, RequestCallback<ResponseBody> callBack) {
        RepositoryCallback<ResponseBody> realCallback;
        realCallback = new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                getLocalData(requestBody, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody data) {
                        callBack.onSuccess(data);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorText) {
                        callBack.onFailure(errorCode, errorText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                    }
                });
            }

            @Override
            public void onFailure(int errorCode, String errorText) {
                getLocalData(requestBody, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody data) {
                        callBack.onSuccess(data);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorText) {
                        callBack.onFailure(errorCode, errorText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                getLocalData(requestBody, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody data) {
                        callBack.onSuccess(data);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorText) {
                        callBack.onFailure(errorCode, errorText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                    }
                });
            }
        };
        getNetData(requestBody, realCallback);
    }

    private void performByNetStoreOnly(RequestBody requestBody, RequestCallback<ResponseBody> callBack) {
        RepositoryCallback<ResponseBody> realCallback = new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                saveLocalData(data, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody data) {
                        callBack.onSuccess(data);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorText) {
                        callBack.onFailure(errorCode, errorText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                    }
                });
            }

            @Override
            public void onFailure(int errorCode, String errorText) {
                callBack.onFailure(errorCode, errorText);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(e);
            }
        };
        getNetData(requestBody, realCallback);
    }

    /**
     * 该方法适用于只是将 UI 层传来的数据保存到本地的场景
     */
    private void performByLocalStoreOnly(RequestBody requestBody, RequestCallback<ResponseBody> callBack) {
        RepositoryCallback<ResponseBody> realCallback = new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                callBack.onSuccess(data);
            }

            @Override
            public void onFailure(int errorCode, String errorText) {
                callBack.onFailure(errorCode, errorText);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(e);
            }
        };
        saveLocalDataOnly(requestBody, realCallback);
    }

    /**
     * 该方法适用于将 UI 层传来的数据保存到本地，保存成功后执行网络请求的场景
     */
    private void performByLocalStoreThenNetGet(RequestBody requestBody, RequestCallback<ResponseBody> callBack) {
        RepositoryCallback<ResponseBody> realCallback = new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                callBack.onSuccess(data);
                getNetData(requestBody, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody data) {
                        callBack.onSuccess(data);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorText) {
                        callBack.onFailure(errorCode, errorText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                    }
                });
            }

            @Override
            public void onFailure(int errorCode, String errorText) {
                callBack.onFailure(errorCode, errorText);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(e);
            }
        };
        saveLocalDataOnly(requestBody, realCallback);
    }

    /**
     * 该方法适用于将 UI 层传来的数据保存到本地，保存成功后执行网络请求的场景
     */
    private void performByLocalStoreThenNetGetAndStore(RequestBody requestBody, RequestCallback<ResponseBody> callBack) {
        RepositoryCallback<ResponseBody> realCallback = new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                callBack.onSuccess(data);
                getNetData(requestBody, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody data) {
                        saveLocalData(data, new RepositoryCallback<ResponseBody>() {
                            @Override
                            public void onSuccess(ResponseBody data) {
                                callBack.onSuccess(data);
                            }

                            @Override
                            public void onFailure(int errorCode, String errorText) {
                                callBack.onFailure(errorCode, errorText);
                            }

                            @Override
                            public void onError(Throwable e) {
                                callBack.onError(e);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int errorCode, String errorText) {
                        callBack.onFailure(errorCode, errorText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e);
                    }
                });
            }

            @Override
            public void onFailure(int errorCode, String errorText) {
                callBack.onFailure(errorCode, errorText);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(e);
            }
        };
        saveLocalDataOnly(requestBody, realCallback);
    }

    private void performByNetGetStoreOnly(RequestBody requestBody, RequestCallback<ResponseBody> callBack) {
        RepositoryCallback<ResponseBody> realCallback = new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                callBack.onSuccess(data);
                saveLocalData(data, new RepositoryCallback<ResponseBody>() {
                });
            }

            @Override
            public void onFailure(int errorCode, String errorText) {
                callBack.onFailure(errorCode, errorText);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(e);
            }
        };
        getNetData(requestBody, realCallback);
    }

    /**
     * 若要改变 DataStrategy，需要在子类中重写该方法并返回子类特定的 DataStrategy
     */
    protected abstract DataStrategy getStrategy();

    protected void getLocalData(RequestBody param, RepositoryCallback<ResponseBody> callBack) {
    }

    protected void getNetData(RequestBody param, RepositoryCallback<ResponseBody> callBack) {
    }

    protected void saveLocalData(ResponseBody data, RepositoryCallback<ResponseBody> callBack) {
    }

    /**
     * 该方法适用于只是将 UI 层传来的数据保存到本地的场景，大部分的 Repository 子类无需关心该方法
     */
    protected void saveLocalDataOnly(RequestBody data, RepositoryCallback<ResponseBody> callBack) {
    }

    protected <T> BaseResponse<T> getResponse(T data) {
        return getResponse(data, false);
    }

    protected <T> BaseResponse<T> getResponse(T data, boolean isForPassport) {
        BaseResponse<T> response = new BaseResponse<>();
        if (data != null) {
            response.setStatus(BaseResponse.Success);
            response.setData(data);
        } else {
            response.setIsNetError();
        }
        return response;
    }

    /**
     * 该接口只用于当前类中的回调，外部调用时请使用 RequestCallback
     */
    public interface RepositoryCallback<ResponseBody> {
        default void onSuccess(ResponseBody data) {
        }

        default void onFailure(int errorCode, String errorText) {
        }

        default void onError(Throwable e) {
        }
    }
}
