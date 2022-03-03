package es.evilmonkey.kinaro.core.request;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = { TestApplicationConfiguration.class })
@AutoConfigureMockMvc
public class DefaultRequestControllerIntTest {

	@Autowired
	MockMvc mvc;

	@Test
	public void test_requestMappingConfiguration_hasBeenApplied() throws Exception {
		//@formatter:off
		this.mvc.perform(post("/commands")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
							{
								"@path": "/ping",
								"text": "Hola Mundo!"
							}
				""")).andDo(print()).andExpect(status().isOk());
		//@formatter:on
	}

}
