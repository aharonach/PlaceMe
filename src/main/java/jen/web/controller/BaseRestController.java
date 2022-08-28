package jen.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

public abstract class BaseRestController<T> {

    /**
     * Get all records.
     *
     * @return List of records
     */
    public abstract ResponseEntity<?> getAll(@RequestParam Optional<Integer> page, @RequestParam Optional<String> sortBy);

    /**
     * Get one record by ID.
     *
     * @param id record id
     * @return entity record
     */
    public abstract ResponseEntity<?> get(Long id);

    /**
     * Create a record.
     *
     * @param newRecord record details
     * @return created record
     */
    public abstract ResponseEntity<?> create(T newRecord);

    /**
     * Update a record by ID.
     *
     * @param id record id
     * @param updatedRecord the updated record with the new details
     * @return the updated record
     */
    public abstract ResponseEntity<?> update(Long id, T updatedRecord);

    /**
     * Delete a record by ID.
     *
     * @return List of records
     */

    public abstract ResponseEntity<?> delete(Long id);
}
