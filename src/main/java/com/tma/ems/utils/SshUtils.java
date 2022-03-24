package com.tma.ems.utils;

import com.tma.ems.entity.Credential;
import com.tma.ems.entity.Device;
import com.tma.ems.exception.ServerException;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

/**
 * create ssh connection and execute command
 */
public class SshUtils {
    private static final String EXPECTED_END_CHARACTER = ": ";
    private static final int DEFAULT_TIMEOUT = 1000;

    /**
     * create shell to execute command
     */
    public static ClientChannel createChannel(ClientSession session) {
        try {
            return session.createChannel(Channel.CHANNEL_SHELL);
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * create ssh client
     */
    public static SshClient createClient() {
        SshClient client = SshClient.setUpDefaultClient();
        client.start();
        return client;
    }

    /**
     * create session and authenticate
     */
    public static ClientSession createSession(SshClient client, String host, String username, String password, int port) {
        try {
            ClientSession session = client.connect(username, host, port).verify(DEFAULT_TIMEOUT, TimeUnit.SECONDS).getSession();
            session.addPasswordIdentity(password);
            session.auth().verify(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            return session;
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * close ssh connection
     */
    public static void close(SshClient client, ClientSession session) {
        session.close(false);
        client.stop();
    }

    /**
     * send command to device an get comamnd
     */
    public static String executeCommand(Device device, String command) {
        Credential credential=device.getCredential();
        String username = credential.getUsername();
        String password = credential.getPassword();

        String host = device.getIpAddress();
        int port = device.getPort();
        //create client
        SshClient client = createClient();
        //create session
        ClientSession session = createSession(client, host, username, password, port);
        //create channel
        ClientChannel channel=createChannel(session);
        //create output command and error of command
        try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
             ByteArrayOutputStream errorStream = new ByteArrayOutputStream()) {
            channel.setOut(responseStream);
            channel.setErr(errorStream);
            try {
                channel.open().verify(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                try (OutputStream pipedIn = channel.getInvertedIn()) {
                    //send command to device and flush output
                    pipedIn.write((command + "\n").getBytes());
                    pipedIn.flush();

                    Thread.sleep(100);
                    waitForResponse(responseStream);
                }
                //close channel
                channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),
                        TimeUnit.SECONDS.toMillis(DEFAULT_TIMEOUT));
            }finally{
                //close ssh connection
                close(client,session);
                return responseStream.toString();
            }
        }catch (Exception e){
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * wait for flush response
     */
    public static void waitForResponse(ByteArrayOutputStream responseStream){
        boolean isCompleted=false;
        while (!isCompleted) {
            String output = responseStream.toString();
            if (output.endsWith(EXPECTED_END_CHARACTER)) {
                isCompleted = true;
            }
        }
    }

}
