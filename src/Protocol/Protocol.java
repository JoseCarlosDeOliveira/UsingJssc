package Protocol;

//STEP 01: Create e new Java Project called UsingJssc
//STEP 02: On it project add folder /lib then copy jssc.jar into it
//STEP 04: Create a class UsingJssc 
//STEP 05: import jssc.SerialPortList
//STEP 06: create a main into UsnigJssc Class as bellow
//STEP 07: Testing jssc.SeriaPortList as bellow
import jssc.SerialPortList;
//STEP 08: Testing jssc.SeriaPort import jssc.SerialPort + SerialPortException + SerialPortTimeoutException
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import Utilitaries.*;

public class Protocol {
	// Local Variables
	static int[] lineStatus = null;
	static boolean bCmdInProccess = false;
	static boolean bSTXReceived = false;
	static boolean bETXReceived = false;
	static boolean portOpenned = false;
	static String[] portas = null; // STEP 06b: SerialPortList.getPortNames()
	static SerialPort sp = null; // STEP 08b: Create a Variable example: static
									// SerialPort sp = null; for jssc.SerialPort
									// private static boolean bCmdInProccess =
									// false;
	static String serial = "";
	static byte[] bytesWrite = null;
	static byte[] bytesRead = null;
	private static byte[] byaCmdPacket;
	private static byte[] byaRspPacket;
	private static byte[] byaRsp = null;

	
	// STEP 06c - Testing jssc.SerialPortList
	public static int testingJsscGetPortList() throws InterruptedException {
		// //STEp 06d: Insert a function call to jssc to get port list.
		portas = SerialPortList.getPortNames();// Save Existing Serial Port List
		int i = 0;
		for (i = 0; i < portas.length; i++) {
			// STEP 06e - Show Found Serial Ports
			System.out.println("testingJsscGetPortList: i[" + i + "]/["
					+ portas.length + "] SerialPortName[" + portas[i] + "]");
		}
		return i;// Return a number of existing ports
	}

