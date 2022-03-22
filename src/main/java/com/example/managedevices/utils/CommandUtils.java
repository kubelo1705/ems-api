package com.example.managedevices.utils;

import com.example.managedevices.constant.Command;
import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Credential;
import com.example.managedevices.entity.Device;
import com.example.managedevices.exception.EmsException;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * handle basic logic and execute command
 */
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
                        Thread.sleep(200);
                    }
                    channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),
                            TimeUnit.SECONDS.toMillis(Command.DEFAULT_TIMEOUT));
                } finally {
                    channel.close(false);
                    client.stop();
                    return new String(responseStream.toByteArray());
                }
            }
        }catch (Exception e){
            throw new EmsException(Message.ERROR_CONNECTION);
        }
    }

    /**
     * map attribute to interface object
     * @param baseCommand
     * @param map
     * @return
     */
    public static String toInterfaceCommand(String baseCommand, Map<String,Object> map){
        baseCommand=baseCommand.replace("interface_name",map.get("interface_name")!=null?map.get("interface_name").toString():"");

        if(map.get("new_interface_name")!=null){
            baseCommand=baseCommand.replace("new_name",map.get("new_name").toString());
        }

        if(map.get("ip_address")==null){
            baseCommand=baseCommand.replace("address ip_address","");
        }else {
            baseCommand=baseCommand.replace("ip_address",map.get("ip_address").toString());
        }

        if(map.get("port_name")==null){
            baseCommand=baseCommand.replace("port port_name","");
        }else {
            baseCommand=baseCommand.replace("port_name",map.get("port_name").toString());
        }

        if(map.get("state")==null){
            baseCommand=baseCommand.replace("state interface_state","");
        }else {
            baseCommand=baseCommand.replace("interface_state",map.get("state").toString());
        }

        if(map.get("netmask")==null){
            baseCommand=baseCommand.replace("netmask netmask_address","");
        }else {
            baseCommand=baseCommand.replace("netmask_address",map.get("netmask").toString());
        }
        return baseCommand.trim();
    }
}
