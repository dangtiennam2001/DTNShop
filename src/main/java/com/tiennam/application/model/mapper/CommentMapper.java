package com.tiennam.application.model.mapper;

import com.tiennam.application.entity.Comment;
import com.tiennam.application.model.dto.CommentDTO;

public class CommentMapper {
    public static CommentDTO toCommentDTO(Comment comment){
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());

        return commentDTO;
    }
}
