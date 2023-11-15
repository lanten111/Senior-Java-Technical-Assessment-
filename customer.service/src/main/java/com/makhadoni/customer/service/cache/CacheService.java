package com.makhadoni.customer.service.cache;

import com.makhadoni.customer.service.domain.Customer;
import com.makhadoni.customer.service.dto.CustomerDto;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CacheService {

    private final ReactiveRedisTemplate<String, CustomerDto> redisTemplate;

    public CacheService(ReactiveRedisTemplate<String, CustomerDto> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<CustomerDto> getValue(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public Flux<CustomerDto> getList(String key){
        return redisTemplate.opsForList().range(key, 0, -1);
    }
    public Mono<Boolean> setValue(String key, CustomerDto customerDto){
        return redisTemplate.opsForValue().set(key, customerDto);
    }
    public Mono<Long> setList(String key, List<CustomerDto> customerDtos){
        return redisTemplate.opsForList().rightPushAll(key, customerDtos);
    }


}
