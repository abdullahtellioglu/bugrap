package com.vaadin.bugrap.views.pages;

import com.vaadin.bugrap.services.CommentService;
import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.services.ReportService;
import com.vaadin.bugrap.services.UserService;
import com.vaadin.bugrap.utils.CookieUtils;
import com.vaadin.bugrap.utils.RequestUtils;
import com.vaadin.bugrap.views.component.ReportDetailBreadcrumb;
import com.vaadin.bugrap.views.component.overview.CommentList;
import com.vaadin.bugrap.views.component.overview.OverviewUpdateBar;
import com.vaadin.bugrap.views.model.GroupedComment;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.upload.FailedEvent;
import com.vaadin.flow.component.upload.FileRejectedEvent;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.*;
import elemental.json.Json;
import org.apache.commons.io.IOUtils;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@PageTitle("Report Detail")
@Route(value = "/report")
@RouteAlias(value = "/report")
public class ReportDetailPage extends VerticalLayout implements HasUrlParameter<Long>, OverviewUpdateBar.ReportsUpdateListener {
    private final ProjectService projectService;
    private final ReportService reportService;
    private final UserService userService;
    private final CommentService commentService;
    private final ReportDetailBreadcrumb reportDetailBreadcrumb;
    private final Label reportNameLabel;
    private final OverviewUpdateBar overviewUpdateBar;
    private final CommentList commentList;
    private final RichTextEditor commentRichTextEditor;
    private final MultiFileMemoryBuffer attachmentFileMemoryBuffer;
    private final Upload attachmentUpload;
    private final VerticalLayout reviewAttachmentParentVerticalLayout = new VerticalLayout();

    private Report report;
    private final Map<String, byte[]> fileMap = new HashMap<>();

