package com.makhadoni.customer.service.cache;

import com.makhadoni.customer.service.modules.auth.dto.UserDto;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
public class UserCacheService {

    private final ReactiveRedisTemplate<String, UserDto> redisTemplate;

    public UserCacheService(ReactiveRedisTemplate<String, UserDto> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<UserDto> getValue(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public Flux<UserDto> getList(String key){
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public Mono<Boolean> setValue(String key, UserDto userDto, Duration timeout){
        return redisTemplate.opsForValue().set(key, userDto, timeout);
    }

    public Mono<Long> setList(String key, List<UserDto> userDtos, Duration timeout){
        return redisTemplate.opsForList().rightPushAll(key, userDtos).timeout(timeout);
    }

    public Mono<Long> evictByKey(String key){
        return redisTemplate.delete(redisTemplate.keys(key));
    }

    public Mono<Long> evictAll(){
        return redisTemplate.delete(redisTemplate.keys("*"));
    }


}
