/*
 * Copyright 2025 Uppsala University Library
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

package se.uu.ub.cora.metadatastorage.converter.datatotextelement;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.text.TextElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.spies.DataAttributeSpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;

public class DataToTextElementConverterTest {

	private DataRecordGroupSpy dataRecordGroup;
	private DataToTextElementConverter converter;
	private List<DataGroup> textPartGroupsList = new ArrayList<>();

	@BeforeMethod
	private void beforeMethod() {
		createTextDataRecordGroup();
		addTranslationToTextDataRecordGroup(new TranslationForTest("en", "a text"));
		addTranslationToTextDataRecordGroup(new TranslationForTest("no", "en tekst"));
		addTranslationToTextDataRecordGroup(new TranslationForTest("sv", "en text"));

		converter = new DataToTextElementConverterImp(dataRecordGroup);
	}

	private void createTextDataRecordGroup() {
		dataRecordGroup = new DataRecordGroupSpy();
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> "someId");
		textPartGroupsList = new ArrayList<>();
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getAllGroupsWithNameInData",
				() -> textPartGroupsList, "textPart");
	}

	private void addTranslationToTextDataRecordGroup(TranslationForTest... translations) {
		for (var translation : translations) {
			DataGroupSpy textPart = createTextPartUsingAttrAndText(translation);
			textPartGroupsList.add(textPart);
		}
	}

	private DataGroupSpy createTextPartUsingAttrAndText(TranslationForTest translation) {
		DataAttributeSpy langAttribute = createAttribute(translation);
		DataGroupSpy textPart = new DataGroupSpy();
		textPart.MRV.setSpecificReturnValuesSupplier("getAttribute", () -> langAttribute, "lang");
		textPart.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				translation::text, "text");
		return textPart;
	}

	private DataAttributeSpy createAttribute(TranslationForTest translation) {
		DataAttributeSpy langAttribute = new DataAttributeSpy();
		langAttribute.MRV.setDefaultReturnValuesSupplier("getValue", translation::lang);
		return langAttribute;
	}

	@Test
	public void testConvertToText_wentWrong() {
		RuntimeException exceptionThrown = new RuntimeException("someSpyError");
		dataRecordGroup.MRV.setAlwaysThrowException("getAllGroupsWithNameInData", exceptionThrown);
		try {
			converter.convert();
			fail();
		} catch (Exception e) {
			assertEquals(e.getMessage(),
					"Data to textElement converter failed for record: someId due to: someSpyError");
			assertEquals(e.getCause(), exceptionThrown);
		}
	}

	@Test
	public void testConvertToText() {
		TextElement textElement = converter.convert();

		assertEquals(textElement.getId(), "someId");
		var translations = textElement.getTranslations();
		assertEquals(translations.size(), 3);

		assertEquals(textElement.getTranslationByLanguage("sv"), "en text");
		assertEquals(textElement.getTranslationByLanguage("no"), "en tekst");
		assertEquals(textElement.getTranslationByLanguage("en"), "a text");
	}

	record TranslationForTest(String lang, String text) {
	}

	@Test
	public void testOnlyForTestGetDataRecordGroup() {
		var dataRecordGroup2 = ((DataToTextElementConverterImp) converter)
				.onlyForTestGetDataRecordGroup();
		assertEquals(dataRecordGroup2, dataRecordGroup);
	}
}
