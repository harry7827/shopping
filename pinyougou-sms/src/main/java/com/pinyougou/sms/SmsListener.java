package com.pinyougou.sms;

import com.pinyougou.sms.utils.SmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SmsListener {
    @Autowired
    private SmsUtils smsUtils;
    @JmsListener(destination = "sms")
    public void sendSMS(Map<String,String> map) {
        System.out.println(map.get("phone")+"......"+map.get("code")+"......param:"+map.get("param"));
        /*try {
            SendSmsResponse response = smsUtils.sendMsg(map.get("phone"), map.get("code"));
            System.out.println("Code=" + response.getCode());
            System.out.println("Message=" + response.getMessage());
            System.out.println("RequestId=" + response.getRequestId());
            System.out.println("BizId=" + response.getBizId());
        } catch (ClientException e) {
            e.printStackTrace();
        }*/
    }
}
