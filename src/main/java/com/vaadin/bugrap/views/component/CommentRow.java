package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.bugrap.domain.entities.Comment;

public class CommentRow extends HorizontalLayout {
    private final Label commentLabel;
    public CommentRow(){
        commentLabel = new Label();

    }
    public void setComment(Comment comment){
        commentLabel.setText(comment.getComment());
    }
}
