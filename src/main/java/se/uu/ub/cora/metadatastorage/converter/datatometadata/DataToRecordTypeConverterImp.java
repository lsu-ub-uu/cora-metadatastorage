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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import se.uu.ub.cora.bookkeeper.recordtype.RecordType;
import se.uu.ub.cora.bookkeeper.recordtype.UniqueIds;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataParent;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.metadatastorage.converter.DataConversionException;

public class DataToRecordTypeConverterImp implements DataToRecordTypeConverter {

	@Override
	public RecordType convert(DataRecordGroup dataRecordGroup) {
		try {
			return convertToRecordType(dataRecordGroup);
		} catch (Exception e) {
			throw handlerError(e, dataRecordGroup.getId());
		}
	}

	private RecordType convertToRecordType(DataRecordGroup dataRecordGroup) {
		String id = dataRecordGroup.getId();
		String definitionId = getLinkedRecordId(dataRecordGroup, "metadataId");
		Optional<String> searchId = getOptionalLinkedRecordId(dataRecordGroup, "search");
		Optional<String> sequenceId = getOptionalLinkedRecordId(dataRecordGroup, "sequence");
		String idSource = dataRecordGroup.getFirstAtomicValueWithNameInData("idSource");
		Collection<UniqueIds> uniqueIds = getUniqueIds(dataRecordGroup);
		boolean isPublic = getBoolean(dataRecordGroup, "public");
		boolean usePermissionUnit = getBoolean(dataRecordGroup, "usePermissionUnit");
		boolean useVisibility = getBoolean(dataRecordGroup, "useVisibility");
		boolean useTrashBin = getBoolean(dataRecordGroup, "useTrashBin");
		boolean storeInArchive = getBoolean(dataRecordGroup, "storeInArchive");

		return new RecordType(id, definitionId, searchId, idSource, sequenceId, uniqueIds, isPublic,
				usePermissionUnit, useVisibility, useTrashBin, storeInArchive);
	}

	private boolean getBoolean(DataRecordGroup dataRecordGroup, String nameInData) {
		return "true".equals(dataRecordGroup.getFirstAtomicValueWithNameInData(nameInData));
	}

	private String getLinkedRecordId(DataParent dataRecordGroup, String nameInData) {
		DataRecordLink link = dataRecordGroup.getFirstChildOfTypeAndName(DataRecordLink.class,
				nameInData);
		return link.getLinkedRecordId();
	}

	private Optional<String> getOptionalLinkedRecordId(DataRecordGroup dataRecordGroup,
			String nameInData) {
		if (dataRecordGroup.containsChildWithNameInData(nameInData)) {
			String definitionId = getLinkedRecordId(dataRecordGroup, nameInData);
			return Optional.of(definitionId);
		}
		return Optional.empty();
	}

	private Collection<UniqueIds> getUniqueIds(DataRecordGroup dataRecordGroup) {
		List<DataGroup> uniques = dataRecordGroup.getChildrenOfTypeAndName(DataGroup.class,
				"unique");
		if (uniques.isEmpty()) {
			return Collections.emptyList();
		}
		return getAndConvertUniqueIds(uniques);
	}

	private Collection<UniqueIds> getAndConvertUniqueIds(List<DataGroup> uniques) {
		List<UniqueIds> list = new ArrayList<>();
		for (DataGroup uniqueGroup : uniques) {
			UniqueIds uniqueIds = getAndConvertOneUniqueIds(uniqueGroup);
			list.add(uniqueIds);
		}
		return list;
	}

	private UniqueIds getAndConvertOneUniqueIds(DataGroup uniqueGroup) {
		String uniqueId = getLinkedRecordId(uniqueGroup, "uniqueTerm");
		Set<String> combineTerms = getCombineTerms(uniqueGroup);
		return new UniqueIds(uniqueId, combineTerms);
	}

	private Set<String> getCombineTerms(DataGroup uniqueGroup) {
		List<DataRecordLink> combineTermsLinks = uniqueGroup
				.getChildrenOfTypeAndName(DataRecordLink.class, "combineTerm");
		Set<String> combineTerms = new LinkedHashSet<>();
		for (DataRecordLink combineTermLink : combineTermsLinks) {
			combineTerms.add(combineTermLink.getLinkedRecordId());
		}
		return combineTerms;
	}

	private DataConversionException handlerError(Exception e, String id) {
		String message = "Data to recordType converter failed for record: {0} due to: {1}";
		String errorMessage = MessageFormat.format(message, id, e.getMessage());
		return DataConversionException.withMessageAndException(errorMessage, e);
	}

}
