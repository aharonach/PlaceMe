package jen.example.hibernate.controller;

import jen.example.hibernate.assembler.GroupModelAssembler;
import jen.example.hibernate.entity.Group;
import jen.example.hibernate.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/groups")
public class GroupRestController extends BaseRestController<Group> {
    private static final Logger logger = LoggerFactory.getLogger(GroupRestController.class);
    private final GroupService service;
    private final GroupModelAssembler assembler;

    @Override
    @GetMapping()
    public ResponseEntity<?> getAll() {
        return null;
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return null;
    }

    @Override
    @PutMapping()
    public ResponseEntity<?> create(@RequestBody Group newRecord) {
        return null;
    }

    @Override
    public ResponseEntity<?> update(Long id, Group updatedRecord) {
        return null;
    }

    @Override
    public ResponseEntity<?> delete(Long id) {
        return null;
    }

    @GetMapping("/{id}/template")
    public ResponseEntity<?> getGroupTemplate(@PathVariable Long id){
        return null;
    }

    @GetMapping("/{id}/pupils")
    public ResponseEntity<?> getPupilsOfGroup(@PathVariable Long id){
        return null;
    }
}
