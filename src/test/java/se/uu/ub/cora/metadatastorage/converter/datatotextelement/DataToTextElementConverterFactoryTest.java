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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.spies.DataRecordGroupSpy;

public class DataToTextElementConverterFactoryTest {

	private DataToTextElementConverterFactory factory;
	private DataRecordGroupSpy dataRecordGroup;

	@BeforeMethod
	private void beforeMethod() {
		factory = new DataToTextElementConverterFactoryImp();
		dataRecordGroup = new DataRecordGroupSpy();
	}

	@Test
	public void testFactor() {
		DataToTextElementConverter converter = factory.factor(dataRecordGroup);

		assertEquals(((DataToTextElementConverterImp) converter).onlyForTestGetDataRecordGroup(),
				dataRecordGroup);
	}
}
