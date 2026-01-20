package com.example.Blogbackend.post;

import com.example.Blogbackend.common.ApiException;
import com.example.Blogbackend.post.dto.PostDtos;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostRepository posts;

    public PostService(PostRepository posts){
        this.posts=posts;
    }

    private String uniqueSlugForTitle(String title){
        String base=SlugUtil.toSlug(title);
        String slug=base;
        int i = 2;

        while (posts.slugExists(slug)){
            slug = base + "_" + i;
            i++;
        }
        return slug;
    }

    private void assertOwnerAdmin(long requestUid, boolean isAdmin,Post post){
        if (isAdmin) return;;
        if (post.authorId() != requestUid){
            throw ApiException.forbidden("You are not allowed to modify this post");

        }
    }

    public PostDtos.PostDetailResponse createDraft(long authorId, PostDtos.CreatePostRequest req){
        String slug=uniqueSlugForTitle(req.title());
        long id= posts.insertDraft(authorId,req.title(),slug, req.Content());
        Post created = posts.findById(id).orElseThrow(() -> ApiException.notFound("Post not found after insert"));
        return toDetail(created);
    }

    public PostDtos.PostDetailResponse update(long postId, long requestUid, boolean isAdmin, PostDtos.UpdatePostRequest req){
        Post existing = posts.findById(postId).orElseThrow(()->ApiException.notFound("Post  not found"));
        assertOwnerAdmin(requestUid,isAdmin,existing);

        String newSlug=SlugUtil.toSlug(req.title());

        if (!newSlug.equals(existing.slug()) && posts.slugExists(newSlug)){
            newSlug=uniqueSlugForTitle(req.title());
        }
        posts.updatePost(postId,req.title(),newSlug,req.content());
        Post updated = posts.findById(postId).orElseThrow(()->ApiException.notFound("Post not found after update"));
        return toDetail(updated);
    }

    public void publish(long postId,long requestUid,boolean isAdmin){
        Post existing = posts.findById(postId).orElseThrow(()-> ApiException.notFound("Post not found"));
        assertOwnerAdmin(requestUid,isAdmin,existing);

        if (existing.status()== PostStatus.PUBLISHED){
            throw new ApiException(HttpStatus.BAD_REQUEST,"ALREADY_PUBLISHED","Post is already published");
        }
        posts.publish(postId);
    }

    public void delete(long postId,long requestUid,boolean isAdmin){
        Post existing = posts.findById(postId).orElseThrow(()->ApiException.notFound("Post not found"));
        assertOwnerAdmin(requestUid,isAdmin,existing);

        posts.softDelete(postId);
    }

    public PostDtos.PostDetailResponse getPublishedBySlug(String slug){
        Post p = posts.findPublishedSBySlug(slug).orElseThrow(()->ApiException.notFound("Post not found"));
        return toDetail(p);
    }

    public PostDtos.PageResponse<PostDtos.PostListItemResponse> listPublished(String q,int page,int size){
        int safePage=Math.max(page,0);
        int safeSize=Math.min(Math.max(size,1),50);
        int offset = safePage * safeSize;

        long total= posts.countPublished(q);
        var list= posts.listPublished(q,safeSize,offset).stream()
                .map(this::toListItem)
                .toList();

        return new PostDtos.PageResponse<>(list,new PostDtos.PageMeta(safePage,safeSize,total));
    }

    private PostDtos.PostListItemResponse toListItem(Post p){
        return new PostDtos.PostListItemResponse(p.id(),p.title(),p.slug(),p.publishedAt());
    }

    private PostDtos.PostDetailResponse toDetail(Post p){
        return new PostDtos.PostDetailResponse(
                p.id(), p.authorId(), p.title(),p.slug(),p.content(),
                p.status().name(),p.publishedAt(),p.createdAt(),p.updatedAt()
        );
    }

}
