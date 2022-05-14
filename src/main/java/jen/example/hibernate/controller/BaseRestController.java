package jen.example.hibernate.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class BaseRestController<T> {
    /**
     * Get all records.
     *
     * @return List of records
     */
    @GetMapping("/")
    public abstract List<T> getAll();

    /**
     * Create a record.
     *
     * @param record record details
     * @return created record
     */
    @PutMapping("/")
    public abstract T create(@RequestBody T record);

    /**
     * Get one record by ID.
     *
     * @param id record id
     * @return entity record
     */
    @GetMapping("/{id}")
    public abstract T get(@PathVariable Long id);

    /**
     * Update a record by ID.
     *
     * @param id record id
     * @param updatedRecord the updated record with the new details
     * @return the updated record
     */
    @PostMapping("/{id}")
    public abstract T update(@PathVariable Long id, @RequestBody T updatedRecord);

    /**
     * Delete a record by ID.
     *
     * @return List of records
     */
    @DeleteMapping("/{id}")
    public abstract T delete(@PathVariable Long id);
}
