package org.jrichardsz.jenkins.plugins.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClassPathProperties {

  private static Properties properties = null;

  public static Properties getDefaultInstance() throws Exception {
    return getCustomInstance(ClassPathProperties.class.getClassLoader()
        .getResourceAsStream("jenkins-plugin.properties"));
  }

  public static void customInitialization(InputStream propertiesInputStream) throws Exception {
    properties = getCustomInstance(propertiesInputStream);
  }

  public static Properties getCustomInstance(InputStream propertiesInputStream) throws Exception {

    Properties properties = null;
    InputStream input = null;

    try {
      properties = new Properties();
      // load a properties file
      properties.load(propertiesInputStream);
      return properties;
    } catch (Exception ex) {
      throw new Exception(
          "Failed to read plugin properties: jenkins-plugin.properties or another for unit tests",
          ex);
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
      properties = getDefaultInstance();
    }

    String value = properties.getProperty(property);
    if (value == null) {
      throw new Exception(
          "Failed to read key in application.properties instance: key[" + property + "]");
    }
    return value;
  }

}
