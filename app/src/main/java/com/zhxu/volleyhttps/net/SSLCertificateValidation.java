package com.zhxu.volleyhttps.net;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 版    权：
 * <p/>
 * 作    者：xz
 * <p/>
 * 创建日期：2016/1/20 22:45
 * <p/>
 * 描    述:默认证书信任所有证书类
 */
public class SSLCertificateValidation {

    /**
     * 信任所有证书
     */
    public static void trustAllCertificate() {

        try {
            //设置TLS方式
            SSLContext sslc = SSLContext.getInstance("TLS");

            //new一个自定义的TrustManager数组，自定义类中不做任何实现
            TrustManager[] trustManagers = {new NullX509TrustManager()};
            sslc.init(null,trustManagers,null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostnameVerifier());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //自定义类实现X509TrustManager接口，但方法不做实现
    private static class NullX509TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    //自定义类实现HostnameVerifier接口，但方法verify直接返回true，默认信任所有
    private static class NullHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
