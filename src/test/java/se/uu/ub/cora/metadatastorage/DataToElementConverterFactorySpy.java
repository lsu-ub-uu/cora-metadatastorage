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
package se.uu.ub.cora.metadatastorage;

import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.metadatastorage.converter.datatometadata.DataToElementConverterFactory;
import se.uu.ub.cora.metadatastorage.converter.datatometadata.DataToRecordTypeConverter;
import se.uu.ub.cora.metadatastorage.converter.datatometadata.DataToTextElementConverter;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class DataToElementConverterFactorySpy implements DataToElementConverterFactory {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public DataToElementConverterFactorySpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("factorDataToTextElement",
				DataToTextElementConverterSpy::new);
		MRV.setDefaultReturnValuesSupplier("factorDataTorRecordType",
				DataToRecordTypeConverterSpy::new);
	}

	@Override
	public DataToTextElementConverter factorDataToTextElement(DataRecordGroup dataRecordGroup) {
		return (DataToTextElementConverter) MCR.addCallAndReturnFromMRV("dataRecordGroup",
				dataRecordGroup);
	}

	@Override
	public DataToRecordTypeConverter factorDataTorRecordType() {
		return (DataToRecordTypeConverter) MCR.addCallAndReturnFromMRV();
	}
}
