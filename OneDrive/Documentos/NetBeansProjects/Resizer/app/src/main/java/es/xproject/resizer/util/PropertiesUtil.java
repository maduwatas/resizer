/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.util;


import es.xproject.resizer.App;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author jsolis
 */
public class PropertiesUtil {

    private static final Logger log = LogManager.getLogger();

    public Properties readProperties(String name) {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(name)) {
            prop.load(input);
            
            App.version = (String) prop.get("version");
            App.code = (String) prop.get("code");
            
        } catch (Exception ex) {
            log.error("unable to read properties");
        }
        return prop;

    }

    public void readVersion() {

        try {

            Properties prop = readProperties("version.properties");
            log.info(prop.getProperty("version"));
            log.info(prop.getProperty("code"));
            // get the property value and print it out
        } catch (Exception ex) {
            log.error("unable to read properties");
        }

    }
}
