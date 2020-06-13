package bg.sofia.uni.fmi.mjt.auth.FileEditors;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;

public class AuditLog {
	private static int id = 0;
	private static final String ERROR_MESSAGE_WRITER = "Problem with the File Writer";
	
	public void writeFailedLogin(SocketChannel socket, String name) {
		try (FileWriter writer = new FileWriter("AuditLog.txt", true);) {
			writer.write("time: [" + LocalDateTime.now() + "] - " + "operation type: failed login - " + "username: "
					+ name + " - IP:" + socket.getLocalAddress() + "\n");
		} catch (IOException e) {
			System.out.println(ERROR_MESSAGE_WRITER);
			e.printStackTrace();
		}
	}

	public void writeConfigChageStart(SocketChannel socketDoer, String doer, String affected, String command) {
		try (FileWriter writer = new FileWriter("AuditLog.txt", true);) {
			id++;
			writer.write("time: [" + LocalDateTime.now() + "] - " + "ID:" + id
					+ " - operation type: configuration change - " + "doer: " + doer + " - IP:"
					+ socketDoer.getLocalAddress() + " - affected: " + affected + " - " + command + "\n");
		} catch (IOException e) {
			System.out.println(ERROR_MESSAGE_WRITER);
			e.printStackTrace();
		}
	}

	public void writeConfigChageFinish(SocketChannel socketDoer, String doer, String affected, boolean isCorrect) {
		try (FileWriter writer = new FileWriter("AuditLog.txt", true);) {
			id++;
			String commandSuccess;
			if(isCorrect) {
				commandSuccess = "successful";
			} else {
				commandSuccess = "unsuccessful";
				
			}
			writer.write("time: [" + LocalDateTime.now() + "] - " + "ID:" + id
					+ " - operation type: configuration change - " + "doer: " + doer + " - IP:"
					+ socketDoer.getLocalAddress() + " - " + commandSuccess + "\n");
		} catch (IOException e) {
			System.out.println(ERROR_MESSAGE_WRITER);
			e.printStackTrace();
		}
	}

}


