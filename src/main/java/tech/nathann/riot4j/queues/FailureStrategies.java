package tech.nathann.riot4j.queues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Sinks;
import reactor.netty.http.client.HttpClientResponse;
import tech.nathann.riot4j.exceptions.RateLimitedException;
import tech.nathann.riot4j.exceptions.RetryableException;
import tech.nathann.riot4j.exceptions.WebException;

import java.util.concurrent.locks.LockSupport;

public class FailureStrategies {
    private static final Logger log = LoggerFactory.getLogger(FailureStrategies.class);

    //in the dangerous realm of internet code. My understanding is that this just retry's emission when multiple threads cause an error in emission
    public static final Sinks.EmitFailureHandler RETRY_ON_SERIALIZED = ((signalType, emitResult) -> {
        if(emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED) {
            log.debug("Sink overlap failure! Retrying...");
            LockSupport.parkNanos(10); //i think this helps with thread utilization but it probably also counts as busy waiting so no clue!
            return true;
        }
        else {
            return Sinks.EmitFailureHandler.FAIL_FAST.onEmitFailure(signalType, emitResult); //if we dont have a serial error we let the fail fast handler deal with it
        }
    });

    public static RuntimeException makeWebException(HttpClientResponse response, String content) {
        //if ratelimited
        if(response.status().code() == 429) {
            return new RateLimitedException(response, content);
        }

        //else if webfail
        WebException error = new WebException(response, content);
        if(response.status().code() / 100 == 5) {
            return new RetryableException(error);
        }
        return error;
    }
}
