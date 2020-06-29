package com.goldze.base.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.goldze.base.utils.LogUtils;


/**
 * 网络工具类
 * 提供判断网络的相关功能
 *
 * @author wanghaiting wwx190216
 */
public final class NetUtil {
    private static final String TAG = "NetUtil";

    /**
     * 检测网络是否连接
     * 判断当前网络的连接情况
     *
     * @param context 上下文
     * @return boolean success
     */
    public static boolean checkNetWorkStatus(Context context) {
        boolean flag = false;

        //获得网络连接服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo netActiveInfo = null;
        if (connManager != null) {
            netActiveInfo = connManager.getActiveNetworkInfo();
        }
        if (netActiveInfo != null) {
            flag = true;
        }

        return flag;
    }

    /**
     * 判断是否是移动网络
     *
     * @param context
     * @return
     */
    public static boolean isDataTraffic(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo netActiveInfo = null;
        if (connManager != null) {
            netActiveInfo = connManager.getActiveNetworkInfo();
        }
        if (netActiveInfo == null) {
            return false;
        }
        int activeNetType = netActiveInfo.getType();

        return activeNetType != ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 二维码返回text 截取
     *
     * @param key
     * @param url
     * @return
     */
    public static String getQRCodeText(@NonNull String key, @NonNull String url) {
        if (TextUtils.isEmpty(url) || !url.contains(key)) {
            return null;
        }

        String[] splitFirst = url.split("[?]");

        if (splitFirst.length < 2 || splitFirst[1] == null || "".equals(splitFirst[1])) {
            return null;
        }

        String[] splitSecond = splitFirst[1].split("[&]");

        if (splitSecond.length < 1) {
            return null;
        }

        for (String s : splitSecond) {
            if (s.contains(key)) {
                String[] splitThird = s.split("[=]");
                if (splitThird.length > 1) {
                    return splitThird[1];
                }
            }
        }
        return null;
    }

    /**
     * 4G: 1
     * WIFI: 2,
     * 3G: 3
     * 2G: 4
     * 其他: 5
     *
     * @return
     */
    public static int getCurrentNetType(Context context) {
        int strNetworkType = 5;
        if (context == null) {
            return strNetworkType;
        }
        ConnectivityManager systemService = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (systemService == null) {
            return strNetworkType;
        }
        NetworkInfo networkInfo = systemService.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = 2;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String strSubTypeName = networkInfo.getSubtypeName();

                LogUtils.d("Network getSubtypeName : " + strSubTypeName);

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = 4;// "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = 3;//"3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = 1;//"4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if ("TD-SCDMA".equalsIgnoreCase(strSubTypeName) || "WCDMA".equalsIgnoreCase(strSubTypeName) || "CDMA2000".equalsIgnoreCase(strSubTypeName)) {
                            strNetworkType = 3;//"3G";
                        } else {
                            strNetworkType = 3;//"3G";
                        }

                        break;
                }
            }
        }
        return strNetworkType;
    }

    /**
     * 判断当前网络是否为wifi
     *
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = null;
        if (connectivityManager != null) {
            activeNetInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 获取当前网络的类型
     *
     * @param context
     * @return
     */
    public static String getNetName(Context context) {
        /**
         * 4G: 1
         * WIFI: 2,
         * 3G: 3
         * 2G: 4
         * 其他: 5
         */
        int currentNetType = getCurrentNetType(context);

        String netType = "WIFI";

        switch (currentNetType) {
            case 1:
                netType = "4G";
                break;
            case 2:
                netType = "WIFI";
                break;
            case 3:
                netType = "3G";
                break;
            case 4:
                netType = "2G";
                break;
            case 5:
                netType = "其他";
                break;
            default:
                break;
        }

        return netType;
    }
}
