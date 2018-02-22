package com.snap.configuration.rabbit;

import org.springframework.stereotype.Component;

@Component
public class Receiver
{
	 public static final String RECEIVE_METHOD_NAME = "receiveMessage";
	 
    public void receiveMessage(String message) {
        System.out.println("Received <" + message + ">");
    }

}
