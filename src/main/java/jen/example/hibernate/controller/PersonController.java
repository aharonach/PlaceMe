package jen.example.hibernate.controller;

import jen.example.hibernate.entity.*;
import jen.example.hibernate.service.PersonService;
import jen.example.hibernate.service.PupilService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PersonController {

    private final PersonService personService;

    private final PupilService pupilService;

    @GetMapping("/")
    public Pupil test() {

        Pupil pupil = new Pupil();
        pupil.setGender(Pupil.Gender.MALE);
        pupil.setFirstName("Gal");
        pupil.setLastName("Yeshua");

//        List<AttributeValue> attributeValues = new ArrayList<>();
//        Attribute attribute = new RangeAttribute();
//        attribute.setName("some attribute");
//        attribute.setDescription("some description");
//        attribute.setPriority(10);
//
//        AttributeValue attributeValue = new AttributeValue();
//        attributeValue.setAttribute(attribute);
//        attributeValue.setPupil(pupil);
//        attributeValue.setValue(5);
//
//        attributeValues.add(attributeValue);
//
//        pupil.setAttributeValues(attributeValues);

        return pupilService.save(pupil);
    }




    @GetMapping("/get")
    public String get() {
        List<Person> persons = personService.fetchList();
        return persons.toString();
    }

    @GetMapping("/add")
    public String add(@RequestParam String name) {
        Person person = new Person();
        person.setName(name);
        Person newPerson = personService.save(person);
        return newPerson.toString();
    }
}