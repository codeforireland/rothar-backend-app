package eu.appbucket.rothar.core.service;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.appbucket.rothar.core.domain.email.EmailService;
import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.UserDao;
import eu.appbucket.rothar.core.persistence.exception.UserDaoException;
import eu.appbucket.rothar.core.service.exception.ServiceException;

public class UserServiceImplTest {
	
	private UserServiceImpl test;
	private Mockery context = new JUnit4Mockery();
	private UserEntry testUser;
	private UserEntry testUserWithInvalidActivationCode;
	private UserEntry testUserWhichIsActivated;
	private UserEntry testUserWhichIsNotActivated;
	private UserDao userDaoMock;
	private EmailService emailServiceMock;
	
	@Before
	public void setup() {
		test = new UserServiceImpl();
		testUser = new UserEntry();
		testUser.setUserId(1);
		testUser.setEmail("email-address");
		testUser.setPassword("generated-password");
		testUser.setActivationCode("generated-activation-code");
		testUserWithInvalidActivationCode = new UserEntry();
		testUserWithInvalidActivationCode.setActivationCode("invalid-activation-code");
		testUserWhichIsActivated = new UserEntry();
		testUserWhichIsActivated.setActivated(true);
		testUserWhichIsActivated.setUserId(2);
		testUserWhichIsNotActivated = new UserEntry();
		testUserWhichIsNotActivated.setActivated(false);
		testUserWhichIsNotActivated.setUserId(3);
		userDaoMock = context.mock(UserDao.class);
		test.setUserDao(userDaoMock);
		emailServiceMock = context.mock(EmailService.class);
		test.setEmailService(emailServiceMock);
	}
	
	@Test
	public void Test_isUserExisting_When_userExists_Then_returnTrue() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExistingById(with(any(Integer.class)));
            will(returnValue(true));
		}});
		boolean userExists = test.isUserExisting(testUser);
		Assert.assertTrue(userExists);
	}
	
	@Test
	public void Test_isUserExisting_When_userDoesntExists_Then_returnFalse() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExistingById(with(any(Integer.class)));
            will(returnValue(false));
		}});
		boolean userExists = test.isUserExisting(testUser);
		Assert.assertFalse(userExists);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_isUserExisting_When_exceptionIsThrowDuringCheckingExistence_Then_throwException() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExistingById(with(any(Integer.class)));
            will(throwException(new UserDaoException("")));
		}});
		test.isUserExisting(testUser);
	}
	
	@Test
	public void Test_findUser_When_userExists_Then_returnUser() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExistingById(with(any(Integer.class)));
            will(returnValue(true));
            oneOf(userDaoMock).findUserById(with(any(Integer.class)));
            will(returnValue(testUser));
		}});
		UserEntry actuallResult = test.findUser(testUser.getUserId());
		UserEntry expectedResult = testUser;
		Assert.assertEquals(expectedResult, actuallResult);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_findUser_When_userDoesntExists_Then_throwException() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExistingById(with(any(Integer.class)));
            will(returnValue(false));
		}});
		test.findUser(testUser.getUserId());
	}
	
	@Test(expected=ServiceException.class)
	public void Test_findUser_When_userExists_but_exceptionIsThrowDuringFindingUser_Then_throwException() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExistingById(with(any(Integer.class)));
            will(returnValue(true));
            oneOf(userDaoMock).findUserById(with(any(Integer.class)));
            will(throwException(new UserDaoException("")));
		}});
		test.findUser(testUser.getUserId());
	}
	
	@Test
	public void Test_createUser_When_userHasUniqueEmail_Then_createUser() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExistingByEmail(with(any(String.class)));
            will(returnValue(false));
            oneOf(userDaoMock).createNewUser(testUser);
            will(returnValue(testUser));
            oneOf(userDaoMock).setupUserActivationCode(with(any(Integer.class)));
            will(returnValue(""));
            oneOf(emailServiceMock).sendUserActivationEmail(testUser);
		}});
		test.createUser(testUser);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_createUser_When_userWithGivenEmailAlreadyExists_Then_throwException() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExistingByEmail(with(any(String.class)));
            will(returnValue(true));
		}});
		test.createUser(testUser);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_createUser_When_userHasUniqueEmail_but_exceptionWasThrowDuringCreatingNewUser_Then_throwException() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExistingByEmail(with(any(String.class)));
            will(returnValue(false));
            oneOf(userDaoMock).createNewUser(testUser);
            will(throwException(new UserDaoException("")));
		}});
		test.createUser(testUser);
	}
	
	@Test
	public void Test_activateUser_When_userExists_and_activationCodeMatch_Then_activateUser() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExistingById(with(any(Integer.class)));
            will(returnValue(true));
            oneOf(userDaoMock).findUserById(with(any(Integer.class)));
            will(returnValue(testUser));
            oneOf(userDaoMock).activateExistingUser(with(any(Integer.class)));            
		}});
		test.activateUser(testUser);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_activateUser_When_userUserDoesntExists_Then_throwException() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExistingById(with(any(Integer.class)));
            will(returnValue(false));
		}});
		test.activateUser(testUser);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_activateUser_When_userExists_but_activationCodeDoesntMatch_Then_throwException() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExistingById(with(any(Integer.class)));
            will(returnValue(true));
            oneOf(userDaoMock).findUserById(with(any(Integer.class)));
            will(returnValue(testUserWithInvalidActivationCode));
		}});
		test.activateUser(testUser);
	}
	
	@Test
	public void Test_updateUser_When_userExists_and_userIsActivate_Then_updateUser() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExistingById(with(any(Integer.class)));
            will(returnValue(true));
            oneOf(userDaoMock).findUserById(with(any(Integer.class)));
            will(returnValue(testUserWhichIsActivated));
            oneOf(userDaoMock).updateExistingUser(testUser);            
		}});
		test.updateUser(testUser);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_updateUser_But_userDoesntExists_Then_throwException() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExistingById(with(any(Integer.class)));
            will(returnValue(false));            
		}});
		test.updateUser(testUser);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_updateUser_When_userExists_but_userIsNotYetActivated_Then_throwException() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExistingById(with(any(Integer.class)));
            will(returnValue(true));
            oneOf(userDaoMock).findUserById(with(any(Integer.class)));
            will(returnValue(testUserWhichIsNotActivated));
		}});
		test.updateUser(testUser);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_updateUser_When_userExists_and_userIsActivated_but_exceptionWasThrowDuringUpdatingUser_Then_throwException() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExistingById(with(any(Integer.class)));
            will(returnValue(true));
            oneOf(userDaoMock).findUserById(with(any(Integer.class)));
            will(returnValue(testUserWhichIsActivated));
            oneOf(userDaoMock).updateExistingUser(testUser);
            will(throwException(new UserDaoException("")));
		}});
		test.updateUser(testUser);
	}
}
