package bg.sofia.uni.fmi.mjt.auth.domain;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import bg.sofia.uni.fmi.mjt.auth.domain.users.AuthenticatedUser;

public class UsersLoader {
	private static final String WRITER_ERROR_MESSAGE = "Problem with the File Writer";
	private static final String READER_ERROR_MESSAGE = "Problem with the Reader";

	public static void load(Domain domain) {
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader("users.txt"))) {
			String line = bufferedReader.readLine();
			while (line != null) {
				String[] tokens = line.split(" ");
				int index = 0;
				String username = tokens[index++];
				String password = tokens[index++];
				String firstname = tokens[index++];
				String lastname = tokens[index++];
				String email = tokens[index++];
				AuthenticatedUser user = new AuthenticatedUser(username, password, firstname, lastname, email);
				domain.getUserRepository().addUser(username, user);

				if (domain.getAdmins().isEmpty()) {
					domain.getAdmins().add(tokens[0]);
				}
				line = bufferedReader.readLine();
			}

		} catch (FileNotFoundException e) {
			System.out.println(READER_ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(WRITER_ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
}
