package eu.appbucket.rothar.web.controller.v2;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.web.domain.user.UserData;

@Ignore
public class UserControllerTest {
	
	private RestTemplate restClient;
	private UserData testUser;
	
	@Before
	public void setup() {
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		restClient = new RestTemplate();
	    restClient.getMessageConverters().add(new StringHttpMessageConverter());
	    restClient.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
	    testUser = new UserData();
	    testUser.setEmail("myuser@example.com");
	    testUser.setName("My User Name");
	    testUser.setPassword("user pass");
	}
	
	@Test
	public void testRegisterUser(){
		
		// proble with user id
		
		deleteTestUser();
		createTestUser();
		findTestUser();
	}
	
	private void deleteTestUser() {
		restClient.delete(
	    		"http://localhost:8080/Rothar/v1/users",
		    	testUser.getEmail(), 
		    	String.class);
	}
	
	private void createTestUser() {
		restClient.postForObject(
	    		"http://localhost:8080/Rothar/v1/users",
		    	testUser, 
		    	String.class);
	}
	
	private UserData findTestUser() {
		return restClient.getForObject(
	    		"http://localhost:8080/Rothar/v1/users",
	    		UserData.class,
	    		testUser.getUserId());
	}
}
