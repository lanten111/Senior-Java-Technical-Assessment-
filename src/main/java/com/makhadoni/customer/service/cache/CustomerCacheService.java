package com.makhadoni.customer.service.cache;

import com.makhadoni.customer.service.modules.customer.dto.CustomerDto;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
public class CustomerCacheService {

    private final ReactiveRedisTemplate<String, CustomerDto> redisTemplate;

    public CustomerCacheService(ReactiveRedisTemplate<String, CustomerDto> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<CustomerDto> getValue(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public Flux<CustomerDto> getList(String key){
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public Mono<Boolean> setValue(String key, CustomerDto customerDto, Duration timeout){
        return redisTemplate.opsForValue().set(key, customerDto, timeout);
    }

    public Mono<Long> setList(String key, List<CustomerDto> customerDtos, Duration timeout){
        return redisTemplate.opsForList().rightPushAll(key, customerDtos).timeout(timeout);
    }

    public Mono<Long> evictByKey(String key){
        return redisTemplate.delete(redisTemplate.keys(key));
    }

    public Mono<Void> evictAll(){
        return redisTemplate.keys("*")
                .flatMap(this::evictByKey)
                .then();
    }


}
