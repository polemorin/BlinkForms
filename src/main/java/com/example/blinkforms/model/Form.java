package com.example.blinkforms.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Form {
    private String formName;
    private ArrayList<Question> questions;
}
