package mockmvc.example;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import global.Log;

@Controller @RequestMapping("/comment/{uuid}")
public class CommentController {
	@Autowired
	private RequestService requestService;
	@Autowired
	private CommentValidator validator;

	public CommentController() {
		Log.append("CC ");
	}

	@InitBinder("commentForm")
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@RequestMapping(method = RequestMethod.POST)
	public String saveComment(
			@PathVariable String uuid, @Valid @ModelAttribute CommentForm commentForm, BindingResult result,
			Model model) {
		RequestComment requestComment = requestService.getRequestCommentByUUID(uuid);

		if (requestComment == null) {
			return "redirect:/dashboard";
		}

		if (result.hasErrors()) {
			return "comment";
		}

		return "ok";
	}
}
