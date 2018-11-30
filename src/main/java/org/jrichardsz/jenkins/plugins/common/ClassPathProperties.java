package org.jrichardsz.jenkins.plugins.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClassPathProperties {

  private static Properties properties = null;

  public static Properties getInstance() throws Exception {

    Properties properties = null;
    InputStream input = null;

    try {

      properties = new Properties();

      // load a properties file
      properties.load(ClassPathProperties.class.getClassLoader()
              .getResourceAsStream("application.properties"));

      return properties;

    } catch (Exception ex) {
      throw new Exception("Failed to read application.properties", ex);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          throw new Exception("Failed to clone application.properties stream", e);
        }
      }
    }

  }

  public static String getProperty(String property) throws Exception {

    if (properties == null) {
      properties = getInstance();
    }

    String value = properties.getProperty(property);
    if (value == null) {
      throw new Exception(
              "Failed to read key in application.properties instance: key[" + property + "]");
    }
    return value;
  }

}
