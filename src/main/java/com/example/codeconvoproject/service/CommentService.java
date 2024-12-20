package com.example.codeconvoproject.service;

import com.example.codeconvoproject.dto.CommentDto.FetchRepliesResponse.*;
import com.example.codeconvoproject.dto.CommentDto.FetchCommentsResponse.*;
import com.example.codeconvoproject.dto.CommentDto.*;
import com.example.codeconvoproject.entity.Comment;
import com.example.codeconvoproject.entity.Post;
import com.example.codeconvoproject.entity.Reply;
import com.example.codeconvoproject.repository.CommentRepository;
import com.example.codeconvoproject.repository.PostRepository;
import com.example.codeconvoproject.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    public CreateCommentResponse createComment(Long postId,
                                               CreateCommentRequest createCommentRequest) {
        Post fetchedPost = postRepository.findById(postId).
                orElseThrow(() -> new RuntimeException("postId에 해당하는 게시글이 존재하지 않습니다."));

        Comment postComment = createCommentRequest.toEntity(fetchedPost);

        Comment postCommentPs = commentRepository.save(postComment);

        return CreateCommentResponse.builder()
                .id(postCommentPs.getId())
                .author(postCommentPs.getAuthor())
                .contents(postCommentPs.getContents())
                .createdAt(postCommentPs.getCreatedAt())
                .updatedAt(postCommentPs.getUpdatedAt())
                .build();
    }

    public UpdateCommentResponse updateComment(Long commentId, UpdateCommentRequest updateCommentRequest) {
        Comment fetchedComment = commentRepository.findById(commentId).
                orElseThrow(() -> new RuntimeException("commentId에 해당하는 댓글이 존재하지 않습니다."));

        Comment comment = updateCommentRequest.toEntity(fetchedComment);

        Comment commentPs = commentRepository.save(comment);

        return UpdateCommentResponse.builder()
                .id(commentPs.getId())
                .author(commentPs.getAuthor())
                .contents(commentPs.getContents())
                .createdAt(commentPs.getCreatedAt())
                .updatedAt(commentPs.getUpdatedAt())
                .build();
    }

    public FetchCommentsResponse fetchComments(Long postId, int pageNumber, int size) {
        // Sort 객체를 생성하여 정렬 기준을 설정합니다.
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        // 페이지 번호와 페이지 크기를 사용하여 PageRequest 객체를 생성합니다.
        PageRequest pageRequest = PageRequest.of(pageNumber, size, sort);

        Page<Comment> fetchedComments = commentRepository.findByPostId(postId, pageRequest);

        List<FetchedComment> comments = fetchedComments.get()
                .map(comment -> FetchedComment.builder()
                        .id(comment.getId())
                        .author(comment.getAuthor())
                        .contents(comment.getContents())
                        .createdAt(comment.getCreatedAt())
                        .updatedAt(comment.getUpdatedAt())
                        .build())
                .toList();

        return FetchCommentsResponse.builder()
                .posts(comments)
                .currentPage(fetchedComments.getNumber())
                .totalPages(fetchedComments.getTotalPages())
                .totalElements(fetchedComments.getTotalElements())
                .build();
    }

    public CreateReplyResponse createReply(Long commentId, CreateReplyRequest createReplyRequest) {
        Comment fetchedComment = commentRepository.findById(commentId).
                orElseThrow(() -> new RuntimeException("commentId에 해당하는 댓글이 존재하지 않습니다."));

        Reply commentReply = createReplyRequest.toEntity(fetchedComment);

        Reply commentReplyPs = replyRepository.save(commentReply);

        return CreateReplyResponse.builder()
                .id(commentReplyPs.getId())
                .author(commentReplyPs.getAuthor())
                .contents(commentReplyPs.getContents())
                .createdAt(commentReplyPs.getCreatedAt())
                .updatedAt(commentReplyPs.getUpdatedAt())
                .build();
    }

    public UpdateReplyResponse updateReply(Long replyId, UpdateReplyRequest updateReplyRequest) {
        Reply fetchedReply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("replyId에 해당하는 대댓글이 존재하지 않습니다."));

        Reply reply = updateReplyRequest.toEntity(fetchedReply);

        Reply replyPs = replyRepository.save(reply);

        return UpdateReplyResponse.builder()
                .id(replyPs.getId())
                .author(replyPs.getAuthor())
                .contents(replyPs.getContents())
                .createdAt(replyPs.getCreatedAt())
                .updatedAt(replyPs.getUpdatedAt())
                .build();
    }

    public FetchRepliesResponse fetchReplies(Long commentId, int pageNumber, int size) {
        // Sort 객체를 생성하여 정렬 기준을 설정합니다.
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        // 페이지 번호와 페이지 크기를 사용하여 PageRequest 객체를 생성합니다.
        PageRequest pageRequest = PageRequest.of(pageNumber, size, sort);

        Page<Reply> fetchedReplies = replyRepository.findByCommentId(commentId, pageRequest);

        List<FetchedReply> replies = fetchedReplies.get()
                .map(reply -> FetchedReply.builder()
                        .id(reply.getId())
                        .author(reply.getAuthor())
                        .contents(reply.getContents())
                        .createdAt(reply.getCreatedAt())
                        .updatedAt(reply.getUpdatedAt())
                        .build())
                .toList();

        return FetchRepliesResponse.builder()
                .replies(replies)
                .currentPage(fetchedReplies.getNumber())
                .totalPages(fetchedReplies.getTotalPages())
                .totalElements(fetchedReplies.getTotalElements())
                .build();
    }
}
