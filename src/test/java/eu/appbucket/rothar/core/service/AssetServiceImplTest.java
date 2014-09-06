package eu.appbucket.rothar.core.service;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.appbucket.rothar.core.domain.asset.AssetEntry;
import eu.appbucket.rothar.core.domain.asset.AssetFilter;
import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.AssetDao;
import eu.appbucket.rothar.core.persistence.exception.AssetDaoException;
import eu.appbucket.rothar.core.service.exception.ServiceException;

public class AssetServiceImplTest {
	
	private AssetServiceImpl test;
	private Mockery context = new JUnit4Mockery();
	private AssetDao assetDaoMock;
	private UserService userServiceMock;
	private final AssetEntry testAsset = new AssetEntry();
	private final AssetFilter testAssetFilter = new AssetFilter.Builder().buildFilterForUser(1);
	
	@Before
	public void setup() {
		test = new AssetServiceImpl();
		assetDaoMock = context.mock(AssetDao.class);
		test.setAssetDao(assetDaoMock);
		userServiceMock = context.mock(UserService.class);
		test.setUserService(userServiceMock);
		testAsset.setAssetId(1);
		testAsset.setUserId(1);
		testAsset.setUuid("2c1b80da-a69b-453b-9d06-94e499e416f1");
	}
	
	@Test
	public void Test_isAssetExisting_When_assetExists_Then_returnTrue() {
		context.checking(new Expectations() {{
            oneOf(assetDaoMock).isAssetExisting(with(any(Integer.class)));
            will(returnValue(true));
		}});
		boolean assetExisting = test.isAssetExisting(testAsset);
		Assert.assertTrue(assetExisting);
	}
	
