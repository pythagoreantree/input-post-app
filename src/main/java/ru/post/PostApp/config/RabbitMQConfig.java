package ru.post.PostApp.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "posts.exchange";
    public static final String ROUTING_KEY = "post.created";
    public static final String QUEUE_NAME = "post_created.queue";

    public static final String DLX_EXCHANGE = "posts.dlx";
    public static final String DLX_ROUTING_KEY = "post.created.dlq";
    public static final String DLX_QUEUE = "post_created.dlq";

    @Bean
    public DirectExchange postsExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue postsQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(postsQueue())
                .to(postsExchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public DirectExchange postsDeadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    @Bean
    public Queue postsDeadLetterQueue() {
        return QueueBuilder.durable(DLX_QUEUE)
                .build();
    }

    @Bean
    public Binding postsDeadLetterBinding() {
        return BindingBuilder
                .bind(postsDeadLetterQueue())
                .to(postsDeadLetterExchange())
                .with(DLX_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public ConnectionFactory connectionFactory(RabbitProperties properties) {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(properties.getHost());
        factory.setPort(properties.getPort());
        factory.setUsername(properties.getUsername());
        factory.setPassword(properties.getPassword());
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         JacksonJsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}