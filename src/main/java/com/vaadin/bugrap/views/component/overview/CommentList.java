package com.vaadin.bugrap.views.component.overview;

import com.vaadin.bugrap.views.model.GroupedComment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

public class CommentList extends VerticalLayout {
    public CommentList() {
        setClassName("comment-list");
        setPadding(false);
    }
    public void setComments(List<GroupedComment> comments){
        if(comments.isEmpty()){
            setVisible(false);
            return;
        }
        setVisible(true);
        removeAll();


        comments.stream().map(comment -> {
            CommentRow commentRow = new CommentRow();
            commentRow.setComment(comment);
            return commentRow;
        }).forEach(this::add);
    }
}
