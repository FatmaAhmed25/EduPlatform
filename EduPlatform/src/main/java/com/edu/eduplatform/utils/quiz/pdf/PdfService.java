package com.edu.eduplatform.utils.quiz.pdf;

import com.edu.eduplatform.models.Answer;
import com.edu.eduplatform.models.Question;
import com.edu.eduplatform.models.Quiz;
import com.edu.eduplatform.repos.QuizRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

@Service
public class PdfService {

    @Autowired
    private QuizRepository quizRepository;

    public byte[] generateQuizPdf(Long quizId) throws IOException, DocumentException {
        Optional<Quiz> quizOptional = quizRepository.findById(quizId);
        if (quizOptional.isPresent()) {
            Quiz quiz = quizOptional.get();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, byteArrayOutputStream);
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

                com.itextpdf.text.List list = new com.itextpdf.text.List(List.UNORDERED);
                int answerIndex = 0;
                BaseColor darkGreen = new BaseColor(0, 100, 0);  // RGB for dark green
                for (Answer answer : question.getAnswers()) {
                    Font answerFont = FontFactory.getFont(FontFactory.HELVETICA, 12, answer.isCorrect() ? darkGreen : BaseColor.BLACK);
                    ListItem listItem = new ListItem(answerLabels[answerIndex] + ") " + answer.getText(), answerFont);
                    listItem.setIndentationLeft(20);
                    list.add(listItem);
                    answerIndex++;
                }
                document.add(list);

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
