package com.makhadoni.customer.service.cache;

import com.makhadoni.customer.service.domain.Customer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CacheService {

    private final ReactiveRedisTemplate<String, Customer> redisTemplate;

    public CacheService(ReactiveRedisTemplate<String, Customer> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Customer> getValue(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public Mono<Boolean> setValue(String key, Customer customer){
        return redisTemplate.opsForValue().set(key, customer);
    }


}
