package jen.web.controller.handler;

import jen.web.util.PagesAndSortHandler;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@ControllerAdvice
public class PaginationParamsHandler {

    @ModelAttribute
    public void getPaginationParams(@RequestParam Optional<Integer> page,
                                    @RequestParam Optional<String> sortField,
                                    @RequestParam Optional<String> sortDirection,
                                    Model model){
        Sort.Direction direction = Sort.Direction.ASC;
        if(sortDirection.isPresent() && sortDirection.get().equalsIgnoreCase("desc")){
            direction = Sort.Direction.DESC;
        }
        model.addAttribute("paginationInfo", new PagesAndSortHandler.PaginationInfo(page, sortField, direction));
    }

}
