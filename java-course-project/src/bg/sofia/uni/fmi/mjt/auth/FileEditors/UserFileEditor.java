package bg.sofia.uni.fmi.mjt.auth.FileEditors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;

public class UserFileEditor {
	private static final String STRING_END_OF_LINE = "\n";
	private static final String STRING_DELIMITER = " ";
	private static final String USERS_FILE_NAME = "users.txt";
	private static final String WRITER_ERROR_MESSAGE = "Problem with the File Writer";
	private static final String READER_ERROR_MESSAGE = "Problem with the Reader";

	public void deleteUserFromFile(String username) {
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(USERS_FILE_NAME));
				FileWriter writer = new FileWriter(USERS_FILE_NAME, true);) {
			String line = bufferedReader.readLine();
			StringBuilder builder = new StringBuilder();
			while (line != null) {
				String[] tokens = line.split(STRING_DELIMITER);
				if (username.equals(tokens[0])) {
					line = bufferedReader.readLine();
					continue;
				}

				builder.append(line);
				builder.append(STRING_END_OF_LINE);
				line = bufferedReader.readLine();
			}
			FileWriter writer2 = new FileWriter(USERS_FILE_NAME, false);
			writer.write(builder.toString());
		} catch (FileNotFoundException e) {
			System.out.println(READER_ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e1) {
			System.out.println(WRITER_ERROR_MESSAGE);
			e1.printStackTrace();
		}
	}

	public void changeConfiguration(String username, String newConfig, int arg) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(USERS_FILE_NAME));
				FileWriter writer = new FileWriter(USERS_FILE_NAME, true);) {
			String line = bufferedReader.readLine();
			StringBuilder builder = new StringBuilder();
			while (line != null) {
				String[] tokens = line.split(STRING_DELIMITER);
				int index = 0;
				if (username.equals(tokens[index])) {
					tokens[arg] = newConfig;
					String usernameInFile = tokens[index++];
					String passwordInFile = tokens[index++];
					String firstnameInFile = tokens[index++];
					String lastnameInFile = tokens[index++];
					String emailInFile = tokens[index++];

					line = usernameInFile + STRING_DELIMITER + passwordInFile + STRING_DELIMITER + firstnameInFile + STRING_DELIMITER + lastnameInFile + STRING_DELIMITER
							+ emailInFile + STRING_END_OF_LINE;
					builder.append(line);
					line = bufferedReader.readLine();
					continue;
				}

				builder.append(line);
				builder.append(STRING_END_OF_LINE);
				line = bufferedReader.readLine();
			}
			FileWriter writer2 = new FileWriter(USERS_FILE_NAME, false);
			writer.write(builder.toString());
		} catch (FileNotFoundException e) {
			System.out.println(READER_ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e1) {
			System.out.println(WRITER_ERROR_MESSAGE);
			e1.printStackTrace();
		}
	}

	public void writeUserInFile(AuthenticatedUser newUser) {
		String userToWrite = newUser.toString();
		try (FileWriter writer = new FileWriter(USERS_FILE_NAME, true);) {

			writer.write(userToWrite);
			writer.write(STRING_END_OF_LINE);
		} catch (IOException e) {
			System.out.println(WRITER_ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

}
