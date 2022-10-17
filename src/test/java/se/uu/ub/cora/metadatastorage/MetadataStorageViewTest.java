/*
 * Copyright 2022 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.metadatastorage;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.storage.spies.RecordStorageSpy;

public class MetadataStorageViewTest {
	private static final String ID_FROM_LOGIN = "someIdFromLogin";
	private static final String APP_TOKEN_ID = "someAppTokenId";
	private static final String USER_ID = "someUserId";
	private RecordStorageSpy recordStorage;
	private MetadataStorageViewImp appTokenStorage;
	private RecordTypeHandlerFactorySpy recordTypeHandlerFactory;
	// private DataGroupToUserSpy dataGroupToUser;
	private DataFactorySpy dataFactorySpy;

	@BeforeMethod
	public void beforeMethod() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);

		recordStorage = new RecordStorageSpy();
		recordTypeHandlerFactory = new RecordTypeHandlerFactorySpy();
		// dataGroupToUser = new DataGroupToUserSpy();
		// appTokenStorage = MetadataStorageViewImp.usingRecordStorageAndRecordTypeHandlerFactory(
		// recordStorage, recordTypeHandlerFactory, dataGroupToUser);
	}

	@Test
	public void testInit() throws Exception {
		assertTrue(appTokenStorage instanceof MetadataStorageViewImp);
	}

	// @Test
	// public void testGetUserById_usingDependencies() throws Exception {
	// appTokenStorage.getUserById(USER_ID);
	//
	// recordStorage.MCR.assertParameterAsEqual("read", 0, "types", List.of("recordType"));
	// recordStorage.MCR.assertParameterAsEqual("read", 0, "id", "user");
	//
	// var userData = recordStorage.MCR.getReturnValue("read", 0);
	//
	// recordTypeHandlerFactory.MCR.assertParameters("factorUsingDataGroup", 0, userData);
	//
	// RecordTypeHandlerSpy userRecordTypeHandler = (RecordTypeHandlerSpy)
	// recordTypeHandlerFactory.MCR
	// .getReturnValue("factorUsingDataGroup", 0);
	// var listOfUserTypes = userRecordTypeHandler.MCR
	// .getReturnValue("getListOfImplementingRecordTypeIds", 0);
	// recordStorage.MCR.assertParameters("read", 1, listOfUserTypes, USER_ID);
	// }
	//
	// @Test
	// public void testGetUserById_userContainsInfo() throws Exception {
	// DataGroupSpy userDataGroup = new DataGroupSpy();
	// userDataGroup.MRV.setReturnValues("getAllGroupsWithNameInData", List.of(),
	// "userAppTokenGroup");
	//
	// recordStorage.MRV.setReturnValues("read", List.of(userDataGroup), Collections.emptyList(),
	// USER_ID);
	//
	// User user = appTokenStorage.getUserById(USER_ID);
	//
	// dataGroupToUser.MCR.assertReturn("groupToUser", 0, user);
	// }
	//
	// @Test
	// public void testGetUserById_throwsError() throws Exception {
	// RecordNotFoundException error = new RecordNotFoundException("error from spy");
	// recordStorage.MRV.setThrowException("read", error, List.of("recordType"), "user");
	//
	// try {
	// appTokenStorage.getUserById(USER_ID);
	// assertTrue(false);
	// } catch (Exception e) {
	// assertTrue(e instanceof MetadataStorageViewException);
	// assertEquals(e.getMessage(),
	// "Error reading user with id: " + USER_ID + " from storage.");
	// assertSame(e.getCause(), error);
	// }
	// }
	//
	// @Test
	// public void testGetUserByIdFromLogin_usingDependencies() throws Exception {
	// setupRecordStorageToReturnUserForReadListUsingFilter();
	//
	// appTokenStorage.getUserByIdFromLogin(ID_FROM_LOGIN);
	//
	// recordStorage.MCR.assertParameterAsEqual("read", 0, "types", List.of("recordType"));
	// recordStorage.MCR.assertParameterAsEqual("read", 0, "id", "user");
	//
	// var userData = recordStorage.MCR.getReturnValue("read", 0);
	//
	// recordTypeHandlerFactory.MCR.assertParameters("factorUsingDataGroup", 0, userData);
	//
	// RecordTypeHandlerSpy userRecordTypeHandler = (RecordTypeHandlerSpy)
	// recordTypeHandlerFactory.MCR
	// .getReturnValue("factorUsingDataGroup", 0);
	// var listOfUserTypes = userRecordTypeHandler.MCR
	// .getReturnValue("getListOfImplementingRecordTypeIds", 0);
	// var filterDataGroup = dataFactorySpy.MCR.getReturnValue("factorGroupUsingNameInData", 0);
	// recordStorage.MCR.assertParameters("readList", 0, listOfUserTypes, filterDataGroup);
	// }
	//
	// private void setupRecordStorageToReturnUserForReadListUsingFilter() {
	// DataGroupSpy userDataGroup = new DataGroupSpy();
	// userDataGroup.MRV.setReturnValues("getAllGroupsWithNameInData", List.of(),
	// "userAppTokenGroup");
	// StorageReadResult readResult = new StorageReadResult();
	// readResult.listOfDataGroups = List.of(userDataGroup);
	// readResult.totalNumberOfMatches = 1;
	// recordStorage.MRV.setDefaultReturnValuesSupplier("readList",
	// (Supplier<StorageReadResult>) () -> readResult);
	// }
	//
	// @Test
	// public void testGetUserByIdFromLogin_userContainsInfo() throws Exception {
	// setupRecordStorageToReturnUserForReadListUsingFilter();
	//
	// User user = appTokenStorage.getUserByIdFromLogin(ID_FROM_LOGIN);
	//
	// dataGroupToUser.MCR.assertReturn("groupToUser", 0, user);
	// }
	//
	// @Test
	// public void testGetUserByIdFromLogin_filterContainsCorrectInfo() throws Exception {
	// setupRecordStorageToReturnUserForReadListUsingFilter();
	//
	// appTokenStorage.getUserByIdFromLogin(ID_FROM_LOGIN);
	//
	// dataFactorySpy.MCR.assertParameters("factorGroupUsingNameInData", 0, "filter");
	// DataGroupSpy filterDataGroup = (DataGroupSpy) dataFactorySpy.MCR
	// .getReturnValue("factorGroupUsingNameInData", 0);
	//
	// dataFactorySpy.MCR.assertParameters("factorGroupUsingNameInData", 1, "part");
	// DataGroupSpy partDataGroup = (DataGroupSpy) dataFactorySpy.MCR
	// .getReturnValue("factorGroupUsingNameInData", 1);
	// filterDataGroup.MCR.assertParameters("addChild", 0, partDataGroup);
	//
	// dataFactorySpy.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 0, "key",
	// "userId");
	// var keyAtomic = dataFactorySpy.MCR.getReturnValue("factorAtomicUsingNameInDataAndValue", 0);
	// partDataGroup.MCR.assertParameters("addChild", 0, keyAtomic);
	//
	// dataFactorySpy.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", 1, "value",
	// ID_FROM_LOGIN);
	// var idFromLoginAtomic = dataFactorySpy.MCR
	// .getReturnValue("factorAtomicUsingNameInDataAndValue", 1);
	// partDataGroup.MCR.assertParameters("addChild", 1, idFromLoginAtomic);
	// }
	//
	// @Test
	// public void testGetUserByIdFromLogin_moreThanOneUserFoundInStorage() throws Exception {
	// setupRecordStorageToReturnUserForReadListUsingFilterNumberOfResults(2);
	//
	// try {
	// appTokenStorage.getUserByIdFromLogin(ID_FROM_LOGIN);
	// assertTrue(false);
	// } catch (Exception e) {
	// assertTrue(e instanceof MetadataStorageViewException);
	// assertEquals(e.getMessage(),
	// "Error reading user with login id: " + ID_FROM_LOGIN + " from storage.");
	// }
	// }
	//
	// @Test
	// public void testGetUserByIdFromLogin_noUserFoundInStorage() throws Exception {
	// setupRecordStorageToReturnUserForReadListUsingFilterNumberOfResults(0);
	//
	// try {
	// appTokenStorage.getUserByIdFromLogin(ID_FROM_LOGIN);
	// assertTrue(false);
	// } catch (Exception e) {
	// assertTrue(e instanceof MetadataStorageViewException);
	// assertEquals(e.getMessage(),
	// "Error reading user with login id: " + ID_FROM_LOGIN + " from storage.");
	// }
	// }
	//
	// private void setupRecordStorageToReturnUserForReadListUsingFilterNumberOfResults(
	// int totalNumberOfMatches) {
	// DataGroupSpy userDataGroup = new DataGroupSpy();
	// userDataGroup.MRV.setReturnValues("getAllGroupsWithNameInData", List.of(),
	// "userAppTokenGroup");
	// StorageReadResult readResult = new StorageReadResult();
	// readResult.listOfDataGroups = List.of(userDataGroup, userDataGroup);
	// readResult.totalNumberOfMatches = totalNumberOfMatches;
	// recordStorage.MRV.setDefaultReturnValuesSupplier("readList",
	// (Supplier<StorageReadResult>) () -> readResult);
	// }
	//
	// @Test
	// public void testGetUserByIdFromLogin_throwsError() throws Exception {
	// RecordNotFoundException error = new RecordNotFoundException("error from spy");
	// recordStorage.MRV.setThrowException("read", error, List.of("recordType"), "user");
	//
	// try {
	// appTokenStorage.getUserByIdFromLogin(ID_FROM_LOGIN);
	// assertTrue(false);
	// } catch (Exception e) {
	// assertTrue(e instanceof MetadataStorageViewException);
	// assertEquals(e.getMessage(),
	// "Error reading user with login id: " + ID_FROM_LOGIN + " from storage.");
	// assertSame(e.getCause(), error);
	// }
	// }
	//
	// @Test
	// public void testGetAppTokenById() throws Exception {
	// AppToken appToken = appTokenStorage.getAppTokenById(APP_TOKEN_ID);
	//
	// recordStorage.MCR.assertParameterAsEqual("read", 0, "types", List.of("appToken"));
	// recordStorage.MCR.assertParameterAsEqual("read", 0, "id", APP_TOKEN_ID);
	//
	// DataGroupSpy appTokenData = (DataGroupSpy) recordStorage.MCR.getReturnValue("read", 0);
	//
	// assertEquals(appToken.id, APP_TOKEN_ID);
	//
	// appTokenData.MCR.assertParameters("getFirstAtomicValueWithNameInData", 0, "token");
	// appTokenData.MCR.assertReturn("getFirstAtomicValueWithNameInData", 0, appToken.tokenString);
	// }
	//
	// @Test
	// public void testGetAppTokenById_throwsError() throws Exception {
	// RecordNotFoundException error = new RecordNotFoundException("error from spy");
	// recordStorage.MRV.setThrowException("read", error, List.of("appToken"), APP_TOKEN_ID);
	//
	// try {
	// appTokenStorage.getAppTokenById(APP_TOKEN_ID);
	// assertTrue(false);
	// } catch (Exception e) {
	// assertTrue(e instanceof MetadataStorageViewException);
	// assertEquals(e.getMessage(),
	// "Error reading appToken with id: " + APP_TOKEN_ID + " from storage.");
	// assertSame(e.getCause(), error);
	// }
	// }
}
