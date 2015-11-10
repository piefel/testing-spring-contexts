package mockmvc.example;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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

/**
 * The same tests as in {@link StandaloneMockitoTest}, but creating the {@link MockMvc} object with a call to
 * {@link MockMvcBuilders#webAppContextSetup(WebApplicationContext)} instead of
 * {@link MockMvcBuilders#standaloneSetup(Object...)}.
 * <p/>
 * This version loads a Spring application context, which defines the beans on which the controller under test
 * depends.
 * Using a configured context is significantly more complex than just letting Mockito instantiate the controller and its
 * mock dependencies, as it adds several unique requirements: a) use of {@link SpringJUnit4ClassRunner} (or equivalent
 * mechanism), b) addition of {@code @WebAppConfiguration} <em>and</em> {@code @ContextConfiguration} to the test class,
 * c) addition of a {@link WebApplicationContext} field to the test class,
 * d) creation of an XML or Java configuration with a suitable bean for each dependency, and
 * e) a necessary call to {@link org.mockito.Mockito#reset(Object[])} in a test setup method.
 * All the above disadvantages, for all we know, come with <em>no</em> discernible gain.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebAppContextMockitoTest.WebAppConfig.class)
public final class WebAppContextMockitoTest {
	@Configuration
	@ComponentScan(excludeFilters = @ComponentScan.Filter(value = Configuration.class, type = FilterType.ANNOTATION))
	static class WebAppConfig {
		@Bean
		RequestService requestService() {
			Log.append("@rs ");
			return mock(RequestService.class);
		}

		@Bean
		CommentValidator validator() {
			Log.append("@cv ");
			return mock(CommentValidator.class);
		}
	}

	// These two fields provide access to the mock beans created in the configuration.
	@Autowired
	RequestService requestService;
	@Autowired
	CommentValidator validator;

	@Autowired
	WebApplicationContext context;
	MockMvc mockMvc;

	@BeforeClass
	public static  void setupLog() {
		Log.init("WebAppContextMockitoTest");
	}

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

		reset(requestService, validator); // without this call, the last test fails
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
			public Object answer(InvocationOnMock invocation) {
				Errors errors = (Errors) invocation.getArguments()[1];
				errors.reject("forcing some error");
				return null;
			}
		}).when(validator).validate(anyObject(), any(Errors.class));

		mockMvc.perform(post("/comment/{uuid}", "123"))
				.andExpect(status().isOk())
				.andExpect(view().name("comment"));
		Log.log();
	}

	//	@Test
	public void saveComment() throws Exception {
		when(requestService.getRequestCommentByUUID("123")).thenReturn(new RequestComment());

		mockMvc.perform(post("/comment/{uuid}", "123"))
				.andExpect(status().isOk())
				.andExpect(view().name("ok"));
		Log.log();
	}
}
