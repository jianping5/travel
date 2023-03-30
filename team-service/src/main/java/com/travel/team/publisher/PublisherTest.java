package com.travel.team.publisher;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 生产者
 */
public class PublisherTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void testSendMessage() throws IOException, TimeoutException {
        // 1.建立连接
        ConnectionFactory factory = new ConnectionFactory();
        // 1.1.设置连接参数，分别是：主机名、端口号、vhost、用户名、密码
        factory.setHost("175.178.127.100");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("jianping5");
        factory.setPassword("567");
        // 1.2.建立连接
        Connection connection = factory.newConnection();

        // 2.创建通道Channel
        Channel channel = connection.createChannel();

        // 3.创建队列
        String queueName = "simple.queue";
        channel.queueDeclare(queueName, false, false, false, null);

        // 4.发送消息
        String message = "hello, rabbitmq!";
        channel.basicPublish("", queueName, null, message.getBytes());
        System.out.println("发送消息成功：【" + message + "】");

        // 5.关闭通道和连接
        channel.close();
        connection.close();

    }

    /**
     * 注意：这种方式不会主动创建队列，只会在已有的队列上发送消息
     */

    public void testSimpleQueue() {
        // 队列名称
        String queueName = "simple.queue";

        // 消息
        String message = "hello, spring AMQP!";

        // 发送消息
        rabbitTemplate.convertAndSend(queueName, message);
    }

    /**
     * 此时消息是平均分配给多个消费者的（消费者预取）
     * @throws InterruptedException
     */
    public void testWorkQueue() throws InterruptedException {
        // 队列名称
        String queueName = "simple.queue";

        // 消息
        String message = "hello, message_";

        // 发送消息
        for (int i = 0; i < 50; i++) {
            // 发送消息
            rabbitTemplate.convertAndSend(queueName, message + i);
            Thread.sleep(20);
        }
    }

    /**
     * fanout交换机会将消息分发到每个与之绑定的队列（不受routingKey的限制）
     */
    public void testFanoutExchange() {
        // 交换机名称
        String exchangeName = "itcast.fanout";

        // 消息名称
        String message = "hello, everyone!";

        rabbitTemplate.convertAndSend(exchangeName, "", message);
    }

    /**
     * direct交换机会根据routingKey将消息发送到对应的队列（bindingKey与之一致）
     */
    public void testDirectExchange() {
        // 交换机名称
        String exchangeName = "itcast.direct";

        // 消息名称
        String message = "红色警报！日本乱排核废水，导致海洋生物变异，惊现哥斯拉！";

        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName, "blue", message);
    }

    /**
     * topicExchange的routingKey可以是一个或多个单词（以**.**分隔）
     */
    public void testSendTopicExchange() {
        // 交换机名称
        String exchangeName = "itcast.topic";
        // 消息
        String message = "喜报！孙悟空大战哥斯拉，胜!";
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName, "china.news", message);
    }

    public void testSendMap() {
        // 准备消息
        Map<String,Object> msg = new HashMap<>();
        msg.put("name", "Jack");
        msg.put("age", 21);
        // 发送消息
        rabbitTemplate.convertAndSend("object.queue", msg);
    }
}
