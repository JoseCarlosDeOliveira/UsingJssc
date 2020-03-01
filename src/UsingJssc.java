//STEP 01: Create e new Java Project To communicate withs serial devices Example: UsingJssc
//STEP 02: On it project add folder /lib then copy jssc.jar into it
//STEP 03: Right Click At Project > Properties > JavaBuild Path > Library > ADD JARs  lib\jssc.jar
//STEP 04:  Create Class example: UsingJssc
//STEP 05a: add a Method: public static void main(String[] args) throws InterruptedException {}
//STEP 05b: To make it easy you can create a new Class for Protocoll example: Protocol
//STEP 05c: If you created a new class open it and to the following steps
//STEP 06a: import jssc.SerialPortList
//STEP 06b: Create a Variable Example: static String[] portas = null;
//STEP 06c: Create a Method example: public static int testingJsscGetPortList() throws InterruptedException
//STEp 06d: Insert a function call to jssc to get port list.
//STEP 07a: Running Java Application: Right Click at Project > RunAs > JavaApplication
//   import jssc.SerialPortList;
//STEP 08a: Testing jssc.SeriaPort import jssc.SerialPort + SerialPortException + SerialPortTimeoutException
//   import jssc.SerialPort;
//   import jssc.SerialPortException;
//   import jssc.SerialPortTimeoutException;
//STEP 08b: Create a Variable example: static SerialPort sp = null; for jssc.SerialPort
//STEP 08b: Create a Method example: public int testingJsscSerialPort()
//Utilitaries for testing
//   import Utilitaries.*;
import Protocol.*;

public class UsingJssc {
	// STEP 07a Create a Java Vars
	public static void main(String[] args) throws InterruptedException {
		int rc = 0;
		rc = Protocol.testingJsscGetPortList();
		System.out.println(" rc[" + rc + "] testingJsscGetPortList");
		rc = Protocol.testingJsscSerialPort();
		System.out.println(" rc[" + rc + "] testingJsscSerialPort");
		return;
	}
}
