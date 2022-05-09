package jen.example.hibernate.service;

import jen.example.hibernate.entity.Person;
import jen.example.hibernate.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService{

    @Autowired
    private PersonRepository personRepository;

    @Override
    public Person save(Person person) {
        return personRepository.save(person);
    }

    @Override
    public List<Person> fetchList() {
        return (List<Person>)personRepository.findAll();
    }

    @Override
    public Person updateById(Person person, Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
