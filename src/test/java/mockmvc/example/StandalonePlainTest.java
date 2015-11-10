package mockmvc.example;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import global.Log;
import mockit.Deencapsulation;

public final class StandalonePlainTest {
	RequestService requestService;
	CommentValidator validator;
	CommentController commentController;
	MockMvc mockMvc;

	@BeforeClass
	public static  void setupLog() {
		Log.init("StandaloneMockitoTest");
	}

	@Before
	public void setup() {
		RequestRepository requestRepository = new RequestRepository();
		requestService = new RequestService();
		Deencapsulation.setField(requestService, "repository", requestRepository);
		validator = new CommentValidator() {
			@Override
			public boolean supports(Class<?> clazz) {
				return true;
			}
		};
		commentController = new CommentController();
		Deencapsulation.setField(commentController, "requestService", requestService);
		Deencapsulation.setField(commentController, "validator", validator);

		mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
	}

	@Test
	public void saveCommentWhenRequestCommentIsNotFound() throws Exception {
		mockMvc.perform(post("/comment/{uuid}", "123"))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:/dashboard"));
		Log.log();
	}

	//	@Test
	public void saveCommentWhenThereIsAFormError() throws Exception {
		mockMvc.perform(post("/comment/{uuid}", "123"))
				.andExpect(status().isOk())
				.andExpect(view().name("comment"));
	}

	//	@Test
	public void saveComment() throws Exception {
		mockMvc.perform(post("/comment/{uuid}", "123"))
				.andExpect(status().isOk())
				.andExpect(view().name("ok"));
	}
}
