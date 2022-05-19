package jen.example.hibernate.service;

import java.util.List;

public interface EntityService<T> {
    T add(T item);

    T getOr404(Long id);

    List<T> all();

    T updateById(Long id, T newItem);

    void deleteById(Long id);

    void validate(T item);
}