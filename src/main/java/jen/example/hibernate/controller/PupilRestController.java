package jen.example.hibernate.controller;

import jen.example.hibernate.entity.AttributeValue;
import jen.example.hibernate.entity.Pupil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("pupils")
public class PupilRestController extends BaseRestController<Pupil> {
    @Override
    public List<Pupil> getAll() {
        return null;
    }

    @Override
    public Pupil create(Pupil record) {
        return null;
    }

    @Override
    public Pupil get(Long id) {
        return null;
    }

    @Override
    public Pupil update(Long id, Pupil updatedRecord) {
        return null;
    }

    @Override
    public Pupil delete(Long id) {
        return null;
    }

    /**
     * Get pupil's group.
     *
     * @param id the pupil ID
     * @return List of attribute values
     */
    @GetMapping("/{id}/group")
    public String getGroup(@PathVariable Long id) {
        return null;
    }

    /**
     * Assign pupil to a group.
     *
     * @param id the pupil ID
     * @param groupId list of attribute ids with values
     * @return List of attribute values
     */
    @PutMapping("/{id}/group")
    @PostMapping("/{id}/group")
    public List<AttributeValue> updateGroup(@PathVariable Long id, @RequestBody Long groupId) {
        return null;
    }

    /**
     * Delete a group from pupil.
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}/group")
    public Boolean deleteGroup(@PathVariable Long id) {
        return null;
    }

    /**
     * Get attribute values of a pupil within a template.
     *
     * @param id the pupil ID
     * @param templateId (optional) the template ID
     * @return List of attribute values
     */
    @GetMapping("/{id}/attributes")
    public List<AttributeValue> getAttributes(@PathVariable Long id,
                                                        @RequestBody(required = false) Long templateId) {
        return null;
    }

    /**
     * Create attribute values of a pupil within a template.
     *
     * @param id the pupil ID
     * @param attributeValues list of attribute ids with values
     * @return List of attribute values
     */
    @PutMapping("/{id}/attributes")
    public List<AttributeValue> putAttributes(@PathVariable Long id, @RequestBody Map<Long, Double> attributeValues) {
        return null;
    }

    /**
     * Update single value of attribute of a pupil.
     *
     * @param id
     * @param attributeId
     * @param value
     * @return
     */
    @PostMapping("/{id}/attributes/{attributeId}")
    public Boolean updateAttributes(@PathVariable Long id, @PathVariable Long attributeId,
                                        @RequestBody Double value) {
        return null;
    }

    /**
     * Delete a attribute value from student.
     *
     * @param id
     * @param attributeId
     * @return
     */
    @DeleteMapping("/{id}/attributes/{attributeId}")
    public Boolean deleteAttribute(@PathVariable Long id, @PathVariable Long attributeId) {
        return null;
    }
}
