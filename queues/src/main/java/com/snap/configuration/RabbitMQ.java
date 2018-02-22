package com.snap.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQ
{

    @Value( "${rabbit.queue.name}" )
    public String QUEUE_NAME;

    @Value( "${rabbit.routing.key.name}" )
    public String ROUTING_KEY;

    @Value( "${rabbit.exchange.name}" )
    public String EXCHANGE_NAME;

    @Value( "${rabbit.queue.delay.time.minutes}" )
    public int DELAY_TIME;

    @Value( "${spring.rabbitmq.is_durable_queue}" )
    private boolean IS_DURABLE_QUEUE;

    public static final String XDELAYED_TYPE = "x-delayed-type";

    public static final String XDELAYED_TYPE_VALUE = "direct";

    public static final String XDELAYED_MESSAGE = "x-delayed-message";

    public static final String XDELAY = "x-delay";

    public Map<String, Object> args = new HashMap<String, Object>();

    @Bean
    public Queue queue()
    {
        return new Queue( QUEUE_NAME, IS_DURABLE_QUEUE );
    }

    @Bean
    public CustomExchange exchange()
    {
        args.put( XDELAYED_TYPE, XDELAYED_TYPE_VALUE );
        return new CustomExchange( EXCHANGE_NAME, XDELAYED_MESSAGE, IS_DURABLE_QUEUE, false, args );
    }

    @Bean
    public Binding binding( Queue queue, Exchange exchange )
    {
        return BindingBuilder.bind( queue ).to( exchange ).with( ROUTING_KEY ).and( args );
    }

    @Bean
    public SimpleMessageListenerContainer container( ConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter )
    {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory( connectionFactory );
        container.setQueueNames( QUEUE_NAME );
        container.setMessageListener( listenerAdapter );
        return container;
    }
    
    @Bean
    public MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, Receiver.RECEIVE_METHOD_NAME);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setRoutingKey(ROUTING_KEY);
        template.setQueue(QUEUE_NAME);
        template.setExchange(EXCHANGE_NAME);
        template.setBeforePublishPostProcessors(setMessagePostProcessor());
        return template;
    }

    public MessagePostProcessor setMessagePostProcessor() {
        int milleseconds = (int) TimeUnit.MINUTES.toMillis(DELAY_TIME);
        return new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message arg0) throws AmqpException {
                arg0.getMessageProperties().setHeader(RabbitMQ.XDELAY, milleseconds);
                return arg0;
            }
        };
    }
}
