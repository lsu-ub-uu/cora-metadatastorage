/*
 * Copyright 2022, 2024 Uppsala University Library
 * Copyright 2025 Olov McKie
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import se.uu.ub.cora.bookkeeper.metadata.CollectTerm;
import se.uu.ub.cora.bookkeeper.metadata.CollectTermHolder;
import se.uu.ub.cora.bookkeeper.metadata.IndexTerm;
import se.uu.ub.cora.bookkeeper.metadata.MetadataElement;
import se.uu.ub.cora.bookkeeper.metadata.PermissionTerm;
import se.uu.ub.cora.bookkeeper.metadata.StorageTerm;
import se.uu.ub.cora.bookkeeper.metadata.converter.DataToMetadataConverter;
import se.uu.ub.cora.bookkeeper.metadata.converter.DataToMetadataConverterProvider;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageView;
import se.uu.ub.cora.bookkeeper.storage.MetadataStorageViewException;
import se.uu.ub.cora.bookkeeper.validator.ValidationType;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.storage.Filter;
import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.StorageReadResult;

public class MetadataStorageViewImp implements MetadataStorageView {
	private RecordStorage recordStorage;

	public static MetadataStorageViewImp usingRecordStorageAndRecordTypeHandlerFactory(
			RecordStorage recordStorage) {
		return new MetadataStorageViewImp(recordStorage);
	}

	private MetadataStorageViewImp(RecordStorage recordStorage) {
		this.recordStorage = recordStorage;
	}

	@Override
	public Collection<DataRecordGroup> getMetadataElements() {
		return readMetadataElementsFromStorageForType("metadata");
	}

	private Collection<DataRecordGroup> readMetadataElementsFromStorageForType(String recordType) {
		try {
			return tryToReadMetadataElementsFromStorageForType(recordType);
		} catch (Exception e) {
			throw createMetadataStorageException(e);
		}
	}

	private Collection<DataRecordGroup> tryToReadMetadataElementsFromStorageForType(
			String recordType) {
		return readListOfElementsFromStorage(recordType);
	}

	private List<DataRecordGroup> readListOfElementsFromStorage(String recordType) {
		StorageReadResult readResult = recordStorage.readList(recordType, new Filter());
		return readResult.listOfDataRecordGroups;
	}

	@Override
	public MetadataElement getMetadataElement(String elementId) {
		try {
			DataRecordGroup dataRecordGroup = recordStorage.read("metadata", elementId);
			DataToMetadataConverter converter = DataToMetadataConverterProvider
					.getConverter(dataRecordGroup);
			return converter.toMetadata();
		} catch (Exception e) {
			throw MetadataStorageViewException
					.usingMessage("Metadata with id: " + elementId + ", not found in storage.");
		}
	}

	@Override
	public Collection<DataGroup> getPresentationElements() {
		return readMetadataElementsFromStorageForTypeGroup("presentation");
	}

	private Collection<DataGroup> readMetadataElementsFromStorageForTypeGroup(String recordType) {
		try {
			return tryToReadMetadataElementsFromStorageForTypeGroup(recordType);
		} catch (Exception e) {
			throw createMetadataStorageException(e);
		}
	}

	private Collection<DataGroup> tryToReadMetadataElementsFromStorageForTypeGroup(
			String recordType) {
		return readListOfElementsFromStorageGroup(recordType);
	}

	private List<DataGroup> readListOfElementsFromStorageGroup(String recordType) {
		StorageReadResult readResult = recordStorage.readList(List.of(recordType), new Filter());
		return readResult.listOfDataGroups;
	}

	@Override
	public Collection<DataGroup> getTexts() {
		return readMetadataElementsFromStorageForTypeGroup("text");
	}

	@Override
	public Collection<DataGroup> getRecordTypes() {
		try {
			return readListOfElementsFromStorageGroup("recordType");
		} catch (Exception e) {
			throw createMetadataStorageException(e);
		}
	}

	private MetadataStorageViewException createMetadataStorageException(Exception e) {
		return MetadataStorageViewException
				.usingMessageAndException("Error getting metadata elements from storage.", e);
	}

	@Override
	public Collection<DataGroup> getCollectTermsAsDataGroup() {
		return readMetadataElementsFromStorageForTypeGroup("collectTerm");
	}

	public RecordStorage onlyForTestGetRecordStorage() {
		return recordStorage;
	}

	@Override
	public Collection<ValidationType> getValidationTypes() {
		StorageReadResult readList = recordStorage.readList("validationType", new Filter());
		return convertToCollectionOfValidationTypes(readList);
	}

	private List<ValidationType> convertToCollectionOfValidationTypes(StorageReadResult readList) {
		List<ValidationType> listOfValidationTypes = new ArrayList<>();
		for (DataRecordGroup dataRecordGroup : readList.listOfDataRecordGroups) {
			ValidationType validationType = createValidationTypeFromDataGroup(dataRecordGroup);
			listOfValidationTypes.add(validationType);
		}
		return listOfValidationTypes;
	}

	private ValidationType createValidationTypeFromDataGroup(DataRecordGroup validationTypeDG) {
		String validatesRecordTypeId = getLinkedRecordIdForLinkByName(validationTypeDG,
				"validatesRecordType");
		String createDefinitionId = getLinkedRecordIdForLinkByName(validationTypeDG,
				"newMetadataId");
		String updateDefinitionId = getLinkedRecordIdForLinkByName(validationTypeDG, "metadataId");
		return new ValidationType(validatesRecordTypeId, createDefinitionId, updateDefinitionId);
	}

	private String getLinkedRecordIdForLinkByName(DataRecordGroup validationTypeDG, String name) {
		DataRecordLink firstChildOfTypeAndName = validationTypeDG
				.getFirstChildOfTypeAndName(DataRecordLink.class, name);
		return firstChildOfTypeAndName.getLinkedRecordId();
	}

	@Override
	public Optional<ValidationType> getValidationType(String validationId) {
		try {
			return readValidationTypeFromStorageById(validationId);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	private Optional<ValidationType> readValidationTypeFromStorageById(String validationId) {
		DataRecordGroup validationTypeDG = recordStorage.read("validationType", validationId);
		ValidationType validationType = createValidationTypeFromDataGroup(validationTypeDG);
		return Optional.of(validationType);
	}

	@Override
	public CollectTermHolder getCollectTermHolder() {
		List<DataRecordGroup> collectTermsList = readCollectTermsFromStorage();
		if (noCollectTermsExistInStorage(collectTermsList)) {
			return new CollectTermHolderImp();
		}
		return convertDataRecordGroupToCollectTerms(collectTermsList);
	}

	private List<DataRecordGroup> readCollectTermsFromStorage() {
		StorageReadResult readList = recordStorage.readList("collectTerm", new Filter());
		return readList.listOfDataRecordGroups;
	}

	private boolean noCollectTermsExistInStorage(List<DataRecordGroup> collectTermsList) {
		return collectTermsList.isEmpty();
	}

	private CollectTermHolderImp convertDataRecordGroupToCollectTerms(
			List<DataRecordGroup> collectTermsList) {
		CollectTermHolderImp collectTermHolder = new CollectTermHolderImp();
		for (DataRecordGroup collecTermsAsRecordGroup : collectTermsList) {
			convertDataRecordGroupToCollectTerm(collectTermHolder, collecTermsAsRecordGroup);
		}
		return collectTermHolder;
	}

	private void convertDataRecordGroupToCollectTerm(CollectTermHolderImp collectTermHolder,
			DataRecordGroup collecTermsAsRecordGroup) {
		DataAttribute typeAttibute = collecTermsAsRecordGroup.getAttribute("type");
		String type = typeAttibute.getValue();
		String id = collecTermsAsRecordGroup.getId();
		CollectTerm collectTerm = createCollectTerm(type, id, collecTermsAsRecordGroup);
		collectTermHolder.addCollectTerm(collectTerm);
	}

	private CollectTerm createCollectTerm(String type, String id,
			DataRecordGroup collecTermsAsRecordGroup) {
		DataGroup extraData = collecTermsAsRecordGroup.getFirstGroupWithNameInData("extraData");
		if ("storage".equals(type)) {
			return createStorageTerm(id, extraData);
		}
		if ("index".equals(type)) {
			return createIndexTerm(id, getNameInDataFromRecordGroup(collecTermsAsRecordGroup),
					extraData);
		}
		return createPermissionTerm(id, getNameInDataFromRecordGroup(collecTermsAsRecordGroup),
				extraData);
	}

	private String getNameInDataFromRecordGroup(DataRecordGroup collecTermsAsRecordGroup) {
		return collecTermsAsRecordGroup.getFirstAtomicValueWithNameInData("nameInData");
	}

	private CollectTerm createStorageTerm(String id, DataGroup extraData) {
		String storageKey = extraData.getFirstAtomicValueWithNameInData("storageKey");
		return StorageTerm.usingIdAndStorageKey(id, storageKey);
	}

	private CollectTerm createIndexTerm(String id, String nameInData, DataGroup extraData) {
		String indexFieldName = extraData.getFirstAtomicValueWithNameInData("indexFieldName");
		String indexType = extraData.getFirstAtomicValueWithNameInData("indexType");
		return IndexTerm.usingIdAndNameInDataAndIndexFieldNameAndIndexType(id, nameInData,
				indexFieldName, indexType);
	}

	private CollectTerm createPermissionTerm(String id, String nameInData, DataGroup extraData) {
		String permissionKey = extraData.getFirstAtomicValueWithNameInData("permissionKey");
		return PermissionTerm.usingIdAndNameInDataAndPermissionKey(id, nameInData, permissionKey);
	}

}