    //TODO make scrollbar to align top of bottom panel.
    //TODO should display notification if user clicks comment.
    public ReportDetailPage(){
        projectService = new ProjectService();
        reportService = new ReportService();
        userService = new UserService();
        commentService = new CommentService();
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        setSpacing(false);
        setPadding(false);
        setClassName("report-detail-page");


        reportDetailBreadcrumb = new ReportDetailBreadcrumb();
        add(reportDetailBreadcrumb);

        reportNameLabel = new Label();
        reportNameLabel.setClassName("report-name");
        overviewUpdateBar = new OverviewUpdateBar();
        overviewUpdateBar.setListener(this);
        commentList = new CommentList();
        //TODO is is correct way to do it ?
        commentList.setMaxHeight(430, Unit.PIXELS);


        VerticalLayout innerPanel = new VerticalLayout(reportNameLabel, overviewUpdateBar, commentList);
        innerPanel.setPadding(true);
        innerPanel.setHeight(100, Unit.PERCENTAGE);
        innerPanel.setWidth(100, Unit.PERCENTAGE);
        add(innerPanel);





        reviewAttachmentParentVerticalLayout.setClassName("bottom-panel");
        reviewAttachmentParentVerticalLayout.setMargin(false);



        VerticalLayout attachmentLayout = new VerticalLayout();
        attachmentLayout.setWidth("unset");
        attachmentLayout.setSpacing(false);
        attachmentLayout.setPadding(false);
//        attachmentLayout.setWidth(50, Unit.PERCENTAGE);

        Label attachmentsLabel = new Label("Attachments");
        attachmentsLabel.setClassName("attachment-label-header");
        attachmentLayout.add(attachmentsLabel);
        Label attachmentDescriptionLabel = new Label("Only PDF, PNG and JPG files are allowed. \nMax file size is 5 MB.");
        attachmentDescriptionLabel.setClassName("attachment-description-label");
        attachmentDescriptionLabel.setWhiteSpace(HasText.WhiteSpace.BREAK_SPACES);
        attachmentLayout.add(attachmentDescriptionLabel);

        attachmentFileMemoryBuffer = new MultiFileMemoryBuffer();

        attachmentUpload = new Upload(attachmentFileMemoryBuffer);
        attachmentUpload.setMaxFileSize( 5 * 1024 * 1024);
        attachmentUpload.setAcceptedFileTypes("image/png", "image/jpeg", "application/pdf");
        attachmentLayout.add(attachmentUpload);


        commentRichTextEditor = new RichTextEditor("Initial value ");

//        commentRichTextEditor.setWidth(50, Unit.PERCENTAGE);

        HorizontalLayout reviewAttachmentHorizontalLayout = new HorizontalLayout();

        reviewAttachmentHorizontalLayout.setWidth(100, Unit.PERCENTAGE);
        reviewAttachmentHorizontalLayout.add(commentRichTextEditor);
        reviewAttachmentHorizontalLayout.add(attachmentLayout);
        reviewAttachmentParentVerticalLayout.add(reviewAttachmentHorizontalLayout);



        Button saveCommentBtn = new Button("Comment", VaadinIcon.CHECK.create());
        saveCommentBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelCommentBtn = new Button("Cancel", VaadinIcon.CLOSE.create());

        HorizontalLayout buttonContainer = new HorizontalLayout(saveCommentBtn, cancelCommentBtn);
        buttonContainer.setPadding(false);
        buttonContainer.setMargin(false);
        reviewAttachmentParentVerticalLayout.add(buttonContainer);
        add(reviewAttachmentParentVerticalLayout);


        cancelCommentBtn.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            commentRichTextEditor.clear();
            attachmentUpload.getElement().setPropertyJson("files", Json.createArray());
            fileMap.clear();
        });

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
            Label errorHeaderLabel = new Label("File upload failed");
            errorHeaderLabel.setClassName("header");
            Label errorDescriptionLabel = new Label("File format not supported. Allowed file formats: PDF, PNG and JPG");
            errorDescriptionLabel.setClassName("description");
            VerticalLayout verticalLayout = new VerticalLayout(errorHeaderLabel, errorDescriptionLabel);
            verticalLayout.setPadding(false);
            verticalLayout.setSpacing(false);
            Notification notification = new Notification(verticalLayout);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setDuration(5000);
            notification.open();
        });

        saveCommentBtn.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> onSaveClick());
    }
    private void onSaveClick() {
        String currentUserName = CookieUtils.getCurrentUserName(RequestUtils.getCurrentHttpRequest());
        Reporter user = null;
        if(currentUserName != null){
            user = userService.getUser(currentUserName);
        }
        if(user == null){
            Notification.show("Session timeout. Please open home page and return");
            return;
        }


        String value = commentRichTextEditor.getHtmlValue();
        GroupedComment groupedComment = new GroupedComment();
        groupedComment.setAttachments(new ArrayList<>());
        groupedComment.setComment(value);
        groupedComment.setTimestamp(new Date());
        groupedComment.setReport(report);
        groupedComment.setAuthor(user);


        fileMap.forEach((fileName, data) -> {
            GroupedComment.Attachment attachment = new GroupedComment.Attachment();
            attachment.setData(data);
            attachment.setName(fileName);
            groupedComment.getAttachments().add(attachment);
        });
        //TODO this bottom part not worked.
//        attachmentFileMemoryBuffer.getFiles().forEach(fileName -> {
//
//
////                InputStream inputStream = attachmentFileMemoryBuffer.getInputStream(fileName);
////                byte[] fileContentByteArray = IOUtils.toByteArray(inputStream);
//        });
        commentService.saveComment(report, groupedComment);
        commentRichTextEditor.clear();
        attachmentUpload.getElement().setPropertyJson("files", Json.createArray());
        fileMap.clear();
        commentList.setComments(commentService.getGroupedComments(report));

    }

    private void setReport(Report report){
        this.report = report;
        reportDetailBreadcrumb.setReportNameAndVersionName(report.getSummary(), report.getVersion() != null ? report.getVersion().getVersion() : "No version");
        overviewUpdateBar.setProjectVersions(projectService.getProjectVersions(report.getProject()));
        overviewUpdateBar.setReporters(userService.getUsers());
        overviewUpdateBar.setOverview(report.getPriority(), report.getType(), report.getStatus(), report.getAssigned(), report.getVersion());

        reportNameLabel.setText(report.getSummary());

        commentList.setComments(commentService.getGroupedComments(report));
    }

    @Override
    public void setParameter(BeforeEvent event, Long reportId) {
        Report report = reportService.getReport(reportId);
        if(report != null){
            overviewUpdateBar.setVisible(true);
            reviewAttachmentParentVerticalLayout.setVisible(true);
            setReport(report);
        } else {
            overviewUpdateBar.setVisible(false);
            reviewAttachmentParentVerticalLayout.setVisible(false);
            reportDetailBreadcrumb.setReportNameAndVersionName("Invalid Report", "");
            Notification invalidReportNotification = new Notification("Please select a valid report to display details");
            invalidReportNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            invalidReportNotification.open();
        }
    }

    @Override
    public void onUpdate(Report.Priority priority, Report.Type type, Report.Status status, Reporter reporter, ProjectVersion version) {
        report.setPriority(priority);
        report.setType(type);
        report.setStatus(status);
        report.setAssigned(reporter);
        report.setVersion(version);
        this.report =  reportService.save(report);
        reportDetailBreadcrumb.setReportNameAndVersionName(report.getSummary(),report.getVersion() != null ? report.getVersion().getVersion() : "No version" );
        Notification.show("Report updated successfully");
    }
}
