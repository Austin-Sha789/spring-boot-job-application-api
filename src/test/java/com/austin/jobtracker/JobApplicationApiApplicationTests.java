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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

	@BeforeEach
	void setUp() {
		repository.deleteAll();
	}

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
		.andExpect(status().isNotFound())
		.andExpect(content().string("Job application not found with id: 999999"));
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
		.andExpect(status().isNotFound())
		.andExpect(content().string("Job application not found with id: 999999"));
	}

	@Test
	void updateApplication_whenInvalid_shouldReturn400() throws Exception {

		JobApplication application = new JobApplication(
			null,
			"Test Company",
			"Test Position",
			"Applied"
		);

		JobApplication savedApplication = repository.save(application);

		String json = """
				{
					"company": "",
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
		.andExpect(status().isBadRequest());

		JobApplication unchanged = repository.findById(savedApplication.getId()).orElseThrow();
		
		assertEquals("Test Company", unchanged.getCompany());
		assertEquals("Test Position", unchanged.getPosition());
		assertEquals("Applied", unchanged.getStatus());
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
		.andExpect(status().isNotFound())
		.andExpect(content().string("Job application not found with id: 999999"));
	}

	@Test
	void getApplications_shouldReturnPageOfApplications() throws Exception {

		JobApplication application1 = 
			new JobApplication(
				null,
				"Company 1",
				"Position 1",
				"Applied"
			);

		JobApplication application2 = 
			new JobApplication(
				null,
				"Company 2",
				"Position 2",
				"Interviewing"
			);

		repository.save(application1);
		repository.save(application2);

		mockMvc.perform(
			get("/applications")
		)
		.andDo(print())
		.andExpect(jsonPath("$.content.length()").value(2))
		.andExpect(jsonPath("$.content[0].company").value("Company 1"))
		.andExpect(jsonPath("$.content[0].position").value("Position 1"))
		.andExpect(jsonPath("$.content[0].status").value("Applied"))
		.andExpect(jsonPath("$.content[1].company").value("Company 2"))
		.andExpect(jsonPath("$.content[1].position").value("Position 2"))
		.andExpect(jsonPath("$.content[1].status").value("Interviewing"))

		.andExpect(jsonPath("$.totalElements").value(2))
		.andExpect(jsonPath("$.totalPages").value(1))
		.andExpect(jsonPath("$.size").value(10))
		.andExpect(jsonPath("$.number").value(0))
		.andExpect(jsonPath("$.first").value(true))
		.andExpect(jsonPath("$.last").value(true));

	}

	@Test
	void getApplicationsByCompany_shouldReturnListOfApplicationsWithCompany() throws Exception {

		JobApplication application1 = 
			new JobApplication(
				null,
				"Company 1",
				"Position 1",
				"Applied"
			);

		JobApplication application2 = 
			new JobApplication(
				null,
				"Company 2",
				"Position 2",
				"Interviewing"
			);

		JobApplication application3 = 
			new JobApplication(
				null,
				"Company 1",
				"Position 3",
				"Interviewing"
			);

		repository.save(application1);
		repository.save(application2);
		repository.save(application3);

		mockMvc.perform(
			get("/applications")
    			.param("company", "Company 1")
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.length()").value(2))
		.andExpect(jsonPath("$.content[0].company").value("Company 1"))
		.andExpect(jsonPath("$.content[0].position").value("Position 1"))
		.andExpect(jsonPath("$.content[0].status").value("Applied"))
		.andExpect(jsonPath("$.content[1].company").value("Company 1"))
		.andExpect(jsonPath("$.content[1].position").value("Position 3"))
		.andExpect(jsonPath("$.content[1].status").value("Interviewing"))

		.andExpect(jsonPath("$.totalElements").value(2))
		.andExpect(jsonPath("$.totalPages").value(1))
		.andExpect(jsonPath("$.size").value(10))
		.andExpect(jsonPath("$.number").value(0))
		.andExpect(jsonPath("$.first").value(true))
		.andExpect(jsonPath("$.last").value(true));

	}

	@Test
	void getApplicationsByStatus_shouldReturnListOfApplicationsWithStatus() throws Exception {

		JobApplication application1 = 
			new JobApplication(
				null,
				"Company 1",
				"Position 1",
				"Applied"
			);

		JobApplication application2 = 
			new JobApplication(
				null,
				"Company 2",
				"Position 2",
				"Interviewing"
			);

		JobApplication application3 = 
			new JobApplication(
				null,
				"Company 1",
				"Position 3",
				"Interviewing"
			);

		repository.save(application1);
		repository.save(application2);
		repository.save(application3);

		mockMvc.perform(
			get("/applications")
    			.param("status", "Interviewing")
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.length()").value(2))
		.andExpect(jsonPath("$.content[0].company").value("Company 2"))
		.andExpect(jsonPath("$.content[0].position").value("Position 2"))
		.andExpect(jsonPath("$.content[0].status").value("Interviewing"))
		.andExpect(jsonPath("$.content[1].company").value("Company 1"))
		.andExpect(jsonPath("$.content[1].position").value("Position 3"))
		.andExpect(jsonPath("$.content[1].status").value("Interviewing"))

		.andExpect(jsonPath("$.totalElements").value(2))
		.andExpect(jsonPath("$.totalPages").value(1))
		.andExpect(jsonPath("$.size").value(10))
		.andExpect(jsonPath("$.number").value(0))
		.andExpect(jsonPath("$.first").value(true))
		.andExpect(jsonPath("$.last").value(true));

	}


	@Test
	void getApplicationsByCompanyAndStatus_shouldReturnListOfApplicationsWithBothCompanyAndStatus() throws Exception {

		JobApplication application1 = 
			new JobApplication(
				null,
				"Company 1",
				"Position 1",
				"Applied"
			);

		JobApplication application2 = 
			new JobApplication(
				null,
				"Company 2",
				"Position 2",
				"Interviewing"
			);

		JobApplication application3 = 
			new JobApplication(
				null,
				"Company 1",
				"Position 3",
				"Applied"
			);

		repository.save(application1);
		repository.save(application2);
		repository.save(application3);

		mockMvc.perform(
			get("/applications")
				.param("company", "Company 1")
				.param("status", "Applied")
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.length()").value(2))
		.andExpect(jsonPath("$.content[0].company").value("Company 1"))
		.andExpect(jsonPath("$.content[0].position").value("Position 1"))
		.andExpect(jsonPath("$.content[0].status").value("Applied"))
		.andExpect(jsonPath("$.content[1].company").value("Company 1"))
		.andExpect(jsonPath("$.content[1].position").value("Position 3"))
		.andExpect(jsonPath("$.content[1].status").value("Applied"))

		.andExpect(jsonPath("$.totalElements").value(2))
		.andExpect(jsonPath("$.totalPages").value(1))
		.andExpect(jsonPath("$.size").value(10))
		.andExpect(jsonPath("$.number").value(0))
		.andExpect(jsonPath("$.first").value(true))
		.andExpect(jsonPath("$.last").value(true));

	}

	@Test
	void getApplicationsWithPageAndSize_Page1() throws Exception {

		JobApplication application1 = 
			new JobApplication(
				null,
				"Company 1",
				"Position 1",
				"Applied"
			);

		JobApplication application2 = 
			new JobApplication(
				null,
				"Company 2",
				"Position 2",
				"Interviewing"
			);

		JobApplication application3 = 
			new JobApplication(
				null,
				"Company 1",
				"Position 3",
				"Applied"
			);

		repository.save(application1);
		repository.save(application2);
		repository.save(application3);
		
		mockMvc.perform(
			get("/applications?page=0&size=2")
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.length()").value(2))
		.andExpect(jsonPath("$.content[0].company").value("Company 1"))
		.andExpect(jsonPath("$.content[0].position").value("Position 1"))
		.andExpect(jsonPath("$.content[0].status").value("Applied"))
		.andExpect(jsonPath("$.content[1].company").value("Company 2"))
		.andExpect(jsonPath("$.content[1].position").value("Position 2"))
		.andExpect(jsonPath("$.content[1].status").value("Interviewing"))

		.andExpect(jsonPath("$.totalElements").value(3))
		.andExpect(jsonPath("$.totalPages").value(2))
		.andExpect(jsonPath("$.size").value(2))
		.andExpect(jsonPath("$.number").value(0))
		.andExpect(jsonPath("$.first").value(true))
		.andExpect(jsonPath("$.last").value(false));
	}

	@Test
	void getApplicationsWithPageAndSize_Page2() throws Exception {

		JobApplication application1 = 
			new JobApplication(
				null,
				"Company 1",
				"Position 1",
				"Applied"
			);

		JobApplication application2 = 
			new JobApplication(
				null,
				"Company 2",
				"Position 2",
				"Interviewing"
			);

		JobApplication application3 = 
			new JobApplication(
				null,
				"Company 1",
				"Position 3",
				"Applied"
			);

		repository.save(application1);
		repository.save(application2);
		repository.save(application3);
		
		mockMvc.perform(
			get("/applications?page=1&size=2")
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.length()").value(1))
		.andExpect(jsonPath("$.content[0].company").value("Company 1"))
		.andExpect(jsonPath("$.content[0].position").value("Position 3"))
		.andExpect(jsonPath("$.content[0].status").value("Applied"))

		.andExpect(jsonPath("$.totalElements").value(3))
		.andExpect(jsonPath("$.totalPages").value(2))
		.andExpect(jsonPath("$.size").value(2))
		.andExpect(jsonPath("$.number").value(1))
		.andExpect(jsonPath("$.first").value(false))
		.andExpect(jsonPath("$.last").value(true));
	}
}
