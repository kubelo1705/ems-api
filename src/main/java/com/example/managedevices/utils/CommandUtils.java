package com.example.managedevices.utils;

import com.example.managedevices.constant.BaseCommand;
import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Credential;
import com.example.managedevices.entity.Device;
import com.example.managedevices.entity.Interface;
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

public class CommandUtils {
    public static String execute(Device device, Credential credential,String command) {
        String username= credential.getUsername();
        String password=credential.getPassword();

        String host= device.getIpAddress();
        int port= device.getPort();


        SshClient client = SshClient.setUpDefaultClient();
        client.start();

        try (ClientSession session = client.connect(username, host, port)
                .verify(BaseCommand.DEFAULT_TIMEOUT, TimeUnit.SECONDS).getSession()) {
            session.addPasswordIdentity(password);
            session.auth().verify(BaseCommand.DEFAULT_TIMEOUT, TimeUnit.SECONDS);

            try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
                 ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
                 ClientChannel channel = session.createChannel(Channel.CHANNEL_SHELL)) {
                channel.setOut(responseStream);
                channel.setErr(errorStream);
                try {
                    channel.open().verify(BaseCommand.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                    try (OutputStream pipedIn = channel.getInvertedIn()) {
                        pipedIn.write((command+"\n").getBytes());
                        pipedIn.flush();
                        Thread.sleep(500);
                    }
                    channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),
                            TimeUnit.SECONDS.toMillis(BaseCommand.DEFAULT_TIMEOUT));
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
    public static String toInterfaceCommand(String baseCommand, Interface inf){
        baseCommand=baseCommand.replace("interface_name",inf.getName()!=null?inf.getName():"");

        baseCommand=baseCommand.replace("name new_interface_name",inf.getName());

        if(inf.getIpAddress()==null){
            baseCommand=baseCommand.replace("address ip_address","");
        }else {
            baseCommand=baseCommand.replace("ip_address",inf.getIpAddress());
        }

        if(inf.getPort()==null){
            baseCommand=baseCommand.replace("port port_name","");
        }else {
            baseCommand=baseCommand.replace("port_name",inf.getPort());
        }

        if(inf.isState()==false){
            baseCommand=baseCommand.replace("state interface_state","");
        }else {
            baseCommand=baseCommand.replace("interface_state","enable");
        }

        if(inf.getNetmask()==null){
            baseCommand=baseCommand.replace("netmask netmask_address","");
        }else {
            baseCommand=baseCommand.replace("netmask address",inf.getNetmask());
        }
        return baseCommand;
    }
}
