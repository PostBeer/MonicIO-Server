package com.example.monicio.Services;

import com.example.monicio.Models.Message;
import com.example.monicio.Repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;


    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> findMessagesByProject_Id(Long project_id) {
        return messageRepository.findMessagesByProject_Id(project_id);
    }

}