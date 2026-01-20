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
package se.uu.ub.cora.metadatastorage.converter.datatometadata;

import java.text.MessageFormat;
import java.util.List;

import se.uu.ub.cora.bookkeeper.text.TextElement;
import se.uu.ub.cora.bookkeeper.text.TextElementImp;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.metadatastorage.converter.DataConversionException;

public class DataToTextElementConverterImp implements DataToTextElementConverter {

	private DataRecordGroup dataRecordGroup;

	public DataToTextElementConverterImp(DataRecordGroup dataRecordGroup) {
		this.dataRecordGroup = dataRecordGroup;
	}

	@Override
	public TextElement convert() {
		try {
			return tryToConvert();
		} catch (Exception e) {
			String message = "Data to textElement converter failed for record: {0} due to: {1}";
			String errorMessage = MessageFormat.format(message, dataRecordGroup.getId(),
					e.getMessage());
			throw DataConversionException.withMessageAndException(errorMessage, e);
		}
	}

	private TextElement tryToConvert() {
		TextElement textElement = TextElementImp.withId(dataRecordGroup.getId());
		List<DataGroup> textParts = dataRecordGroup.getAllGroupsWithNameInData("textPart");
		for (DataGroup textPart : textParts) {
			DataAttribute attribute = textPart.getAttribute("lang");
			String text = textPart.getFirstAtomicValueWithNameInData("text");
			textElement.addTranslation(attribute.getValue(), text);
		}
		return textElement;
	}

	public DataRecordGroup onlyForTestGetDataRecordGroup() {
		return dataRecordGroup;
	}

}
