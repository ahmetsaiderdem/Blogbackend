package com.example.Blogbackend.comment;

import com.example.Blogbackend.comment.dto.CommentDtos;
import com.example.Blogbackend.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {

    private final CommentService service;
    private final UserRepository users;


    public CommentController(CommentService service,UserRepository users){
        this.service=service;
        this.users=users;
    }

    @GetMapping("/api/posts/{slug}/comments")
    public CommentDtos.PageResponse<CommentDtos.CommentResponse> list(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return service.listComments(slug,page,size);
    }

    @PostMapping("/api/posts/{slug}/comments")
    public CommentDtos.CommentResponse add(
            @PathVariable String slug,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CommentDtos.CreateCommentRequest req
            ){
        long uid = ((Number) jwt.getClaims().get("uid")).longValue();

        var user=users.findByEmail(jwt.getSubject())
                .orElseThrow(()->new RuntimeException("INVALID_CREDENTIALS"));

        return service.addComment(slug,uid,req,user.displayName());
    }

    @DeleteMapping("/api/comments/{id}")
    public Object delete(
            @PathVariable long id,
            @AuthenticationPrincipal Jwt jwt,
            Authentication auth
    ){
        long uid=((Number) jwt.getClaims().get("uid")).longValue();
        boolean isAdmin=hasRole(auth,"ROLE_ADIMN");

        service.deleteComment(id,uid,isAdmin);
        return new Object(){public final String status = "ok";};
    }


    private boolean hasRole(Authentication auth,String role){
        if (auth==null)return false;
        for (GrantedAuthority a : auth.getAuthorities()){
            if (role.equals(a.getAuthority()))return true;
        }
        return false;
    }
}
