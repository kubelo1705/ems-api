package com.example.managedevices.helper;

import com.example.managedevices.entity.Device;
import com.example.managedevices.exception.ServerException;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.common.channel.ChannelOutputStream;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class CommandExecute {
    private final SshClient client;
    private final ClientChannel channel;

    public CommandExecute(Device device){
        String host=device.getIpAddress();
        String username=device.getCredential().getUsername();
        String password=device.getCredential().getPassword();
        int port=device.getPort();

        //create client
        client=SshClient.setUpDefaultClient();
        client.start();

        //create session
        ClientSession session;
        try {
            session = client.connect(username, host, port).verify(2000, TimeUnit.SECONDS).getSession();
            session.addPasswordIdentity(password);
            session.auth().verify(2000, TimeUnit.SECONDS);
        }catch (Exception e){
            throw new ServerException(e.getMessage());
        }

        //create channel
        try {
            channel= session.createChannel(Channel.CHANNEL_SHELL);
        }catch (Exception e){
            throw new ServerException(e.getMessage());
        }
    }

    public void close(){
        channel.close(false);
        client.stop();
    }

    public String executeCommand(String command){
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        channel.setOut(responseStream);
        channel.setErr(errorStream);
        try {
            channel.open().verify(2000, TimeUnit.SECONDS);
            try (ChannelOutputStream pipedIn = (ChannelOutputStream) channel.getInvertedIn()) {
                byte[] commandBytes = (command + "\n").getBytes(StandardCharsets.UTF_8);
                pipedIn.write(commandBytes);
                pipedIn.flush();
                Thread.sleep(100);
                boolean isCompleted = false;
                while (!isCompleted) {
                    String output = responseStream.toString();
                    if (output.endsWith(": ")) {
                        isCompleted = true;
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),
                    TimeUnit.SECONDS.toMillis(1));
            return responseStream.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
