package web.controller;

import web.util.PagesAndSortHandler;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class BaseRestController<T> {

    /**
     * Get all records.
     *
     * @return List of records
     */
    @GetMapping()
    public abstract ResponseEntity<?> getAll(@ParameterObject @ModelAttribute PagesAndSortHandler.PaginationInfo pageInfo);

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
