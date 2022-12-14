package com.github.dimka9910.redistest.app.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import lombok.Setter;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableCaching
@EnableScheduling
@EnableRedisRepositories(basePackages = "com.github.dimka9910.redistest.service.redis.repository")
@ConfigurationProperties(prefix = "spring.redis.cluster")
public class RedisConfigLettuce {

    @Setter
    private List<String> nodes;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(50);
        genericObjectPoolConfig.setMinIdle(5);
        genericObjectPoolConfig.setMaxTotal(50);
        genericObjectPoolConfig.setMaxWait(Duration.ofMillis(5000));
        genericObjectPoolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(2000));

        List<RedisNode> listNodes = createNodes();

        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        redisClusterConfiguration.setClusterNodes(listNodes);
        // Configure automated topology refresh.
        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enablePeriodicRefresh(Duration.ofSeconds(60)) // Refresh the topology periodically.
                .enableAllAdaptiveRefreshTriggers() // Refresh the topology based on events.
                .build();

        ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
                // Redis command execution timeout. Only when the command execution times out will a reconnection be triggered using the new topology.
                .timeoutOptions(TimeoutOptions.enabled(Duration.ofSeconds(60)))
                .topologyRefreshOptions(topologyRefreshOptions)
                .build();
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(60))
                .poolConfig(genericObjectPoolConfig)
                .readFrom(ReadFrom.ANY)
                .clientOptions(clusterClientOptions)
                .build();
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisClusterConfiguration, clientConfig);
        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);
        // Use Jackson2JsonRedisSerializer to replace the default JdkSerializationRedisSerializer to serialize and deserialize the Redis value.
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(mapper);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // String serialization of keys
        template.setKeySerializer(stringRedisSerializer);
        // String serialization of hash keys
        template.setHashKeySerializer(stringRedisSerializer);
        // Jackson serialization of values
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // Jackson serialization of hash values
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }


    public List<RedisNode> createNodes(){
        List<RedisNode> listNodes = new ArrayList();
        String[] ipAndPort;
        RedisNode redisNode;

        ipAndPort = nodes.get(0).split(":");
        redisNode = RedisNode.newRedisNode()
                .listeningAt(ipAndPort[0], Integer.parseInt(ipAndPort[1]))
                .withId("1")
                .promotedAs(RedisNode.NodeType.MASTER)
                .build();
        listNodes.add(redisNode);

        ipAndPort = nodes.get(1).split(":");
        redisNode = RedisNode.newRedisNode()
                .listeningAt(ipAndPort[0], Integer.parseInt(ipAndPort[1]))
                .withId("2")
                .promotedAs(RedisNode.NodeType.MASTER)
                .build();
        listNodes.add(redisNode);

        ipAndPort = nodes.get(2).split(":");
        redisNode = RedisNode.newRedisNode()
                .listeningAt(ipAndPort[0], Integer.parseInt(ipAndPort[1]))
                .withId("3")
                .promotedAs(RedisNode.NodeType.MASTER)
                .build();
        listNodes.add(redisNode);

        ipAndPort = nodes.get(3).split(":");
        redisNode = RedisNode.newRedisNode()
                .listeningAt(ipAndPort[0], Integer.parseInt(ipAndPort[1]))
                .withId("4")
                .promotedAs(RedisNode.NodeType.SLAVE)
                .slaveOf("1")
                .build();
        listNodes.add(redisNode);

        ipAndPort = nodes.get(4).split(":");
        redisNode = RedisNode.newRedisNode()
                .listeningAt(ipAndPort[0], Integer.parseInt(ipAndPort[1]))
                .withId("5")
                .promotedAs(RedisNode.NodeType.SLAVE)
                .slaveOf("2")
                .build();
        listNodes.add(redisNode);

        ipAndPort = nodes.get(5).split(":");
        redisNode = RedisNode.newRedisNode()
                .listeningAt(ipAndPort[0], Integer.parseInt(ipAndPort[1]))
                .withId("6")
                .promotedAs(RedisNode.NodeType.SLAVE)
                .slaveOf("3")
                .build();
        listNodes.add(redisNode);

        return listNodes;
    }
}