package mockmvc.example;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;

import global.Log;
import mockit.Capturing;
import mockit.Delegate;
import mockit.Expectations;

/**
 * The same tests as in {@link StandaloneJMockitTest}, but creating the {@link MockMvc} object with a call to
 * {@link MockMvcBuilders#webAppContextSetup(WebApplicationContext)} instead of
 * {@link MockMvcBuilders#standaloneSetup(Object...)}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebAppContextJMockitTest.WebAppConfig.class)
public final class WebAppContextJMockitTest {
	@Configuration
	@ComponentScan(excludeFilters = @ComponentScan.Filter(value = Configuration.class, type = FilterType.ANNOTATION))
	static class WebAppConfig {
		@Bean
		RequestService requestService() {
			Log.append("@rs ");
			return new RequestService() {
				@Override
				public RequestComment getRequestCommentByUUID(String uuid) {
					Log.append("grc");
					return null;
				}
			};
		}

		@Bean
		CommentValidator validator() {
			Log.append("@cv ");
			return new CommentValidator();
		}
	}

	// These two fields provide access to the beans created in the configuration, as mocked instances.
	@Capturing
	RequestService requestService;
	@Capturing
	CommentValidator validator;

	@Autowired
	WebApplicationContext context;
	MockMvc mockMvc;

	@BeforeClass
	public static  void setupLog() {
		Log.init("WebAppContextJMockitTest");
	}

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

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
		Log.log();
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
		Log.log();
	}
}
