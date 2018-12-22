package room.basic.service.tcp;

import org.eclipse.etrice.runtime.java.messaging.*;
import org.eclipse.etrice.runtime.java.modelbase.*;
import org.eclipse.etrice.runtime.java.debugging.*;

import static org.eclipse.etrice.runtime.java.etunit.EtUnit.*;

import room.basic.service.tcp.PTcpControl.*;
import room.basic.service.tcp.PTcpPayload.*;

/*--------------------- begin user code ---------------------*/
import java.net.Socket;
import java.io.*;
	
class ClientRxThread extends Thread{	
	private Socket sock;
	PTcpPayloadPort port;
	public ClientRxThread (PTcpPayloadPort port, Socket sock){
		this.sock = sock;
		this.port = port;
	}
	public void run(){
		try{
			InputStream in = sock.getInputStream();
			DTcpPayload d = new DTcpPayload();
			d.setConnectionId(0);
			int c;
			while ((c=in.read(d.getData()))!=-1){
				d.setLength(c);
				port.receive(d);
			}
		}
		catch (IOException e){
			System.err.println("ClientRx: " + e.toString());
		}
	
	}
}

/*--------------------- end user code ---------------------*/


public class ATcpClient extends ActorClassBase {

	/*--------------------- begin user code ---------------------*/
	Socket socket;
	InputStream in;
	OutputStream out;
	/*--------------------- end user code ---------------------*/

	//--------------------- ports
	protected PTcpControlPort ControlPort = null;
	protected PTcpPayloadPort PayloadPort = null;

	//--------------------- saps

	//--------------------- services

	//--------------------- optional actors

	//--------------------- interface item IDs
	public static final int IFITEM_ControlPort = 1;
	public static final int IFITEM_PayloadPort = 2;

	/*--------------------- attributes ---------------------*/
	public  int lastError;

	/*--------------------- operations ---------------------*/
	public  void stopUser() {
		try{
		if(socket != null){
		socket.close();
		}
		}catch(IOException e){
		System.err.println(e.toString());}
	}


	//--------------------- construction
	public ATcpClient(IRTObject parent, String name) {
		super(parent, name);
		setClassName("ATcpClient");

		// initialize attributes
		this.setLastError(0);

		// own ports
		ControlPort = new PTcpControlPort(this, "ControlPort", IFITEM_ControlPort);
		PayloadPort = new PTcpPayloadPort(this, "PayloadPort", IFITEM_PayloadPort);

		// own saps

		// own service implementations

		// sub actors

		// wiring


		/* user defined constructor body */

	}

	/* --------------------- attribute setters and getters */
	public void setLastError(int lastError) {
		 this.lastError = lastError;
	}
	public int getLastError() {
		return this.lastError;
	}


	//--------------------- port getters
	public PTcpControlPort getControlPort (){
		return this.ControlPort;
	}
	public PTcpPayloadPort getPayloadPort (){
		return this.PayloadPort;
	}

	//--------------------- lifecycle functions
	public void stop(){
		super.stop();
	}

	public void destroy(){
		/* user defined destructor body */
		DebuggingService.getInstance().addMessageActorDestroy(this);
		super.destroy();
	}

	/* state IDs */
	public static final int STATE_closed = 2;
	public static final int STATE_opened = 3;
	public static final int STATE_error = 4;
	public static final int STATE_MAX = 5;
	
	/* transition chains */
	public static final int CHAIN_TRANS_INITIAL_TO__closed = 1;
	public static final int CHAIN_TRANS_tr0_FROM_closed_TO_cp0_BY_openControlPort = 2;
	public static final int CHAIN_TRANS_tr1_FROM_opened_TO_closed_BY_closeControlPort = 3;
	public static final int CHAIN_TRANS_tr3_FROM_opened_TO_opened_BY_sendPayloadPort_tr3 = 4;
	
	/* triggers */
	public static final int POLLING = 0;
	public static final int TRIG_ControlPort__open = IFITEM_ControlPort + EVT_SHIFT*PTcpControl.IN_open;
	public static final int TRIG_ControlPort__close = IFITEM_ControlPort + EVT_SHIFT*PTcpControl.IN_close;
	public static final int TRIG_PayloadPort__send = IFITEM_PayloadPort + EVT_SHIFT*PTcpPayload.IN_send;
	
