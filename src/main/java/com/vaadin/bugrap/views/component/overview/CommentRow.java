package com.vaadin.bugrap.views.component.overview;

import com.helger.commons.io.stream.ByteBufferInputStream;
import com.vaadin.bugrap.utils.DateUtils;
import com.vaadin.bugrap.views.model.GroupedComment;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;
import org.vaadin.bugrap.domain.entities.Comment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

public class CommentRow extends HorizontalLayout {
    private final Span description;
    private final VerticalLayout metaDataLayout;
    private final VerticalLayout attachmentContainerLayout;
    private final Avatar avatar;
    private final Label userDisplayNameLabel;
    private final Label timeDisplayLabel;
    private final Label attachmentLabel;
    public CommentRow(){
        setClassName("comment-row");
        setPadding(true);
        setWidth(100, Unit.PERCENTAGE);

        description = new Span();


        description.setWidth(80, Unit.PERCENTAGE);



        userDisplayNameLabel = new Label();
        userDisplayNameLabel.setClassName("user-label");
        timeDisplayLabel = new Label();
        timeDisplayLabel.setClassName("time-label");


        VerticalLayout nameAndTimeContainer = new VerticalLayout();
        nameAndTimeContainer.setPadding(false);
        nameAndTimeContainer.setSpacing(false);
        nameAndTimeContainer.add(userDisplayNameLabel);
        nameAndTimeContainer.add(timeDisplayLabel);

        avatar = new Avatar();


        HorizontalLayout avatarContainerLayout = new HorizontalLayout();
        avatarContainerLayout.setAlignItems(Alignment.CENTER);
        avatarContainerLayout.add(avatar);
        avatarContainerLayout.add(nameAndTimeContainer);


        metaDataLayout = new VerticalLayout();
        metaDataLayout.setPadding(false);
        metaDataLayout.add(avatarContainerLayout);

        attachmentLabel = new Label("Attachments");
        attachmentLabel.setClassName("attachment-label");
        metaDataLayout.add(attachmentLabel);


        metaDataLayout.setWidth(20, Unit.PERCENTAGE);
        add(description, metaDataLayout);

        attachmentContainerLayout = new VerticalLayout();
        attachmentContainerLayout.setPadding(false);
        attachmentContainerLayout.setMargin(false);
        metaDataLayout.add(attachmentContainerLayout);
    }
    public void setComment(GroupedComment comment){
        attachmentContainerLayout.removeAll();
        description.getElement().setProperty("innerHTML",comment.getComment());
        avatar.setName(comment.getAuthor().getName());
        timeDisplayLabel.setText(DateUtils.getRelativeFormat(comment.getTimestamp()));
        userDisplayNameLabel.setText(comment.getAuthor().getName());
        if(CollectionUtils.isEmpty(comment.getAttachments())){
            attachmentLabel.setText("No attachment");
        }else{
            attachmentLabel.setText(String.format("%d attachments", comment.getAttachments().size()));
            addAttachments(comment.getAttachments());
        }
    }
    private void addAttachments(List<GroupedComment.Attachment> attachments){
        attachments.forEach(attachment -> {
            Anchor download = new Anchor(new StreamResource(attachment.getName(), (InputStreamFactory) () -> new ByteArrayInputStream(attachment.getData())), "");
            download.getElement().setAttribute("download", true);

            download.add(VaadinIcon.FILE_O.create(), new Label(attachment.getName()));
            attachmentContainerLayout.add(download);
        });
    }
}
