package com.vaadin.bugrap.views.component;

import com.vaadin.bugrap.views.model.GroupedComment;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
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
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This component used to save comment and add attachments.
 */
public class CommentAttachmentLayout extends VerticalLayout {
    private final RichTextEditor commentRichTextEditor;
    private final MultiFileMemoryBuffer attachmentFileMemoryBuffer;
    private final Upload attachmentUpload;
    private final Button saveCommentBtn;
    private final ConfirmDialog emptyTextConfirmDialog;

    private final Map<String, byte[]> fileMap = new HashMap<>();

    public CommentAttachmentLayout(){
        setMargin(false);
        setJustifyContentMode(JustifyContentMode.END);
        setHeight(440, Unit.PIXELS);

        Span attachmentsLabel = new Span("Attachments");
        attachmentsLabel.setClassName("attachment-label-header");

        Span attachmentDescriptionLabel = new Span("Only PDF, PNG and JPG files are allowed. \nMax file size is 5 MB.");
        attachmentDescriptionLabel.setClassName("attachment-description-label");
        attachmentDescriptionLabel.setWhiteSpace(HasText.WhiteSpace.BREAK_SPACES);

        attachmentFileMemoryBuffer = new MultiFileMemoryBuffer();
        attachmentUpload = new Upload(attachmentFileMemoryBuffer);
        attachmentUpload.setMaxFileSize( 5 * 1024 * 1024);
        attachmentUpload.setAcceptedFileTypes("image/png", "image/jpeg", "application/pdf");

        VerticalLayout attachmentLayout = new VerticalLayout(attachmentsLabel, attachmentDescriptionLabel, attachmentUpload);
        attachmentLayout.setWidth("unset");
        attachmentLayout.setSpacing(false);
        attachmentLayout.setPadding(false);

        commentRichTextEditor = new RichTextEditor();
        commentRichTextEditor.getElement()
                .executeJs("this._editor.root.setAttribute('placeholder', $0)", "Write a new comment...");
        HorizontalLayout reviewAttachmentHorizontalLayout = new HorizontalLayout(commentRichTextEditor, attachmentLayout);
        reviewAttachmentHorizontalLayout.setHeight(100, Unit.PERCENTAGE);
        reviewAttachmentHorizontalLayout.setWidth(100, Unit.PERCENTAGE);

        saveCommentBtn = new Button("Comment", VaadinIcon.CHECK.create());
        saveCommentBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveCommentBtn.setEnabled(false);

        Button cancelCommentBtn = new Button("Cancel", VaadinIcon.CLOSE.create());

        HorizontalLayout buttonContainer = new HorizontalLayout(saveCommentBtn, cancelCommentBtn);
        buttonContainer.setWidth(100, Unit.PERCENTAGE);
        buttonContainer.setPadding(false);
        buttonContainer.setMargin(false);
        add(reviewAttachmentHorizontalLayout);
        add(buttonContainer);


        initializeEvents(cancelCommentBtn);


        emptyTextConfirmDialog = new ConfirmDialog();
        emptyTextConfirmDialog.setHeader("Save failed");
        emptyTextConfirmDialog.setText("Comment should not be empty.");
        emptyTextConfirmDialog.setConfirmText("OK");

    }


    private void initializeEvents(Button cancelCommentBtn){
        commentRichTextEditor.addKeyPressListener((ComponentEventListener<KeyPressEvent>) event ->
                saveCommentBtn.setEnabled(StringUtils.isNotEmpty(commentRichTextEditor.getHtmlValue())));

        commentRichTextEditor.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<RichTextEditor, String>>)
                event -> saveCommentBtn.setEnabled(isCommentValid()));

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

    /**
     * Clears comment area, attachments and disable the save button.
     */
    public void clear(){
        commentRichTextEditor.clear();
        attachmentUpload.getElement().setPropertyJson("files", Json.createArray());
        fileMap.clear();
        saveCommentBtn.setEnabled(false);
    }


    private boolean isCommentValid(){
        try{
            Document parse = Jsoup.parse(commentRichTextEditor.getHtmlValue());
            String text = parse.text();
            return StringUtils.isNotEmpty(text) && !commentRichTextEditor.isEmpty();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the grouped comment. Also check {@link GroupedComment}
     *
     * @param report Report to save comment
     * @param user Current user to saving comment
     * @return the comment.
     */
    public GroupedComment getGroupedComment(Report report, Reporter user){
        GroupedComment groupedComment = new GroupedComment();
        groupedComment.setAttachments(new ArrayList<>());
        groupedComment.setComment(commentRichTextEditor.getHtmlValue());
        groupedComment.setTimestamp(new Date());
        groupedComment.setReport(report);
        groupedComment.setAuthor(user);

        fileMap.forEach((fileName, data) -> {
            GroupedComment.Attachment attachment = new GroupedComment.Attachment();
            attachment.setData(data);
            attachment.setName(fileName);
            groupedComment.getAttachments().add(attachment);
        });
        return groupedComment;
    }

    /**
     * Save button click listener. If comment is empty display a dialog otherwise invoke the given event.
     * @param clickEventComponentEventListener consumer
     */
    public void setSaveClickListener(ComponentEventListener<ClickEvent<Button>> clickEventComponentEventListener) {
        saveCommentBtn.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            if(!isCommentValid()){
                emptyTextConfirmDialog.open();
            }else{
                clickEventComponentEventListener.onComponentEvent(event);
            }
        });
    }
}
