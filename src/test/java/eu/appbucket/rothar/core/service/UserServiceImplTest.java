package eu.appbucket.rothar.core.service;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.UserDao;
import eu.appbucket.rothar.core.persistence.exception.UserDaoException;
import eu.appbucket.rothar.core.service.exception.ServiceException;

public class UserServiceImplTest {
	
	private UserServiceImpl test;
	private Mockery context = new JUnit4Mockery();
	private UserEntry testUser;
	private UserDao userDaoMock;
	
	@Before
	public void setup() {
		test = new UserServiceImpl();
		testUser = new UserEntry();
		testUser.setUserId(1);
		userDaoMock = context.mock(UserDao.class);
		test.setUserDao(userDaoMock);
	}
	
	@Test
	public void Test_isUserExisting_When_userExists_Then_returnTrue() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExisting(with(any(Integer.class)));
            will(returnValue(true));
		}});
		boolean userExists = test.isUserExisting(testUser);
		Assert.assertTrue(userExists);
	}
	
	@Test
	public void Test_isUserExisting_When_userDoesntExists_Then_returnFalse() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExisting(with(any(Integer.class)));
            will(returnValue(false));
		}});
		boolean userExists = test.isUserExisting(testUser);
		Assert.assertFalse(userExists);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_isUserExisting_When_exceptionIsThrowDuringCheckingExistence_Then_throwException() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExisting(with(any(Integer.class)));
            will(throwException(new UserDaoException("")));
		}});
		test.isUserExisting(testUser);
	}
	
	@Test
	public void Test_findUser_When_userExists_Then_returnUser() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExisting(with(any(Integer.class)));
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
            oneOf(userDaoMock).isUserExisting(with(any(Integer.class)));
            will(returnValue(false));
		}});
		test.findUser(testUser.getUserId());
	}
	
	@Test(expected=ServiceException.class)
	public void Test_findUser_When_userExists_but_exceptionIsThrowDuringFindingUser_Then_throwException() {
		context.checking(new Expectations() {{
            oneOf(userDaoMock).isUserExisting(with(any(Integer.class)));
            will(returnValue(true));
            oneOf(userDaoMock).findUserById(with(any(Integer.class)));
            will(throwException(new UserDaoException("")));
		}});
		test.findUser(testUser.getUserId());
	}
	
	@Test
	public void Test_createUser() {
		throw new RuntimeException("Implement test according to the interface.");
	}
	
	@Test
	public void Test_activateUser() {
		throw new RuntimeException("Implement test according to the interface.");
	}
	
	@Test
	public void Test_updateUser() {
		throw new RuntimeException("Implement test according to the interface.");
	}
}
