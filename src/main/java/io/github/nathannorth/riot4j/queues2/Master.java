//package io.github.nathannorth.riot4j.queues2;
//
//import io.github.nathannorth.riot4j.queues.LimitedQueue;
//import io.github.nathannorth.riot4j.queues.Retryable;
//import reactor.core.publisher.Mono;
//import reactor.core.publisher.Sinks;
//import reactor.netty.http.client.HttpClient;
//
//import java.time.Duration;
//
//public class Master {
//    public static final Master exampleMaster = new Master();
//
//    private final Sinks.Many<Retryable> in = Sinks.many().multicast().onBackpressureBuffer(1024, false);
//
//    public Master() {
//        in.asFlux() //take a retryable and try it
//                .flatMap(retryable -> retryable.getTry()
//                                .onErrorResume(e -> {
//                                    if(e.equals("global rate limit")) { //if we hit global then we wait and try again
//                                        return Mono.delay(Duration.ofSeconds(10)).flatMap(delay -> retryable.getTry());
//                                    }
//                                    if(e.equals("bucket rate limit")) { //if we hit bucket then we use the bucket handle hotsource to tell our bucket that its stupid
//                                        retryable.bucketHandle().emitNext(Mono.error(new Exception("bucket rate limit")));
//                                        return Mono.empty(); //then we return empty to continue on with our lives
//                                    }
//                                    return Mono.error(e); //other random type of error
//                                })
//                                //we will on reach these on success
//                                .doOnNext(ourTry -> retryable.completed().emitNext(ourTry)) //on success we emit da value to the og requester
//                                .doOnNext(ourTry -> retryable.bucketHandle().emitValue(true)) // we also tell our bucket that we finished the thing so it can send us its next request
//                        , 1)
//                .subscribe();
//    }
//
//    //push new item to master and send back the bucket handle so our bucket can wait for completion
//    public Mono<String> push(Retryable r) {
//        in.emitNext(r, Sinks.EmitFailureHandler.FAIL_FAST);
//        return r.bucketHandle().asMono();
//    }
//}
//class Bucket {
//    private final Sinks.Many<Retryable> in = Sinks.many().multicast().onBackpressureBuffer(1024, false);
//
//    public Bucket() {
//        in.asFlux()
//                .flatMap(retryable -> tryPush(retryable), 1)
//                .subscribe();
//
//    }
//
//    public Mono<Void> tryPush(Retryable r) {
//        return Master.exampleMaster.push(r) //push to master, and get back the bucket handle
//                .onErrorResume(e -> { //if the bucket handle errors then we need to wait and then submit our request to master agian
//                    if(e.equals("bucket rate limit")) {
//                        return Mono.delay(Duration.ofSeconds(10)).flatMap(delay -> tryPush(r));
//                    }
//                    return Mono.error(e); //other error
//                }); //as long as our bucket handle completes we can just move on to our lives
//    }
//
//    //push new item to master and send back the bucket handle so our bucket can wait for completion
//    public Mono<String> push(HttpClient.ResponseReceiver<?> input) {
//        Retryable r = new Retryable(input);
//        in.emitNext(r, Sinks.EmitFailureHandler.FAIL_FAST);
//        return r.completed().asMono();
//    }
//}