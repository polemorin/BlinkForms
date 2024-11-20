package com.example.blinkforms.service;

import com.example.blinkforms.model.Form;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;

@Service
public class FormService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Load Form from JSON file
    public Form loadFormFromJson(String filePath) throws IOException {
        return objectMapper.readValue(new File(filePath), Form.class);
    }

    // Load Form from YAML file
    public Form loadFormFromYaml(String filePath) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        return yaml.loadAs(new FileInputStream(filePath), Form.class);
    }

    public void saveFilledFormAsJson(Form form) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Save the form object to a file as JSON
            String fileName = form.getFormName() + java.time.Instant.now().getEpochSecond() + ".json";
            objectMapper.writeValue(new File(fileName), form);
            System.out.println("Form has been written to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFilledFormAsYaml(Form form) {
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);

        String fileName = form.getFormName() + java.time.Instant.now().getEpochSecond() + ".yaml";

        try (FileWriter writer = new FileWriter(fileName)) {
            // Serialize the Java object to YAML and write to the file
            yaml.dump(form, writer);
            System.out.println("Form has been written to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
