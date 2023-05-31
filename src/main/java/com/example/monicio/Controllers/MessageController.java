package com.example.monicio.Controllers;

import com.example.monicio.Models.Message;
import com.example.monicio.Services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin(origins = "*")
public class MessageController {
    @Autowired
    private MessageService messageService;


    @MessageMapping("chat.send/{projectId}")
    @SendTo("/topic/{projectId}")
    public Message sendMessage(@Payload Message message) {
        return messageService.save(message);
    }

    @ResponseBody
    @GetMapping("/api/messages/project/{projectId}")
    List<Message> getAllProjectMessages(@PathVariable Long projectId) {
        return messageService.findMessagesByProject_Id(projectId);
    }

}