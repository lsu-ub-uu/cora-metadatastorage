package se.uu.ub.cora.userstorage.spies;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.spider.recordtype.RecordTypeHandler;
import se.uu.ub.cora.spider.recordtype.internal.RecordTypeHandlerFactory;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class RecordTypeHandlerFactorySpy implements RecordTypeHandlerFactory {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public RecordTypeHandlerFactorySpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("factorUsingDataGroup", RecordTypeHandlerSpy::new);
	}

	@Override
	public RecordTypeHandler factorUsingDataGroup(DataGroup dataGroup) {
		return (RecordTypeHandler) MCR.addCallAndReturnFromMRV("dataGroup", dataGroup);
	}

}
