package com.austin.jobtracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;

import com.austin.jobtracker.model.JobApplication;
import com.austin.jobtracker.repository.JobApplicationRepository;

@SpringBootTest
@AutoConfigureMockMvc
class JobApplicationApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JobApplicationRepository repository;

	@Test
	void contextLoads() {
	}

	@Test
	void createApplication_shouldReturn201() throws Exception {

		String json = """
				{
					"company": "Test Company",
					"position": "Test Position",
					"status": "Applied"
				}
				""";

		mockMvc.perform(
			post("/applications")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
		)
		.andDo(print())
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.company").value("Test Company"))
		.andExpect(jsonPath("$.position").value("Test Position"))
		.andExpect(jsonPath("$.status").value("Applied"))
		.andExpect(jsonPath("$.id").exists());
	}

	@Test
	void createApplication_withBlankCompany_shouldReturn400() throws Exception {

		String json = """
				{
					"company": "",
					"position": "Test Position",
					"status": "Applied"
				}
				""";

		mockMvc.perform(
			post("/applications")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
		)
		.andDo(print())
		.andExpect(status().isBadRequest());
	}

	@Test
	void getApplicationById_shouldReturnApplication() throws Exception {

		JobApplication application = 
			new JobApplication(
				null,
				"Test Company",
				"Test Position",
				"Applied"
			);

		JobApplication savedApplication = repository.save(application);

		mockMvc.perform(
			get("/applications/" + savedApplication.getId())
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(savedApplication.getId()))
		.andExpect(jsonPath("$.company").value("Test Company"))
		.andExpect(jsonPath("$.position").value("Test Position"))
		.andExpect(jsonPath("$.status").value("Applied"));
	}

	@Test
	void getApplicationById_whenNotFound_shouldReturn404() throws Exception {

		mockMvc.perform(
			get("/applications/999999")
		)
		.andDo(print())
		.andExpect(status().isNotFound());
	}

	@Test
	void updateApplication_shouldReturnUpdateApplication() throws Exception {

		JobApplication application =
			new JobApplication(
				null,
				"Test Company",
				"Test Position",
				"Applied"
			);

		JobApplication savedApplication = repository.save(application);
		
		
		String json = """
				{
					"company": "Updated Company",
					"position": "Updated Position",
					"status": "Interviewing"
				}
				""";

		mockMvc.perform(
			put("/applications/" + savedApplication.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(savedApplication.getId()))
		.andExpect(jsonPath("$.company").value("Updated Company"))
		.andExpect(jsonPath("$.position").value("Updated Position"))
		.andExpect(jsonPath("$.status").value("Interviewing"));	

	}

	@Test
	void updateApplication_whenNotFound_shouldReturn404() throws Exception {

		String json = """
				{
					"company": "Updated Company",
					"position": "Updated Position",
					"status": "Interviewing"
				}
				""";

		mockMvc.perform(
			put("/applications/999999")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
		)
		.andDo(print())
		.andExpect(status().isNotFound());
	}

	@Test
	void deleteApplication_shouldReturn204() throws Exception {

		JobApplication application = 
			new JobApplication(
				null,
				"Test Company",
				"Test Position",
				"Applied"
			);

		JobApplication savedApplication = repository.save(application);

		mockMvc.perform(
			delete("/applications/" + savedApplication.getId())
		)
		.andDo(print())
		.andExpect(status().isNoContent());

		assertFalse(repository.existsById(savedApplication.getId()));
	}
	
	@Test
	void deleteApplication_whenNotFound_shouldReturn404() throws Exception {	

		mockMvc.perform(
			delete("/applications/999999")
		)
		.andDo(print())
		.andExpect(status().isNotFound());
	}
}
