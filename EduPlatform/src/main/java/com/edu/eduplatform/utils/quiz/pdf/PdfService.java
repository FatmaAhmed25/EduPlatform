package com.edu.eduplatform.utils.quiz.pdf;

import com.edu.eduplatform.models.*;
import com.edu.eduplatform.repos.*;
import com.google.cloud.storage.Blob;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PdfService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private StudentRepo studentRepository;

    @Autowired
    private MCQSubmissionRepo mcqSubmissionRepo;

    @Autowired
    private EssaySubmissionRepo essaySubmissionRepo;

    @Autowired
    private CheatingReportRepo cheatingReportRepo;

    @Value("${bucket.name}")
    private String bucketName;

    private BaseColor lightBlue = new BaseColor(94, 135, 247);

    private Storage storage;

    public PdfService() throws IOException {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    public byte[] generateCheatingReportPdf(Long cheatingReportId) throws IOException, DocumentException {
        Optional<CheatingReport> cheatingReportOptional = cheatingReportRepo.findById(cheatingReportId);

        if (cheatingReportOptional.isPresent()) {
            CheatingReport cheatingReport = cheatingReportOptional.get();
            QuizSubmission quizSubmission = cheatingReport.getQuizSubmission();
            Student student = quizSubmission.getStudent();
            Quiz quiz = quizSubmission.getQuiz();

            String folderName = cheatingReport.getFolderName();
            List<String> imageUrls = fetchImageUrlsFromFirebase(folderName);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

            String logoPath = "src/main/resources/logo.png";
            FooterPageEvent event = new FooterPageEvent(logoPath);
            writer.setPageEvent(event);

            document.open();

            // Add quiz title, student name, course name, and cheating status
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidths(new int[]{100});
            headerTable.setSpacingAfter(20f);
            headerTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.setWidthPercentage(100);

            headerTable.addCell(getStyledCell("Quiz Title: " + quiz.getTitle(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16), lightBlue));
            headerTable.addCell(getStyledCell("Student Name: " + student.getUsername(), FontFactory.getFont(FontFactory.HELVETICA, 12), BaseColor.WHITE));
            headerTable.addCell(getStyledCell("Course Name: " + quiz.getCourse().getTitle(), FontFactory.getFont(FontFactory.HELVETICA, 12), BaseColor.WHITE));
            headerTable.addCell(getStyledCell("Cheating Status: " + quizSubmission.getCheatingStatus(), FontFactory.getFont(FontFactory.HELVETICA, 12), BaseColor.WHITE));
            document.add(headerTable);

            // Add images with timestamps in a table
            PdfPTable imageTable = new PdfPTable(2);
            imageTable.setWidths(new int[]{50, 50});
            imageTable.setWidthPercentage(100);
            imageTable.setSpacingBefore(10f);
            imageTable.setSpacingAfter(10f);

            for (String imageUrl : imageUrls) {
                Image image = Image.getInstance(downloadImageFromFirebase(imageUrl));
                image.scaleToFit(200, 200); // resize images to 200x200

                PdfPCell imageCell = new PdfPCell(image);
                imageCell.setBorderColor(lightBlue);
                imageCell.setPaddingBottom(15f); // Add space between cells

                String timestamp = extractTimestampFromImageName(imageUrl);
                PdfPCell timestampCell = new PdfPCell(new Paragraph("Timestamp: " + timestamp, FontFactory.getFont(FontFactory.HELVETICA, 12)));
                timestampCell.setBorderColor(lightBlue);
                timestampCell.setPaddingBottom(15f); // Add space between cells

                imageTable.addCell(imageCell);
                imageTable.addCell(timestampCell);
            }
            document.add(imageTable);

            document.close();
            return byteArrayOutputStream.toByteArray();
        } else {
            throw new IllegalArgumentException("Cheating Report not found");
        }
    }

    private PdfPCell getStyledCell(String text, Font font, BaseColor backgroundColor) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBorderColor(lightBlue);
        cell.setBackgroundColor(backgroundColor);
        cell.setPaddingBottom(10f); // Add space between cells
        return cell;
    }
    private List<String> fetchImageUrlsFromFirebase(String folderName) {
        Bucket bucket = storage.get(bucketName);
        return StreamSupport.stream(bucket.list(Storage.BlobListOption.prefix(folderName)).iterateAll().spliterator(), false)
                .map(Blob::getName)
                .collect(Collectors.toList());
    }

    private byte[] downloadImageFromFirebase(String imageUrl) throws IOException {
        Blob blob = storage.get(bucketName, imageUrl);
        return blob.getContent();
    }

    private String extractTimestampFromImageName(String imageName) {
        // Example: processedimage_2024-07-03_12-16-52.jpg
        String[] parts = imageName.split("_");
        return parts[1] + " " + parts[2].replace(".jpg", "").replace("-", ":");
    }

    public byte[] generateStudentSubmissionPdf(Long studentId, Long quizId) throws IOException, DocumentException {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        Optional<Quiz> quizOptional = quizRepository.findById(quizId);

        if (studentOptional.isPresent() && quizOptional.isPresent()) {
            Student student = studentOptional.get();
            Quiz quiz = quizOptional.get();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

            // Path to your logo file
            String logoPath = "src/main/resources/logo.png";
            FooterPageEvent event = new FooterPageEvent(logoPath);
            writer.setPageEvent(event);

            document.open();

            // Add quiz title centered at the top


            // Add some spacing after the quiz title
            document.add(new Paragraph("\n"));

            // Add student name and submission time
            Paragraph studentInfo = new Paragraph();
            studentInfo.add(new Chunk("Student Name: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK)));
            studentInfo.add(new Chunk(student.getUsername(), FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            studentInfo.add(Chunk.NEWLINE);

            // Retrieve submission time based on quiz type
            String submissionTimeLabel = "";
            String gradeLabel = "";
            if (quiz.getQuestions().stream().anyMatch(q -> q instanceof MCQQuestion)) {
                // MCQ Quiz
                MCQSubmission mcqSubmission = mcqSubmissionRepo.findByQuizQuizIdAndStudentUserID(quizId, studentId);
                       // .orElseThrow(() -> new IllegalArgumentException("MCQ Submission not found"));
                submissionTimeLabel = "Submission Time: " + mcqSubmission.getSubmissionTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                gradeLabel = "Grade: " + mcqSubmission.getTotalGrade() + " out of " + quiz.getTotalGrade();
            } else {
                // Essay Quiz
                EssaySubmission essaySubmission = essaySubmissionRepo.findByQuizQuizIdAndStudentUserID(quizId, studentId);
                       // .orElseThrow(() -> new IllegalArgumentException("Essay Submission not found"));
                submissionTimeLabel = "Submission Time: " + essaySubmission.getSubmissionTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                gradeLabel = "Grade: " + essaySubmission.getTotalGrade()+ " out of " + quiz.getTotalGrade();
            }

            studentInfo.add(new Chunk(submissionTimeLabel, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            studentInfo.setAlignment(Element.ALIGN_RIGHT);
            document.add(studentInfo);

            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));


            // Add grade label in a circle
            PdfPTable table = new PdfPTable(1);
            PdfPCell cell = new PdfPCell(new Phrase(gradeLabel, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.WHITE)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLUE);
            cell.setFixedHeight(30);
            table.addCell(cell);
            document.add(table);

            // Add some spacing after the grade circle
            document.add(new Paragraph("\n"));

            Paragraph quizTitle = new Paragraph(quiz.getTitle(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK));
            quizTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(quizTitle);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));


            // Determine quiz type
            if (quiz.getQuestions().stream().anyMatch(q -> q instanceof MCQQuestion)) {
                // MCQ Quiz
                MCQSubmission mcqSubmission = mcqSubmissionRepo.findByQuizQuizIdAndStudentUserID(quizId, studentId);
                    //    .orElseThrow(() -> new IllegalArgumentException("MCQ Submission not found"));

                addMCQSubmissionDetails(document, mcqSubmission);
            } else {
                // Essay Quiz
                EssaySubmission essaySubmission = essaySubmissionRepo.findByQuizQuizIdAndStudentUserID(quizId, studentId);
                       // .orElseThrow(() -> new IllegalArgumentException("Essay Submission not found"));

                addEssaySubmissionDetails(document, essaySubmission);
            }

            document.close();
            return byteArrayOutputStream.toByteArray();
        } else {
            throw new IllegalArgumentException("Student or Quiz not found");
        }
    }

    private void addMCQSubmissionDetails(Document document, MCQSubmission mcqSubmission) throws DocumentException {
        // Add total score
//        Paragraph totalScoreParagraph = new Paragraph("Total Score: " + mcqSubmission.getTotalScore(),
//                FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
//        document.add(totalScoreParagraph);

        // Add answers
        for (StudentMCQAnswer answer : mcqSubmission.getAnswers()) {
            Paragraph questionParagraph = new Paragraph("Question: " + answer.getQuestion().getText() + " (Points: " + answer.getQuestion().getPoints() + ")",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK));
            document.add(questionParagraph);

            // Check if the answer is correct to set color
            BaseColor answerColor = answer.isCorrect() ? BaseColor.GREEN : BaseColor.RED;

            Paragraph answerParagraph = new Paragraph("Answer: " + answer.getAnswer().getText(),
                    FontFactory.getFont(FontFactory.HELVETICA, 12, answerColor));
            document.add(answerParagraph);

            // Add some spacing after each question-answer pair
            document.add(new Paragraph("\n"));
        }
    }

    private void addEssaySubmissionDetails(Document document, EssaySubmission essaySubmission) throws DocumentException {
//        // Add overall grade
//        Paragraph overallGradeParagraph = new Paragraph("Overall Grade: " + essaySubmission.getOverallGrade(),
//                FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
//        document.add(overallGradeParagraph);

        // Add answers
        for (StudentEssayAnswer answer : essaySubmission.getAnswers()) {
            Paragraph questionParagraph = new Paragraph("Question: " + answer.getQuestion().getText() + " (Points: " + answer.getQuestion().getPoints() + ")",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK));
            document.add(questionParagraph);


            Paragraph answerParagraph = new Paragraph("Answer: " + answer.getAnswer(),
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            document.add(answerParagraph);

            // Add some spacing after each question-answer pair
            document.add(new Paragraph("\n"));
        }
    }

    public byte[] generateQuizPdf(Long quizId) throws IOException, DocumentException {
        Optional<Quiz> quizOptional = quizRepository.findById(quizId);
        if (quizOptional.isPresent()) {
            Quiz quiz = quizOptional.get();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

            // Path to your logo file
            String logoPath = "src/main/resources/logo.png";
            FooterPageEvent event = new FooterPageEvent(logoPath);
            writer.setPageEvent(event);

            document.open();

            // Add course title centered at the top
            Paragraph courseTitle = new Paragraph(quiz.getCourse().getTitle(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK));
            courseTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(courseTitle);

            // Add some spacing after the course title
            document.add(new Paragraph("\n"));

            // Add quiz title
            Paragraph quizTitle = new Paragraph(quiz.getTitle(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK));
            document.add(quizTitle);

            // Add some spacing after the quiz title
            document.add(new Paragraph("\n"));

            // Add questions and answers
            char[] answerLabels = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
            for (Question question : quiz.getQuestions()) {
                Paragraph questionParagraph = new Paragraph("Question: " + question.getText() + " (Points: " + question.getPoints() + ")",
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK));
                document.add(questionParagraph);

                // Check if the question is an MCQQuestion
                if (question instanceof MCQQuestion) {
                    com.itextpdf.text.List list = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
                    int answerIndex = 0;
                    BaseColor darkGreen = new BaseColor(0, 100, 0);  // RGB for dark green
                    for (Answer answer : ((MCQQuestion) question).getAnswers()) {
                        Font answerFont = FontFactory.getFont(FontFactory.HELVETICA, 12, answer.isCorrect() ? darkGreen : BaseColor.BLACK);
                        ListItem listItem = new ListItem(answerLabels[answerIndex] + ") " + answer.getText(), answerFont);
                        listItem.setIndentationLeft(20);
                        list.add(listItem);
                        answerIndex++;
                    }
                    document.add(list);
                }

                // Add some spacing after each question
                document.add(new Paragraph("\n"));
            }

            document.close();
            return byteArrayOutputStream.toByteArray();
        } else {
            throw new IllegalArgumentException("Quiz not found");
        }
    }
}