	// STEP 08b: Create a Method example: public int testingJsscSerialPort()
	public static int testingJsscSerialPort() throws InterruptedException {
		int i = 0;
		int rc = -230;
		for (i = 0; i < portas.length; i++) {
			// STEP 08c - Set sp with serial port name
			sp = new SerialPort(portas[i]); // Like Windows: DCB (Device Control
											// Block)
			String serialname = sp.getPortName();// Obtain SerialName
			try {
				// STEP 08d - openPort() Like Windows: CreateFile where
				// sp.PortName = Existing Port
				sp.openPort();
				System.out.println("testingJsscSerialPort: serialname["
						+ serialname + "] openPort() OK");
			} catch (SerialPortException e) {
				e.printStackTrace();
				System.err.println("testingJsscSerialPort: serialname["
						+ serialname + "] openPort Fail e[" + e.getMessage()
						+ "]");
				continue;
			}
			// STEP 08e - getLineStatus Like Windows: GetCommState
			try { // 0 - 3 Com valor numerico Sinais: DSR, CTS, DCD, RI
				sp.setDTR(true); // Ligar o DTR Like Windows: SetCommState
				sp.setRTS(true); // Ligar o RTS Like Windows: SetCommState
				lineStatus = sp.getLinesStatus(); // DSR, CTS, DCD, RI
				for (int l = 0; l < lineStatus.length; l++) {
					System.out.println("testingJsscSerialPort: serialname["
							+ serialname + "] LineStatus l[" + l + "]= ["
							+ lineStatus[l] + "]");
				}
				System.out.println("testingJsscSerialPort: serialname["
						+ serialname + "] isOpened?[" + sp.isOpened() + "]");
				System.out.println("testingJsscSerialPort: serialname["
						+ serialname + "]    isDSR?[" + sp.isDSR() + "]");
				System.out.println("testingJsscSerialPort: serialname["
						+ serialname + "]    isCTS?[" + sp.isCTS() + "]");
				System.out.println("testingJsscSerialPort: serialname["
						+ serialname + "]   isRING?[" + sp.isRING() + "]");
				System.out.println("testingJsscSerialPort: serialname["
						+ serialname + "]   isRLSD?[" + sp.isRLSD() + "]");
			} catch (SerialPortException e) {
				e.printStackTrace();
				System.err.println("testingJsscSerialPort: serialname["
						+ serialname + "] getLinesStatus Fail e["
						+ e.getMessage() + "]");
				try {
					sp.closePort();
				} catch (SerialPortException e1) {
					e1.printStackTrace();
					System.err.println("testingJsscSerialPort: serialname["
							+ serialname + "] closePort Fail e1["
							+ e1.getMessage() + "]");
				}
				continue;
			}
			// STEP 08f - setParams to Openned port like Windows: SetCommState
			try {
				//sp.setParams(2400, sp.DATABITS_7, sp.STOPBITS_2, sp.PARITY_EVEN);
				sp.setParams(115200, 8, 1, 1);// 2400,7,2,E
				System.out.println("testingJsscSerialPort: serialname["
						+ serialname + "] setParams(2400, 7, 2, 2) OK");
			} catch (SerialPortException e) {
				e.printStackTrace();
				System.err.println("testingJsscSerialPort: serialname["
						+ serialname + "] setParams(2400,7,2,E) Fail e["
						+ e.getMessage() + "]");
				try {
					sp.closePort();
				} catch (SerialPortException e1) {
					e1.printStackTrace();
					System.err.println("testingJsscSerialPort: serialname["
							+ serialname + "] closePort Fail e1["
							+ e1.getMessage() + "]");
				}
				continue;
			}
			if (serialname.equalsIgnoreCase("COM4") == false) {
				try {
					sp.closePort();
					System.out.println("testingJsscSerialPort: serialname["
							+ serialname + "] closePort OK Skipping...");
				} catch (SerialPortException e1) {
					e1.printStackTrace();
					System.err.println("testingJsscSerialPort: serialname["
							+ serialname + "] closePort Fail e1["
							+ e1.getMessage() + "]");
				}
				continue;
			}
			// Step 08g Communicating with Openned Device
			bytesWrite = new byte[1];
			bytesWrite[0] = (byte) 0xAA;// CMD
			rc = procCmd(1, bytesWrite, null, 1000);
			if (rc < 0) {
				System.err.println("testingJsscSerialPort: serialname["
						+ serialname + "] procCmd Fail rc[" + rc + "]");
				continue;
			}
			try {
				sp.closePort();
			} catch (SerialPortException e1) {
				e1.printStackTrace();
				System.err.println("testingJsscSerialPort: serialname["
						+ serialname + "] closePort Fail e1[" + e1.getMessage()
						+ "]");
			}
			continue;
		}
		return 0;
	}
    // ==========================================
	// Serial Device Communication Protocol Steps
    // ==========================================
	public static int procCmd(int iProtocol, byte[] byaCmd, byte[] byaCmdParam,
			int iTimeout) throws InterruptedException {
		int rc = -100;
		int iCmdRetry = 3;
		int iRspRetry = 3;

		while (bCmdInProccess == true) {
			Thread.sleep(100);
		}
		bCmdInProccess = true;
		bSTXReceived = false;

		rc = packCmd(iProtocol, byaCmd, byaCmdParam, iTimeout);
		if (rc < 0) {
			bCmdInProccess = false;
			System.err.println("procCmd:packCmd Fail rc[" + rc + "]");
			return rc;
		}
		while (--iCmdRetry > 0) {
			rc = sendCmdPkt(byaCmdPacket, iTimeout);
			if (rc < 0) { // Fail to send a CmdPacket
				System.err.println("procCmd:sendCmdPkt Fail rc[" + rc + "]");
				return rc;
			}
			rc = waitConfirmation(iTimeout);
			if (rc < 0) { // Fail to Receive ACK or NAK from device
				if (rc != -115) { // Not Received NAK
					bCmdInProccess = false;
					System.err.println("procCmd:waitConfirmation Fail rc[" + rc
							+ "]");
					return rc;
				}
			} else if (rc == 0) { // Received ACK
				break;
			} else if (rc == 1) { // Received STX
				bSTXReceived = true;
				break;
			}
		}
		// here: CmdPkt was sent and ACK was received
		while (--iRspRetry > 0) {
			rc = recvRspPkt(iProtocol, iTimeout);
			if (rc >= 0){ //LRC OK Match send ACK
				rc = sendConfirmation(iProtocol,true,1000);
				break;
			}
			else if (rc == -115){ //LRC Missmatch NAK
				rc = sendConfirmation(iProtocol,false,1000);
				System.err.println("procCmd:recvRspPkt NAK Received rc[" + rc + "] iRspRetry[" + rc + "]LRC ER Missmatch");
				
			} else {// Fail to send a CmdPacket
				System.err.println("procCmd:recvRspPkt Fail rc[" + rc + "]");
				bCmdInProccess = false;
				return rc;
			}
		}
		if (iRspRetry == 0){
			return rc;
		}
		rc = unpackRspPkt(iProtocol);
		if (rc < 0) {
			// Unpack Error
			System.err.println("procCmd:unpackRspPkt Fail rc[" + rc + "]");
		}
		System.out.println("procCmd: rc[" + rc + "] Success");
		bCmdInProccess = false;
		return rc;
	}

