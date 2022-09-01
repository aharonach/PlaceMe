package jen.web.util;

import jen.web.entity.Template;
import jen.web.service.RepositoryTestUtils;
import jen.web.service.TemplateService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ImportExportUtilsTest {

    @Autowired ImportExportUtils importExportUtils;

    @Autowired TemplateService templateService;
    @Autowired RepositoryTestUtils repositoryTestUtils;


    @BeforeEach
    void setUp() {
        repositoryTestUtils.clearAllData();
    }

    @AfterEach
    void tearDown() {
        repositoryTestUtils.verifyAllTablesAreEmpty();
    }

    @Test
    public void checkFields() throws IOException {
//        Template receivedTemplate = templateService.add(repositoryTestUtils.createTemplate1());
//        System.out.println(importExportUtils.getFieldList(receivedTemplate));
//        templateService.deleteById(receivedTemplate.getId());
//
//
//        try(BufferedReader csvReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream("text".getBytes())))){
//            String line;
//            while((line = csvReader.readLine()) != null){
//                System.out.println(line);
//            }
//        }
    }

}