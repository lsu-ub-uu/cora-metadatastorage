/*
* Copyright 2022, 2024 Uppsala University Library
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.CollectTermHolder;
import se.uu.ub.cora.bookkeeper.metadata.IndexTerm;
import se.uu.ub.cora.bookkeeper.metadata.PermissionTerm;
import se.uu.ub.cora.bookkeeper.metadata.StorageTerm;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewException;
import se.uu.ub.cora.bookkeeper.validator.ValidationType;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.spies.DataAttributeSpy;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.storage.Filter;
import se.uu.ub.cora.storage.RecordNotFoundException;
import se.uu.ub.cora.storage.StorageReadResult;
import se.uu.ub.cora.storage.spies.RecordStorageSpy;

public class MetadataStorageViewTest {
	private MetadataStorageView metadataStorage;
	private RecordStorageSpy recordStorage;
	private DataFactorySpy dataFactorySpy;
	private StorageReadResult resultWithValues;

	@BeforeMethod
	public void beforeMethod() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);

		recordStorage = new RecordStorageSpy();
		createReadResultWithValues();
		recordStorage.MRV.setDefaultReturnValuesSupplier("readList", () -> resultWithValues);

		metadataStorage = MetadataStorageViewImp
				.usingRecordStorageAndRecordTypeHandlerFactory(recordStorage);
	}

	private void createReadResultWithValues() {
		resultWithValues = new StorageReadResult();
		resultWithValues.listOfDataGroups = List.of(new DataGroupSpy(), new DataGroupSpy(),
				new DataGroupSpy());
	}

	@Test
	public void testGetMetadataElements() throws Exception {
		callAndAssertListFromStorageByRecordType("metadata", metadataStorage::getMetadataElements);
	}

	private void callAndAssertListFromStorageByRecordType(String recordType,
			Callable<Collection<DataGroup>> methodToCall) throws Exception {
		Collection<DataGroup> result = methodToCall.call();
		assertCollectionFromStorage(recordType, result);
	}

	private void assertCollectionFromStorage(String recordType, Collection<DataGroup> result) {
		assertExpectedCollection(recordType, result);
	}

	private void assertExpectedCollection(String recordType, Collection<DataGroup> result) {
		recordStorage.MCR.assertParameterAsEqual("readList", 0, "types", List.of(recordType));
		assertEmptyFilter();

		StorageReadResult readResult = (StorageReadResult) recordStorage.MCR
				.getReturnValue("readList", 0);

		assertSame(result, readResult.listOfDataGroups);
	}

	private void assertEmptyFilter() {
		Filter filter = (Filter) recordStorage.MCR
				.getValueForMethodNameAndCallNumberAndParameterName("readList", 0, "filter");
		assertFalse(filter.filtersResults());
	}

	@Test
	public void testGetPresentationElements() throws Exception {
		callAndAssertListFromStorageByRecordType("presentation",
				metadataStorage::getPresentationElements);
	}

	@Test
	public void testGetTexts() throws Exception {
		callAndAssertListFromStorageByRecordType("text", metadataStorage::getTexts);
	}

	@Test
	public void testGetRecordTypes() throws Exception {
		var recordTypes = metadataStorage.getRecordTypes();

		recordStorage.MCR.assertParameterAsEqual("readList", 0, "types", List.of("recordType"));
		assertEmptyFilter();
		StorageReadResult readResult = (StorageReadResult) recordStorage.MCR
				.getReturnValue("readList", 0);

		assertSame(recordTypes, readResult.listOfDataGroups);
	}

	@Test
	public void testGetCollectTermsAsDataGroup() throws Exception {
		callAndAssertListFromStorageByRecordType("collectTerm",
				metadataStorage::getCollectTermsAsDataGroup);
	}

	@Test
	public void testAllExceptions() throws Exception {
		testGetMetadataElementsThrowsException(metadataStorage::getMetadataElements);
		testGetMetadataElementsThrowsException(metadataStorage::getPresentationElements);
		testGetMetadataElementsThrowsException(metadataStorage::getTexts);
		testGetMetadataElementsThrowsException(metadataStorage::getRecordTypes);
		testGetMetadataElementsThrowsException(metadataStorage::getCollectTermsAsDataGroup);
	}

	private void testGetMetadataElementsThrowsException(
			Callable<Collection<DataGroup>> methodToCall) {
		RuntimeException errorToThrow = new RuntimeException();
		recordStorage.MRV.setAlwaysThrowException("readList", errorToThrow);

		try {
			methodToCall.call();
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(e instanceof MetadataStorageViewException);
			assertEquals(e.getMessage(), "Error getting metadata elements from storage.");
			assertEquals(e.getCause(), errorToThrow);
		}
	}

	@Test
	public void testValidationTypesEmptyCollection() throws Exception {
		StorageReadResult storageReadResult = new StorageReadResult();
		storageReadResult.listOfDataGroups = Collections.emptyList();
		recordStorage.MRV.setDefaultReturnValuesSupplier("readList", () -> storageReadResult);

		Collection<ValidationType> validationTypes = metadataStorage.getValidationTypes();

		recordStorage.MCR.assertParameter("readList", 0, "type", "validationType");
		assertFilterSentToReadListIsCreated();
		assertEquals(validationTypes.size(), 0);
	}

	private void assertFilterSentToReadListIsCreated() {
		Object filter = recordStorage.MCR
				.getValueForMethodNameAndCallNumberAndParameterName("readList", 0, "filter");
		assertTrue(filter instanceof Filter);
	}

	@Test
	public void testValidationTypesCollectionHasValidationTypes() throws Exception {
		setUpRecordStorageToReturnTwoDataGroupsForValidationType();

		Collection<ValidationType> validationTypes = metadataStorage.getValidationTypes();

		assertReturnedCollectionContainsValidationTypesWithCorrectData(validationTypes);
	}

	private void setUpRecordStorageToReturnTwoDataGroupsForValidationType() {
		DataRecordGroupSpy validationTypeDG1 = createDataGroupWithDataForValidationType("1");
		DataRecordGroupSpy validationTypeDG2 = createDataGroupWithDataForValidationType("2");
		StorageReadResult storageReadResult = new StorageReadResult();
		storageReadResult.listOfDataRecordGroups = List.of(validationTypeDG1, validationTypeDG2);
		recordStorage.MRV.setDefaultReturnValuesSupplier("readList", () -> storageReadResult);
	}

	private void assertReturnedCollectionContainsValidationTypesWithCorrectData(
			Collection<ValidationType> validationTypes) {
		var validationType1 = new ValidationType("someRecordTypeToValidates1",
				"createDefinitionId1", "updateDefinitionId1");
		var validationType2 = new ValidationType("someRecordTypeToValidates2",
				"createDefinitionId2", "updateDefinitionId2");

		assertTrue(validationTypes.contains(validationType1));
		assertTrue(validationTypes.contains(validationType2));
	}

	public DataRecordGroupSpy createDataGroupWithDataForValidationType(String suffix) {
		DataRecordGroupSpy dataRecordGroup = new DataRecordGroupSpy();

		DataRecordLinkSpy validatesRecordType = new DataRecordLinkSpy();
		validatesRecordType.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> "someRecordTypeToValidates" + suffix);
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> validatesRecordType, DataRecordLink.class, "validatesRecordType");

		DataRecordLinkSpy newMetadataId = new DataRecordLinkSpy();
		newMetadataId.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> "createDefinitionId" + suffix);
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> newMetadataId, DataRecordLink.class, "newMetadataId");

		DataRecordLinkSpy metadataId = new DataRecordLinkSpy();
		metadataId.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> "updateDefinitionId" + suffix);
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> metadataId, DataRecordLink.class, "metadataId");

		return dataRecordGroup;
	}

	@Test
	public void testValidationTypeDoesNotExistInStorage() throws Exception {
		recordStorage.MRV.setThrowException("read",
				RecordNotFoundException.withMessage("not found"));

		Optional<ValidationType> validationType = metadataStorage
				.getValidationType("someValidationTypeId");

		assertTrue(validationType.isEmpty());
	}

	@Test
	public void testValidationTypeExistInStorage() throws Exception {
		setUpRecordStorageForReadForOneValidationType();

		Optional<ValidationType> validationType = metadataStorage
				.getValidationType("someValidationTypeId1");

		var expectedValidationType = new ValidationType("someRecordTypeToValidates1",
				"createDefinitionId1", "updateDefinitionId1");
		assertTrue(validationType.isPresent());
		assertEquals(validationType.get(), expectedValidationType);
	}

	private void setUpRecordStorageForReadForOneValidationType() {
		DataRecordGroupSpy validationTypeDG1 = createDataGroupWithDataForValidationType("1");
		DataRecordGroupSpy validationTypeDG2 = createDataGroupWithDataForValidationType("2");
		recordStorage.MRV.setSpecificReturnValuesSupplier("read", () -> validationTypeDG1,
				"validationType", "someValidationTypeId1");
		recordStorage.MRV.setSpecificReturnValuesSupplier("read", () -> validationTypeDG2,
				"validationType", "someValidationTypeId2");
	}

	@Test
	public void testGetEmptyCollectTerms() throws Exception {
		StorageReadResult storageReadResult = new StorageReadResult();
		recordStorage.MRV.setDefaultReturnValuesSupplier("readList", () -> storageReadResult);

		CollectTermHolder collectTermHolder = metadataStorage.getCollectTermHolder();

		assertTrue(collectTermHolder instanceof CollectTermHolderImp);
		Filter filter = (Filter) recordStorage.MCR
				.getValueForMethodNameAndCallNumberAndParameterName("readList", 0, "filter");
		assertFalse(filter.filtersResults());
	}

	@Test
	public void testGetCollectIndexTerms() throws Exception {
		StorageReadResult storageReadResult = new StorageReadResult();
		recordStorage.MRV.setDefaultReturnValuesSupplier("readList", () -> storageReadResult);
		String suffix = "i1";
		storageReadResult.listOfDataRecordGroups.add(createIndexTermAsRecordGroupSpy(suffix));

		CollectTermHolder collectTermHolder = metadataStorage.getCollectTermHolder();

		IndexTerm indexTerm = (IndexTerm) collectTermHolder.getCollectTermById("someId" + suffix);
		assertEquals(indexTerm.type, "index");
		assertEquals(indexTerm.id, "someId" + suffix);
		assertEquals(indexTerm.nameInData, "someNameInDataValue" + suffix);
		assertEquals(indexTerm.indexFieldName, "someIndexFieldNameValue" + suffix);
		assertEquals(indexTerm.indexType, "someIndexTypeValue" + suffix);
	}

	@Test
	public void testGetCollectStorageTerms() throws Exception {
		StorageReadResult storageReadResult = new StorageReadResult();
		recordStorage.MRV.setDefaultReturnValuesSupplier("readList", () -> storageReadResult);
		String suffix = "s1";
		storageReadResult.listOfDataRecordGroups.add(createStorageTermAsRecordGroupSpy(suffix));

		CollectTermHolder collectTermHolder = metadataStorage.getCollectTermHolder();

		StorageTerm storageTerm = (StorageTerm) collectTermHolder
				.getCollectTermById("someId" + suffix);
		assertEquals(storageTerm.type, "storage");
		assertEquals(storageTerm.id, "someId" + suffix);
		assertEquals(storageTerm.storageKey, "someStorageKeyValue" + suffix);
	}

	@Test
	public void testGetCollectPermissionTerms() throws Exception {
		StorageReadResult storageReadResult = new StorageReadResult();
		recordStorage.MRV.setDefaultReturnValuesSupplier("readList", () -> storageReadResult);
		String suffix = "p1";
		storageReadResult.listOfDataRecordGroups.add(createPermissionTermAsRecordGroupSpy(suffix));

		CollectTermHolder collectTermHolder = metadataStorage.getCollectTermHolder();

		PermissionTerm permissionTerm = (PermissionTerm) collectTermHolder
				.getCollectTermById("someId" + suffix);
		assertEquals(permissionTerm.type, "permission");
		assertEquals(permissionTerm.id, "someId" + suffix);
		assertEquals(permissionTerm.nameInData, "someNameInDataValue" + suffix);
		assertEquals(permissionTerm.permissionKey, "somePermissionKeyValue" + suffix);
	}

	@Test
	public void testGetCollectTerms() {
		StorageReadResult storageReadResult = new StorageReadResult();
		recordStorage.MRV.setDefaultReturnValuesSupplier("readList", () -> storageReadResult);
		storageReadResult.listOfDataRecordGroups.add(createPermissionTermAsRecordGroupSpy("p1"));
		storageReadResult.listOfDataRecordGroups.add(createStorageTermAsRecordGroupSpy("s1"));
		storageReadResult.listOfDataRecordGroups.add(createIndexTermAsRecordGroupSpy("i1"));
		storageReadResult.listOfDataRecordGroups.add(createPermissionTermAsRecordGroupSpy("p2"));
		storageReadResult.listOfDataRecordGroups.add(createStorageTermAsRecordGroupSpy("s2"));
		storageReadResult.listOfDataRecordGroups.add(createIndexTermAsRecordGroupSpy("i2"));

		CollectTermHolder collectTermHolder = metadataStorage.getCollectTermHolder();

		PermissionTerm permissionTerm = (PermissionTerm) collectTermHolder
				.getCollectTermById("someId" + "p2");
		assertEquals(permissionTerm.type, "permission");
		StorageTerm storageTerm = (StorageTerm) collectTermHolder
				.getCollectTermById("someId" + "s2");
		assertEquals(storageTerm.type, "storage");
		IndexTerm indexTerm = (IndexTerm) collectTermHolder.getCollectTermById("someId" + "i2");
		assertEquals(indexTerm.type, "index");
	}

	@Test(expectedExceptions = MetadataStorageViewException.class, expectedExceptionsMessageRegExp = ""
			+ "Metadata with id: someId, not found in storage.")
	public void testGetMetadataElementDoNotExists() {
		recordStorage.MRV.setAlwaysThrowException("read",
				RecordNotFoundException.withMessage("someException"));

		metadataStorage.getMetadataElement("someId");
	}

	@Test
	public void testGetMetadataElement() {
		metadataStorage.getMetadataElement("someId");

		recordStorage.MCR.assertParameters("read", 0, "metadata", "someId");
	}

	private DataRecordGroupSpy createIndexTermAsRecordGroupSpy(String suffix) {
		String type = "index";
		Pair indexFieldName = new Pair("indexFieldName", "someIndexFieldNameValue" + suffix);
		Pair indexType = new Pair("indexType", "someIndexTypeValue" + suffix);
		return createCollectTermAsRecordGroup(type, suffix, true, indexFieldName, indexType);
	}

	record Pair(String nameInData, String value) {
	}

	private DataRecordGroupSpy createCollectTermAsRecordGroup(String type, String suffix,
			boolean addNameInData, Pair... pairs) {
		String id = "someId" + suffix;
		Optional<String> nameInData = Optional.empty();
		if (addNameInData) {
			nameInData = Optional.of("someNameInDataValue" + suffix);
		}
		DataRecordGroupSpy recordGroup = createCollectTermAsRecord(type, id, nameInData);
		return createExtraDataAndaddChilds(recordGroup, pairs);
	}

	private DataRecordGroupSpy createExtraDataAndaddChilds(DataRecordGroupSpy recordGroup,
			Pair... pairs) {
		DataGroupSpy extraData = new DataGroupSpy();
		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				() -> extraData, "extraData");
		for (Pair pair : pairs) {
			extraData.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
					() -> pair.value, pair.nameInData);
		}
		return recordGroup;
	}

	private DataRecordGroupSpy createCollectTermAsRecord(String type, String id,
			Optional<String> nameInData) {
		DataRecordGroupSpy recordGroup = new DataRecordGroupSpy();
		DataAttributeSpy typeAttribute = new DataAttributeSpy();
		recordGroup.MRV.setDefaultReturnValuesSupplier("getAttribute", () -> typeAttribute);
		typeAttribute.MRV.setDefaultReturnValuesSupplier("getValue", () -> type);
		recordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> id);

		if (nameInData.isPresent()) {
			recordGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
					() -> true, "nameInData");
			recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
					() -> nameInData.get(), "nameInData");
		}
		return recordGroup;
	}

	private DataRecordGroupSpy createStorageTermAsRecordGroupSpy(String suffix) {
		String type = "storage";
		Pair storageKey = new Pair("storageKey", "someStorageKeyValue" + suffix);
		return createCollectTermAsRecordGroup(type, suffix, true, storageKey);
	}

	private DataRecordGroupSpy createPermissionTermAsRecordGroupSpy(String suffix) {
		String type = "permission";
		Pair permissionKey = new Pair("permissionKey", "somePermissionKeyValue" + suffix);
		return createCollectTermAsRecordGroup(type, suffix, true, permissionKey);
	}
}