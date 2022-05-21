package jen.example.hibernate.service;

import java.util.List;

// @TODO Add a 'save' method without requesting an ID, only item object.
public interface EntityService<T> {
    T add(T item);

    T getOr404(Long id);

    List<T> all();

    T updateById(Long id, T newItem);

    void deleteById(Long id);
}