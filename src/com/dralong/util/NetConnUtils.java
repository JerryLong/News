package com.dralong.util;

import android.content.Context;
import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.*;

/**
 * Created by Jerry on 2015/8/26.
 * 定义网络连接工具类
 */
public class NetConnUtils {
    private Context mContext;

    public NetConnUtils(Context context) {
        mContext = context;
    }

    //定义回调接口
    public interface NetInterface {
        public void onCallBack(String result);

        public void onErrorBack(String errorBack);
    }

    //声明回调接口
    public NetInterface mNetInterface;

    public void setNetInterface(NetInterface netInterface) {
        mNetInterface = netInterface;
    }

    public NetConnUtils(NetInterface netInterface) {
        mNetInterface = netInterface;
    }

    /**
     * @param url          请求url地址
     * @param cacheKey     缓存key
     * @param netInterface 回调接口
     */
    public void getResult(String url, String cacheKey, NetInterface netInterface) {
        mNetInterface = netInterface;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String result;
                try {
                    result = resultByGet(url, cacheKey);
                } catch (ExceptionDeal e) {
                    e.printStackTrace();
                    result = null;
                    mNetInterface.onErrorBack(e.getMessage());
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result != null)
                    mNetInterface.onCallBack(result);
            }
        }.execute();
    }

    private String resultByGet(String url, String cacheKey) throws ExceptionDeal {
        String result = "";
        try {
            HttpClient client = new DefaultHttpClient();
//            HttpGet数据请求超时
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
//            HTTPGet数据读取超时
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);
            int returnCode = response.getStatusLine().getStatusCode();
            switch (returnCode) {
                case 200:
                    result = readIt(response.getEntity().getContent());
                    if (mContext != null && cacheKey != null)
                        PreferenceCache.getInstance(mContext).putCache(cacheKey, result);
                    break;
                default:
                    throw new ExceptionDeal("服务器异常！");
            }
        } catch (IOException e) {
            throw new ExceptionDeal("网络连接异常，请检查网络！");
        }
        return result;
    }


    /**
     * @param stream
     * @return
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public String readIt(InputStream stream) throws IOException,
            UnsupportedEncodingException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        // 创建包装流
        BufferedReader br = new BufferedReader(reader);
        // 定义String类型用于储存单行数据
        String line = null;
        // 创建StringBuffer对象用于存储所有数据
        StringBuffer sb = new StringBuffer();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();

    }
}
