package com.tiennam.application.controller.admin;

import com.tiennam.application.entity.Comment;

import com.tiennam.application.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminCommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/admin/comments")
    public String homePages(Model model,
                            @RequestParam(defaultValue = "", required = false) String content,
                            @RequestParam(defaultValue = "1", required = false) Integer page) {
        Page<Comment> comments = commentService.adminListCommentPages(content, page);
        model.addAttribute("comments", comments.getContent());
        model.addAttribute("totalPages", comments.getTotalPages());
        model.addAttribute("currentPage", comments.getPageable().getPageNumber() + 1);
        return "admin/comment/list";
    }

    @GetMapping("/api/admin/comments/list")
    public ResponseEntity<Object> getListCommentPages(@RequestParam(defaultValue = "", required = false) String content,
                                                      @RequestParam(defaultValue = "1", required = false) Integer page) {
        Page<Comment> comments = commentService.adminListCommentPages(content, page);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/api/admin/comments/{id}")
    public ResponseEntity<Object> deleteComment(@PathVariable long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok("Xóa bình luận thành công!");
    }

    @GetMapping("/api/admin/comments/{id}")
    public ResponseEntity<Object> getCommentById(@PathVariable long id){
        Comment comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }
}
