/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.api.Constants;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.connection.ServiceConnector;
import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.util.MCServiceCredentials;
import com.github.derrop.proxy.api.util.NetworkAddress;
import com.github.derrop.proxy.storage.MCServiceCredentialsStorage;
import com.mojang.authlib.exceptions.AuthenticationException;
import net.kyori.text.TextComponent;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

public class CommandAccount extends NonTabCompleteableCommandCallback {

    private final ServiceRegistry registry;

    public CommandAccount(ServiceRegistry registry) {
        super("proxy.command.help", null);
        this.registry = registry;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender sender, @NotNull String[] args, @NotNull String fullLine) throws CommandExecutionException {
        ServiceConnector connector = this.registry.getProviderUnchecked(ServiceConnector.class);
        MCServiceCredentialsStorage storage = this.registry.getProviderUnchecked(MCServiceCredentialsStorage.class);

        if (args.length == 5 && args[0].equalsIgnoreCase("add")) {
            NetworkAddress address = NetworkAddress.parse(args[1]);
            boolean exportable = Boolean.parseBoolean(args[4]);

            if (address == null) {
                sender.sendMessage("§cInvalid address");
                return CommandResult.BREAK;
            }

            if (storage.get(args[2]) != null) {
                sender.sendMessage("§cThat account is already registered");
                return CommandResult.BREAK;
            }

            sender.sendMessage("§aImporting account " + args[2] + "...");
            MCServiceCredentials credentials = new MCServiceCredentials(args[2], args[3], address.asString(), exportable);
            storage.insert(args[2], credentials);
            this.connect(connector, credentials, address, sender);
            sender.sendMessage("§aSuccessfully imported account " + args[2]);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("close")) {
            this.closeAll(connector, sender, client -> (client.getCredentials().getEmail() != null && client.getCredentials().getEmail().equalsIgnoreCase(args[1])) ||
                    args[1].equalsIgnoreCase(client.getName()));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("closeAll")) {
            NetworkAddress address = NetworkAddress.parse(args[1]);
            if (address == null) {
                sender.sendMessage("§cInvalid address");
                return CommandResult.BREAK;
            }

            this.closeAll(connector, sender, client -> client.getServerAddress().equals(address));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            storage.delete(args[1]);
            connector.getClientByEmail(args[1]).ifPresent(client -> {
                sender.sendMessage("§cDisconnecting online client...");
                client.close();
                sender.sendMessage("§cDisconnected client");
            });
            sender.sendMessage("§cDeleted the account " + args[1]);
        } else if (args.length == 1 && args[0].equalsIgnoreCase("export")) {
            sender.sendMessage("§aString account export...");

            try {
                String exportFile = "account-export-" + System.nanoTime() + ".txt";
                Path path = Paths.get(exportFile);
                if (Files.notExists(path)) {
                    Files.createFile(path);
                }

                for (MCServiceCredentials credentials : storage.getAll()) {
                    if (credentials.isExportable()) {
                        sender.sendMessage("Exporting account " + credentials.getEmail() + "@" + credentials.getDefaultServer() + "...");
                        Files.write(
                                path,
                                (credentials.getEmail() + ":" + credentials.getPassword() + "->" + credentials.getDefaultServer()).getBytes(StandardCharsets.UTF_8),
                                StandardOpenOption.APPEND
                        );
                    }
                }

                sender.sendMessage("Export results saved to " + exportFile);
            } catch (IOException exception) {
                sender.sendMessage("§cUnable to save accounts");
                exception.printStackTrace();
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("import")) {
            Path path = Paths.get(args[1]);
            if (Files.notExists(path)) {
                sender.sendMessage("§cThe file does not exists");
                return CommandResult.BREAK;
            }

            try {
                List<String> strings = Files.readAllLines(path, StandardCharsets.UTF_8);

                int lines = 0;
                for (String string : strings) {
                    ++lines;

                    String[] credentialsSplit = string.split(":");
                    if (credentialsSplit.length != 2) {
                        sender.sendMessage("§cInvalid line " + lines);
                        continue;
                    }

                    String[] hostSplit = credentialsSplit[1].split("->");
                    if (hostSplit.length != 2) {
                        sender.sendMessage("§cInvalid line " + lines);
                        continue;
                    }

                    String email = credentialsSplit[0];
                    String password = hostSplit[0];
                    String defaultServer = hostSplit[1];

                    if (storage.get(email) != null) {
                        sender.sendMessage("§cThat account with the email " + email + " is already registered");
                        continue;
                    }

                    NetworkAddress address = NetworkAddress.parse(defaultServer);
                    if (address == null) {
                        sender.sendMessage("§cUnable to parse network address from string: \"" + defaultServer + "\"");
                        continue;
                    }

                    MCServiceCredentials credentials = new MCServiceCredentials(email, password, defaultServer, true);
                    storage.insert(email, credentials);

                    sender.sendMessage("§aImported " + credentials.getEmail() + " successfully, trying to connect");
                    this.connect(connector, credentials, address, sender);
                }
            } catch (IOException exception) {
                sender.sendMessage("Unable to import file");
                exception.printStackTrace();
            }
        } else if (args.length == 3 && args[1].equalsIgnoreCase("setpassword")) {
            MCServiceCredentials credentials = storage.get(args[0]);
            if (credentials == null) {
                sender.sendMessage("§cThe account " + args[0] + " is unknown");
                return CommandResult.BREAK;
            }

            if (credentials.getPassword().equals(args[2])) {
                sender.sendMessage("§cThe old and the new password are identical");
                return CommandResult.BREAK;
            }

            credentials.setPassword(args[2]);
            storage.update(args[0], credentials);

            connector.getClientByEmail(args[0]).ifPresent(client -> {
                sender.sendMessage("§cDisconnecting online client...");
                client.close();
                sender.sendMessage("§cDisconnected client");
            });

            sender.sendMessage("Trying to connect to the default server using the new credentials...");
            this.connect(connector, credentials, NetworkAddress.parse(credentials.getDefaultServer()), sender);
        } else if (args.length == 3 && args[1].equalsIgnoreCase("setdefaultserver")) {
            MCServiceCredentials credentials = storage.get(args[0]);
            if (credentials == null) {
                sender.sendMessage("§cThe account " + args[0] + " is unknown");
                return CommandResult.BREAK;
            }

            if (credentials.getDefaultServer().equals(args[2])) {
                sender.sendMessage("§cThe old and the new server ip are identical");
                return CommandResult.BREAK;
            }

            NetworkAddress address = NetworkAddress.parse(args[2]);
            if (address == null) {
                sender.sendMessage("§cUnable to parse the new default server ip");
                return CommandResult.BREAK;
            }

            credentials.setDefaultServer(args[2]);
            storage.update(args[0], credentials);
        } else if (args.length == 3 && args[1].equalsIgnoreCase("setemail")) {
            MCServiceCredentials credentials = storage.get(args[0]);
            if (credentials == null) {
                sender.sendMessage("§cThe account " + args[0] + " is unknown");
                return CommandResult.BREAK;
            }

            if (credentials.getEmail().equals(args[2])) {
                sender.sendMessage("§cThe old and the new email is identical");
                return CommandResult.BREAK;
            }

            credentials.setEmail(args[2]);
            storage.delete(args[0]);
            storage.insert(args[2], credentials);

            connector.getClientByEmail(args[0]).ifPresent(client -> {
                sender.sendMessage("§cDisconnecting online client...");
                client.close();
                sender.sendMessage("§cDisconnected client");
            });

            sender.sendMessage("Trying to connect to the default server using the new credentials...");
            this.connect(connector, credentials, NetworkAddress.parse(credentials.getDefaultServer()), sender);
        } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            for (MCServiceCredentials credentials : storage.getAll()) {
                sender.sendMessage(" -> " + credentials.getEmail() + " / " + credentials.getDefaultServer()
                        + " (" + (credentials.isExportable() ? "exportable" : "not exportable") + ")");
            }
        } else {
            this.sendHelp(sender);
        }

        return CommandResult.END;
    }

