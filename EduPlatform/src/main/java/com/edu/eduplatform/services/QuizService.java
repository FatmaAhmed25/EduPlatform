package com.edu.eduplatform.services;


import com.edu.eduplatform.dtos.AnswerDTO;
import com.edu.eduplatform.dtos.QuestionDTO;
import com.edu.eduplatform.dtos.QuizDTO;
import com.edu.eduplatform.models.Answer;
import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.models.Question;
import com.edu.eduplatform.models.Quiz;
import com.edu.eduplatform.repos.AnswerRepository;
import com.edu.eduplatform.repos.CourseRepo;
import com.edu.eduplatform.repos.QuestionRepository;
import com.edu.eduplatform.repos.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private CourseRepo courseRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    public Quiz createQuiz(QuizDTO quizDTO) {
        Course course = courseRepository.findById(quizDTO.getCourseId()).orElseThrow(() -> new RuntimeException("Course not found"));

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDTO.getTitle());
        quiz.setStartTime(quizDTO.getStartTime());
        quiz.setEndTime(quizDTO.getEndTime());
        quiz.setTotalGrade(quizDTO.getTotalGrade());
        quiz.setCourse(course);

        quiz = quizRepository.save(quiz);

        for (QuestionDTO questionDTO : quizDTO.getQuestions()) {
            Question question = new Question();
            question.setText(questionDTO.getText());
            question.setPoints(questionDTO.getPoints());
            question.setQuiz(quiz);
            question = questionRepository.save(question);

            for (AnswerDTO answerDTO : questionDTO.getAnswers()) {
                Answer answer = new Answer();
                answer.setText(answerDTO.getText());
                answer.setCorrect(answerDTO.isCorrect());
                answer.setQuestion(question);
                answerRepository.save(answer);
            }
        }
        return quiz;
    }

    public Question addQuestionToQuiz(Long quizId, Question question) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
        question.setQuiz(quiz);
        return questionRepository.save(question);
    }

    public Answer addAnswerToQuestion(Long questionId, Answer answer) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new RuntimeException("Question not found"));
        answer.setQuestion(question);
        return answerRepository.save(answer);
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Quiz getQuizById(Long quizId) {
        return quizRepository.findByQuizIdWithQuestions(quizId);

    }

    public List<Quiz> getQuizzesByCourseId(Long courseId) {
        return quizRepository.findByCourseCourseId(courseId);
    }
}
