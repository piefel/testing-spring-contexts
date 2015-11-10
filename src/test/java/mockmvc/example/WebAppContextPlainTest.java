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
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import global.Log;

/**
 * The same tests as in {@link StandalonePlainTest}, but creating the {@link MockMvc} object with a call to
 * {@link MockMvcBuilders#webAppContextSetup(WebApplicationContext)} instead of
 * {@link MockMvcBuilders#standaloneSetup(Object...)}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration @ContextConfiguration
public final class WebAppContextPlainTest {
	@Configuration
	static class WebAppConfig {
		@Bean
		CommentController commentController() {
			Log.append("@cc ");
			return new CommentController();
		}

		@Bean
		RequestRepository requestRepository() {
			Log.append("@rr ");
			return new RequestRepository();
		}

		@Bean
		RequestService requestService() {
			Log.append("@rs ");
			return new RequestService();
		}

		@Bean
		CommentValidator validator() {
			Log.append("@cv ");
			return new CommentValidator() {
				@Override
				public boolean supports(Class<?> clazz) {
					return true;
				}
			};
		}
	}

	// These two fields provide access to the beans created in the configuration.
	@Autowired
	RequestService requestService;
	@Autowired
	CommentValidator validator;

	@Autowired
	WebApplicationContext context;
	MockMvc mockMvc;

	@BeforeClass
	public static  void setupLog() {
		Log.init("WebAppContextPlainTest");
	}

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
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
