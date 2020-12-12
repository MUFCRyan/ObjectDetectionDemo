package com.mufcryan.objectdetectiondemo.net;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mufcryan.objectdetectiondemo.net.rx_calladapter.RxJava3CallAdapterFactory;

import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * single instance, includeing many type retrofit.
 * please just use the right one.
 */
public class RetrofitClient {

    private static final String TAG = RetrofitClient.class.getSimpleName();
    private static final String BASE_HOST = "http://192.168.43.20:10080"; //不应该使用base，在api中配置好
    //private static final String BASE_HOST = "http://140.143.145.153:10080";

    private Retrofit defaultClient;
    private Retrofit cacheClient;
    private Retrofit uploadClient;
    private Retrofit downloadClient;
    private Retrofit sslClient;
    private Retrofit pollClient;

    public static final int DEFALUT_CLIENT_TYPE = 0;
    public static final int UPLOAD_CLIENT_TYPE = 1;
    public static final int DOWNLOAD_CLIENT_TYPE = 2;
    public static final int CACHE_CLIENT_TYPE = 3;
    public static final int SSL_CLIENT_TYPE = 4;
    public static final int POLL_CLIENT_TYPE = 5;

    final Map<Integer, List<Interceptor>> interceptorMap = new HashMap<>();
    final Map<Integer, List<Interceptor>> networkInterceptorMap = new HashMap<>();

    private static class ConverterHolder {

        static Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        private static GsonConverterFactory INSTANCE = GsonConverterFactory.create(gson);
    }

    private static class CallRxjava2AdapterHolder {
        private static RxJava3CallAdapterFactory INSTANCE = RxJava3CallAdapterFactory.create();
    }

    private static class SingletonHolder {
        private static RetrofitClient INSTANCE = new RetrofitClient();
    }

    public static RetrofitClient getInstance() {
        return SingletonHolder.INSTANCE;
    }


    private RetrofitClient() {
    }

