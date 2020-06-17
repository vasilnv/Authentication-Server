package bg.sofia.uni.fmi.mjt.auth;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import bg.sofia.uni.fmi.mjt.auth.commands.TestCommandAddAdmin;
import bg.sofia.uni.fmi.mjt.auth.commands.TestCommandDeleteUser;
import bg.sofia.uni.fmi.mjt.auth.commands.TestCommandLogin;
import bg.sofia.uni.fmi.mjt.auth.commands.TestCommandLogout;
import bg.sofia.uni.fmi.mjt.auth.commands.TestCommandRegister;
import bg.sofia.uni.fmi.mjt.auth.commands.TestCommandRemoveAdmin;
import bg.sofia.uni.fmi.mjt.auth.commands.TestCommandResetPassword;
import bg.sofia.uni.fmi.mjt.auth.commands.TestCommandUpdateUser;

@RunWith(Suite.class)

@SuiteClasses({
	TestSessionRepository.class,
	TestUserRepository.class,
	TestUserUpdater.class,
	TestDomain.class,
	TestCommandAddAdmin.class,
	TestCommandDeleteUser.class,
	TestCommandLogin.class,
	TestCommandLogout.class,
	TestCommandRegister.class,
	TestCommandRemoveAdmin.class,
	TestCommandResetPassword.class,
	TestCommandUpdateUser.class
})

public class AllTests {

}
