package io.github.nathannorth.riot4j.queues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Sinks;

import java.util.concurrent.locks.LockSupport;

public class FailureStrategy {
    private static final Logger log = LoggerFactory.getLogger(FailureStrategy.class);

    //in the dangerous realm of internet code. My understanding is that this just retry's emission when multiple threads cause an error in emission
    public static final Sinks.EmitFailureHandler RETRY_ON_SERIALIZED = ((signalType, emitResult) -> {
        if(emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED) {
            log.warn("Sink overlap failure! Retrying...");
            LockSupport.parkNanos(10); //i think this helps with thread utilization but it probably also counts as busy waiting so no clue!
            return true;
        }
        else {
            return Sinks.EmitFailureHandler.FAIL_FAST.onEmitFailure(signalType, emitResult); //if we dont have a serial error we let the fail fast handler deal with it
        }
    });
}
