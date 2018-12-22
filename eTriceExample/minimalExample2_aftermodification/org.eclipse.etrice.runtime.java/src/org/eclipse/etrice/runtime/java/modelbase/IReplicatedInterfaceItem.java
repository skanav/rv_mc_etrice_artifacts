/*******************************************************************************
 * Copyright (c) 2012 protos software gmbh (http://www.protos.de).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * CONTRIBUTORS:
 * 		Henrik Rentz-Reichert (initial contribution)
 * 
 *******************************************************************************/

package org.eclipse.etrice.runtime.java.modelbase;

/**
 * @author Henrik Rentz-Reichert
 *
 */
public interface IReplicatedInterfaceItem extends IInterfaceItem {

	 InterfaceItemBase createSubInterfaceItem();
	 void removeItem(InterfaceItemBase item);
}
