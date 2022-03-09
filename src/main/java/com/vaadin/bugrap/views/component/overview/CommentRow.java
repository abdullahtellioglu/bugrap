package com.vaadin.bugrap.views.component.overview;

import com.vaadin.bugrap.utils.DateUtils;
import com.vaadin.bugrap.views.model.GroupedComment;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.util.List;

public class CommentRow extends HorizontalLayout {
    private final Span description;
    private final VerticalLayout attachmentContainerLayout;
    private final Avatar avatar;
    private final Label userDisplayNameLabel;
    private final Label timeDisplayLabel;
    private final Label attachmentLabel;
    public CommentRow(){
        setClassName("comment-row");
        setPadding(true);
        setWidth(100, Unit.PERCENTAGE);

        userDisplayNameLabel = new Label();
        userDisplayNameLabel.setClassName("user-label");
        timeDisplayLabel = new Label();
        timeDisplayLabel.setClassName("time-label");


        VerticalLayout nameAndTimeContainer = new VerticalLayout(userDisplayNameLabel, timeDisplayLabel);
        nameAndTimeContainer.setPadding(false);
        nameAndTimeContainer.setSpacing(false);

        avatar = new Avatar();

        HorizontalLayout avatarContainerLayout = new HorizontalLayout(avatar, nameAndTimeContainer);
        avatarContainerLayout.setAlignItems(Alignment.CENTER);

        description = new Span();
        description.setWidth(80, Unit.PERCENTAGE);

        attachmentLabel = new Label("Attachments");
        attachmentLabel.setClassName("attachment-label");

        VerticalLayout metaDataLayout = new VerticalLayout(avatarContainerLayout, attachmentLabel);
        metaDataLayout.setPadding(false);
        metaDataLayout.setWidth(20, Unit.PERCENTAGE);
        add(description, metaDataLayout);

        attachmentContainerLayout = new VerticalLayout();
        attachmentContainerLayout.setPadding(false);
        attachmentContainerLayout.setMargin(false);
        metaDataLayout.add(attachmentContainerLayout);
    }

    @Override
    public void setMargin(boolean margin) {
        super.setMargin(margin);
        if(margin){
            setWidth("calc(100% - var(--lumo-space-m) * 2)");
        }else{
            setWidth(100, Unit.PERCENTAGE);
        }
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
