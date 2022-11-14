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

import java.util.Collection;
import java.util.List;

import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewException;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.spider.recordtype.RecordTypeHandler;
import se.uu.ub.cora.spider.recordtype.internal.RecordTypeHandlerFactory;
import se.uu.ub.cora.storage.Filter;
import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.StorageReadResult;

public class MetadataStorageViewImp implements MetadataStorageView {
	private RecordStorage recordStorage;
	private RecordTypeHandlerFactory recordTypeHandlerFactory;

	public static MetadataStorageViewImp usingRecordStorageAndRecordTypeHandlerFactory(
			RecordStorage recordStorage, RecordTypeHandlerFactory recordTypeHandlerFactory) {
		return new MetadataStorageViewImp(recordStorage, recordTypeHandlerFactory);
	}

	private MetadataStorageViewImp(RecordStorage recordStorage,
			RecordTypeHandlerFactory recordTypeHandlerFactory) {
		this.recordStorage = recordStorage;
		this.recordTypeHandlerFactory = recordTypeHandlerFactory;
	}

	@Override
	public Collection<DataGroup> getMetadataElements() {
		return readMetadataElementsFromStorageForType("metadata");
	}

	private Collection<DataGroup> readMetadataElementsFromStorageForType(String recordType) {
		try {
			return tryToReadMetadataElementsFromStorageForType(recordType);
		} catch (Exception e) {
			throw createMetadataStorageException(e);
		}
	}

	private Collection<DataGroup> tryToReadMetadataElementsFromStorageForType(String recordType) {
		List<String> listOfTypeIds = createListOfElementTypesUsingRecordHandler(recordType);
		return readListOfElementsFromStorage(listOfTypeIds);
	}

	private List<String> createListOfElementTypesUsingRecordHandler(String recordType) {
		var recordTypeDataGroup = recordStorage.read(List.of("recordType"), recordType);
		RecordTypeHandler recordTypeHandler = recordTypeHandlerFactory
				.factorUsingDataGroup(recordTypeDataGroup);
		return recordTypeHandler.getListOfImplementingRecordTypeIds();
	}

	private Collection<DataGroup> readListOfElementsFromStorage(List<String> listOfTypeIds) {
		StorageReadResult readResult = recordStorage.readList(listOfTypeIds, new Filter());
		return readResult.listOfDataGroups;
	}

	@Override
	public Collection<DataGroup> getPresentationElements() {
		return readMetadataElementsFromStorageForType("presentation");
	}

	@Override
	public Collection<DataGroup> getTexts() {
		return readMetadataElementsFromStorageForType("text");
	}

	@Override
	public Collection<DataGroup> getRecordTypes() {
		try {
			return readListOfElementsFromStorage(List.of("recordType"));
		} catch (Exception e) {
			throw createMetadataStorageException(e);
		}

	}

	private MetadataStorageViewException createMetadataStorageException(Exception e) {
		return MetadataStorageViewException
				.usingMessageAndException("Error getting metadata elements from storage.", e);
	}

	@Override
	public Collection<DataGroup> getCollectTerms() {
		return readMetadataElementsFromStorageForType("collectTerm");
	}

	public RecordStorage onlyForTestGetRecordStorage() {
		return recordStorage;
	}

	public RecordTypeHandlerFactory onlyForTestGetRecordTypeHandlerFactory() {
		return recordTypeHandlerFactory;
	}
}
