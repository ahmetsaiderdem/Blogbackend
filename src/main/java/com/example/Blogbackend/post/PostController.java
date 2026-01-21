package com.example.Blogbackend.post;

import com.example.Blogbackend.post.dto.PostDtos;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService service;


    public PostController(PostService service){
        this.service=service;
    }


    @GetMapping
    public PostDtos.PageResponse<PostDtos.PostListItemResponse> list(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10")int size,
            @RequestParam(required = false)String q
    ){
        return service.listPublished(q,page,size);
    }

    @GetMapping("/{slug}")
    public PostDtos.PostDetailResponse getBySlug(@PathVariable String slug){
        return service.getPublishedBySlug(slug);
    }

    @PostMapping
    public PostDtos.PostDetailResponse create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody PostDtos.CreatePostRequest req
            ){
        long uid=((Number) jwt.getClaims().get("uid")).longValue();
        return service.createDraft(uid,req);
    }

    @PutMapping("/{id}")
    public PostDtos.PostDetailResponse update(
            @AuthenticationPrincipal Jwt jwt,
            Authentication auth,
            @PathVariable long id,
            @Valid @RequestBody PostDtos.UpdatePostRequest req
            ){
        long uid=((Number) jwt.getClaims().get("uid")).longValue();
        boolean isAdmin=hasRole(auth,"ROLE_ADMIN");
        return service.update(id,uid,isAdmin,req);
    }

    @PostMapping("/{id}/publish")
    public Object publish(
            @AuthenticationPrincipal Jwt jwt,
            Authentication auth,
            @PathVariable long id
    ){
        long uid=((Number) jwt.getClaims().get("uid")).longValue();
        boolean isAdmin=hasRole(auth,"ROLE_ADMIN");
        service.publish(id,uid,isAdmin);
        return new Object(){public final String status = "ok";};
    }

    @DeleteMapping("/{id}")
    public Object delete(
            @AuthenticationPrincipal Jwt jwt,
            Authentication auth,
            @PathVariable long id
    ){
        long uid=((Number) jwt.getClaims().get("uid")).longValue();
        boolean isAdmin=hasRole(auth,"ROLE_ADMIN");
        service.delete(id,uid,isAdmin);
        return new Object(){public final String status = "ok";};
    }

    private boolean hasRole(Authentication auth,String role){
        if (auth==null)return false;
        for (GrantedAuthority a: auth.getAuthorities()){
            if (role.equals(a.getAuthority()))return true;
        }
        return false;
    }
}
