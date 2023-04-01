package com.travel.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 主题交换机
 *
 * @author jianping5
 * @createDate 28/3/2023 下午 6:26
 */
@Configuration
public class TopicConfig {
    /**
     * 声明交换机
     * @return
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("travel.topic");
    }

    /**
     * 声明第一个队列（用户缓存更新）
     * @return
     */
    @Bean
    public Queue cacheQueue() {
        return new Queue("travel.cache");
    }

    /**
     * 绑定队列 1 到 topic 交换机
     * @param cacheQueue
     * @param topicExchange
     * @return
     */
    @Bean
    public Binding bindingQueue1(Queue cacheQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(cacheQueue).to(topicExchange).with("cache.#");
    }

    /**
     * 声明第二个队列
     * @return
     */
    @Bean
    public Queue teamQueue() {
        return new Queue("travel.team");
    }

    /**
     * 绑定队列 2 到 topic 交换机
     * @param teamQueue
     * @param topicExchange
     * @return
     */
    @Bean
    public Binding bindingQueue2(Queue teamQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(teamQueue).to(topicExchange).with("team.#");
    }

    /**
     * 声明第三个队列
     * @return
     */
    @Bean
    public Queue derivativeQueue() {
        return new Queue("travel.derivative");
    }

    /**
     * 绑定队列 3 到 topic 交换机
     * @param derivativeQueue
     * @param topicExchange
     * @return
     */
    @Bean
    public Binding bindingQueue3(Queue derivativeQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(derivativeQueue).to(topicExchange).with("cache.#");
    }

}