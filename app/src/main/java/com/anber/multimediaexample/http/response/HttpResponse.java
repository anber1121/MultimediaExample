package com.anber.multimediaexample.http.response;

public class HttpResponse<T> {
    private int code;

    private T response;

    public int getCode() {
        return code;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccessful() {
        return getResponse() != null && code == 200;
    }
}
