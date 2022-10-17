package se.uu.ub.cora.userstorage.spies;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import se.uu.ub.cora.bookkeeper.metadata.Constraint;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.spider.recordtype.RecordTypeHandler;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class RecordTypeHandlerSpy implements RecordTypeHandler {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public RecordTypeHandlerSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getListOfImplementingRecordTypeIds",
				(Supplier<List<String>>) () -> Collections.emptyList());
	}

	@Override
	public boolean isAbstract() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasParent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildOfBinary() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean representsTheRecordTypeDefiningSearches() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean representsTheRecordTypeDefiningRecordTypes() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasLinkedSearch() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSearchId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean shouldAutoGenerateId() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getNewMetadataId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMetadataId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataGroup getMetadataGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getCombinedIdsUsingRecordId(String recordId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPublicForRead() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRecordPartReadConstraint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRecordPartWriteConstraint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRecordPartCreateConstraint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Constraint> getRecordPartReadConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Constraint> getRecordPartWriteConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Constraint> getRecordPartCreateWriteConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RecordTypeHandler> getImplementingRecordTypeHandlers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getListOfImplementingRecordTypeIds() {
		return (List<String>) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public List<String> getListOfRecordTypeIdsToReadFromStorage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRecordTypeId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean storeInArchive() {
		// TODO Auto-generated method stub
		return false;
	}

}