	// state names
	protected static final String stateStrings[] = {
		"<no state>",
		"<top>",
		"closed",
		"opened",
		"error"
	};
	
	// history
	protected int history[] = {NO_STATE, NO_STATE, NO_STATE, NO_STATE, NO_STATE};
	
	private void setState(int new_state) {
		DebuggingService.getInstance().addActorState(this,stateStrings[new_state]);
		this.state = new_state;
	}
	
	/* Entry and Exit Codes */
	
	/* Action Codes */
	protected void action_TRANS_INITIAL_TO__closed() {
	    System.out.println("Client Init !");
	}
	protected void action_TRANS_tr0_FROM_closed_TO_cp0_BY_openControlPort(InterfaceItemBase ifitem, DTcpControl transitionData) {
	    lastError=0;
	    try{
	    	socket = new Socket(transitionData.IPAddr,transitionData.TcpPort);
	    	(new ClientRxThread(PayloadPort, socket)).start();
	    	out = socket.getOutputStream();
	    }
	    catch(IOException e){
	    	System.err.println(e.toString());
	    	lastError=1;
	    }
	}
	protected void action_TRANS_tr1_FROM_opened_TO_closed_BY_closeControlPort(InterfaceItemBase ifitem) {
	    try{
	    	if (socket!=null){
	    		socket.close();
	    	}
	    }
	    catch (IOException e){
	    	System.err.println(e.toString());
	    }
	}
	protected void action_TRANS_tr2_FROM_cp0_TO_opened(InterfaceItemBase ifitem, DTcpControl transitionData) {
	    ControlPort.established();
	}
	protected void action_TRANS_socketError_FROM_cp0_TO_error_COND_socketError(InterfaceItemBase ifitem, DTcpControl transitionData) {
	    ControlPort.error();
	    try{
	    	socket.close();
	    }
	    catch(IOException e){
	    	System.err.println(e.toString());
	    }
	}
	protected void action_TRANS_tr3_FROM_opened_TO_opened_BY_sendPayloadPort_tr3(InterfaceItemBase ifitem, DTcpPayload transitionData) {
	    try{
	    	out.write(transitionData.getData(),0,transitionData.length);
	    }
	    catch(IOException e){
	    	System.err.println(e.toString());
	    }
	}
	
	/* State Switch Methods */
	/**
	 * calls exit codes while exiting from the current state to one of its
	 * parent states while remembering the history
	 * @param current__et - the current state
	 * @param to - the final parent state
	 */
	private void exitTo(int current__et, int to) {
		while (current__et!=to) {
			switch (current__et) {
				case STATE_closed:
					this.history[STATE_TOP] = STATE_closed;
					current__et = STATE_TOP;
					break;
				case STATE_error:
					this.history[STATE_TOP] = STATE_error;
					current__et = STATE_TOP;
					break;
				case STATE_opened:
					this.history[STATE_TOP] = STATE_opened;
					current__et = STATE_TOP;
					break;
				default:
					/* should not occur */
					break;
			}
		}
	}
	
	/**
	 * calls action, entry and exit codes along a transition chain. The generic data are cast to typed data
	 * matching the trigger of this chain. The ID of the final state is returned
	 * @param chain__et - the chain ID
	 * @param generic_data__et - the generic data pointer
	 * @return the +/- ID of the final state either with a positive sign, that indicates to execute the state's entry code, or a negative sign vice versa
	 */
	private int executeTransitionChain(int chain__et, InterfaceItemBase ifitem, Object generic_data__et) {
		switch (chain__et) {
			case ATcpClient.CHAIN_TRANS_INITIAL_TO__closed:
			{
				action_TRANS_INITIAL_TO__closed();
				return STATE_closed;
			}
			case ATcpClient.CHAIN_TRANS_tr0_FROM_closed_TO_cp0_BY_openControlPort:
			{
				DTcpControl transitionData = (DTcpControl) generic_data__et;
				action_TRANS_tr0_FROM_closed_TO_cp0_BY_openControlPort(ifitem, transitionData);
				if (lastError!=0) {
				action_TRANS_socketError_FROM_cp0_TO_error_COND_socketError(ifitem, transitionData);
				return STATE_error;}
				else {
				action_TRANS_tr2_FROM_cp0_TO_opened(ifitem, transitionData);
				return STATE_opened;}
			}
			case ATcpClient.CHAIN_TRANS_tr1_FROM_opened_TO_closed_BY_closeControlPort:
			{
				action_TRANS_tr1_FROM_opened_TO_closed_BY_closeControlPort(ifitem);
				return STATE_closed;
			}
			case ATcpClient.CHAIN_TRANS_tr3_FROM_opened_TO_opened_BY_sendPayloadPort_tr3:
			{
				DTcpPayload transitionData = (DTcpPayload) generic_data__et;
				action_TRANS_tr3_FROM_opened_TO_opened_BY_sendPayloadPort_tr3(ifitem, transitionData);
				return STATE_opened;
			}
				default:
					/* should not occur */
					break;
		}
		return NO_STATE;
	}
	
