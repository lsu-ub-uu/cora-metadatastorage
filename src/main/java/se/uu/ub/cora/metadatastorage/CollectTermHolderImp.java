/*
 * Copyright 2017, 2024 Uppsala University Library
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

import java.util.HashMap;
import java.util.Map;

import se.uu.ub.cora.bookkeeper.metadata.CollectTerm;
import se.uu.ub.cora.bookkeeper.metadata.CollectTermHolder;

public final class CollectTermHolderImp implements CollectTermHolder {
	private Map<String, CollectTerm> collectTerm = new HashMap<>();

	public void addCollectTerm(CollectTerm storageTerm) {
		collectTerm.put(storageTerm.id, storageTerm);
	}

	@Override
	public CollectTerm getCollectTermById(String collectTermId) {
		return collectTerm.get(collectTermId);
	}
}
