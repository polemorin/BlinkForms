package com.example.blinkforms;

import com.example.blinkforms.model.Form;
import com.example.blinkforms.service.FormService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.*;

@SpringBootApplication
public class BlinkFormsApplication implements CommandLineRunner {

    @Autowired
    private FormService formService;

    public static void main(String[] args) {
        SpringApplication.run(BlinkFormsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize a scanner to read user input from the terminal
        Scanner scanner = new Scanner(System.in);
        ArrayList<Form> loadedForms = new ArrayList<>();

        // Main menu
        while (true) {
            System.out.println("Welcome, choose an action:");
            System.out.println("1. import a form");
            System.out.println("2. fill in a form");
            String userInput = scanner.nextLine();

            if (userInput.equals("1")) {
                System.out.println("enter the path to the form");
                String filePath = scanner.nextLine();
                try {
                    importForm(filePath, loadedForms);
                } catch (IOException e) {
                    System.out.println("Error loading form: " + e.getMessage());
                }
            } else if (userInput.equals("2")) {
                System.out.println("Choose a form:");

                showForms(loadedForms);

                userInput = scanner.nextLine();

                try {
                    int formId = Integer.parseInt(userInput) - 1;
                    if (formId < 0 || formId >= loadedForms.size()) {
                        throw new IndexOutOfBoundsException("Invalid input! Please enter a number corresponding to the form");
                    }

                    fillForm(loadedForms.get(formId));

                    System.out.println("Thank you for filling the form! Here is the filled form:");

                    displayForm(loadedForms.get(formId));

                    System.out.println("Save to file? y/n");
                    userInput = scanner.nextLine();

                    if (userInput.equals("y")) {
                        boolean validFileType = false;
                        while (!validFileType) {
                            System.out.println("Save as JSON or YAML? json/yaml");
                            userInput = scanner.nextLine();
                            if (userInput.equals("json")) {
                                formService.saveFilledFormAsJson(loadedForms.get(formId));
                                validFileType = true;
                            } else if (userInput.equals("yaml")) {
                                formService.saveFilledFormAsYaml(loadedForms.get(formId));
                                validFileType = true;
                            } else {
                                System.out.println("Incorrect file type please try again");
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a number");
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                }

            } else {
                System.out.println("Invalid input, please try again");
            }
        }
    }

    private static void showForms(ArrayList<Form> loadedForms) {
        for (int i = 0; i < loadedForms.size(); i++) {
            System.out.println((i + 1) + ".   " + loadedForms.get(i).getFormName());
        }
    }

    private void importForm(String filePath, ArrayList<Form> loadedForms) throws IOException {
        Form form = loadForm(filePath);
        loadedForms.add(form);
        System.out.println("form imported.");
    }


    private Form loadForm(String filePath) throws IOException {
        // Load the form from either a JSON or YAML file based on the file extension
        if (filePath.endsWith(".json")) {
            return formService.loadFormFromJson(filePath);
        } else if (filePath.endsWith(".yaml") || filePath.endsWith(".yml")) {
            return formService.loadFormFromYaml(filePath);
        } else {
            throw new IOException("Unsupported file format. Only JSON and YAML are supported.");
        }
    }

    private void displayForm(Form form) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            String jsonString = objectMapper.writeValueAsString(form);
            System.out.println(jsonString);
        } catch (IOException e) {
            System.out.println("Error serializing form: " + e.getMessage());
        }
    }

    private void fillForm(Form form) {
        //ask user question and update form with answers

        Scanner scanner = new Scanner(System.in);
        form.getQuestions().forEach(question -> {
            String answer = null;
            do {
                System.out.println(question.getQuestion());
                if(question.isMandatory()){
                    System.out.println("This is a mandatory question");
                }

                if (question.getType().name().equals("TEXT")) {
                    answer = scanner.nextLine();
                    question.setAnswer(answer);
                } else if (question.getType().name().equals("MULTIPLE_CHOICE")) {
                    System.out.println("Options: " + String.join(", ", question.getOptions()));
                    answer = scanner.nextLine();
                    if (question.getOptions().contains(answer)) {
                        question.setAnswer(answer);
                    } else {
                        System.out.println("Invalid answer. Please try again.");
                        answer = null;
                    }
                }
            } while (answer == null || (answer.isEmpty() && question.isMandatory()));
        });
    }
}