	public static int packCmd(int iProtocol, byte[] byaCmd, byte[] byaCmdParam,
			int iTimeout) throws InterruptedException {
		int rc = -101; // Invalid Protocol
		int i;
		byte byLRC = 0;
		if (iProtocol == 1) { // STX LEN <Cmd><Param> ETX LRC(Len > ETX)
			if (byaCmdParam != null) {
				byaCmdPacket = new byte[byaCmd.length + byaCmdParam.length + 4];// STX
																				// LEN
																				// <Cmd><Param>
																				// ETX
																				// LRC
				byaCmdPacket[0] = 0x02; // STX
				byaCmdPacket[1] = (byte) (byaCmd.length + byaCmdParam.length); // LEN
			} else {
				byaCmdPacket = new byte[byaCmd.length + 4];// STX LEN <Cmd> ETX
															// LRC
				byaCmdPacket[0] = 0x02; // STX
				byaCmdPacket[1] = (byte) (byaCmd.length); // LEN
			}
			for (i = 0; i < byaCmd.length; i++) {
				byaCmdPacket[i + 2] = byaCmd[i];// <Cmd>
			}
			if (byaCmdParam != null) {
				for (i = 1; i < byaCmdPacket.length; i++) {
					byaCmdPacket[i + byaCmd.length + 2] = byaCmdParam[i];// <Param>
				}
			}
			byaCmdPacket[byaCmdPacket.length - 2] = 0x03;// ETX
			// Compute LRC
			for (i = 1; i < byaCmdPacket.length - 1; i++) {
				byLRC ^= byaCmdPacket[i];
			}
			byaCmdPacket[byaCmdPacket.length - 1] = byLRC;
			rc = byaCmdPacket.length;
			System.out
					.println("packCmd: Protocol[" + iProtocol + "] rc[" + rc
							+ "] CmdPacket["
							+ Utilitaries.byteArrayToHexString(byaCmdPacket)
							+ "]Done!");

		} else if (iProtocol == 0) { // STX <Cmd><Param> ETX LRC(cmd > ETX)
			byaCmdPacket = new byte[byaCmd.length + byaCmdParam.length + 3];// STX
																			// LEN
																			// <Cmd><Param>
																			// ETX
																			// LRC
			byaCmdPacket[0] = 0x02; // STX
			for (i = 0; i < byaCmd.length; i++) {
				byaCmdPacket[i + 1] = byaCmd[i]; // <Cmd>
			}
			for (i = 0; i < byaCmdParam.length; i++) {
				byaCmdPacket[i + byaCmd.length + 1] = byaCmdParam[i];// <Param>
			}
			// Compute LRC
			for (i = 1; i < byaCmdParam.length - 2; i++) {
				byLRC ^= byaCmdPacket[i];
			}
			byaCmdPacket[byaCmdPacket.length - 1] = byLRC;
			rc = byaCmdPacket.length;
			System.out
					.println("packCmd: Protocol[" + iProtocol + "] rc[" + rc
							+ "] CmdPacket["
							+ Utilitaries.byteArrayToHexString(byaCmdPacket)
							+ "]Done!");
		} else {
			// Protocol not 0 not 1
			System.err.println("packCmd: Protocol[" + iProtocol + "] rc[" + rc
					+ "] Fail Expected 0 or 1");
		}
		return rc;
	}

