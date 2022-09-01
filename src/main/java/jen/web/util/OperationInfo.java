package jen.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OperationInfo {
    private int success = 0;
    private List<String> errors = new ArrayList<>();

    public void addError(String error){
        errors.add(error);
    }

    public void addSuccess(){
        success++;
    }

    public List<String> getErrors(){
        return Collections.unmodifiableList(errors);
    }

    public int getTotalOperationCount(){
        return errors.size() + success;
    }

    public int getErrorsCount(){
        return errors.size();
    }

    public int getSuccessCount(){
        return success;
    }
}
