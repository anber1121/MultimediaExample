package com.anber.multimediaexample.http.retrofit;

import android.text.TextUtils;
import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 在此写用途
 *
 * @version V1.0 <描述当前版本功能>
 * @FileName: HttpIntercepterLog.java
 * @author: yh
 * @date: 2016-11-04 15:14
 */

public class HttpIntercepterLog implements Interceptor {

    private static final String TAG = "HttpIntercepterLog";

    private static final Charset UTF8 = Charset.forName("UTF-8");

    public static final String HTTPTIME = "HttpTimeStatistics";

    private static HttpIntercepterLog httpIntercepterLog;

    public static HttpIntercepterLog get() {
        if (httpIntercepterLog == null) {
            httpIntercepterLog = new HttpIntercepterLog();
        }
        return httpIntercepterLog;
    }

    /**
     * 重定向
     */
    private static final int ERROR_301 = 0x012D;

    private static final int ERROR_302 = 0x012E;

    private static final int ERROR_201 = 0x00C9;

    public enum Level {
        /**
         * No logs.
         */
        NONE,
        /**
         * Logs request and response lines.
         * <p>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1 (3-byte body)
         *
         * <-- 200 OK (22ms, 6-byte body)
         * }</pre>
         */
        BASIC,
        /**
         * Logs request and response lines and their respective headers.
         * <p>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
         * }</pre>
         */
        HEADERS,
        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         * <p>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
         * }</pre>
         */
        BODY
    }

    private Level level = Level.NONE;

    /**
     * Change the level at which this interceptor logs.
     */
    public HttpIntercepterLog setLevel(Level level) {
        if (level == null) {
            throw new NullPointerException("level == null. Use Level.NONE instead.");
        }
        this.level = level;
        return this;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Level level = this.level;

        Request request = chain.request();

        String requestUrl = "";

        HttpUrl url = request.url();

        if (url != null) {
        }

        if (level == Level.NONE) {
            return chain.proceed(request);
        }

        boolean logBody = level == Level.BODY;
        boolean logHeaders = logBody || level == Level.HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage = "Request Url:--> " + request.method() + ' ' + request.url() + ' ' + protocol;
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        Log.d(TAG,  requestStartMessage);

        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    Log.d(TAG,  "Content-Type: " + requestBody.contentType());
                }
                if (requestBody.contentLength() != -1) {
                    Log.d(TAG,  "Content-Length: " + requestBody.contentLength());
                }
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    Log.d(TAG,  name + ": " + headers.value(i));
                }
            }

            if (!logBody || !hasRequestBody) {
                Log.d(TAG,  "--> END " + request.method());
            } else if (bodyEncoded(request.headers())) {
                Log.d(TAG,  "--> END " + request.method() + " (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }
                Log.d(TAG,  "");
                if (isPlaintext(buffer)) {

                    String requestBodyString = buffer.readString(charset);
                    Log.d(TAG,  "Request Body:--> " + requestBodyString);
                    Log.d(TAG,  "--> END " + request.method()
                            + " (" + requestBody.contentLength() + "-byte body)");
                } else {
                    Log.d(TAG,  "--> END " + request.method() + " (binary "
                            + requestBody.contentLength() + "-byte body omitted)");
                }
            }
        }

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            Log.d(TAG,  "Response Body:<-- HTTP FAILED: " + e);

            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            Log.d(TAG,  HttpIntercepterLog.HTTPTIME + " [V6]|time:" + tookMs + "ms" + "|" + tookMs / 1000 + "s" + "|" + "Url:" + url);
            throw e;
        }
        Log.d(TAG,  "Response Code = " + response.code());

        //************打印返回 header************

        Headers headers = response.headers();
        if (headers != null) {
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    Log.d(TAG,  "Response hearders || " + name + ": " + headers.value(i));
                }
            }
        }

        /**
         * 获取EPG请求中的Cookie值
         */
        if (!TextUtils.isEmpty(response.header("Set-Cookie"))) {

            String cookie = response.header("Set-Cookie");
            Log.d(TAG,  "[Set Cookie] = " + cookie);
        }


        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        Log.d(TAG,  "Response Body:<-- " + response.code() + ' ' + response.message() + ' '
                + response.request().url() + " (" + tookMs + "ms" + (!logHeaders ? ", "
                + bodySize + " body" : "") + ')');

        Log.d(TAG,  HttpIntercepterLog.HTTPTIME + " [V6]|time:" + tookMs + "ms" + "|" + tookMs / 1000 + "s" + "|" + "Url:" + url);
        if (logHeaders) {
            if (!logBody || !HttpHeaders.hasBody(response)) {
                Log.d(TAG,  "<-- END HTTP");
            } else if (bodyEncoded(response.headers())) {
                Log.d(TAG,  "<-- END HTTP (encoded body omitted)");
            } else {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                /**
                 * 防止没有数据时，json解析失败，抛出异常java.io.EOFException
                 */
                if (response.code() == ERROR_201 && buffer.size() == 0) {
                    buffer.writeUtf8("{ \"message\":\"code = 201\"}");
                }

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        Log.d(TAG,  "");
                        Log.d(TAG,  "Couldn't decode the response body; charset is likely malformed.");
                        Log.d(TAG,  "<-- END HTTP");

                        return response;
                    }
                }

                if (!isPlaintext(buffer)) {
                    Log.d(TAG,  "");
                    Log.d(TAG,  "<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                    return response;
                }

                if (contentLength != 0) {
                    Log.d(TAG,  "");
                    String body = buffer.clone().readString(charset);
                    Log.d(TAG,  "Before Format:" + body);
                }
                Log.d(TAG,  "<-- END HTTP (" + buffer.size() + "-byte body)");
            }
        }

        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }


}