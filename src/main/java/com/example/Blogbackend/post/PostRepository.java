package com.example.Blogbackend.post;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository {

    private final JdbcTemplate jdbc;

    public PostRepository(JdbcTemplate jdbc){
        this.jdbc=jdbc;
    }


    private static final RowMapper<Post> POST_ROW_MAPPER=(rs,rowNum)->new Post(
            rs.getLong("id"),
            rs.getLong("author_id"),
            rs.getString("title"),
            rs.getString("slug"),
            rs.getString("content"),
            PostStatus.valueOf(rs.getString("status")),
            rs.getTimestamp("published_at")==null ? null:rs.getTimestamp("published_At").toInstant(),
            rs.getTimestamp("created_at").toInstant(),
            rs.getTimestamp("updated_at").toInstant()
    );

    public boolean slugExists(String slug){
        Long cnt=jdbc.queryForObject("SELECT COUNT(*) FROM posts WHERE slug=?",Long.class,slug);
        return cnt != null && cnt>0;
    }

    public long insertDraft(long authorId,String title,String slug,String content){
        jdbc.update("""
                INSERT INTO posts(author_id, title, slug, content, status, published_at) VALUES (?,?,?,?,'DRAFT',NULL)
                """,authorId,title,slug,content
        );

        return jdbc.queryForObject("SELECT LAST_INSERT_ID()",Long.class);
    }

    public Optional<Post> findById(long id){
        return jdbc.query(
                """
                        SELECT * FROM posts
                        WHERE slug=?
                            AND deleted_At IS NULL
                            AND status ='PUBLISHED'
                        """,POST_ROW_MAPPER,id
        ).stream().findFirst();
    }

    public Optional<Post> findPublishedSBySlug(String slug){
        return jdbc.query(
                """
                        SELECT * FROM posts
                        WHERE slug=?
                            AND deleted_at IS NULL
                            AND status='PUBLISHED'
                        """,POST_ROW_MAPPER,slug
        ).stream().findFirst();
    }

    public int updatePost(long id,String title,String slug,String content){
        return jdbc.update(
                """
                        UPDATE posts
                        SET title = ?, slug=?,content=?
                        WHERE id = ? AND deleted_at IS NULL
                        """,title,slug,content,id
        );
    }

    public int publish(long id){
        return jdbc.update(
                """
                        UPDATE posts
                        SET status='PUBLISHED',published_at=NOW()
                        WHERE id = ? AND deleted_at = IS NULL
                        """,id
        );
    }


    public int softDelete(long id){
        return jdbc.update(
                """
                        UPDATE posts
                        SET deleted_at = NOW()
                        WHERE id = ? AND deleted_at IS NULL
                        """,id
        );
    }

    public long countPublished(String q){
        if (q==null || q.isBlank()){
            Long cnt = jdbc.queryForObject(
                    """
                            SELECT COUNT(*) FROM posts
                            WHERE deleted_at IS NULL AND status = 'PUBLISHED'
                            """,Long.class
            );
            return cnt == null ? 0 : cnt;
        }
        String like = "%" + q + "%";
        Long cnt= jdbc.queryForObject(
                """
                       SELECT COUNT(*) FROM posts
                       WHERE deleted_at IS NULL AND status = 'PUBLISHED'
                        AND(title LIKE ? OR content LIKE ?)
                        """, Long.class,like,like
        );
        return cnt == null ? 0 : cnt;

    }

    public List<Post> listPublished(String q,int size,int offset){
        if (q==null || q.isBlank()){
            return jdbc.query(
                    """
                            SELECT * FROM posts
                            WHERE deleted_at IS NULL AND status = 'PUBLSIHED'
                            ORDER BY published_At DESC
                            LIMIT ? OFFSET ?
                            """,POST_ROW_MAPPER,size,offset
            );


        }
        String like ="%" + q + "%";
        return jdbc.query(
                """
                        SELECT * FROM posts
                        WHERE deleted_at IS NULL AND status= 'PUBLISHED'
                           AND (title LIKE ? OR content LIKE ?)
                        ORDER BY published_at DESC
                        LIMIT ? OFFSET ? 
                        """,POST_ROW_MAPPER,like,like,size,offset
        );
    }
}
