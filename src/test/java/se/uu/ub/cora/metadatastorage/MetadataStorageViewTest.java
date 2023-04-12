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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewException;
import se.uu.ub.cora.bookkeeper.validator.ValidationType;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.metadatastorage.spies.RecordTypeHandlerFactorySpy;
import se.uu.ub.cora.metadatastorage.spies.RecordTypeHandlerSpy;
import se.uu.ub.cora.storage.Filter;
import se.uu.ub.cora.storage.RecordNotFoundException;
import se.uu.ub.cora.storage.StorageReadResult;
import se.uu.ub.cora.storage.spies.RecordStorageSpy;

public class MetadataStorageViewTest {
	private MetadataStorageView metadataStorage;
	private RecordStorageSpy recordStorage;
	private RecordTypeHandlerFactorySpy recordTypeHandlerFactory;
	private DataFactorySpy dataFactorySpy;
	private StorageReadResult resultWithValues;

	@BeforeMethod
	public void beforeMethod() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);

		recordStorage = new RecordStorageSpy();
		createReadResultWithValues();
		recordStorage.MRV.setDefaultReturnValuesSupplier("readList",
				(Supplier<StorageReadResult>) () -> resultWithValues);

		recordTypeHandlerFactory = new RecordTypeHandlerFactorySpy();
		metadataStorage = MetadataStorageViewImp.usingRecordStorageAndRecordTypeHandlerFactory(
				recordStorage, recordTypeHandlerFactory);
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
		var listOfImplementingTypes = assertAndGetImplementingTypes(recordType);
		assertExpectedCollection(result, listOfImplementingTypes);
	}

	private void assertExpectedCollection(Collection<DataGroup> result,
			Object listOfImplementingTypes) {
		recordStorage.MCR.assertParameter("readList", 0, "types", listOfImplementingTypes);
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

	private Object assertAndGetImplementingTypes(String recordType) {
		recordStorage.MCR.assertParameterAsEqual("read", 0, "types", List.of("recordType"));
		recordStorage.MCR.assertParameterAsEqual("read", 0, "id", recordType);

		var metadataRecordType = recordStorage.MCR.getReturnValue("read", 0);

		recordTypeHandlerFactory.MCR.assertParameters("factorUsingDataGroup", 0,
				metadataRecordType);
		RecordTypeHandlerSpy recordTypeHandler = (RecordTypeHandlerSpy) recordTypeHandlerFactory.MCR
				.getReturnValue("factorUsingDataGroup", 0);

		var listOfImplementingTypes = recordTypeHandler.MCR
				.getReturnValue("getListOfImplementingRecordTypeIds", 0);
		return listOfImplementingTypes;
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
	public void testGetCollectTerms() throws Exception {
		callAndAssertListFromStorageByRecordType("collectTerm", metadataStorage::getCollectTerms);
	}

	@Test
	public void testAllExceptions() throws Exception {
		testGetMetadataElementsThrowsException(metadataStorage::getMetadataElements);
		testGetMetadataElementsThrowsException(metadataStorage::getPresentationElements);
		testGetMetadataElementsThrowsException(metadataStorage::getTexts);
		testGetMetadataElementsThrowsException(metadataStorage::getRecordTypes);
		testGetMetadataElementsThrowsException(metadataStorage::getCollectTerms);
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

		recordStorage.MCR.assertParameterAsEqual("readList", 0, "types", List.of("validationType"));
		Object filter = recordStorage.MCR
				.getValueForMethodNameAndCallNumberAndParameterName("readList", 0, "filter");

		assertTrue(filter instanceof Filter);
		assertEquals(validationTypes.size(), 0);

	}

	@Test
	public void testValidationTypesCollectionHasValidationTypes() throws Exception {
		setUpRecordStorageToReturnTwoDataGroupsForValidationType();

		Collection<ValidationType> validationTypes = metadataStorage.getValidationTypes();

		assertReturnedCollectionContainsValidationTypesWithCorrectData(validationTypes);
	}

	private void setUpRecordStorageToReturnTwoDataGroupsForValidationType() {
		DataGroupSpy validationTypeDG1 = createDataGroupWithDataForValidationType("1");
		DataGroupSpy validationTypeDG2 = createDataGroupWithDataForValidationType("2");
		StorageReadResult storageReadResult = new StorageReadResult();
		storageReadResult.listOfDataGroups = List.of(validationTypeDG1, validationTypeDG2);
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

	public DataGroupSpy createDataGroupWithDataForValidationType(String suffix) {
		DataGroupSpy dataGroup = new DataGroupSpy();

		DataRecordLinkSpy validatesRecordType = new DataRecordLinkSpy();
		validatesRecordType.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> "someRecordTypeToValidates" + suffix);
		dataGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> validatesRecordType, DataRecordLink.class, "validatesRecordType");

		DataRecordLinkSpy newMetadataId = new DataRecordLinkSpy();
		newMetadataId.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> "createDefinitionId" + suffix);
		dataGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> newMetadataId, DataRecordLink.class, "newMetadataId");

		DataRecordLinkSpy metadataId = new DataRecordLinkSpy();
		metadataId.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> "updateDefinitionId" + suffix);
		dataGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> metadataId, DataRecordLink.class, "metadataId");

		return dataGroup;
	}

	@Test
	public void testValidationTypeDoesNotExistInStorage() throws Exception {
		recordStorage.MRV.setThrowException("read", new RecordNotFoundException("not found"));

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
		DataGroupSpy validationTypeDG1 = createDataGroupWithDataForValidationType("1");
		DataGroupSpy validationTypeDG2 = createDataGroupWithDataForValidationType("2");
		recordStorage.MRV.setSpecificReturnValuesSupplier("read", () -> validationTypeDG1,
				List.of("validationType"), "someValidationTypeId1");
		recordStorage.MRV.setSpecificReturnValuesSupplier("read", () -> validationTypeDG2,
				List.of("validationType"), "someValidationTypeId2");
	}
}
