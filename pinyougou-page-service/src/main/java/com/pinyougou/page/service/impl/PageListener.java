package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class PageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;

    private Logger logger=Logger.getLogger(PageListener.class);

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            System.out.println("接收到消息："+textMessage);
            long goodsId = Long.parseLong(textMessage.getText());
            itemPageService.genItemHtml(goodsId);
            System.out.println("模板生成成功！");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
