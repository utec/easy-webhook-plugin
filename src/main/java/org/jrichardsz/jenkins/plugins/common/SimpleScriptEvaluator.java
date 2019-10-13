package org.jrichardsz.jenkins.plugins.common;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleScriptEvaluator {

  private static final Logger LOGGER = Logger.getLogger(SimpleScriptEvaluator.class.getName());

  public Object execute(String script, Map<String, String> parameters, ClassLoader classLoader) {

    Object value = null;

    try {

      Binding binding = new Binding();

      for (Entry<String, String> entry : parameters.entrySet()) {
        binding.setVariable(entry.getKey(), entry.getValue());
      }

      GroovyShell shell = null;

      if (classLoader != null) {
        shell = new GroovyShell(classLoader, binding);
      } else {
        shell = new GroovyShell(binding);
      }

      value = shell.evaluate(script);

    } catch (Exception ex) {
      LOGGER.log(Level.SEVERE, String.format("Failed to execute script \n %s", script), ex);
    }

    return value;
  }
}
