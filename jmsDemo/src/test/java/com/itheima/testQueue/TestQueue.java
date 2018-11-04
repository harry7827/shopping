package com.itheima.testQueue;

import com.itheima.jmsTemplate.QueueProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext-jms-queue.xml")
public class TestQueue {
    @Autowired
    private QueueProducer queueProducer;
    @Test
    public void testSend(){
        queueProducer.sendTextMessage("SpringJms-点对点");
    }
    @Test
    public void testQueue(){
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
