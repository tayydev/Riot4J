package io.github.nathannorth.riot4j.queues;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.HashMap;

public class BucketMaster {
    private final QueueOfQueues master = new QueueOfQueues();
    private final HashMap<String, WebRequestQueue> buckets = new HashMap<>(); //todo replace string with enum

    public Mono<String> pushToBucket(String bucketName, HttpClient.ResponseReceiver<?> input) {
        WebRequestQueue bucket = buckets.computeIfAbsent(bucketName,
                key -> buckets.put(key, new WebRequestQueue())
        );
        return master.push(bucket.push(input));
    }
}
