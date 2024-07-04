package com.edu.eduplatform.controllers;

import com.edu.eduplatform.dtos.CreateCommentDTO;
import com.edu.eduplatform.dtos.UserCommentDTO;
import com.edu.eduplatform.dtos.getCommentDTO;
import com.edu.eduplatform.models.Announcement;
import com.edu.eduplatform.models.Comment;
import com.edu.eduplatform.models.User;
import com.edu.eduplatform.repos.AnnouncementRepo;
import com.edu.eduplatform.repos.UserRepo;
import com.edu.eduplatform.services.AnnouncementService;
import com.itextpdf.text.pdf.qrcode.Mode;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import java.time.LocalDateTime;

@Controller
public class CommentWebSocketController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    UserRepo userRepo;

    @Autowired
    AnnouncementRepo announcementRepo;

    @Autowired
    ModelMapper modelMapper;

    @MessageMapping("/announcement/{announcementId}/comments")
    @SendTo("/topic/announcement/{announcementId}/comments")
    public getCommentDTO send(@Payload CreateCommentDTO createCommentDTO,
                              @DestinationVariable Long announcementId) throws Exception {

        Announcement announcement = announcementRepo.findById(createCommentDTO.getAnnouncementId())
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        User user = userRepo.findById(createCommentDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setContent(createCommentDTO.getCommentContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setAnnouncement(announcement);
        comment.setUser(user);

        // Save the comment
        comment = announcementService.addComment(createCommentDTO);

        // Map Comment entity to getCommentDTO
        getCommentDTO commentDTO = modelMapper.map(comment, getCommentDTO.class);

        // Map User details to UserCommentDTO
        UserCommentDTO userDTO = new UserCommentDTO();
        userDTO.setUserId(user.getUserID());
        userDTO.setUserName(user.getUsername());
        commentDTO.setUserCommentDTO(userDTO);

        return commentDTO;
    }
}


