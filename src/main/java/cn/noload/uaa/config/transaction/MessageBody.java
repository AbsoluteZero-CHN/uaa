package cn.noload.uaa.config.transaction;

import java.util.HashMap;
import java.util.Map;

public class MessageBody {

    private String url;

    private HttpMethod httpMethod;

    private Map<String, String> headers = new HashMap<>();

    private Object body;

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /************************语法糖部分************************/
    public MessageBody url(String url) {
        this.url = url;
        return this;
    }

    public MessageBody addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public MessageBody httpMethod (HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public MessageBody body(Object body) {
        this.body = body;
        return this;
    }

    /**
     * `GET` 请求是幂等操作, 所以不需要如此调用
     * */
    public enum HttpMethod {
        POST, PUT, DELETE
    }
}