	@Test
	public void Test_isAssetExisting_When_assetDoesntExists_Then_returnFalse() {
		context.checking(new Expectations() {{
            oneOf(assetDaoMock).isAssetExisting(with(any(Integer.class)));
            will(returnValue(false));
		}});
		boolean assetExisting = test.isAssetExisting(testAsset);
		Assert.assertFalse(assetExisting);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_isAssetExisting_When_exceptionIsThrownDuringCheckingExistence_Then_rethrowException() {
		context.checking(new Expectations() {{
            oneOf(assetDaoMock).isAssetExisting(with(any(Integer.class)));
            will(throwException(new AssetDaoException("")));
		}});
		test.isAssetExisting(testAsset);
	}
	
	@Test
	public void Test_isAssetOwnedByUser_When_userIsAssetOwner_Then_returnTrue() {
		context.checking(new Expectations() {{
            oneOf(assetDaoMock).isAssetOwnedByUser(with(any(Integer.class)), with(any(Integer.class)));
            will(returnValue(true));
		}});
		boolean assetOwnedByUser = test.isAssetOwnedByUser(testAsset);
		Assert.assertTrue(assetOwnedByUser);
	}
	
	@Test
	public void Test_isAssetOwnedByUser_When_userIsNotAssetOwner_Then_returnFalse() {
		context.checking(new Expectations() {{
            oneOf(assetDaoMock).isAssetOwnedByUser(with(any(Integer.class)), with(any(Integer.class)));
            will(returnValue(false));
		}});
		boolean assetOwnedByUser = test.isAssetOwnedByUser(testAsset);
		Assert.assertFalse(assetOwnedByUser);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_isAssetOwnedByUser_When_exceptionIsThrownDuringCheckingOwnership_Then_rethrowException() {
		context.checking(new Expectations() {{
            oneOf(assetDaoMock).isAssetOwnedByUser(with(any(Integer.class)), with(any(Integer.class)));
            will(throwException(new AssetDaoException("")));
		}});
		test.isAssetOwnedByUser(testAsset);
	}
	
	@Test
	public void Test_createAsset_When_assetOwnerExists_Then_createNewAsset() {
		context.checking(new Expectations() {{
			oneOf(assetDaoMock).isAssetExisting(with(any(String.class)));
			will(returnValue(false));
			oneOf(userServiceMock).isUserExistingById(with(any(UserEntry.class)));
			will(returnValue(true));
            oneOf(assetDaoMock).createNewAsset(with(any(AssetEntry.class)));
		}});
		test.createAsset(testAsset);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_createAsset_When_assetOwnerDoesntExists_Then_throwException() {
		context.checking(new Expectations() {{
			oneOf(assetDaoMock).isAssetExisting(with(any(String.class)));
			will(returnValue(false));
			oneOf(userServiceMock).isUserExistingById(with(any(UserEntry.class)));
			will(returnValue(false));
		}});
		test.createAsset(testAsset);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_createAsset_When_assetAlreadyExists_Then_throwException() {
		context.checking(new Expectations() {{
			oneOf(assetDaoMock).isAssetExisting(with(any(String.class)));
			will(returnValue(true));
		}});
		test.createAsset(testAsset);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_createAsset_When_assetOwnerExists_but_exceptionIsThrowDuringCreatingNewAsset_Then_throwException() {
		context.checking(new Expectations() {{
			oneOf(assetDaoMock).isAssetExisting(with(any(String.class)));
			will(returnValue(false));
			oneOf(userServiceMock).isUserExistingById(with(any(UserEntry.class)));
			will(returnValue(true));
			oneOf(assetDaoMock).createNewAsset(with(any(AssetEntry.class)));
			will(throwException(new AssetDaoException("")));
		}});
		test.createAsset(testAsset);
	}
	
	@Test
	public void Test_updateAsset_When_assetExists_and_assetOwnerExists_Then_updateExistingAsset() {
		context.checking(new Expectations() {{
			oneOf(assetDaoMock).isAssetExisting(with(any(Integer.class)));
			will(returnValue(true));
			oneOf(userServiceMock).isUserExistingById(with(any(UserEntry.class)));
			will(returnValue(true));
			oneOf(assetDaoMock).isAssetOwnedByUser(with(any(Integer.class)), with(any(Integer.class)));
			will(returnValue(true));
			oneOf(assetDaoMock).updateExistingAsset(with(any(AssetEntry.class)));
		}});
		test.updateAsset(testAsset);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_updateAsset_When_assetDoesntExists_Then_throwException() {
		context.checking(new Expectations() {{
			oneOf(assetDaoMock).isAssetExisting(with(any(Integer.class)));
			will(returnValue(false));
		}});
		test.updateAsset(testAsset);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_updateAsset_When_assetExists_but_assetOwnerDoesntExists_Then_throwException() {
		context.checking(new Expectations() {{
			oneOf(assetDaoMock).isAssetExisting(with(any(Integer.class)));
			will(returnValue(true));
			oneOf(userServiceMock).isUserExistingById(with(any(UserEntry.class)));
			will(returnValue(false));
		}});
		test.updateAsset(testAsset);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_updateAsset_When_assetExists_and_assetOwnerExists_but_userIsNotAssetOwner_Then_throwException() {
		context.checking(new Expectations() {{
			oneOf(assetDaoMock).isAssetExisting(with(any(Integer.class)));
			will(returnValue(true));
			oneOf(userServiceMock).isUserExistingById(with(any(UserEntry.class)));
			will(returnValue(true));
			oneOf(assetDaoMock).isAssetOwnedByUser(with(any(Integer.class)), with(any(Integer.class)));
			will(returnValue(false));			
		}});
		test.updateAsset(testAsset);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_updateAsset_When_assetExists_and_assetOwnerExists_and_userIsAssetOwner_but_exceptionIsThrowDuringUpdatingExistingAsset_Then_throwException() {
		context.checking(new Expectations() {{
			oneOf(assetDaoMock).isAssetExisting(with(any(Integer.class)));
			will(returnValue(true));
			oneOf(userServiceMock).isUserExistingById(with(any(UserEntry.class)));
			will(returnValue(true));
			oneOf(assetDaoMock).isAssetOwnedByUser(with(any(Integer.class)), with(any(Integer.class)));
			will(returnValue(true));
			oneOf(assetDaoMock).updateExistingAsset(with(any(AssetEntry.class)));
			will(throwException(new AssetDaoException("")));
		}});
		test.updateAsset(testAsset);
	}
	
	@Test
	public void Test_findAsset_When_assetExists_and_assetOwnerExists_and_userIsAssetOwner_Then_returnAsset() {
		context.checking(new Expectations() {{
			oneOf(assetDaoMock).isAssetExisting(with(any(Integer.class)));
			will(returnValue(true));
			oneOf(userServiceMock).isUserExistingById(with(any(UserEntry.class)));
			will(returnValue(true));
			oneOf(assetDaoMock).isAssetOwnedByUser(with(any(Integer.class)), with(any(Integer.class)));
			will(returnValue(true));
			oneOf(assetDaoMock).findAssetByUserAndAssetId(with(any(Integer.class)), with(any(Integer.class)));
			will(returnValue(testAsset));
		}});
		AssetEntry actuallResult = test.findAsset(testAsset.getUserId(), testAsset.getAssetId());
		AssetEntry expectedResult = testAsset;
		Assert.assertEquals(expectedResult, actuallResult);
	}
	
	@Test(expected=ServiceException.class)
	public void Test_findAsset_When_assetDoesntExists_Then_throwException() {
		context.checking(new Expectations() {{
			oneOf(assetDaoMock).isAssetExisting(with(any(Integer.class)));
			will(returnValue(false));
		}});
		test.findAsset(testAsset.getUserId(), testAsset.getAssetId());
	}
	
	@Test(expected=ServiceException.class)
	public void Test_findAsset_When_assetExists_but_assetOwnerDoesntExists_Then_throwException() {
		context.checking(new Expectations() {{
			oneOf(assetDaoMock).isAssetExisting(with(any(Integer.class)));
			will(returnValue(true));
			oneOf(userServiceMock).isUserExistingById(with(any(UserEntry.class)));
			will(returnValue(false));
		}});
		test.findAsset(testAsset.getUserId(), testAsset.getAssetId());
	}
	
	@Test(expected=ServiceException.class)
	public void Test_findAsset_When_assetExists_and_assetOwnerExists_but_userIsNotAssetOwner_Then_throwException() {
		context.checking(new Expectations() {{
			oneOf(assetDaoMock).isAssetExisting(with(any(Integer.class)));
			will(returnValue(true));
			oneOf(userServiceMock).isUserExistingById(with(any(UserEntry.class)));
			will(returnValue(true));
			oneOf(assetDaoMock).isAssetOwnedByUser(with(any(Integer.class)), with(any(Integer.class)));
			will(returnValue(false));
			
		}});
		test.findAsset(testAsset.getUserId(), testAsset.getAssetId());
	}
	
	@Test(expected=ServiceException.class)
	public void Test_findAsset_When_assetExists_and_assetOwnerExists_and_userIsNotAssetOwner_but_exceptionIsThrowDuringFindingAsset_Then_throwException() {
		context.checking(new Expectations() {{
			oneOf(assetDaoMock).isAssetExisting(with(any(Integer.class)));
			will(returnValue(true));
			oneOf(userServiceMock).isUserExistingById(with(any(UserEntry.class)));
			will(returnValue(true));
			oneOf(assetDaoMock).isAssetOwnedByUser(with(any(Integer.class)), with(any(Integer.class)));
			will(returnValue(true));
			oneOf(assetDaoMock).findAssetByUserAndAssetId(with(any(Integer.class)), with(any(Integer.class)));
			will(throwException(new AssetDaoException("")));
		}});
		test.findAsset(testAsset.getUserId(), testAsset.getAssetId());
	}
	
	@Test
	public void Test_findAssets_When_assetsFound_Then_returnAssetsList() {
		context.checking(new Expectations() {{
			oneOf(assetDaoMock).findAssets(with(any(AssetFilter.class)));
			will(returnValue(new ArrayList<AssetEntry>()));
		}});
		List<AssetEntry> actualResults = test.findAssets(testAssetFilter);
		Assert.assertNotNull(actualResults);
	}
}
