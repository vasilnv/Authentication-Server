package bg.sofia.uni.fmi.mjt.auth.domain.commands;

public class CommandExecutor {

	
    public String executeOperation(CommandOperation command) {
        return command.execute();
    }
}