	public static int sendCmdPkt(byte[] byaCmdPkt, int iTimeout)
			throws InterruptedException {
		int rc = -102;
		boolean bWrote = false;
		try {
			bWrote = sp.writeBytes(byaCmdPkt);
			if (bWrote == true) {
				rc = byaCmdPkt.length;
				System.out.println("sendCmdPkt: rc[" + rc
						+ "] writeBytes success");
			} else {
				System.err.println("sendCmdPkt: rc[" + rc
						+ "] writeBytes Fail Fail");
			}
		} catch (SerialPortException e) {
			e.printStackTrace();
			System.err.println("sendCmdPkt: rc[" + rc + "] e[" + e.getMessage()
					+ "] Fail");
		}
		return rc;
	}

	public static int waitConfirmation(int iTimeout)
			throws InterruptedException {
		int rc = -103; // SerialPortException
		int iDelay = 0;
		byte[] byaRead = new byte[3];
		while (true) {
			// Check if there are bytes into input buffer
			try {
				if (sp.getInputBufferBytesCount() == 0) {
					if(++iDelay == 10){
						rc = -114; // No Bytes REceived
						System.err.println("waitConfirmation: rc[" + rc
								+ "] sp.getInputBufferBytesCount() = 0 No Bytes Received");
						break;
					}
					Thread.sleep(100);
				}
			} catch (SerialPortException e) {
				e.printStackTrace();
				System.err.println("waitConfirmation: rc[" + rc
						+ "] sp.getInputBufferBytesCount Fail e["
						+ e.getMessage() + "]");
				break;
			}
			//There are at least one byte at input buffer
			try {
				byaRead = sp.readBytes(1, iTimeout);
			} catch (SerialPortException e) {
				e.printStackTrace();
				System.err.println("waitConfirmation: rc[" + rc + "] Fail e["
						+ e.getMessage() + "]");
				break;
			} catch (SerialPortTimeoutException e1) {
				rc = -113; // SerialPortTimeoutException
				e1.printStackTrace();
				System.err.println("waitConfirmation: rc[" + rc + "] Fail e1["
						+ e1.getMessage() + "] Timeout");
				break;
			}
			if (byaRead[0] == 0x06) { // ACK Received
				rc = 0; // ACK Received
				System.out.println("waitConfirmation: rc[" + rc
						+ "] ACK Received[" + byaRead[0] + "]");
				break;
			} else if (byaRead[0] == 0x02) {// STX Received
				rc = 1; // STX Received
				System.out.println("waitConfirmation: rc[" + rc
						+ "] STX Received[" + byaRead[0] + "]");
				break;
			} else if (byaRead[0] == 0x15) { // NAK Received
				rc = -115; // NAK Received
				System.out.println("waitConfirmation: rc[" + rc
						+ "] NAK Received[" + byaRead[0] + "]");
				break;
			} else if (byaRead[0] == 0x00) { // NULL Received
				rc = -116; // NULL Received
				System.out.println("waitConfirmation: rc[" + rc
						+ "] NUL Received[" + byaRead[0] + "]");
				break;
			} else if (byaRead[0] < 0) { // FAil
				rc = byaRead[0];
				System.err.println("waitConfirmation: rc[" + rc
						+ "] ERR Received[" + byaRead[0]
						+ "] Expected STX,ACK or NAK");
				break;
			} else {
				System.out.println("waitConfirmation: rc[" + rc
						+ "] ??? Received[" + byaRead[0]
						+ "] Expected STX,ACK or NAK Continue");
			}
		}
		return rc;
	}

