package com.example.Blogbackend.comment;

import com.example.Blogbackend.comment.dto.CommentDtos;
import com.example.Blogbackend.common.ApiException;
import com.example.Blogbackend.post.Post;
import com.example.Blogbackend.post.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CommentService {


    private final CommentRepository comments;
    private final PostRepository posts;

    public CommentService(CommentRepository comments,PostRepository posts){
        this.comments=comments;
        this.posts=posts;
    }

    private Post getPublishedPostOrThrow(String slug){
        return posts.findPublishedSBySlug(slug).orElseThrow(()-> ApiException.notFound("Post not found"));
    }


    public CommentDtos.CommentResponse addComment(String postSlug, long userId, CommentDtos.CreateCommentRequest req,String userDisplayName){
         Post post= getPublishedPostOrThrow(postSlug);

         long id= comments.insert(post.id(), userId, req.content());

         var created = comments.findById(id).orElseThrow(()->ApiException.notFound("Comment not found"));

         return new CommentDtos.CommentResponse(
                 created.id(),
                 created.userId(),
                 userDisplayName,
                 created.content(),
                 created.createdAt()
         );
    }

    public CommentDtos.PageResponse<CommentDtos.CommentResponse> listComments(String postSlug,int page,int size){
        Post post= getPublishedPostOrThrow(postSlug);

        int safePage=Math.max(page,0);
        int safeSize=Math.min(Math.max(size,1),50);
        int offset = safePage*safeSize;

        long total = comments.countByPostId(post.id());

        var items=comments.listByPostId(post.id(),safeSize,offset).stream()
                .map(r->new CommentDtos.CommentResponse(r.id(),r.userId(),r.userDisplayName(),r.content(),r.createdAt()))
                .toList();

        return new CommentDtos.PageResponse<>(items,new CommentDtos.PageMeta(safePage,safeSize,total));
    }

    public void deleteComment(long commentId,long requestUserId,boolean isAdmin){
        var c=comments.findById(commentId).orElseThrow(()->ApiException.notFound("Comment not found"));

        if (!isAdmin && c.userId() != requestUserId){
            throw ApiException.forbidden("You can only delete your own comment");
        }
        int updated=comments.softDelete(commentId);
        if (updated==0){
            throw new ApiException(HttpStatus.BAD_REQUEST,"ALREADY_DELETED","Comment is already deleted");
        }
    }
}
