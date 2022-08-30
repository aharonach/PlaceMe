package jen.web.controller.handler;

import jen.web.util.PagesAndSortHandler;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@ControllerAdvice
public class PaginationParamsHandler {

    @ModelAttribute
    public void getPaginationParams(@RequestParam Optional<Integer> page,
                                    @RequestParam Optional<String> sortBy,
                                    @RequestParam(required = false) boolean descending,
                                    Model model){
        model.addAttribute("paginationInfo", new PagesAndSortHandler.PaginationInfo(page, sortBy, descending));
    }

}
