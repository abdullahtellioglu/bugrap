package com.vaadin.bugrap.views.model;

import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.Date;
import java.util.List;

public class GroupedComment {
    private List<Comment> groupedComments;
    private Reporter author;
    private String comment;
    private Date timestamp;
    private Report report;
    private List<Attachment> attachments;

    public static class Attachment {
        private String name;
        private byte[] data;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }
    }

    public List<Comment> getGroupedComments() {
        return groupedComments;
    }

    public void setGroupedComments(List<Comment> groupedComments) {
        this.groupedComments = groupedComments;
    }

    public Reporter getAuthor() {
        return author;
    }

    public void setAuthor(Reporter author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

}
