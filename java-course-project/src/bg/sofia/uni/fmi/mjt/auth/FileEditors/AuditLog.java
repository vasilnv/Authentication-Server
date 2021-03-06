package bg.sofia.uni.fmi.mjt.auth.FileEditors;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;

public class AuditLog {
	private static final String AUDIT_LOG_FILE_NAME = "AuditLog.txt";
	private static final String ERROR_MESSAGE_WRITER = "Problem with the File Writer";
	private static int id = 0;
	
	public void writeFailedLogin(SocketChannel socket, String name) {
		try (FileWriter writer = new FileWriter(AUDIT_LOG_FILE_NAME, true);) {
			writer.write("time: [" + LocalDateTime.now() + "] - " + "operation type: failed login - " + "username: "
					+ name + " - IP:" + socket.getLocalAddress() + "\n");
		} catch (IOException e) {
			System.out.println(ERROR_MESSAGE_WRITER);
			e.printStackTrace();
		}
	}

	public void writeConfigChangeStart(SocketChannel socket, String influencer, String affected, String command) {
		try (FileWriter writer = new FileWriter(AUDIT_LOG_FILE_NAME, true);) {
			id++;
			writer.write("time: [" + LocalDateTime.now() + "] - " + "ID:" + id
					+ " - operation type: configuration change - " + "doer: " + influencer + " - IP:"
					+ socket.getLocalAddress() + " - affected: " + affected + " - " + command + "\n");
		} catch (IOException e) {
			System.out.println(ERROR_MESSAGE_WRITER);
			e.printStackTrace();
		}
	}

	public void writeConfigChangeFinish(SocketChannel socketDoer, String doer, String affected, boolean isCorrect) {
		try (FileWriter writer = new FileWriter(AUDIT_LOG_FILE_NAME, true);) {
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


