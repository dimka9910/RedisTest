//package com.github.dimka9910.redistest.app.config;
//
//import lombok.Setter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisClusterConfiguration;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
//import org.springframework.data.redis.serializer.GenericToStringSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//import org.springframework.scheduling.annotation.EnableScheduling;
//
//import java.util.List;
//
//
//@Configuration
//@EnableCaching
//@EnableScheduling
//@EnableRedisRepositories(basePackages = "com.github.dimka9910.redistest.service.redis.repository")
//@ConfigurationProperties(prefix = "spring.redis.cluster")
//public class RedisConfig {
//
//    @Setter
//    private List<String> nodes;
//
//    @Bean
//    public JedisConnectionFactory jedisConnectionFactory() {
//
//        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
//        config.setHostName("172.23.0.2");
//        config.setPort(4025);
//
////        new JedisClusterConnection()
//        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(nodes);
//        return new JedisConnectionFactory(redisClusterConfiguration);
////        return new JedisConnectionFactory(config);
//    }
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(){
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(jedisConnectionFactory());
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        return redisTemplate;
//    }
//}