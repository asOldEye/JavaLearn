package com.OldEye.chat.controller;

import com.OldEye.chat.ChatApplication;
import com.OldEye.chat.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.sql.SQLException;

//контроллер чата, занимается переправкой сообщений между клиентами
@Controller
@Component
public class ChatController {

    //отправляет сообщение всем подключенным к чату пользователям
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    //дополняет аттрибуты сессии нового пользователя его именем
    //отправляет имя пользователя в базу данных
    //отправляет в чат сообщение о его подключении
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        try {
            ChatApplication.databaseHolder.insert(new String[]{"insert into USERS (name) values (" + chatMessage.getSender() + ");"});
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chatMessage;
    }

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    //ловит события отключения клиентов и отправляет сообщение об этом подключенным клиентам через внутренний функционал спринг
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);

            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}