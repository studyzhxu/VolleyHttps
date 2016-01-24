package com.zhxu.volleyhttps.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.zhxu.volleyhttps.R;
import com.zhxu.volleyhttps.application.VolleyApplication;
import com.zhxu.volleyhttps.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * 版    权：
 * <p/>
 * 作    者：xz
 * <p/>
 * 创建日期：2016/1/20 23:32
 * <p/>
 * 描    述:请求网络类
 */
public class DataRequester {

    private static final String TAG = "DataRequester";

    /**
     * 创建请求类型枚举
     */
    private enum Type{
        HTTP,
        HTTPS_DEFALT,
        HTTPS_SELF_CERTIFIED;
    }

    public enum Method {
        GET,
        POST
    }

    /** String数据请求成功返回接口 */
    public interface StringResponseListener extends Response.Listener<String>{}

    /** Json数据请求成功返回接口 */
    public interface JsonResponseListener extends Response.Listener<JSONObject>{}

    /** JsonArray数据请求成功返回接口 */
    public interface JsonArrayResponseListener extends Response.Listener<JSONArray>{}

    /** 请求失败返回接口 */
    public interface ResponseErrorListener extends Response.ErrorListener{}


    private StringResponseListener mStringResponseListener ;
    private JsonResponseListener mJsonResponseListener ;
    private JsonArrayResponseListener mJsonArrayResponseListener ;
    private ResponseErrorListener mResponseErrorListener ;

    /** 请求队列 从application中获取*/
    private RequestQueue mRequestQueue ;
    /**请求地址*/
    private String url ;
    /**请求方式*/
    private Method method ;


    private JSONObject jsonBody;
    private Map<String,String> mapBody;
    private Map<String,String> headers;
    private String strBody;


    private DataRequester(Context context,Type type){
        if(type == Type.HTTP){
            mRequestQueue = VolleyApplication.getInstance().getRequestQueueWithHttp() ;
        }
        if(type == Type.HTTPS_DEFALT){
            mRequestQueue = VolleyApplication.getInstance().getRequestQueueWithDefaultSsl() ;
        }
        if(type == Type.HTTPS_SELF_CERTIFIED){
            mRequestQueue = VolleyApplication.getInstance().getRequestQueueWithSelfCertifiedSsl() ;
        }
    }

    /**
     * 普通http请求
     * @param context
     * @return
     */
    public static DataRequester withHttp(Context context){
        return new DataRequester(context,Type.HTTP) ;
    }

    /**
     * 默认证书HTTPS请求
     * @param context
     * @return
     */
    public static DataRequester withDefaultHttps(Context context){
        return new DataRequester(context,Type.HTTPS_DEFALT) ;
    }

    /**
     * 自定义证书HTTPS请求
     * @param context
     * @return
     */
    public static DataRequester withSelfCertifiedHttps(Context context){
        return new DataRequester(context,Type.HTTPS_SELF_CERTIFIED) ;
    }

    /**
     * 设置请求url
     * @param url
     * @return
     */
    public DataRequester setUrl(String url){
        this.url = url ;
        return this ;
    }

    /**
     * 设置请求方式
     * @param method
     * @return
     */
    public DataRequester setMethod(Method method){
        this.method = method ;
        return this ;
    }

    /**
     * 设置JsonObject请求体
     * @param body
     * @return
     */
    public DataRequester setBody(JSONObject body) {
        this.jsonBody = body;
        return this;
    }

    /**
     * 设置String请求体
     * @param body
     * @return
     */
    public DataRequester setBody(String body) {
        this.strBody = body;
        return this;
    }

    /**
     * 设置Map请求体
     * @param stringRequestParam
     * @return
     */
    public DataRequester setBody (Map<String,String> stringRequestParam){
        this.mapBody = stringRequestParam;
        return this;
    }

    /**
     * 设置String请求成功监听
     * @param mStringResponseListener
     * @return
     */
    public DataRequester setStringResponseListener(StringResponseListener mStringResponseListener) {
        this.mStringResponseListener = mStringResponseListener;
        return this;
    }