	public static int recvRspPkt(int iProtocol, int iTimeout)
			throws InterruptedException {
		int rc = -104;
		int i = 0;
		byte byLRCRcvd = 0;
		byte byLRCCalc = 0;
		byte[] byaRead = new byte[1];
		byaRspPacket = new byte[1];
		if (bSTXReceived == true) {
			byaRspPacket[i++] = 0x02; // Save STX at begin of RspPkt
		}
		int iDelay = 0;
		while (true) {
			// Check if there are at least one byte at input buffer
			try {
				if (sp.getInputBufferBytesCount() == 0) {
					iDelay++;
					if (iDelay >= 10) {
						rc = -113; // No bytes to read
						System.err.println("recvRspPkt: rc[" + rc
								+ "] sp.getInputBufferBytesCount()=0 iDelay["
								+ iDelay + "]No bytes to read");
						return rc;
					}
					Thread.sleep(100);
					continue;
				}
				rc = byaRspPacket.length; //Size of Packet
			} catch (SerialPortException e) {
				rc = -114;
				e.printStackTrace();
				System.err.println("recvRspPkt: rc[" + rc
						+ "] sp.getInputBufferBytesCount() Fail e["
						+ e.getMessage() + "]");
				break;
			}
			// Read One byte from device
			try {
				byaRead = sp.readBytes(1, iTimeout);
			} catch (SerialPortException e) {
				rc = -115;
				e.printStackTrace();
				System.err.println("recvRspPkt: rc[" + rc + "] Fail e["
						+ e.getMessage() + "]");
				break;
			} catch (SerialPortTimeoutException e1) {
				rc = -113; // SerialPortTimeoutException
				e1.printStackTrace();
				System.err.println("recvRspPkt: rc[" + rc + "] Fail e1["
						+ e1.getMessage() + "] Timeout");
				break;
			}
			// One byte was read
			if ((bSTXReceived == false) && (byaRead[0] == 0x02)) {
				// STX received
				System.out.println("recvRspPkt: rc[" + rc + "] STX Received["
						+ byaRead[0] + "]");
				bSTXReceived = true;
				byaRspPacket[i++] = 0x02;
			} else if ((byaRead[0] == 0x03) && (bETXReceived == false)) { // ETX
																			// Received
				System.out.println("recvRspPkt: rc[" + rc + "] ETX Received["
						+ byaRead[0] + "] i[" + i + "]");
				bETXReceived = true;
				if (byaRspPacket.length < i) {
					byaRspPacket[i++] = 0x03;
				} else {
					byte[] byaRead2 = new byte[byaRspPacket.length + 1];
					System.arraycopy(byaRspPacket, 0, byaRead2, 0,
							byaRspPacket.length);
					byaRead2[byaRspPacket.length] = 0x03;
					byaRspPacket = new byte[byaRead2.length];
					System.arraycopy(byaRead2, 0, byaRspPacket, 0,
							byaRead2.length);
					i++;
				}
			} else if (bETXReceived == true) { // ETX was Received reading LRC
				byLRCRcvd = byaRead[0];
				System.out.println("recvRspPkt: rc[" + rc + "] LRC Received["
						+ byLRCRcvd + "] i[" + i + "]");
				if (byaRspPacket.length < i) {
					byaRspPacket[i++] = byLRCRcvd;
				} else {
					byte[] byaRead2 = new byte[byaRspPacket.length + 1];
					System.arraycopy(byaRspPacket, 0, byaRead2, 0,
							byaRspPacket.length);
					byaRead2[byaRspPacket.length] = byLRCRcvd;
					byaRspPacket = new byte[byaRead2.length];
					System.arraycopy(byaRead2, 0, byaRspPacket, 0,
							byaRead2.length);
					i++;
				}
				// Computing LRC now
				byLRCCalc = 0;
				// Compute Response LRC
				for (i = 1; i < byaRspPacket.length - 1; i++) {
					byLRCCalc ^= byaRspPacket[i];
				}
				i++;
				if (byLRCCalc == byLRCRcvd) {
					// LRC OK Match
					rc = byaRspPacket.length; //Size of Packet
					System.out.println("recvRspPkt: rc[" + rc + "] LRCrcvd["
							+ byLRCRcvd + "] LRCcalc["
							+ byLRCCalc + "] OK Match     i[" + i + "] RspPacket[" +  Utilitaries.byteArrayToHexString(byaRspPacket)+ "] RsppacketLen[" +byaRspPacket.length +"]");
				} else {
					// LRC ER Missmatch
					rc = -115; 
					System.err.println("recvRspPkt: rc[" + rc + "] LRCrcvd["
							+ byLRCRcvd + "] LRCcalc["
							+ byLRCCalc + "] ER MissMatch i[" + i + "] RspPacket[" +  Utilitaries.byteArrayToHexString(byaRspPacket)+ "] RsppacketLen[" +byaRspPacket.length +"]");
				}
				break;
			} else {
				//Store new byte into byaRspPacket
				byte[] byaRead2 = new byte[byaRspPacket.length + 1];
				System.arraycopy(byaRspPacket, 0, byaRead2, 0,
						byaRspPacket.length);
				byaRead2[byaRspPacket.length] = byaRead[0];
				byaRspPacket = new byte[byaRead2.length];
				System.arraycopy(byaRead2, 0, byaRspPacket, 0,
						byaRead2.length);
				i++;
			}
		}
		if(rc < 0){
			System.err.println("recvRspPkt: rc[" + rc + "] Fail");
		} else {
			System.out.println("recvRspPkt: rc[" + rc + "] Len["+byaRspPacket.length+"] Successfull Complete!");
		}
		return rc;
	}

