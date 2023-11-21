package com.makhadoni.customer.service.config;

import com.makhadoni.customer.service.modules.auth.dto.UserDto;
import com.makhadoni.customer.service.modules.customer.dto.CustomerDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

    @Bean
    public ReactiveRedisTemplate<String , CustomerDto> reactiveCustomerDtoRedisTemplate(ReactiveRedisConnectionFactory connectionFactory){
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<CustomerDto> valueSerializer = new Jackson2JsonRedisSerializer<>(CustomerDto.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, CustomerDto> builder = RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, CustomerDto> context = builder.value(valueSerializer).build();


        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String , UserDto> reactiveUserDtoRedisTemplate(ReactiveRedisConnectionFactory connectionFactory){
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<UserDto> valueSerializer = new Jackson2JsonRedisSerializer<>(UserDto.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, UserDto> builder = RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, UserDto> context = builder.value(valueSerializer).build();


        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }
}
