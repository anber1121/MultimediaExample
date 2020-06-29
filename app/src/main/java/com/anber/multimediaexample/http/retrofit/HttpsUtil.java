package com.anber.multimediaexample.http.retrofit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RawRes;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


/**
 * 在此写用途
 *
 * @version V1.0 <描述当前版本功能>
 * @FileName: com.fmcc.reversion.host.common.utils.netutil.HttpsUtil.java
 * @author: yh
 * @date: 2017-06-14 11:06
 * <p>
 * Android 4.X 对TLS1.1、TLS1.2的支持参考了http://blog.csdn.net/joye123/article/details/53888252
 */

public class HttpsUtil {
    private static final String TAG = "HttpsUtil";

    protected HttpsUtil() {
    }

    /**
     * 包装的 SSL(Secure Socket Layer)参数类
     */
    public static class SSLParams {
        private SSLSocketFactory sSLSocketFactory;
        private X509TrustManager trustManager;

        public SSLSocketFactory getsSLSocketFactory() {
            return sSLSocketFactory;
        }

        public X509TrustManager getTrustManager() {
            return trustManager;
        }
    }

    /**
     * @param context        上下文
     * @param certificatesId "XXX.cer" 文件 (文件位置res/raw/XXX.cer)
     * @param bksFileId      "XXX.bks"文件(文件位置res/raw/XXX.bks)
     * @param password       The certificate's password.
     * @return SSLParams
     */
    public static SSLParams getSslSocketFactory(Context context, @RawRes int[] certificatesId, @RawRes int bksFileId, String password) {
        if (context == null) {
            throw new NullPointerException("context == null");
        }
        SSLParams sslParams = new SSLParams();
        try {
            TrustManager[] trustManagers = null;
            if (certificatesId != null) {
                trustManagers = prepareTrustManager(context, certificatesId);
            }

            KeyManager[] keyManagers = null;
            if (!TextUtils.isEmpty(password)) {
                keyManagers = prepareKeyManager(context, bksFileId, password);
            }


            //创建TLS类型的SSLContext对象，that uses our TrustManager
            String tls = "TLS";
            SSLContext sslContext = SSLContext.getInstance(tls);

            X509TrustManager x509TrustManager;
            if (trustManagers != null) {
                Log.e(TAG,"trustManagers != null");
                x509TrustManager = new MyTrustManager(chooseTrustManager(trustManagers));
            } else {
                Log.e(TAG,"UnSafeTrustManager");
                x509TrustManager = new UnSafeTrustManager();
            }
            //用上面得到的trustManagers初始化SSLContext，这样sslContext就会信任keyStore中的证书
            sslContext.init(keyManagers, new TrustManager[]{x509TrustManager}, null);
            sslParams.sSLSocketFactory = sslContext.getSocketFactory();
            sslParams.trustManager = x509TrustManager;
            return sslParams;
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new AssertionError(e);
        }
    }


    /**
     * 主机名校验方法
     */
    public static HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return hostname.equalsIgnoreCase(session.getPeerHost());
            }
        };
    }

    private static TrustManager[] prepareTrustManager(Context context, int[] certificatesId) {
        if (certificatesId == null || certificatesId.length <= 0) {
            return new TrustManager[9];
        }

        try {
            //创建X.509格式的CertificateFactory
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            // 创建一个默认类型的KeyStore，存储我们信任的证书
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (int certificateId : certificatesId) {
                //从本地资源中获取证书的流
                InputStream cerInputStream = context.getResources().openRawResource(certificateId);
                String certificateAlias = Integer.toString(index++);

                //certificate是java.security.cert.Certificate，而不是其他Certificate
                //证书工厂根据证书文件的流生成证书Certificate
                Certificate certificate = certificateFactory.generateCertificate(cerInputStream);
                //将证书certificate作为信任的证书放入到keyStore中
                keyStore.setCertificateEntry(certificateAlias, certificate);
                cerInputStream.close();
            }

            //TrustManagerFactory是用于生成TrustManager的,这里创建一个默认类型的TrustManagerFactory
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            //用我们之前的keyStore实例初始化TrustManagerFactory，这样trustManagerFactory就会信任keyStore中的证书
            trustManagerFactory.init(keyStore);
            return trustManagerFactory.getTrustManagers();
        } catch (Exception e) {
            Log.e(TAG, "" + e);
        }
        return new TrustManager[0];
    }

    private static KeyManager[] prepareKeyManager(Context context, @RawRes int bksFileId, String password) {

        try {
            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(context.getResources().openRawResource(bksFileId), password.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();

        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | IOException e) {
            Log.e(TAG, "" + e);
        }
        return new KeyManager[0];
    }


    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }


    /**
     * 客户端不对证书做任何检查;
     * 客户端不对证书做任何验证的做法有很大的安全漏洞。
     */
    private static class UnSafeTrustManager implements X509TrustManager {

        @SuppressLint("TrustAllX509TrustManager")
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            Log.d(TAG, "[checkClientTrusted]");
        }

        @SuppressLint("TrustAllX509TrustManager")
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            Log.d(TAG, "[checkServerTrusted]");
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class MyTrustManager implements X509TrustManager {
        private X509TrustManager defaultTrustManager;
        private X509TrustManager localTrustManager;

        private MyTrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException {
            //TrustManagerFactory是用于生成TrustManager的,创建一个默认类型的TrustManagerFactory
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            defaultTrustManager = chooseTrustManager(trustManagerFactory.getTrustManagers());
            this.localTrustManager = localTrustManager;
        }


        @SuppressLint("TrustAllX509TrustManager")
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)  {
            Log.d(TAG, "[checkClientTrusted X509Certificate]");
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                defaultTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException ce) {
                localTrustManager.checkServerTrusted(chain, authType);
            }
        }


        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
