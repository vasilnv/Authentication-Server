package bg.sofia.uni.fmi.mjt.auth.FileEditors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import bg.sofia.uni.fmi.mjt.auth.domain.DataOrganizer;
import bg.sofia.uni.fmi.mjt.auth.domain.Domain;
import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;

public class UserFileEditor {
	private static final int NULL_ARG = 0;
	private static final int FIRST_ARG = 1;
	private static final int SECOND_ARG = 2;
	private static final int THIRD_ARG = 3;
	private static final int FOURTH_ARG = 4;
	private static final String ERROR_MESSAGE_WRITER = "Problem with the File Writer";
	private static final String ERROR_MESSAGE_READER = "Problem with the Reader";
	

	public void load(DataOrganizer dataOrganizer) {
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader("users.txt"))) {
			String line = bufferedReader.readLine();
			while (line != null) {
				String[] tokens = line.split(" ");
				String username = tokens[NULL_ARG];
				String password = tokens[FIRST_ARG];
				String firstname = tokens[SECOND_ARG];
				String lastname = tokens[THIRD_ARG];
				String email = tokens[FOURTH_ARG];
				AuthenticatedUser user = new AuthenticatedUser(username, password, firstname, lastname, email);
				dataOrganizer.getUsers().put(username, user);

				if (dataOrganizer.getAdmins().isEmpty()) {
					dataOrganizer.getAdmins().add(tokens[NULL_ARG]);
				}
				line = bufferedReader.readLine();
			}

		} catch (FileNotFoundException e) {
			System.out.println(ERROR_MESSAGE_READER);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(ERROR_MESSAGE_WRITER);
			e.printStackTrace();
		}
	}
	
	public void deleteUserFromFile (String username) {
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader("users.txt"));
				FileWriter writer = new FileWriter("users.txt", true);) {
			String line = bufferedReader.readLine();
			StringBuilder builder = new StringBuilder();
			while (line != null) {
				String[] tokens = line.split(" ");
				if (username.equals(tokens[NULL_ARG])) {
					line = bufferedReader.readLine();
					continue;
				}

				builder.append(line);
				builder.append("\n");
				line = bufferedReader.readLine();
			}
			FileWriter writer2 = new FileWriter("users.txt", false);
			writer.write(builder.toString());
		} catch (FileNotFoundException e) {
			System.out.println(ERROR_MESSAGE_READER);
			e.printStackTrace();
		} catch (IOException e1) {
			System.out.println(ERROR_MESSAGE_WRITER);
			e1.printStackTrace();
		}
	}
		
	public void changeConfiguration(String username, String newConfig, int arg) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader("users.txt"));
				FileWriter writer = new FileWriter("users.txt", true);) {
			String line = bufferedReader.readLine();
			StringBuilder builder = new StringBuilder();
			while (line != null) {
				String[] tokens = line.split(" ");
				if (username.equals(tokens[NULL_ARG])) {
					tokens[arg] = newConfig;
					String usernameInFile = tokens[NULL_ARG];
					String passwordInFile = tokens[FIRST_ARG];
					String firstnameInFile = tokens[SECOND_ARG];
					String lastnameInFile = tokens[THIRD_ARG];
					String emailInFile = tokens[FOURTH_ARG];


					line = usernameInFile + " " + passwordInFile + " " + firstnameInFile + " "
							+ lastnameInFile + " " + emailInFile + "\n";
					builder.append(line);
					line = bufferedReader.readLine();
					continue;
				}

				builder.append(line);
				builder.append("\n");
				line = bufferedReader.readLine();
			}
			FileWriter writer2 = new FileWriter("users.txt", false);
			writer.write(builder.toString());
		} catch (FileNotFoundException e) {
			System.out.println(ERROR_MESSAGE_READER);
			e.printStackTrace();
		} catch (IOException e1) {
			System.out.println(ERROR_MESSAGE_WRITER);
			e1.printStackTrace();
		}
	}
	public void writeUserInFile(AuthenticatedUser newUser) {
		String userToWrite = newUser.toString();
		try (FileWriter writer = new FileWriter("users.txt", true);) {

			writer.write(userToWrite);
			writer.write("\n");
		} catch (IOException e) {
			System.out.println(ERROR_MESSAGE_WRITER);
			e.printStackTrace();
		}
	}

}
