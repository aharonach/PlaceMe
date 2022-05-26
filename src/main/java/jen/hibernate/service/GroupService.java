package jen.hibernate.service;

import jen.hibernate.entity.Group;
import jen.hibernate.exception.NotFound;
import jen.hibernate.repository.GroupRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GroupService implements EntityService<Group> {

    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);

    @Getter
    private final GroupRepository repository;

    @Override
    @Transactional
    public Group add(Group group) {
        // todo: validate that id dont exists
        return repository.save(group);
    }

    @Override
    public Group getOr404(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFound("Could not find group " + id));
    }

    @Override
    public List<Group> all() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Group updateById(Long id, Group newGroup) {
        Group group = getOr404(id);

        group.setName(newGroup.getName());
        group.setDescription(newGroup.getDescription());
        group.setTemplate(newGroup.getTemplate());
        group.setPupils(newGroup.getPupils());

        return repository.save(group);
    }

    @Override
    public void deleteById(Long id) {
        Group group = getOr404(id);
        repository.delete(group);
    }

    public Set<Group> getByIds(Set<Long> ids) {
        return repository.getAllByIdIn(ids);
    }
}
