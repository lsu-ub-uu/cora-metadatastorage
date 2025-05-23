/*
 * Copyright 2022, 2025 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewInstanceProvider;
import se.uu.ub.cora.metadatastorage.converter.datatotextelement.DataToTextElementConverterFactoryImp;
import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.RecordStorageProvider;

public class MetadataStorageViewInstanceProviderImp implements MetadataStorageViewInstanceProvider {

	@Override
	public int getOrderToSelectImplementionsBy() {
		return 0;
	}

	@Override
	public MetadataStorageView getStorageView() {
		RecordStorage recordStorage = RecordStorageProvider.getRecordStorage();
		return MetadataStorageViewImp.usingRecordStorageAndTextConverterFactory(recordStorage,
				new DataToTextElementConverterFactoryImp());
	}
}
