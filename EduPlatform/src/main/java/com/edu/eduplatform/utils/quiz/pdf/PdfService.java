package com.edu.eduplatform.utils.quiz.pdf;

import com.edu.eduplatform.models.*;
import com.edu.eduplatform.repos.EssaySubmissionRepo;
import com.edu.eduplatform.repos.MCQSubmissionRepo;
import com.edu.eduplatform.repos.QuizRepository;
import com.edu.eduplatform.repos.StudentRepo;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

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
                gradeLabel = "Grade: " + mcqSubmission.getTotalScore() + " out of " + quiz.getTotalGrade();
            } else {
                // Essay Quiz
                EssaySubmission essaySubmission = essaySubmissionRepo.findByQuizQuizIdAndStudentUserID(quizId, studentId);
                       // .orElseThrow(() -> new IllegalArgumentException("Essay Submission not found"));
                submissionTimeLabel = "Submission Time: " + essaySubmission.getSubmittedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                gradeLabel = "Grade: " + essaySubmission.getOverallGrade() + " out of " + quiz.getTotalGrade();
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
                    com.itextpdf.text.List list = new com.itextpdf.text.List(List.UNORDERED);
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