    private void closeAll(ServiceConnector connector, CommandSender sender, Predicate<ServiceConnection> tester) {
        for (ServiceConnection client : connector.getOnlineClients()) {
            if (tester.test(client)) {
                sender.sendMessage("§7Closing client §e" + client.getName() + "#" + client.getUniqueId() + " §7(§e" + client.getCredentials().getEmail() + "§7)...");
                try {
                    client.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                sender.sendMessage("§aDone");
            }
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("acc add <default-address> <E-Mail> <Password> <exportable>");
        sender.sendMessage("acc <email> setEmail <new-email>");
        sender.sendMessage("acc <email> setPassword <password>");
        sender.sendMessage("acc <email> setDefaultServer <default-server>");
        sender.sendMessage("acc close <E-Mail|Username>");
        sender.sendMessage("acc delete <e-mail>");
        sender.sendMessage("acc closeAll <address>");
        sender.sendMessage("acc export");
        sender.sendMessage("acc import <file-path>");
        sender.sendMessage("acc list");
        // TODO acc start <e-mail> to let an account connect to its default server
    }

    private void connect(ServiceConnector connector, MCServiceCredentials credentials, NetworkAddress address, CommandSender sender) {
        try {
            boolean success = connector.createConnection(credentials, address).connect().get(5, TimeUnit.SECONDS);

            ServiceConnection client = connector.getOnlineClients().stream()
                    .filter(proxyClient -> proxyClient.getCredentials().equals(credentials))
                    .filter(proxyClient -> proxyClient.getServerAddress().equals(address))
                    .findFirst()
                    .orElse(null);
            if (client == null) {
                sender.sendMessage("§cFailed, reason unknown. Look into the console for more information");
                return;
            }

            sender.sendMessage(success ? ("§aSuccessfully connected as §e" + credentials.getEmail() + " §7(§e" + client.getName() + "#" + client.getName() + "§7) §ato §e" + address) : "§cFailed to connect to §e" + address);
            if (sender instanceof Player) {
                TextComponent component = TextComponent.of(Constants.MESSAGE_PREFIX + "§aClick to connect");
                component.clickEvent(ClickEvent.runCommand("/switch " + client.getName()));
                component.hoverEvent(HoverEvent.showText(TextComponent.of("§7Switch to §e" + client.getName() + "#" + client.getUniqueId())));
                sender.sendMessage(component);
            }
        } catch (ExecutionException | InterruptedException | TimeoutException exception) {
            exception.printStackTrace();
        } catch (AuthenticationException exception) {
            sender.sendMessage("§cInvalid credentials");
            System.out.println("Invalid credentials for " + credentials.getEmail());
        }
    }
}
