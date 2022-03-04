package com.vaadin.bugrap.views.component;

import com.vaadin.bugrap.views.model.GroupedComment;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.upload.FileRejectedEvent;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import elemental.json.Json;
import org.apache.commons.io.IOUtils;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommentAttachmentLayout extends VerticalLayout {
    private final RichTextEditor commentRichTextEditor;
    private final MultiFileMemoryBuffer attachmentFileMemoryBuffer;
    private final Upload attachmentUpload;
    private final Button saveCommentBtn;


    private final Map<String, byte[]> fileMap = new HashMap<>();

    public CommentAttachmentLayout(){
        setMargin(false);
        setJustifyContentMode(JustifyContentMode.END);
        VerticalLayout attachmentLayout = new VerticalLayout();
        attachmentLayout.setWidth("unset");
        attachmentLayout.setSpacing(false);
        attachmentLayout.setPadding(false);

        Span attachmentsLabel = new Span("Attachments");
        attachmentsLabel.setClassName("attachment-label-header");
        attachmentLayout.add(attachmentsLabel);
        Span attachmentDescriptionLabel = new Span("Only PDF, PNG and JPG files are allowed. \nMax file size is 5 MB.");
        attachmentDescriptionLabel.setClassName("attachment-description-label");
        attachmentDescriptionLabel.setWhiteSpace(HasText.WhiteSpace.BREAK_SPACES);
        attachmentLayout.add(attachmentDescriptionLabel);


        attachmentFileMemoryBuffer = new MultiFileMemoryBuffer();
        attachmentUpload = new Upload(attachmentFileMemoryBuffer);
        attachmentUpload.setMaxFileSize( 5 * 1024 * 1024);
        attachmentUpload.setAcceptedFileTypes("image/png", "image/jpeg", "application/pdf");
        attachmentLayout.add(attachmentUpload);


        commentRichTextEditor = new RichTextEditor();


        HorizontalLayout reviewAttachmentHorizontalLayout = new HorizontalLayout();

        reviewAttachmentHorizontalLayout.setWidth(100, Unit.PERCENTAGE);
        reviewAttachmentHorizontalLayout.add(commentRichTextEditor);
        reviewAttachmentHorizontalLayout.add(attachmentLayout);
        add(reviewAttachmentHorizontalLayout);



        saveCommentBtn = new Button("Comment", VaadinIcon.CHECK.create());
        saveCommentBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelCommentBtn = new Button("Cancel", VaadinIcon.CLOSE.create());

        HorizontalLayout buttonContainer = new HorizontalLayout(saveCommentBtn, cancelCommentBtn);
        buttonContainer.setWidth(100, Unit.PERCENTAGE);
        buttonContainer.setPadding(false);
        buttonContainer.setMargin(false);
        add(buttonContainer);

        cancelCommentBtn.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> clear());

        attachmentUpload.addSucceededListener((ComponentEventListener<SucceededEvent>) event -> {
            String fileName = event.getFileName();
            InputStream inputStream = attachmentFileMemoryBuffer.getInputStream(fileName);
            try{
                byte[] fileContentByteArray = IOUtils.toByteArray(inputStream);
                fileMap.put(fileName, fileContentByteArray);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });

        attachmentUpload.addFileRejectedListener((ComponentEventListener<FileRejectedEvent>) event -> {
            Span errorHeaderSpan = new Span("File upload failed");
            errorHeaderSpan.setClassName("header");
            Span errorDescriptionSpan = new Span("File format not supported. Allowed file formats: PDF, PNG and JPG");
            errorDescriptionSpan.setClassName("description");
            VerticalLayout verticalLayout = new VerticalLayout(errorHeaderSpan, errorDescriptionSpan);
            verticalLayout.setPadding(false);
            verticalLayout.setSpacing(false);
            Notification notification = new Notification(verticalLayout);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setDuration(5000);
            notification.open();
        });
    }

    public void clear(){
        commentRichTextEditor.clear();
        attachmentUpload.getElement().setPropertyJson("files", Json.createArray());
        fileMap.clear();
    }


    public GroupedComment getGroupedComment(Report report, Reporter user){
        GroupedComment groupedComment = new GroupedComment();
        groupedComment.setAttachments(new ArrayList<>());
        groupedComment.setComment(getCommentText());
        groupedComment.setTimestamp(new Date());
        groupedComment.setReport(report);
        groupedComment.setAuthor(user);

        getFileMap().forEach((fileName, data) -> {
            GroupedComment.Attachment attachment = new GroupedComment.Attachment();
            attachment.setData(data);
            attachment.setName(fileName);
            groupedComment.getAttachments().add(attachment);
        });
        return groupedComment;
    }

    public Map<String, byte[]> getFileMap() {
        return fileMap;
    }

    public String getCommentText(){
        return commentRichTextEditor.getHtmlValue();
    }

    public void setSaveClickListener(ComponentEventListener<ClickEvent<Button>> clickEventComponentEventListener) {
        saveCommentBtn.addClickListener(clickEventComponentEventListener);
    }
}
