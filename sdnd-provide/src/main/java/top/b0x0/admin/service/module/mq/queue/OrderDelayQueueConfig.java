package top.b0x0.admin.service.module.mq.queue;

import top.b0x0.admin.common.util.constants.RabbitMqConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 延迟队列
 *
 * @author TANG
 */
@Configuration
public class OrderDelayQueueConfig {

    /**
     * 创建 订单延迟队列
     * 设置 x-dead-letter-exchange 参数,
     * 设置 x-dead-letter-routing-key 参数
     * 超时后消息会通过 x-dead-letter-exchange 转发到 x-dead-letter-routing-key绑定的队列中
     */
    @Bean
    public Queue orderDelayQueue() {
        // 设置超时转发策略
        // 超时后消息会通过 x-dead-letter-exchange 转发到 x-dead-letter-routing-key绑定的队列中
        Map<String, Object> arguments = new HashMap<>(2);
        arguments.put("x-dead-letter-exchange", RabbitMqConstants.ORDER_DLX_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", RabbitMqConstants.ORDER_DLK_KEY);
        return QueueBuilder.durable(RabbitMqConstants.ORDER_DELAY_QUEUE).withArguments(arguments).build();
    }

    /**
     * 延迟交换机
     */
    @Bean
    public DirectExchange orderDelayExchange() {
        return new DirectExchange(RabbitMqConstants.ORDER_EXCHANGE, true, false, null);
    }

    /**
     * 死信交换机
     */
    @Bean
    public DirectExchange orderProcessExchange() {
        return new DirectExchange(RabbitMqConstants.ORDER_DLX_EXCHANGE);
    }

    /**
     * 绑定 订单延迟队列 绑定 延迟交换机
     */
    @Bean
    public Binding orderQueueBinding() {
        return BindingBuilder.bind(orderDelayQueue()).to(orderDelayExchange()).with(RabbitMqConstants.ORDER_DLK_KEY);
    }

    /**
     * 创建 超时订单任务处理队列
     */
    @Bean
    public Queue orderTimeoutQueue() {
        return QueueBuilder.durable(RabbitMqConstants.ORDER_TIMEOUT_QUEUE).build();
    }


    /**
     * 绑定 超时订单任务处理队列 绑定 延迟交换机
     */
    @Bean
    public Binding orderTimeoutQueueBinding() {
        return BindingBuilder.bind(orderTimeoutQueue()).to(orderProcessExchange()).with(RabbitMqConstants.ORDER_DLK_KEY);
    }
}
