package io.github.nathannorth.riot4j.exceptions;

import reactor.netty.http.client.HttpClientResponse;

public class WebFailure extends RuntimeException {
    private final HttpClientResponse response;
    private final String content;

    public WebFailure(HttpClientResponse response, String content) {
        super("Error code: " + response.status().code());
        this.response = response;
        this.content = content;
    }

    public HttpClientResponse getResponse() {
        return response;
    }
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "WebFailure{" +
                "response=" + response +
                ", content='" + content + '\'' +
                "} " + super.toString();
    }
}