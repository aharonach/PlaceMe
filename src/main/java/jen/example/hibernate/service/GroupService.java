package jen.example.hibernate.service;

import jen.example.hibernate.entity.Group;
import jen.example.hibernate.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GroupService implements EntityService<Group> {

    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);

    private final GroupRepository repository;

    @Override
    @Transactional
    public Group add(Group group) {
        // todo: validate that id dont exists
        return repository.save(group);
    }

    @Override
    public Group getOr404(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFound(id));
    }

    @Override
    public List<Group> all() {
        return repository.findAll();
    }

    @Override
    public Group updateById(Long id, Group newGroup) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        Group group = getOr404(id);
        repository.delete(group);
    }

    public Set<Group> getByIds(Set<Long> ids) {
        return repository.getAllByIdIn(ids);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class NotFound extends RuntimeException{
        public NotFound(Long id){
            super("Could not find group " + id);
        }
    }
}
