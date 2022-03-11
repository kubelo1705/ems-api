package com.example.managedevices.utils;

import com.example.managedevices.constant.Command;
import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Credential;
import com.example.managedevices.entity.Device;
import com.example.managedevices.exception.DeviceException;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class CommandUtils {
    public static String execute(Device device, Credential credential,String command) {
        String username= credential.getUsername();
        String password=credential.getPassword();

        String host= device.getIpAddress();
        int port= device.getPort();


        SshClient client = SshClient.setUpDefaultClient();
        client.start();

        try (ClientSession session = client.connect(username, host, port)
                .verify(Command.DEFAULT_TIMEOUT, TimeUnit.SECONDS).getSession()) {
            session.addPasswordIdentity(password);
            session.auth().verify(Command.DEFAULT_TIMEOUT, TimeUnit.SECONDS);

            try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
                 ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
                 ClientChannel channel = session.createChannel(Channel.CHANNEL_SHELL)) {
                channel.setOut(responseStream);
                channel.setErr(errorStream);
                try {
                    channel.open().verify(Command.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                    try (OutputStream pipedIn = channel.getInvertedIn()) {
                        pipedIn.write((command+"\n").getBytes());
                        pipedIn.flush();
                        Thread.sleep(500);
                    }
                    channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),
                            TimeUnit.SECONDS.toMillis(Command.DEFAULT_TIMEOUT));
                } finally {
                    channel.close(false);
                    client.stop();
                    return FormatUtils.formatOutput(new String(responseStream.toByteArray()),command);
                }
            }
        }catch (Exception e){
            throw new DeviceException(Message.ERROR_CONNECTION);
        }
    }
}
