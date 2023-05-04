package com.example.demo;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(), "/ws").setAllowedOrigins("*");
    }

    private static class WebSocketHandler extends TextWebSocketHandler {

        private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        private ScheduledFuture<?> pingTask;


        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            pingTask = executor.scheduleAtFixedRate(() -> {
                if (session.isOpen()) {
                    try {
                        System.out.println("Session status:"+session.isOpen());
                        session.sendMessage(new TextMessage("ping"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    pingTask.cancel(true);
                    System.out.println("Session status:"+session.isOpen());
                    System.out.println("Stop Pinging");
                }
            }, 5, 30, TimeUnit.SECONDS);
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            System.out.println("Connection Closed");
            pingTask.cancel(true);
        }

        @Override
        public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            System.out.println("Received message from client: " + message.getPayload());
//            session.sendMessage(new TextMessage("Received message: " + message.getPayload()));
        }
    }
}
