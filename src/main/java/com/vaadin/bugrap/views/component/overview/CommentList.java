package com.vaadin.bugrap.views.component.overview;

import com.vaadin.bugrap.views.model.GroupedComment;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.bugrap.domain.entities.Comment;

import java.util.List;

public class CommentList extends Div {
    private final VerticalLayout commentListVerticalLayout = new VerticalLayout();

    public CommentList() {
        commentListVerticalLayout.setPadding(false);
        commentListVerticalLayout.setWidth(100, Unit.PERCENTAGE);
        setWidth(100, Unit.PERCENTAGE);
        Scroller scroller = new Scroller();
        scroller.setMaxHeight(400, Unit.PIXELS);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.setContent(commentListVerticalLayout);
        add(scroller);
    }
    public void setComments(List<GroupedComment> comments){
        if(comments.isEmpty()){
            setVisible(false);
            return;
        }
        setVisible(true);
        commentListVerticalLayout.removeAll();


        comments.stream().map(comment -> {
            CommentRow commentRow = new CommentRow();
            commentRow.setComment(comment);
            return commentRow;
        }).forEach(commentListVerticalLayout::add);
    }
}
