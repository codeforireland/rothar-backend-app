package eu.appbucket.rothar.web.controller.v2;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import eu.appbucket.rothar.web.domain.user.UserData;

//@Ignore
public class UserControllerTest {
	
	private RestTemplate restClient;
	private HttpEntity request;
	HttpHeaders headers;
	
	@Before
	public void setup() {
		headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    request = new HttpEntity(headers);
		restClient = new RestTemplate();
	    restClient.getMessageConverters().add(new StringHttpMessageConverter());
	    restClient.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
	}
	
	@Test
	public void testRegisterUser() {
		UserData newUser = createTestUser();
		UserData foundUser = findTestUser(newUser);
		Assert.assertEquals(newUser.getUserId(), foundUser.getUserId());
	}
	
	private UserData createTestUser() {
		UserData newUser = restClient.postForObject(
	    		"http://localhost:8080/Rothar/v2/users",
	    		request, 
	    		UserData.class);
		return newUser;
	}
	
	private UserData findTestUser(UserData newUser) {
		UserData foundUser = restClient.getForObject(
	    		"http://localhost:8080/Rothar/v2/users/{userId}",
	    		UserData.class,
	    		newUser.getUserId());
		return foundUser;
	}
}
