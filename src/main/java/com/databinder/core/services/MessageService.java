package com.databinder.core.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.databinder.core.dto.MessageResponse;
import com.databinder.core.entities.Message;
import com.databinder.core.entities.User;
import com.databinder.core.enums.MessageStatus;
import com.databinder.core.mapping.ResponseMapper;
import com.databinder.core.repositories.MessageRepository;
import com.databinder.core.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    
    private final AuthenticationService authenticationService;
    
    public MessageResponse createMessage(Long userId, String header, String body) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Message message = new Message();
        message.setToUser(user);
        message.setHeader(header);
        message.setBody(body);

        return ResponseMapper.toResponse(messageRepository.save(message));
    }
    
    public List<MessageResponse> getMyMessages() {
    	User currentUser = authenticationService.getCurrentUser();

        return getMessagesForUser(currentUser.getId());
    }
    

    public List<MessageResponse> getMessagesForUser(Long userId) {
        return messageRepository.findByToUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(ResponseMapper::toResponse)
                .toList();
    }

    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }
    
    
    public MessageResponse updateMessage(Long messageId, MessageStatus status, Boolean read)
    {
    	Message msg = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
    	
    	msg.setStatus(status);
    	
    	if(read != null)
    	{
    		msg.setRead(read);
    	}
    	
    	return ResponseMapper.toResponse(messageRepository.save(msg));
    	
    }
    
    public void save(Message message) {
        messageRepository.save(message);
    }

}