package tech.nathann.riot4j.queues.nlimiter;

import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.codec.EncoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.PrematureCloseException;
import tech.nathann.riot4j.exceptions.RetryableException;
import tech.nathann.riot4j.exceptions.WebFailure;

import java.nio.charset.StandardCharsets;

public class Request {
    private static final Logger log = LoggerFactory.getLogger(Request.class);

    private final HttpClient.ResponseReceiver<?> httpRequest;
    private final Sinks.One<String> callback = Sinks.one();

    public Request(HttpClient.ResponseReceiver<?> httpRequest) {
        this.httpRequest = httpRequest;
    }

    public Mono<String> getRequest() {
        return httpRequest.responseSingle(((response, byteBufMono) -> {
            Mono<String> content = byteBufMono.asString(StandardCharsets.UTF_8);

            log.debug("Headers for request: " + response.responseHeaders());

            if(response.status().code() / 100 == 2) return content;
            else return content
                    .switchIfEmpty(Mono.just(""))
                    .flatMap(data -> Mono.error(WebFailure.of(response, data)));
        })).onErrorResume(error -> {
            if(error instanceof PrematureCloseException || error instanceof ConnectTimeoutException || error instanceof EncoderException) {
                log.warn("Converting Netty error " + error.getMessage() + " to empty RetryableException");
                return Mono.error(new RetryableException());
            }
            else return Mono.error(error);
        });
    }

    public Sinks.One<String> getCallback() {
        return callback;
    }

    public Mono<String> getResponse() {
        return callback.asMono();
    }
}
