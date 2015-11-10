package mockmvc.example;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;

import global.Log;

public final class StandaloneMockitoTest {
	@Mock
	RequestService requestService;
	@Mock
	CommentValidator validator;
	@InjectMocks
	CommentController commentController;
	MockMvc mockMvc;

	@BeforeClass
	public static  void setupLog() {
		Log.init("StandaloneMockitoTest");
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
		when(validator.supports(any(Class.class))).thenReturn(true);
	}

	@Test
	public void saveCommentWhenRequestCommentIsNotFound() throws Exception {
		when(requestService.getRequestCommentByUUID("123")).thenReturn(null);

		mockMvc.perform(post("/comment/{uuid}", "123"))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:/dashboard"));
		Log.log();
	}

	//	@Test
	public void saveCommentWhenThereIsAFormError() throws Exception {
		when(requestService.getRequestCommentByUUID("123")).thenReturn(new RequestComment());

		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
				Errors errors = (Errors) invocationOnMock.getArguments()[1];
				errors.reject("forcing some error");
				return null;
			}
		}).when(validator).validate(anyObject(), any(Errors.class));

		mockMvc.perform(post("/comment/{uuid}", "123"))
				.andExpect(status().isOk())
				.andExpect(view().name("comment"));
	}

	//	@Test
	public void saveComment() throws Exception {
		when(requestService.getRequestCommentByUUID("123")).thenReturn(new RequestComment());

		mockMvc.perform(post("/comment/{uuid}", "123"))
				.andExpect(status().isOk())
				.andExpect(view().name("ok"));
	}
}
