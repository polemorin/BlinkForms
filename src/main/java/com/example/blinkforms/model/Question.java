package com.example.blinkforms.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Question {
    private String question;
    private ArrayList<String> options;
    private QuestionType type;
    private String answer;
    private boolean mandatory;
}
