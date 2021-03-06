package com.vaadin.bugrap.views.component.overview;

import com.vaadin.bugrap.views.model.GroupedComment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

/**
 * Comment list is a container of Comment cards.
 */
public class CommentList extends VerticalLayout {
    private boolean commentRowPadding;
    private boolean commentRowMargin;
    public CommentList() {
        setClassName("comment-list");
        setPadding(false);
    }

    /**
     * Adds padding to comment cards
     * @param commentRowPadding if padding true otherwise false
     */
    public void setCommentRowPadding(boolean commentRowPadding){
        this.commentRowPadding = commentRowPadding;
    }

    /**
     * Adds margin to comment cards
     * @param commentRowMargin if margin true otherwise false
     */
    public void setCommentRowMargin(boolean commentRowMargin) {
        this.commentRowMargin = commentRowMargin;
    }

    /**
     * Creates cards of given comments. Removes all children after that creates comment rows based on given list.
     * @param comments list of comments.
     */
    public void setComments(List<GroupedComment> comments){
        if(comments.isEmpty()){
            setVisible(false);
            return;
        }
        setVisible(true);
        removeAll();


        comments.stream().map(comment -> {
            CommentRow commentRow = new CommentRow();
            commentRow.setMargin(commentRowMargin);
            commentRow.setPadding(commentRowPadding);
            commentRow.setComment(comment);
            return commentRow;
        }).forEach(this::add);
    }
}
