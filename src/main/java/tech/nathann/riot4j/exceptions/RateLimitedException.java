package tech.nathann.riot4j.exceptions;

import reactor.netty.http.client.HttpClientResponse;

public class RateLimitedException extends WebException {
    private final int secs;
    private final boolean isMethod;

    public RateLimitedException(HttpClientResponse response, String content) {
        super(response, content);
        this.secs = Integer.parseInt(response.responseHeaders().get("Retry-After"));
        this.isMethod = response.responseHeaders().get("X-Rate-Limit-Type").equals("method");
    }

    public int getSecs() {
        return secs;
    }

    public boolean isMethod() {
        return isMethod;
    }
}