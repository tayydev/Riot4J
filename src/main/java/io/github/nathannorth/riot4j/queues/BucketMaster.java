package io.github.nathannorth.riot4j.queues;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.HashMap;

/**
 * I'm trying to implement bucketed ratelimiting with an additional master client ratelimit. I make api requests to certain buckets,
 * and if I hit a bucket limit, the bucket stops, but allows other buckets requests to still flow through the master limiter.
 * If the master limiter hits ratelimit, then all requests, halt. Since all requests flow through master, I will only ever
 * have one 429 to the server in the event of a global ratelimit. If I let buckets flow individually we could potentially
 * have up to # of buckets of 429s in the event of hitting master ratelimit.
 *
 * My issue is that I don't know how to have my entire system not halt when i hit a *bucket ratelimit*. Since all items
 * in each bucket are started by the master queue, when one bucket delays the entire system halts.
 */
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