    /**
     * 设置Json请求成功返回监听
     * @param mJsonResponseListener
     * @return
     */
    public DataRequester setJsonResponseListener(JsonResponseListener mJsonResponseListener) {
        this.mJsonResponseListener = mJsonResponseListener;
        return this;
    }

    /**
     * 设置JsonArray请求成功监听
     * @param mJsonArrayResponseListener
     * @return
     */
    public DataRequester setJsonArrayResponseListener (JsonArrayResponseListener mJsonArrayResponseListener){
        this.mJsonArrayResponseListener = mJsonArrayResponseListener;
        return this;
    }

    /**
     * 设置请求出错监听
     * @param mResponseErrorListener
     * @return
     */
    public DataRequester setResponseErrorListener(ResponseErrorListener mResponseErrorListener) {
        this.mResponseErrorListener = mResponseErrorListener;
        return this;
    }

    /**
     * String请求
     */
    public void requestString(){
        StringRequest request = null ;

        if(Method.GET == method){
            request = new StringRequest(
                    Request.Method.GET,
                    url,
                    mStringResponseListener,
                    mResponseErrorListener);
        }
        if(Method.POST == method){
            request = new StringRequest(
                    Request.Method.POST,
                    url,
                    mStringResponseListener,
                    mResponseErrorListener){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return mapBody ;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    if (headers != null){
                        return headers;
                    }else {
                        return super.getHeaders();
                    }
                }
            };
        }
        mRequestQueue.add(request) ;
    }

    /**
     * Json请求
     */
    public void requestJson() {
        JsonObjectRequest request = new JsonObjectRequest( url,
                jsonBody,
                mJsonResponseListener,
                mResponseErrorListener );

        if (jsonBody != null) {
            LogUtils.d(TAG, request.getBody().toString());
        }

        mRequestQueue.add( request );
    }

    /**
     * JsonArray请求
     */
    public void requestJsonArray() {
        JsonArrayRequest request = new JsonArrayRequest (url,
                mJsonArrayResponseListener,
                mResponseErrorListener );

        mRequestQueue.add( request );
    }


    /** 图片显示的控件 */
    private ImageView mImageView ;
    /** 下载图片的缓存 */
    private ImageLoader.ImageCache mCache = null ;
    /** 下载过程过程中显示的图片 */
    private int mDefaultImage ;
    /** 下载失败后显示的图片 */
    private int mFailImage ;
    /** 图片的宽度 */
    private int maxWidth = 0 ;
    /** 图片的高度 */
    private int maxHeight = 0;

    /**
     * 设置图片显示的控件
     * @param iv
     * @return
     */
    public DataRequester setImageView(ImageView iv){
        this.mImageView = iv ;
        return this ;
    }

    /**
     * 设置下载图片过程中显示的图片
     * @param defaultImage
     * @return
     */
    public DataRequester setDafaultImage(int defaultImage){
        this.mDefaultImage = defaultImage ;
        return this ;
    }

    /**
     * 设置下载失败时显示的图片
     * @param failImage
     * @return
     */
    public DataRequester setFailImage(int failImage){
        this.mFailImage = failImage ;
        return this ;
    }

    /**
     * 设置图片的缓存
     * @param cache
     * @return
     */
    public DataRequester setImageCache(ImageLoader.ImageCache cache){
        this.mCache = cache ;
        return this ;
    }

    /**
     * 设置下载图片的最大宽度，默认为0，可以不设置
     * @param width
     * @return
     */
    public DataRequester setImageWidth(int width){
        this.maxWidth = width ;
        return this ;
    }

    /**
     * 设置下载图片的最大高度，默认为0，可以不设置
     * @param height
     * @return
     */
    public DataRequester setImageHeight(int height){
        this.maxHeight = height;
        return this ;
    }


    /**
     * 请求图片
     */
    public void requestImage(){
        if(mCache == null){
            mCache = new ImageLoader.ImageCache() {
                @Override
                public Bitmap getBitmap(String url) {
                    return null;
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {

                }
            } ;
        }
        ImageLoader imageLoader = new ImageLoader(mRequestQueue, mCache);
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(mImageView,mDefaultImage, mFailImage);

        imageLoader.get(url,listener, 200, 200);
    }



}
