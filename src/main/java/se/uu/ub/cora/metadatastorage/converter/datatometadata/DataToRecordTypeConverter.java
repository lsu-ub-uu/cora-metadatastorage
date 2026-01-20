/*
 * Copyright 2026 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.recordtype.RecordType;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.metadatastorage.converter.DataConversionException;

public interface DataToRecordTypeConverter {

	/**
	 * convert returns the recordtype linked to a {@link DataRecordGroup} as a record of
	 * type{@link RecordType}
	 * 
	 * @param dataRecordGroup
	 *            this is record group that we want to get the recordType information from.
	 * @return A recordType record with information of the recordType of dataRecordGroup.
	 * 
	 * @throws DataConversionException
	 *             if conversion goes wrong.
	 */
	RecordType convert(DataRecordGroup dataRecordGroup);

}
