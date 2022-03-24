package com.tma.ems.helper;

import com.tma.ems.entity.Device;
import com.tma.ems.exception.ServerException;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.common.channel.ChannelOutputStream;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class CommandExecute {
    private final SshClient client;
    private ClientChannel channel;
    private String serialNumber = "";
    private ClientSession session;
    private final String host;
    private final String username;
    private final String password;
    private final int port;


    public CommandExecute(Device device) {
        host = device.getIpAddress();
        username = device.getCredential().getUsername();
        password = device.getCredential().getPassword();
        port = device.getPort();
        if (device.getSerialNumber() != null) {
            serialNumber = device.getSerialNumber();
        }

        //create client
        client = SshClient.setUpDefaultClient();
        client.start();

        //create session
        createSession();

        //create channel
        try {
            channel = session.createChannel(Channel.CHANNEL_SHELL);
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

    public void createSession() {
        try {
            session = client.connect(username, host, port).verify(2000, TimeUnit.SECONDS).getSession();
            session.addPasswordIdentity(password);
            session.auth().verify(2000, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

    public void close() {
        channel.close(false);
        session.close(false);
        client.stop();
    }

    public String executeCommand(String command) {
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        channel.setOut(responseStream);
        channel.setErr(errorStream);
        try {
//            if (!channel.isOpen()) {
//                channel.open().verify(2000, TimeUnit.SECONDS);
//            }

            channel.open().verify(2000, TimeUnit.SECONDS);
            try (ChannelOutputStream pipedIn = (ChannelOutputStream) channel.getInvertedIn()) {
                byte[] commandBytes = (command + "\n").getBytes(StandardCharsets.UTF_8);
                pipedIn.write(commandBytes);
                pipedIn.flush();
                Thread.sleep(100);
                boolean isCompleted = false;
                while (!isCompleted) {
                    String output = responseStream.toString();
                    if (output.endsWith(serialNumber + ": ")) {
                        isCompleted = true;
                    }
                }
            } catch (Exception e) {
                throw new ServerException(e.getMessage() + ":Command execute");
            }

            return responseStream.toString();
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }
}
