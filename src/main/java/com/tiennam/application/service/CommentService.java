package com.tiennam.application.service;

import com.tiennam.application.entity.Comment;
import com.tiennam.application.model.dto.CommentDTO;
import com.tiennam.application.model.request.CreateCommentPostRequest;
import com.tiennam.application.model.request.CreateCommentProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    List<CommentDTO> getListComment();

    void deleteComment(long id);

    Comment getCommentById(long id);
    Page<Comment> adminListCommentPages(String content, Integer page);

    Comment createCommentPost(CreateCommentPostRequest createCommentPostRequest, long userId);
    Comment createCommentProduct(CreateCommentProductRequest createCommentProductRequest, long userId);
}
