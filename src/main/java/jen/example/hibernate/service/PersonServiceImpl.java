package jen.example.hibernate.service;

import jen.example.hibernate.entity.Person;
import jen.example.hibernate.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService{

    private final PersonRepository repository;

    @Override
    public Person save(Person person) {
        return repository.save(person);
    }

    @Override
    public List<Person> fetchList() {
        return (List<Person>)repository.findAll();
    }

    @Override
    public Person updateById(Person person, Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
