package com.zhxu.volleyhttps.net;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * 版    权：
 * <p/>
 * 作    者：xz
 * <p/>
 * 创建日期：2016/1/20 22:50
 * <p/>
 * 描    述:SSLSocketFactory工厂类
 */
public class SelfSSLSocketFactory {

    /**
     * 获取SSLSocketFactory
     * @param context
     * @return
     */
    public static SSLSocketFactory getSSLSocketFactory(Context context) {
        try {
            return setCertificates(context, context.getAssets().open(CertificateConfig.trustStoreFileName)) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null ;
    }


    /**
     * 产生SSLSocketFactory
     * @param context
     * @param certificates
     * @return
     */
    private static SSLSocketFactory setCertificates(Context context,InputStream... certificates){
        try{
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates){
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

                try{
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e){
                    e.printStackTrace() ;
                }
            }

            //取得SSL的SSLContext实例
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.
                    getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            //初始化keystore
            KeyStore clientKeyStore = KeyStore.getInstance(CertificateConfig.KEY_STORE_TYPE_BKS);
            clientKeyStore.load(context.getAssets().open(CertificateConfig.keyStoreFileName), CertificateConfig.keyStorePassword.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, CertificateConfig.trustStorePassword.toCharArray());

            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory() ;

        } catch (Exception e){
            e.printStackTrace();
        }
        return null ;
    }
}
