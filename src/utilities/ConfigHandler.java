package utilities;

import java.io.*;
import java.util.Properties;

public class ConfigHandler {

    // private String filePath;
    private Properties properties;

    /*
    Description: ConfigHandler object to handle input from .properties files such as configurations,
    environments, etc.
    Arguments: String filePath, is the path to the given config file
    Returns: ConfigHandler Object
    */
    public ConfigHandler(String filePath) {

        this.properties = loadProperties(filePath);

    }

    /*
    Description: Returns the content of a .properties file into a properties object
    Arguments: String filePath, is the path to the given config file
    Returns: ConfigHandler Object
    */
    private Properties loadProperties(String filePath) {
        try {
            InputStream input = ConfigHandler.class.getClassLoader().getResourceAsStream("config.properties");
            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return null;
            }
            prop.load(input);
            this.properties=prop;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;

    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

}