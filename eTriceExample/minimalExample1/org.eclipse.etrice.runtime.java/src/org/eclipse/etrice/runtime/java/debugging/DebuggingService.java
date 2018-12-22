/*******************************************************************************
 * Copyright (c) 2010 protos software gmbh (http://www.protos.de).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * CONTRIBUTORS:
 * 		Thomas Schuetz and Henrik Rentz-Reichert (initial contribution)
 * 
 *******************************************************************************/

package org.eclipse.etrice.runtime.java.debugging;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.etrice.runtime.java.messaging.Address;
import org.eclipse.etrice.runtime.java.messaging.IRTObject;
import org.eclipse.etrice.runtime.java.modelbase.ActorClassBase;
import org.eclipse.etrice.runtime.java.modelbase.OptionalActorInterfaceBase;
import org.eclipse.etrice.runtime.java.modelbase.PortBase;
import org.eclipse.etrice.runtime.java.modelbase.SubSystemClassBase;


/**
 * @author Thomas Schuetz
 *
 * DebuggingService for logging and tracing
 * provides an MSG Generator for async and sync messages 
 * 
 */
public class DebuggingService {
	
	private static DebuggingService instance = null;

	private MSCLogger asyncLogger = new MSCLogger(); 
	private MSCLogger syncLogger = new MSCLogger(); 

	private Map<Address, PortBase> portInstances = new HashMap<Address, PortBase>();

	/**
	 * private constructor (singleton pattern)
	 */
	private DebuggingService(){}
	
	/**
	 * Singleton access.
	 * 
	 * @return the one and only instance
	 */
	public static DebuggingService getInstance(){
		if (instance == null) {
			instance = new DebuggingService();
		}
		return instance;
	}
	
	public void addMessageAsyncOut(Address source, Address target, String msg){
		PortBase srcPort = portInstances.get(source);
		PortBase tgtPort = portInstances.get(target);
		if (srcPort!=null && tgtPort!=null)
			asyncLogger.addMessageAsyncOut(srcPort.getActor().getInstancePath(), tgtPort.getActor().getInstancePath(), msg);
	}
	public void addMessageAsyncIn(Address source, Address target, String msg){
		PortBase srcPort = portInstances.get(source);
		PortBase tgtPort = portInstances.get(target);
		if (source!=null && target!=null)
			asyncLogger.addMessageAsyncIn(srcPort.getActor().getInstancePath(), tgtPort.getActor().getInstancePath(), msg);
		
		// TODO: this was only a quickfix to trace unconnected ports
//		if (source==null)
//			asyncLogger.addMessageAsyncIn("~", portInstances.get(target).getActor().getInstancePath(), msg);
//		else
//			asyncLogger.addMessageAsyncIn(portInstances.get(source).getActor().getInstancePath(), portInstances.get(target).getActor().getInstancePath(), msg);
			
	}
	public void addMessageSyncCall(Address source, Address target, String msg){
		PortBase srcPort = portInstances.get(source);
		PortBase tgtPort = portInstances.get(target);
		if (srcPort!=null && tgtPort!=null)
			asyncLogger.addMessageSyncCall(srcPort.getActor().getInstancePath(), tgtPort.getActor().getInstancePath(), msg);
	}
	public void addMessageSyncReturn(Address source, Address target, String msg){
		PortBase srcPort = portInstances.get(source);
		PortBase tgtPort = portInstances.get(target);
		if (srcPort!=null && tgtPort!=null)
			asyncLogger.addMessageSyncReturn(srcPort.getActor().getInstancePath(), tgtPort.getActor().getInstancePath(), msg);
	}
	
	public void addActorState(ActorClassBase actor, String state){
		asyncLogger.addActorState(actor.getInstancePath(), state);
	}
	
	public void addMessageActorCreate(SubSystemClassBase parent, String refName) {
		asyncLogger.addMessageActorCreate(parent.getInstancePath(), parent.getInstancePath()+IRTObject.PATH_DELIM+refName);
	}
	
	public void addMessageActorCreate(ActorClassBase parent, String refName) {
		asyncLogger.addMessageActorCreate(parent.getInstancePath(), parent.getInstancePath()+IRTObject.PATH_DELIM+refName);
	}
	
	public void addMessageActorDestroy(ActorClassBase inst) {
		if (!(inst.getParent() instanceof OptionalActorInterfaceBase))
			asyncLogger.addMessageActorDestroy(inst.getParent().getInstancePath(), inst.getInstancePath());
	}
	
	public void addMessageActorCreate(OptionalActorInterfaceBase oai, String actorClass, String name) {
		asyncLogger.addNote(oai.getParent().getInstancePath(), "dynamically creating actor class "+actorClass);
		asyncLogger.addMessageActorCreate(oai.getParent().getInstancePath(), oai.getInstancePath()+IRTObject.PATH_DELIM+name);
	}
	
	public void addMessageActorDestroy(OptionalActorInterfaceBase oai, String name) {
		asyncLogger.addMessageActorDestroy(oai.getParent().getInstancePath(), oai.getInstancePath()+IRTObject.PATH_DELIM+name);
	}
	
	public void addVisibleComment(String comment) {
		asyncLogger.addVisibleComment(comment);
	}
	
	public void addPortInstance(PortBase port){
		portInstances.put(port.getAddress(), port);
	}
	
	public void removePortInstance(PortBase port){
		portInstances.remove(port.getAddress());
	}
	
	public MSCLogger getSyncLogger() {
		return syncLogger;
	}
	
	public MSCLogger getAsyncLogger() {
		return asyncLogger;
	}
	
}
