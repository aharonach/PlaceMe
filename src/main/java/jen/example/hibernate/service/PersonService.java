package jen.example.hibernate.service;

import jen.example.hibernate.entity.Person;

import java.util.List;

public interface PersonService {
    // Save operation
    Person save(Person person);

    // Read operation
    List<Person> fetchList();

    // Update operation
    Person updateById(Person person, Long id);

    // Delete operation
    void deleteById(Long id);
}
