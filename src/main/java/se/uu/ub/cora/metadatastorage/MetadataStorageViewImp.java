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

import java.util.Collection;
import java.util.List;

import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.data.DataGroup;

public class MetadataStorageViewImp implements MetadataStorageView {
	private static final String USER = "user";
	private static final String APP_TOKEN = "appToken";
	private static final List<String> LIST_RECORD_TYPE = List.of("recordType");
	private static final String ERROR_MESSAGE = "Error reading {0} with id: {1} from storage.";
	private static final String ERROR_MESSAGE_LOGIN_ID = "Error reading user with login id: {0} from storage.";

	@Override
	public Collection<DataGroup> getMetadataElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataGroup> getPresentationElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataGroup> getTexts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataGroup> getRecordTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataGroup> getCollectTerms() {
		// TODO Auto-generated method stub
		return null;
	}

	// private RecordStorage recordStorage;
	// protected List<String> userRecordTypeNames = new ArrayList<>();
	// private RecordTypeHandlerFactory recordTypeHandlerFactory;
	// private DataGroupToUser dataGroupToUser;
	//
	// public static MetadataStorageViewImp usingRecordStorageAndRecordTypeHandlerFactory(
	// RecordStorage recordStorage, RecordTypeHandlerFactory recordTypeHandlerFactory,
	// DataGroupToUser dataGroupToUser) {
	// return new MetadataStorageViewImp(recordStorage, recordTypeHandlerFactory, dataGroupToUser);
	// }
	//
	// private MetadataStorageViewImp(RecordStorage recordStorage,
	// RecordTypeHandlerFactory recordTypeHandlerFactory, DataGroupToUser dataGroupToUser) {
	// this.recordStorage = recordStorage;
	// this.recordTypeHandlerFactory = recordTypeHandlerFactory;
	// this.dataGroupToUser = dataGroupToUser;
	// }
	//
	// @Override
	// public User getUserById(String userId) {
	// try {
	// return tryToGetUserById(userId);
	// } catch (Exception e) {
	// String formatErrorMessage = MessageFormat.format(ERROR_MESSAGE, USER, userId);
	// throw UserStorageViewException.usingMessageAndException(formatErrorMessage, e);
	// }
	// }
	//
	// private User tryToGetUserById(String userId) {
	// var listOfUserTypes = getImplementingTypesForUser();
	// var userDataGroup = recordStorage.read(listOfUserTypes, userId);
	// return dataGroupToUser.groupToUser(userDataGroup);
	// }
	//
	// private List<String> getImplementingTypesForUser() {
	// DataGroup userRecordType = recordStorage.read(LIST_RECORD_TYPE, USER);
	// RecordTypeHandler recordTypeHandler = recordTypeHandlerFactory
	// .factorUsingDataGroup(userRecordType);
	// return recordTypeHandler.getListOfImplementingRecordTypeIds();
	// }
	//
	// @Override
	// public User getUserByIdFromLogin(String idFromLogin) {
	// try {
	// return tryToGetUserByIdFromLogin(idFromLogin);
	// } catch (UserStorageViewException e) {
	// throw e;
	// } catch (Exception e) {
	// String formatErrorMessage = MessageFormat.format(ERROR_MESSAGE_LOGIN_ID, idFromLogin);
	// throw UserStorageViewException.usingMessageAndException(formatErrorMessage, e);
	// }
	// }
	//
	// private User tryToGetUserByIdFromLogin(String idFromLogin) {
	// var listOfUserTypes = getImplementingTypesForUser();
	// DataGroup filter = createFilter(idFromLogin);
	// StorageReadResult usersList = recordStorage.readList(listOfUserTypes, filter);
	// assertOnlyOneUserFound(usersList, idFromLogin);
	// return dataGroupToUser.groupToUser(usersList.listOfDataGroups.get(0));
	// }
	//
	// private void assertOnlyOneUserFound(StorageReadResult userReadResult, String idFromLogin) {
	// if (foundNoneOrMultipleUsers(userReadResult)) {
	// String formatErrorMessage = MessageFormat.format(ERROR_MESSAGE_LOGIN_ID, idFromLogin);
	// throw UserStorageViewException.usingMessage(formatErrorMessage);
	// }
	// }
	//
	// private boolean foundNoneOrMultipleUsers(StorageReadResult userReadResult) {
	// return userReadResult.totalNumberOfMatches != 1;
	// }
	//
	// private DataGroup createFilter(String idFromLogin) {
	// DataGroup filter = DataProvider.createGroupUsingNameInData("filter");
	// DataGroup partGroup = DataProvider.createGroupUsingNameInData("part");
	// filter.addChild(partGroup);
	// partGroup.addChild(DataProvider.createAtomicUsingNameInDataAndValue("key", "userId"));
	// partGroup.addChild(DataProvider.createAtomicUsingNameInDataAndValue("value", idFromLogin));
	// return filter;
	// }
	//
	// @Override
	// public AppToken getAppTokenById(String appTokenId) {
	// try {
	// return tryToGetAppTokenById(appTokenId);
	// } catch (Exception e) {
	// String formatErrorMessage = MessageFormat.format(ERROR_MESSAGE, APP_TOKEN, appTokenId);
	// throw UserStorageViewException.usingMessageAndException(formatErrorMessage, e);
	// }
	// }
	//
	// private AppToken tryToGetAppTokenById(String appTokenId) {
	// DataGroup appTokenGroup = recordStorage.read(List.of(APP_TOKEN), appTokenId);
	// String tokenValue = appTokenGroup.getFirstAtomicValueWithNameInData("token");
	// return new AppToken(appTokenId, tokenValue);
	// }
	//
	// public RecordStorage onlyForTestGetRecordStorage() {
	// return recordStorage;
	// }
	//
	// public RecordTypeHandlerFactory onlyForTestGetRecordTypeHandlerFactory() {
	// return recordTypeHandlerFactory;
	// }
	//
	// public DataGroupToUser onlyForTestGetDataGroupToUser() {
	// return dataGroupToUser;
	// }

}
