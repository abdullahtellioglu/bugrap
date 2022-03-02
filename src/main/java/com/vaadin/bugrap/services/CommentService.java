package com.vaadin.bugrap.services;

import ch.qos.logback.core.util.StringCollectionUtil;
import com.vaadin.bugrap.views.model.GroupedComment;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommentService {
    private final BugrapRepository bugrapRepository;
    public CommentService(){
        bugrapRepository = new BugrapRepository();
    }
    public List<Comment> getComments(Report report){
        return bugrapRepository.findComments(report);
    }


    @Transactional
    public void saveComment(Report report, GroupedComment groupedComment){
        List<GroupedComment.Attachment> attachments = groupedComment.getAttachments();
        List<Comment> comments = new ArrayList<>();
        Date currentTimestamp = groupedComment.getTimestamp() != null ? groupedComment.getTimestamp() : new Date();
        attachments.forEach(attachment -> {
            Comment comment = new Comment();
            comment.setTimestamp(currentTimestamp);
            comment.setType(Comment.Type.ATTACHMENT);
            comment.setAttachment(attachment.getData());
            comment.setAttachmentName(attachment.getName());
            comment.setAuthor(groupedComment.getAuthor());
            comment.setReport(report);
            comments.add(comment);
        });
        if(StringUtils.isNotEmpty(groupedComment.getComment())){
            Comment comment = new Comment();
            comment.setComment(groupedComment.getComment());
            comment.setTimestamp(currentTimestamp);
            comment.setType(Comment.Type.COMMENT);
            comment.setAuthor(groupedComment.getAuthor());
            comment.setReport(report);
            comments.add(comment);
        }
        comments.forEach(bugrapRepository::save);

    }

    /**
     * Groups comments by timestamp + author. The reason is all comments are raw records. All rows needs to be grouped for attachments.
     * @param report
     * @return list of comments or empty
     */
    public List<GroupedComment> getGroupedComments(Report report){
        List<Comment> comments = bugrapRepository.findComments(report);

        Map<String, List<Comment>> groupedComments = comments.stream().collect(Collectors.groupingBy(k -> k.getTimestamp().getTime() + "_" + k.getAuthor().getId()));
        List<GroupedComment> commentGroups = new ArrayList<>();
        groupedComments.forEach((hashId, list) -> {
           GroupedComment comment = new GroupedComment();
           list.stream().filter(f -> Comment.Type.COMMENT.equals(f.getType())).findFirst().ifPresent(hasCommentEntity -> {
               comment.setComment(hasCommentEntity.getComment());
           });
           comment.setAuthor(list.get(0).getAuthor());
           comment.setReport(list.get(0).getReport());
           comment.setTimestamp(list.get(0).getTimestamp());
            List<GroupedComment.Attachment> attachments = list.stream().filter(f -> Comment.Type.ATTACHMENT.equals(f.getType())).map(attachmentComment -> {
                GroupedComment.Attachment attachment = new GroupedComment.Attachment();
                attachment.setName(attachmentComment.getAttachmentName());
                attachment.setData(attachmentComment.getAttachment());
                return attachment;
            }).collect(Collectors.toList());
            comment.setAttachments(attachments);
            comment.setGroupedComments(list);

            commentGroups.add(comment);
        });
        return commentGroups;
    }
}
