package com.frameworkium.bdd;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final Properties properties;
    private static final String environmentPrefix = System.getProperty("env", "sit") + ".";

    /* initialise data, the key defaults to this when none is specified at command line */
    static {
        properties = initPropertiesFromFile("config");
    }

    private static Properties initPropertiesFromFile(String fileName) {
        Properties tempProperties = new Properties();
        String propsLocation = String.format("properties/%s.properties", fileName);
        InputStream inputStream = null;
        try {
            inputStream = Config.class.getClassLoader().getResourceAsStream(propsLocation);
            tempProperties.load(inputStream);
        } catch (IOException ex) {
            throw new RuntimeException("Couldn't find properties file at location: " + propsLocation, ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return  tempProperties;
    }

    public static void setProperty(String key, String value)  {
        properties.setProperty(environmentPrefix+key, value);
    }

    public static String getProperty(String s) {
        return properties.getProperty(environmentPrefix + s);
    }
}