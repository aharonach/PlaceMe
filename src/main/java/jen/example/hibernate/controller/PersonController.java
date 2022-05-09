package jen.example.hibernate.controller;

import jen.example.hibernate.entity.Person;
import jen.example.hibernate.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PersonController {

    private final PersonService personService;

    @GetMapping("/get")
    public String get() {
        List<Person> persons = personService.fetchList();
        return persons.toString();
    }

    @GetMapping("/add")
    public String add() {
        Person person = new Person();
        Person newPerson = personService.save(person);
        return newPerson.toString();
    }
}