package jen.web.exception;

public class EntityAlreadyExists extends BadRequest{
    public EntityAlreadyExists(String message) {
        super(message);
    }
}
