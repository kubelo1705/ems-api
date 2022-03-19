package com.example.managedevices.mapper;

import com.example.managedevices.entity.Interface;

import java.util.Map;

public class ObjectMapper {
    public static Interface mapToInterface(Map<String,Object> map){
        Interface inf=new Interface();

        return inf;
    }
}
