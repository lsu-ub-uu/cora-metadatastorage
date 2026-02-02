/*
 * Copyright 2025, 2026 Uppsala University Library
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

package se.uu.ub.cora.metadatastorage.converter.datatometadata;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.recordtype.RecordType;
import se.uu.ub.cora.bookkeeper.recordtype.UniqueIds;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.metadatastorage.converter.DataConversionException;

public class DataToRecordTypeConverterTest {

	private DataRecordGroupSpy dataRecordGroup;
	private DataToRecordTypeConverter converter;

	@BeforeMethod
	private void beforeMethod() {
		createRecordGroup();

		converter = new DataToRecordTypeConverterImp();
	}

	private void createRecordGroup() {
		dataRecordGroup = new DataRecordGroupSpy();
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> "someId");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> "someIdSource", "idSource");
		addRecordLinkToDataRecordGroup("metadataId", "someDefinitionId");
	}

	private void addRecordLinkToDataRecordGroup(String nameInData, String linkedId) {
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				() -> true, nameInData);
		DataRecordLinkSpy definitinLink = new DataRecordLinkSpy();
		definitinLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> linkedId);
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> definitinLink, DataRecordLink.class, nameInData);
	}

	@Test
	public void testConvertId() {
		RecordType recordType = converter.convert(dataRecordGroup);

		assertEquals(recordType.id(), "someId");
	}

	@Test
	public void testConvertDefinitionId() {
		RecordType recordType = converter.convert(dataRecordGroup);

		assertEquals(recordType.definitionId(), "someDefinitionId");
	}

	@Test
	public void testSearch_DoNotExists() {
		RecordType recordType = converter.convert(dataRecordGroup);

		assertEquals(recordType.searchId(), Optional.empty());
	}

	@Test
	public void testSearch() {
		addRecordLinkToDataRecordGroup("search", "someSearchId");

		RecordType recordType = converter.convert(dataRecordGroup);

		assertEquals(recordType.searchId(), Optional.of("someSearchId"));
	}

	@Test
	public void testIdSource() {
		RecordType recordType = converter.convert(dataRecordGroup);

		assertEquals(recordType.idSource(), "someIdSource");
	}

	@Test
	public void testSequenceId_DoNotExists() {
		RecordType recordType = converter.convert(dataRecordGroup);

		assertEquals(recordType.sequenceId(), Optional.empty());
	}

	@Test
	public void testSequenceId() {
		addRecordLinkToDataRecordGroup("sequence", "someSequenceId");

		RecordType recordType = converter.convert(dataRecordGroup);

		assertEquals(recordType.sequenceId(), Optional.of("someSequenceId"));
	}

	@Test
	public void testUniqueIds_DoNotExists() {
		RecordType recordType = converter.convert(dataRecordGroup);

		assertEquals(recordType.uniqueIds(), Collections.emptyList());
	}

	@Test
	public void testUniqueIds_OneUniqueTerm() {
		DataGroupSpy uniqueDefinition01 = createUniqueDefinition("unique01",
				Collections.emptyList());
		addUnique(List.of(uniqueDefinition01));

		RecordType recordType = converter.convert(dataRecordGroup);

		assertEquals(recordType.uniqueIds().size(), 1);
		UniqueIds uniqueIds01 = getFirstElementFromCollection(recordType.uniqueIds());
		assertEquals(uniqueIds01.uniqueId(), "unique01_LinkId");
		assertEquals(uniqueIds01.combineTermId(), Collections.emptyList());
	}

	private UniqueIds getFirstElementFromCollection(Collection<UniqueIds> collection) {
		Iterator<UniqueIds> iterator = collection.iterator();
		return iterator.next();
	}

	private void addUnique(List<DataGroupSpy> allUniqueDefinitions) {
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getChildrenOfTypeAndName",
				() -> allUniqueDefinitions, DataGroup.class, "unique");
	}

	private DataGroupSpy createUniqueDefinition(String uniqueTerm, List<String> combineTerms) {
		DataGroupSpy uniqueDefinition = new DataGroupSpy();
		uniqueDefinition.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> createRecordLink(uniqueTerm), DataRecordLink.class, "uniqueTerm");

		List<DataRecordLink> combineTermsList = createListOfCombineTerms(combineTerms);
		uniqueDefinition.MRV.setSpecificReturnValuesSupplier("getChildrenOfTypeAndName",
				() -> combineTermsList, DataRecordLink.class, "combineTerm");

		return uniqueDefinition;
	}

	private List<DataRecordLink> createListOfCombineTerms(List<String> combineTerms) {
		List<DataRecordLink> combineTermsList = new ArrayList<>();
		for (String coombineTerm : combineTerms) {
			combineTermsList.add(createRecordLink(coombineTerm));
		}
		return combineTermsList;
	}

	private DataRecordLinkSpy createRecordLink(String id) {
		DataRecordLinkSpy link = new DataRecordLinkSpy();
		link.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> id + "_LinkId");
		return link;
	}

	@Test
	public void testUniqueIds_OneUniqueTermAndOneCombineTerms() {
		DataGroupSpy uniqueDefinition01 = createUniqueDefinition("unique01", List.of("combine01"));
		addUnique(List.of(uniqueDefinition01));

		RecordType recordType = converter.convert(dataRecordGroup);

		UniqueIds uniqueIds01 = new UniqueIds("unique01", Set.of("combine01"));
		assertUniqueDefinition(recordType, uniqueIds01);
	}

	private void assertUniqueDefinition(RecordType recordType, UniqueIds... expectedUniqueIds) {
		assertEquals(recordType.uniqueIds().size(), expectedUniqueIds.length);
		Object[] uniqueIdsArray = recordType.uniqueIds().toArray();
		int i = 0;
		for (UniqueIds uniqueIdsExpected : expectedUniqueIds) {
			assertUniqueTerm(uniqueIdsExpected, (UniqueIds) uniqueIdsArray[i]);
			assertCombineTerms(uniqueIdsExpected, (UniqueIds) uniqueIdsArray[i]);
			i++;
		}
	}

	private void assertCombineTerms(UniqueIds uniqueIdsExpected, UniqueIds uniqueIds) {
		Set<String> expectedCombineTerms = uniqueIdsExpected.combineTermId();
		Set<String> combineTermIdSet = uniqueIds.combineTermId();
		assertEquals(combineTermIdSet.size(), expectedCombineTerms.size());

		for (String expectedCombineTerm : expectedCombineTerms) {
			assertCombineTerm(combineTermIdSet, expectedCombineTerm);
		}
	}

	private void assertUniqueTerm(UniqueIds uniqueIdsExpected, UniqueIds uniqueIds) {
		assertEquals(uniqueIds.uniqueId(), uniqueIdsExpected.uniqueId() + "_LinkId");
	}

	private void assertCombineTerm(Set<String> combineTermIdSet, String combineTermExpected) {
		combineTermIdSet.contains(combineTermExpected + "_LinkId");
	}

	@Test
	public void testUniqueIds_OneUniqueTermAndTwoCombineTerms() {
		DataGroupSpy uniqueDefinition01 = createUniqueDefinition("unique01",
				List.of("combine01", "combine02"));
		addUnique(List.of(uniqueDefinition01));

		RecordType recordType = converter.convert(dataRecordGroup);

		UniqueIds uniqueIds01 = new UniqueIds("unique01", Set.of("combine01", "combine02"));
		assertUniqueDefinition(recordType, uniqueIds01);
	}

	@Test
	public void testUniqueIds_TwoUniqueTermAndTwoCombineTerms() {
		DataGroupSpy uniqueDefinition1 = createUniqueDefinition("unique1",
				List.of("combine11", "combine12"));
		DataGroupSpy uniqueDefinition2 = createUniqueDefinition("unique2",
				List.of("combine21", "combine22"));
		addUnique(List.of(uniqueDefinition1, uniqueDefinition2));

		RecordType recordType = converter.convert(dataRecordGroup);

		UniqueIds uniqueIds1 = new UniqueIds("unique1", Set.of("combine11", "combine12"));
		UniqueIds uniqueIds2 = new UniqueIds("unique2", Set.of("combine21", "combine22"));
		assertUniqueDefinition(recordType, uniqueIds1, uniqueIds2);
	}

	@Test
	public void testIsPublic() {
		addAtomicToDataRecordGroup("public", "true");

		RecordType recordType = converter.convert(dataRecordGroup);

		assertEquals(recordType.isPublic(), true);
	}

	private void addAtomicToDataRecordGroup(String nameInData, String value) {
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> value, nameInData);
	}

	@Test
	public void testUsePermissionUnit() {
		addAtomicToDataRecordGroup("usePermissionUnit", "true");

		RecordType recordType = converter.convert(dataRecordGroup);

		assertEquals(recordType.usePermissionUnit(), true);
	}

	@Test
	public void testUseVisibility() {
		addAtomicToDataRecordGroup("useVisibility", "true");

		RecordType recordType = converter.convert(dataRecordGroup);

		assertEquals(recordType.useVisibility(), true);
	}

	@Test
	public void testUseTrashBin() {
		addAtomicToDataRecordGroup("useTrashBin", "true");

		RecordType recordType = converter.convert(dataRecordGroup);

		assertEquals(recordType.useTrashBin(), true);
	}

	@Test
	public void testStoreInArchive() {
		addAtomicToDataRecordGroup("storeInArchive", "true");

		RecordType recordType = converter.convert(dataRecordGroup);

		assertEquals(recordType.storeInArchive(), true);
	}

	@Test
	public void testConvert_wentWrong() {
		RuntimeException originException = new RuntimeException("someSpyError");
		dataRecordGroup.MRV.setAlwaysThrowException("getFirstChildOfTypeAndName",
				originException);

		try {
			converter.convert(dataRecordGroup);
			fail();
		} catch (Exception e) {
			System.err.println(e.getClass().toString());
			assertTrue(e instanceof DataConversionException);
			assertEquals(e.getMessage(),
					"Data to recordType converter failed for record: someId due to: someSpyError");
			assertEquals(e.getCause(), originException);
		}
	}
}
