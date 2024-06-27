package com.edu.eduplatform.controllers;

import com.edu.eduplatform.dtos.CreateCommentDTO;
import com.edu.eduplatform.models.Announcement;
import com.edu.eduplatform.models.Comment;
import com.edu.eduplatform.models.User;
import com.edu.eduplatform.repos.AnnouncementRepo;
import com.edu.eduplatform.repos.UserRepo;
import com.edu.eduplatform.services.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class CommentWebSocketController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    UserRepo userRepo;

    @Autowired
    AnnouncementRepo announcementRepo;

    @MessageMapping("/announcement/{announcementId}/comments")
    @SendTo("/topic/announcement/{announcementId}/comments")
    public Comment send(@Payload CreateCommentDTO createCommentDTO,
                        @DestinationVariable Long announcementId) throws Exception {

        System.out.println(createCommentDTO);

        Announcement announcement = announcementRepo.findById(createCommentDTO.getAnnouncementId())
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        // Find the User by userId
        User user = userRepo.findById(createCommentDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Map CreateCommentDTO to Comment entity
        Comment comment = new Comment();
        comment.setContent(createCommentDTO.getCommentContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setAnnouncement(announcement);
        comment.setUser(user);

        // Save the comment
        comment = announcementService.addComment(createCommentDTO);

        return comment;
    }
}


