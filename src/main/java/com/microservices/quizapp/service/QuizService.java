package com.microservices.quizapp.service;

import com.microservices.quizapp.dao.QuestionDao;
import com.microservices.quizapp.dao.QuizDao;
import com.microservices.quizapp.model.Question;
import com.microservices.quizapp.model.QuestionWrapper;
import com.microservices.quizapp.model.Quiz;
import com.microservices.quizapp.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    QuizDao quizDao;

    @Autowired
    QuestionDao questionDao;

    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {

        List<Question> questions = questionDao.findRandomQuestionsByCategory(category, numQ);

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>("success", HttpStatus.CREATED);

    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {

        //fetch quiz obj from database
        Optional<Quiz> quiz = quizDao.findById(id);

        //get Questions from quiestion db
        List<Question> questionsFromDB = quiz.get().getQuestions();

        //get questions for user
        List<QuestionWrapper> questionsForUser = new ArrayList<>();

        for(Question q: questionsFromDB){
            QuestionWrapper qw = new QuestionWrapper(q.getId(), q.getQuestionTitle(), q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4());
            questionsForUser.add(qw);
        }
        return new ResponseEntity<>(questionsForUser, HttpStatus.OK);



    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {

        Quiz quiz = quizDao.findById(id).get();
        List<Question> questions = quiz.getQuestions();
        int right = 0;
        for (int i = 0; i < responses.size(); i++) {
            Response response = responses.get(i);
            Question question = questions.get(i);
            if (response.getResponse().equals(question.getRightAnswer())) {
                right++;
            }
        }
        return new ResponseEntity<>(right, HttpStatus.OK);
    }
}