	public static int sendConfirmation(int iProtocol, boolean bAck, int iTimeout)
			throws InterruptedException {
		int rc = -105;
		if((iProtocol == 1) &&(bAck == true)){
			rc = 0; //Not Necessary to send ACK
			System.out.println("sendConfirmation: rc[" + rc + "] Protocol[" + iProtocol + "] Not Necessary!");
			return rc;
		}
		System.err.println("sendConfirmation: rc[" + rc + "] Fail");
		return rc;
	}

	public static int unpackRspPkt(int iProtocol) throws InterruptedException {
		int rc = -106;
		if(byaRspPacket == null){
			System.err.println("unpackRspPkt: rc[" + rc + "] Fail byaRspPacket=null");
			return rc;
		} else if (byaRspPacket.length == 0){
			System.err.println("unpackRspPkt: rc[" + rc + "] Fail byaRspPacket length = 0");
			return rc;
		} else if (byaRspPacket[0] != 0x02){
			System.err.println("unpackRspPkt: rc[" + rc + "] Fail byaRspPacket do not start with STX[" + byaRspPacket[0] + "]");
			return rc;
		} else if (byaRspPacket[byaRspPacket.length-2] != 0x03){
			System.err.println("unpackRspPkt: rc[" + rc + "] Fail byaRspPacket do not ents  with ETX[" + byaRsp[byaRspPacket.length-2] + "] + LRC");
			return rc;
		}
		//Packet starts with STX and terminate with ETX LRC
		if (iProtocol == 1){
			//Store new byte into byaRspPacket
			byaRsp = new byte[byaRspPacket.length-5]; //STX LEN CMD <Data> ETX LRC
			System.arraycopy(byaRspPacket, 3, byaRsp, 0,
					byaRspPacket.length-5);
			rc = byaRsp.length;
			System.out.println("unpackRspPkt: rc[" + rc + "] Protocol[" + iProtocol + "] Rsp[" + Utilitaries.byteArrayToHexString(byaRsp) + "]Done");
		} else {
			System.err.println("unpackRspPkt: rc[" + rc + "] Protocol[" + iProtocol + "] Fail");
		}
		return rc;
	}

	public static byte[] getRsp() {
		return byaRsp;
	}

	public static void setRsp(byte[] byaRsp) {
		Protocol.byaRsp = byaRsp;
	}


}
