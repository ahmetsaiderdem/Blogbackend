package com.example.Blogbackend.post;


import com.example.Blogbackend.post.dto.PostDtos;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me/posts")
public class MyPostController {

    private final PostService service;

    public MyPostController(PostService service){
        this.service=service;
    }


    @GetMapping
    public PostDtos.PageResponse<PostDtos.MyPostListItemResponse> myPosts(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "ALL")String status,
            @RequestParam(required = false)String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        long uid=((Number) jwt.getClaims().get("uid")).longValue();
        return service.listMine(uid,status,q,page,size);
    }

    @GetMapping("/{id}")
    public PostDtos.PostDetailResponse myPostDetail(
            @AuthenticationPrincipal Jwt jwt,
            Authentication auth,
            @PathVariable long id
    ){
        long uid=((Number) jwt.getClaims().get("uid")).longValue();
        boolean isAdmin=hasRole(auth,"ROLE_ADMIN");
        return service.getMineById(id,uid,isAdmin);
    }

    private boolean hasRole(Authentication auth,String role){
        if (auth==null)return false;
        for (GrantedAuthority a :auth.getAuthorities()){
            if (role.equals(a.getAuthority()))return true;
        }
        return false;
    }
}