    public OkHttpClient getOkHttpClient(int clientType) {
        List<Interceptor> interceptors;
        List<Interceptor> networkInterceptors;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        switch (clientType) {
            case UPLOAD_CLIENT_TYPE:
                interceptors = interceptorMap.get(UPLOAD_CLIENT_TYPE);
                networkInterceptors = networkInterceptorMap.get(UPLOAD_CLIENT_TYPE);
                builder.retryOnConnectionFailure(true);
                builder.connectTimeout(10, TimeUnit.SECONDS);
                builder.writeTimeout(30, TimeUnit.SECONDS);
                builder.readTimeout(30, TimeUnit.SECONDS);
                break;
            case DOWNLOAD_CLIENT_TYPE:
                interceptors = interceptorMap.get(DOWNLOAD_CLIENT_TYPE);
                networkInterceptors = networkInterceptorMap.get(DOWNLOAD_CLIENT_TYPE);
                builder.retryOnConnectionFailure(true);
                builder.connectTimeout(10, TimeUnit.SECONDS);
                builder.writeTimeout(30, TimeUnit.SECONDS);
                builder.readTimeout(30, TimeUnit.SECONDS);
                break;
            case CACHE_CLIENT_TYPE:
                interceptors = interceptorMap.get(CACHE_CLIENT_TYPE);
                networkInterceptors = networkInterceptorMap.get(CACHE_CLIENT_TYPE);
                builder.connectTimeout(5, TimeUnit.SECONDS);
                builder.writeTimeout(5, TimeUnit.SECONDS);
                builder.readTimeout(30, TimeUnit.SECONDS);
                break;
            case SSL_CLIENT_TYPE:
                //TODO: add cert verify，request sign md5 here
                interceptors = interceptorMap.get(SSL_CLIENT_TYPE);
                networkInterceptors = networkInterceptorMap.get(SSL_CLIENT_TYPE);
                builder.connectTimeout(10, TimeUnit.SECONDS);
                builder.writeTimeout(30, TimeUnit.SECONDS);
                builder.readTimeout(30, TimeUnit.SECONDS);
                break;
            case POLL_CLIENT_TYPE:
                interceptors = interceptorMap.get(DEFALUT_CLIENT_TYPE);
                networkInterceptors = networkInterceptorMap.get(DEFALUT_CLIENT_TYPE);
                builder.connectTimeout(20, TimeUnit.SECONDS);
                builder.writeTimeout(5, TimeUnit.SECONDS);
                builder.readTimeout(80, TimeUnit.SECONDS);
                break;
            default:
                interceptors = interceptorMap.get(DEFALUT_CLIENT_TYPE);
                networkInterceptors = networkInterceptorMap.get(DEFALUT_CLIENT_TYPE);
                builder.connectTimeout(10, TimeUnit.SECONDS);
                builder.writeTimeout(30, TimeUnit.SECONDS);
                builder.readTimeout(30, TimeUnit.SECONDS);
                break;
        }

        if(interceptors != null) {
            for (Interceptor interceptor:interceptors) {
                builder.addInterceptor(interceptor);
            }
        }
        if(networkInterceptors != null) {
            for (Interceptor interceptor:networkInterceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        }

        builder.retryOnConnectionFailure(true);

        SSLSocketFactory factory = createExpiredSSLSocketFactory();
        if (factory != null) {
            builder.sslSocketFactory(factory);
        }
        return builder.build();
    }

    private Retrofit.Builder getBaseBuilder(String hostUrl) {
        String host = (hostUrl != null && hostUrl.length() > 0) ? hostUrl : BASE_HOST;
        return new Retrofit.Builder().addConverterFactory(ConverterHolder.INSTANCE)
                .addCallAdapterFactory(CallRxjava2AdapterHolder.INSTANCE).baseUrl(host);
    }

    public Retrofit getDefaultRetrofit(String hostUrl) {
        if (defaultClient == null) {

            defaultClient = getBaseBuilder(hostUrl)
                    .client(getOkHttpClient(DEFALUT_CLIENT_TYPE)).build();
        }
        return defaultClient;
    }


    public Retrofit getCacheRetrofit(String hostUrl) {
        if (cacheClient == null) {
            cacheClient = getBaseBuilder(hostUrl)
                    .client(getOkHttpClient(CACHE_CLIENT_TYPE)).build();
        }
        return cacheClient;
    }

    public Retrofit getUploadRetrofit(String hostUrl) {
        if (uploadClient == null) {
            uploadClient = getBaseBuilder(hostUrl)
                    .client(getOkHttpClient(UPLOAD_CLIENT_TYPE)).build();
        }
        return uploadClient;
    }

    public Retrofit getDownloadRetrofit(String hostUrl) {
        if (downloadClient == null) {
            downloadClient = getBaseBuilder(hostUrl)
                    .client(getOkHttpClient(DOWNLOAD_CLIENT_TYPE)).build();
        }
        return downloadClient;
    }

    public Retrofit getPollRetrofit(String hostUrl) {
        if (pollClient == null) {

            pollClient = getBaseBuilder(hostUrl)
                    .client(getOkHttpClient(POLL_CLIENT_TYPE)).build();
        }
        return pollClient;
    }

    /**
     * 获取Https请求的Retrofit
     * @param hostUrl
     * @return
     */
    public Retrofit getSafeRetrofit(String hostUrl) {

        return getSafeRetrofit(hostUrl, 0);
    }

    public synchronized Retrofit getSafeRetrofit(String hostUrl, int port) {

        // when hostUrl is null, retrofit okhttp will use default host,
        // but Retrofit api can use header(with BaseUrlInterceptor.API_HOST_KEY) to set own host for okhttp.
        if (hostUrl != null && !hostUrl.startsWith("https")) {
            hostUrl = "https:" + hostUrl.split(":")[1];
        }

        if(port > 0) {
            hostUrl = hostUrl + ":" + port;
        }

        if (sslClient == null) {
            sslClient = getBaseBuilder(hostUrl)
                    .client(getOkHttpClient(SSL_CLIENT_TYPE)).build();
        }
        return sslClient;
    }

    /**
     * @param clientType , 5 type, see DEFALUT_CLIENT_TYPE
     * @param interceptor
     * @return
     */
    public synchronized RetrofitClient addInterceptor(int clientType, Interceptor interceptor) {
        if (interceptor == null || clientType < DEFALUT_CLIENT_TYPE || clientType > SSL_CLIENT_TYPE ) {
            throw new IllegalArgumentException("error: Interceptor = " + interceptor + "  or clientType = " + clientType);
        }

        List<Interceptor> interceptors = interceptorMap.get(clientType);
        if(interceptors != null) {
            for (Interceptor tInterceptor:interceptors) {
                if(tInterceptor.getClass().equals(interceptor.getClass())) {
                    return this;
                }
            }

            interceptors.add(interceptor);
        } else {
            interceptors = new ArrayList<>();
            interceptors.add(interceptor);
            interceptorMap.put(clientType, interceptors);
        }

        switch (clientType){
            case UPLOAD_CLIENT_TYPE:
                uploadClient = null;
                break;
            case DOWNLOAD_CLIENT_TYPE:
                downloadClient = null;
                break;
            case CACHE_CLIENT_TYPE:
                cacheClient = null;
                break;
            case SSL_CLIENT_TYPE:
                sslClient = null;
                break;
            default:
                defaultClient = null;
                break;
        }

        return this;
    }

    /**
     *
     * @param clientType , 5 type, see DEFALUT_CLIENT_TYPE
     * @param interceptor
     * @return
     */
    public synchronized RetrofitClient addNetworkInterceptor(int clientType, Interceptor interceptor) {
        if (interceptor == null || clientType < DEFALUT_CLIENT_TYPE || clientType > SSL_CLIENT_TYPE ) {
            throw new IllegalArgumentException("error: NetworkInterceptor = " + interceptor + "  or clientType = " + clientType);
        }

        List<Interceptor> interceptors = networkInterceptorMap.get(clientType);
        if(interceptors != null) {
            for (Interceptor tInterceptor:interceptors) {
                if(tInterceptor.getClass().equals(interceptor.getClass())) {
                    return this;
                }
            }

            interceptors.add(interceptor);
        } else {
            interceptors = new ArrayList<>();
            interceptors.add(interceptor);
            networkInterceptorMap.put(clientType, interceptors);
        }

        switch (clientType){
            case UPLOAD_CLIENT_TYPE:
                uploadClient = null;
                break;
            case DOWNLOAD_CLIENT_TYPE:
                downloadClient = null;
                break;
            case CACHE_CLIENT_TYPE:
                cacheClient = null;
                break;
            case SSL_CLIENT_TYPE:
                sslClient = null;
                break;
            default:
                defaultClient = null;
                break;
        }

        return this;
    }

    private SSLSocketFactory createExpiredSSLSocketFactory() {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);
            TrustManager[] trustManagers = tmf.getTrustManagers();
            final X509TrustManager origTrustmanager = (X509TrustManager) trustManagers[0];

            TrustManager[] wrappedTrustManagers = new TrustManager[]{ new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return origTrustmanager.getAcceptedIssuers();
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                            origTrustmanager.checkClientTrusted(certs, authType);
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                            try {
                                origTrustmanager.checkServerTrusted(certs, authType);
                            } catch (CertificateException e2) {
                                if (e2.getCause() == null) {
                                    throw new CertificateException();
                                } else if (!"timestamp check failed".equals(e2.getCause().getMessage())
                                        && !(e2.getCause() instanceof CertificateExpiredException)
                                        && !(e2 instanceof CertificateExpiredException)) {
                                    throw new CertificateException();
                                }
                            }
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, wrappedTrustManagers, new SecureRandom());

            SSLSocketFactory ssfFactory = sc.getSocketFactory();
            return ssfFactory;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
