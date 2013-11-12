package edu.ipfw.locationshare.datamodel;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProcedureCall {
    private String Command;
    private LinkedHashMap<String,String> Parameters;

    public ProcedureCall(){
        this.Parameters=new LinkedHashMap<String, String>();
    }

    public String getCommand() {
        return Command;
    }

    public void setCommand(String command) {
        Command = command;
    }

    public LinkedHashMap<String, String> getParameters() {
        return Parameters;
    }

    public void setParameters(LinkedHashMap<String, String> parameters) {
        Parameters = parameters;
    }

    public String toString(){
        LinkedHashMap json = new LinkedHashMap();
        json.put("Command", this.getCommand());

        LinkedHashMap parameters = new LinkedHashMap();
        for (Map.Entry<String, String> entry : this.Parameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            parameters.put(key,value);
        }
        json.put("Parameters", parameters);

        return JSONValue.toJSONString(json);
    }
}
