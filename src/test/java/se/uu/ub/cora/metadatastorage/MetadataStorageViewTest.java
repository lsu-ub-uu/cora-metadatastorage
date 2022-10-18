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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewException;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.metadatastorage.spies.RecordTypeHandlerFactorySpy;
import se.uu.ub.cora.metadatastorage.spies.RecordTypeHandlerSpy;
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
		var emptyFilter = dataFactorySpy.MCR.getReturnValue("factorGroupUsingNameInData", 0);
		recordStorage.MCR.assertParameter("readList", 0, "types", listOfImplementingTypes);
		recordStorage.MCR.assertParameter("readList", 0, "filter", emptyFilter);

		StorageReadResult readResult = (StorageReadResult) recordStorage.MCR
				.getReturnValue("readList", 0);

		assertSame(result, readResult.listOfDataGroups);
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
		callAndAssertListFromStorageByRecordType("recordType", metadataStorage::getRecordTypes);
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
		recordStorage.MRV.setAlwaysThrowException("read", errorToThrow);

		try {
			methodToCall.call();
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(e instanceof MetadataStorageViewException);
			assertEquals(e.getMessage(), "Error getting metadata elements from storage.");
			assertEquals(e.getCause(), errorToThrow);
		}
	}
}
