package se.uu.ub.cora.metadatastorage.spies;

import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandler;
import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandlerFactory;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class RecordTypeHandlerFactorySpy implements RecordTypeHandlerFactory {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public RecordTypeHandlerFactorySpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("factorUsingDataGroup", RecordTypeHandlerSpy::new);
		MRV.setDefaultReturnValuesSupplier("factorUsingRecordTypeId", RecordTypeHandlerSpy::new);
	}

	@Override
	public RecordTypeHandler factorUsingDataGroup(DataGroup dataGroup) {
		return (RecordTypeHandler) MCR.addCallAndReturnFromMRV("dataGroup", dataGroup);
	}

	@Override
	public RecordTypeHandler factorUsingRecordTypeId(String recordTypeId) {
		return (RecordTypeHandler) MCR.addCallAndReturnFromMRV("recordTypeId", recordTypeId);
	}

	@Override
	public RecordTypeHandler factorUsingDataRecordGroup(DataRecordGroup dataRecordGroup) {
		// TODO Auto-generated method stub
		return null;
	}
}
