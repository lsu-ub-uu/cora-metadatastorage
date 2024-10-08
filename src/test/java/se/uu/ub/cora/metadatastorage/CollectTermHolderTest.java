/*
 * Copyright 2017, 2019, 2024 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.metadata.CollectTerm;
import se.uu.ub.cora.bookkeeper.metadata.IndexTerm;
import se.uu.ub.cora.bookkeeper.metadata.PermissionTerm;
import se.uu.ub.cora.bookkeeper.metadata.StorageTerm;

public class CollectTermHolderTest {
	CollectTermHolderImp holder;

	@BeforeMethod
	public void beforeMethod() {
		holder = new CollectTermHolderImp();
	}

	@Test
	public void testAddAndReadStorageTerm() {
		StorageTerm storageTerm = StorageTerm.usingIdAndStorageKey("someId",
				"someStorageKey");

		holder.addCollectTerm(storageTerm);
		CollectTerm collectTerm = holder.getCollectTermById("someId");
		assertEquals(collectTerm, storageTerm);
	}

	@Test
	public void testAddAndReadPermissionTerm() {
		PermissionTerm permissionTerm = PermissionTerm.usingIdAndNameInDataAndPermissionKey(
				"someId", "someNameInData", "somePermissionKey");

		holder.addCollectTerm(permissionTerm);
		assertEquals(holder.getCollectTermById("someId"), permissionTerm);
	}

	@Test
	public void testAddAndReadIndexTerm() {
		IndexTerm indexTerm = IndexTerm.usingIdAndNameInDataAndIndexFieldNameAndIndexType(
				"someId", "someNameInData", "someFiledName", "someIndexType");

		holder.addCollectTerm(indexTerm);
		assertEquals(holder.getCollectTermById("someId"), indexTerm);
	}

}