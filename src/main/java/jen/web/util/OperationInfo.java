package jen.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OperationInfo {
    private List<String> errors = new ArrayList<>();

    public void addError(String error){
        this.errors.add(error);
    }
    public void addErrors(List<String> errors){
        this.errors.addAll(errors);
    }
    public List<String> getErrors(){
        return Collections.unmodifiableList(errors);
    }
    public int getErrorsCount(){
        return errors.size();
    }
}
