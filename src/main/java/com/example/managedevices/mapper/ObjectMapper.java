package com.example.managedevices.mapper;

import com.example.managedevices.entity.Interface;

import java.util.Map;

/**
 * Map data to object
 */
public class ObjectMapper {
    public static Interface mapToInterface(Map<String,Object> map){
        Interface inf=new Interface();
        if(map.get("interface_name")!=null){
            inf.setName(map.get("interface_name").toString());
        }

        if(map.get("new_interface_name")!=null){
            inf.setName(map.get("new_interface_name").toString());
        }

        if(map.get("ip_address")!=null){
            inf.setIpAddress(map.get("ip_address").toString());
        }

        if(map.get("interface_state")!=null){
            inf.setState(map.get("interface_state").toString().equalsIgnoreCase("enable"));
        }

        if(map.get("netmask")!=null){
            inf.setNetmask(map.get("netmask").toString());
        }

        return inf;
    }
}
