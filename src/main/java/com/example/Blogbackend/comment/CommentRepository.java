package com.example.Blogbackend.comment;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepository {

    private final JdbcTemplate jdbc;

    public CommentRepository(JdbcTemplate jdbc){
        this.jdbc=jdbc;
    }

    private static final RowMapper<Comment> COMMENT_ROW_MAPPER=(rs, rowNum) -> new Comment(
            rs.getLong("id"),
            rs.getLong("post_id"),
            rs.getLong("user_id"),
            rs.getString("content"),
            rs.getTimestamp("created_at").toInstant()
    );


    public long insert(long postId,long userId,String content){

        jdbc.update(
                """
                        INSERT INTO comments(post_id, user_id, content) VALUES (?,?,?)
                        """,postId,userId,content
        );
        return jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    public Optional<Comment> findById(long id){
        return jdbc.query(
                """
                        SELECT * FROM comments WHERE id=? AND deleted_at IS NULL
                        """,COMMENT_ROW_MAPPER,id
        ).stream().findFirst();
    }


    public int softDelete(long id){
        return jdbc.update(
                """
                        UPDATE comments
                        SET deleted_at = NOW()
                        WHERE id = ? AND deleted_at IS NULL
                        """,id
        );
    }

    public long countByPostId(long postId){
        Long cnt= jdbc.queryForObject(
                """
                        SELECT COUNT(*) FROM comments
                        WHERE post_id = ? AND deleted_at IS NULL
                        """, Long.class,postId

        );
        return cnt == null ? 0 : cnt;
    }

    public List<CommentListRow> listByPostId(long postId, int size, int offset){

        return jdbc.query(
                """
                        SELECT c.id, c.user_id, u.display_name, c.content, c.created_at
                        FROM comments c
                        JOIN users u ON u.id = c.user_id
                        WHERE c.post_id = ?
                            AND c.deleted_at IS NULL
                        ORDER BY c.created_at DESC
                        LIMIT ? OFFSET ? 
                        """,(rs,rowNum)->new CommentListRow(
                                rs.getLong("id"),
                                rs.getLong("user_id"),
                                rs.getString("display_name"),
                                rs.getString("content"),
                                rs.getTimestamp("created_at").toInstant()

                ),postId,size,offset
        );
    }


    public record CommentListRow(
            long id,
            long userId,
            String userDisplayName,
            String content,
            java.time.Instant createdAt
    ){}

}
