package com.tiennam.application.service.impl;

import com.tiennam.application.exception.InternalServerException;
import com.tiennam.application.exception.NotFoundException;
import com.tiennam.application.model.dto.CommentDTO;
import com.tiennam.application.model.mapper.CommentMapper;
import com.tiennam.application.repository.CommentRepository;
import com.tiennam.application.entity.Comment;
import com.tiennam.application.entity.Post;
import com.tiennam.application.entity.Product;
import com.tiennam.application.entity.User;
import com.tiennam.application.model.request.CreateCommentPostRequest;
import com.tiennam.application.model.request.CreateCommentProductRequest;
import com.tiennam.application.service.CommentService;
import com.tiennam.application.config.Contant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Comment createCommentPost(CreateCommentPostRequest createCommentPostRequest, long userId) {
        Comment comment = new Comment();
        Post post = new Post();
        post.setId(createCommentPostRequest.getPostId());
        comment.setPost(post);
        User user = new User();
        user.setId(userId);
        comment.setUser(user);
        comment.setContent(createCommentPostRequest.getContent());
        comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        try {
            commentRepository.save(comment);
        } catch (Exception e) {
            throw new InternalServerException("Có lỗi trong quá trình bình luận!");
        }
        return comment;
    }

    @Override
    public Comment createCommentProduct(CreateCommentProductRequest createCommentProductRequest, long userId) {
        Comment comment = new Comment();
        comment.setContent(createCommentProductRequest.getContent());
        comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        User user = new User();
        user.setId(userId);
        comment.setUser(user);
        Product product = new Product();
        product.setId(createCommentProductRequest.getProductId());
        comment.setProduct(product);
        try {
            commentRepository.save(comment);
        } catch (Exception e) {
            throw new InternalServerException("Có lỗi trong quá trình bình luận!");
        }
        return comment;
    }

    @Override
    public List<CommentDTO> getListComment() {
        List<Comment> comments = commentRepository.findAll();
        List<CommentDTO> commentDTOS = new ArrayList<>();
        for (Comment comment : comments) {
            commentDTOS.add(CommentMapper.toCommentDTO(comment));
        }
        return commentDTOS;
    }

    @Override
    public Page<Comment> adminListCommentPages(String content, Integer page) {
        page--;
        if (page < 0) {
            page = 0;
        }
        Pageable pageable = PageRequest.of(page, Contant.LIMIT_COMMENT, Sort.by("created_at").descending());
        return commentRepository.adminListCommentPages(content, pageable);
    }

    @Override
    public void deleteComment(long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isEmpty()) {
            throw new NotFoundException("Bình luận không tồn tại!");
        }
        try {
            commentRepository.deleteById(id);
        } catch (Exception ex) {
            throw new InternalServerException("Lỗi khi xóa bình luận!");
        }
    }

    @Override
    public Comment getCommentById(long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isEmpty()) {
            throw new NotFoundException("bình luận không tồn tại!");
        }
        return comment.get();
    }
}