	/**
	 * calls entry codes while entering a state's history. The ID of the final leaf state is returned
	 * @param state__et - the state which is entered
	 * @return - the ID of the final leaf state
	 */
	private int enterHistory(int state__et) {
		if (state__et >= STATE_MAX) {
			state__et =  (state__et - STATE_MAX);
		}
		while (true) {
			switch (state__et) {
				case STATE_closed:
					/* in leaf state: return state id */
					return STATE_closed;
				case STATE_error:
					/* in leaf state: return state id */
					return STATE_error;
				case STATE_opened:
					/* in leaf state: return state id */
					return STATE_opened;
				case STATE_TOP:
					state__et = this.history[STATE_TOP];
					break;
				default:
					/* should not occur */
					break;
			}
		}
		/* return NO_STATE; // required by CDT but detected as unreachable by JDT because of while (true) */
	}
	
	public void executeInitTransition() {
		int chain__et = ATcpClient.CHAIN_TRANS_INITIAL_TO__closed;
		int next__et = executeTransitionChain(chain__et, null, null);
		next__et = enterHistory(next__et);
		setState(next__et);
	}
	
	/* receiveEvent contains the main implementation of the FSM */
	public void receiveEventInternal(InterfaceItemBase ifitem, int localId, int evt, Object generic_data__et) {
		int trigger__et = localId + EVT_SHIFT*evt;
		int chain__et = NOT_CAUGHT;
		int catching_state__et = NO_STATE;
	
		if (!handleSystemEvent(ifitem, evt, generic_data__et)) {
			switch (getState()) {
				case STATE_closed:
					switch(trigger__et) {
						case TRIG_ControlPort__open:
							{
								chain__et = ATcpClient.CHAIN_TRANS_tr0_FROM_closed_TO_cp0_BY_openControlPort;
								catching_state__et = STATE_TOP;
							}
						break;
						default:
							/* should not occur */
							break;
					}
					break;
				case STATE_error:
					break;
				case STATE_opened:
					switch(trigger__et) {
						case TRIG_ControlPort__close:
							{
								chain__et = ATcpClient.CHAIN_TRANS_tr1_FROM_opened_TO_closed_BY_closeControlPort;
								catching_state__et = STATE_TOP;
							}
						break;
						case TRIG_PayloadPort__send:
							{
								chain__et = ATcpClient.CHAIN_TRANS_tr3_FROM_opened_TO_opened_BY_sendPayloadPort_tr3;
								catching_state__et = STATE_TOP;
							}
						break;
						default:
							/* should not occur */
							break;
					}
					break;
				default:
					/* should not occur */
					break;
			}
		}
		if (chain__et != NOT_CAUGHT) {
			exitTo(getState(), catching_state__et);
			{
				int next__et = executeTransitionChain(chain__et, ifitem, generic_data__et);
				next__et = enterHistory(next__et);
				setState(next__et);
			}
		}
	}
	public void receiveEvent(InterfaceItemBase ifitem, int evt, Object generic_data__et) {
		int localId = (ifitem==null)? 0 : ifitem.getLocalId();
		receiveEventInternal(ifitem, localId, evt, generic_data__et);
	}

};
