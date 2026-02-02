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
package se.uu.ub.cora.metadatastorage;

import java.util.Collections;
import java.util.Optional;

import se.uu.ub.cora.bookkeeper.recordtype.RecordType;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.metadatastorage.converter.datatometadata.DataToRecordTypeConverter;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class DataToRecordTypeConverterSpy implements DataToRecordTypeConverter {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public DataToRecordTypeConverterSpy() {
		RecordType recordType = new RecordType("someId", "someDefinitionId", Optional.empty(),
				"someIdSource", Optional.empty(), Collections.emptyList(), false, false, false,
				false, false);

		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("convert", () -> recordType);
	}

	@Override
	public RecordType convert(DataRecordGroup dataRecordGroup) {
		return (RecordType) MCR.addCallAndReturnFromMRV("dataRecordGroup", dataRecordGroup);
	}
}
