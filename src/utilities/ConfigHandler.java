package utilities;

public class ConfigHandler {

    // private String filePath;
    private Properties properties;

    """
    Description: ConfigHandler object to handle input from .properties files such as configurations,
    environments, etc.
    Arguments: String filePath, is the path to the given config file
    Returns: ConfigHandler Object
    """
    public ConfigHandler(String filePath) {

        this.properties = loadProperties(filePath);

    }

    """
    Description: Returns the content of a .properties file into a properties object
    Arguments: String filePath, is the path to the given config file
    Returns: ConfigHandler Object
    """
    private Properties loadProperties(String filePath) {

        File configFile = new File(filePath);

        try {
            FileReader reader = new FileReader(configFile);
            Properties properties = new Properties();
            properties.load(reader);
            reader.close();
        } catch (FileNotFoundException ex) {
            // file does not exist
        } catch (IOException ex) {
            // I/O error
        }

        return properties;

    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

}