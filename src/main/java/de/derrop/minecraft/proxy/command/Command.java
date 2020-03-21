package de.derrop.minecraft.proxy.command;

public abstract class Command {

    private String[] names;
    private String permission;

    public Command(String[] names, String permission) {
        this.names = names;
        this.permission = permission;
    }

    public Command(String... names) {
        this.names = names;
    }

    public String[] getNames() {
        return this.names;
    }

    public String getPermission() {
        return this.permission;
    }

    public boolean canExecute(CommandSender sender) {
        return this.getPermission() == null || sender.hasPermission(this.getPermission());
    }

    public abstract void execute(CommandSender sender, String input, String[] args);

}