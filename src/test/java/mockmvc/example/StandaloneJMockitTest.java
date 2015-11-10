package mockmvc.example;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;

import global.Log;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;

public final class StandaloneJMockitTest {
	@Injectable
	RequestService requestService;
	@Injectable
	CommentValidator validator;
	@Tested(availableDuringSetup = true)
	CommentController commentController;
	MockMvc mockMvc;

	@BeforeClass
	public static  void setupLog() {
		Log.init("StandaloneJMockitTest");
	}

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
		new Expectations() {{
			validator.supports((Class<?>) any);
			result = true;
		}};
	}

	@Test
	public void saveCommentWhenRequestCommentIsNotFound() throws Exception {
		new Expectations() {{
			requestService.getRequestCommentByUUID("123");
			result = null;
		}};

		mockMvc.perform(post("/comment/{uuid}", "123"))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:/dashboard"));
		Log.log();
	}

	//	@Test
	public void saveCommentWhenThereIsAFormError() throws Exception {
		new Expectations() {{
			requestService.getRequestCommentByUUID("123");
			result = new RequestComment();

			validator.validate(any, (Errors) any);
			result = new Delegate() {
				@SuppressWarnings("unused")
				void validate(Object target, Errors errors) {
					errors.reject("forcing some error");
				}
			};
		}};

		mockMvc.perform(post("/comment/{uuid}", "123"))
				.andExpect(status().isOk())
				.andExpect(view().name("comment"));
	}

	//	@Test
	public void saveComment() throws Exception {
		new Expectations() {{
			requestService.getRequestCommentByUUID("123");
			result = new RequestComment();
		}};

		mockMvc.perform(post("/comment/{uuid}", "123"))
				.andExpect(status().isOk())
				.andExpect(view().name("ok"));
	}
}
