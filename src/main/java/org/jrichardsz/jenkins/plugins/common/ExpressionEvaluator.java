package org.jrichardsz.jenkins.plugins.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jrichardsz.jenkins.plugins.easywebhook.exceptions.RequiredParameterWasNotFoundException;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

public class ExpressionEvaluator {

  private SimpleScriptEvaluator simpleScriptEvaluator = new SimpleScriptEvaluator();


  public Map<String, String> execute(String webhookPayload,
      ArrayList<String> variablesToBeEvaluated, String scmId) throws Exception {

    if (variablesToBeEvaluated == null) {
      return null;
    }

    Configuration conf = Configuration.defaultConfiguration();
    Configuration customConf = conf.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

    Object document = customConf.jsonProvider().parse(webhookPayload);

    HashMap<String, String> parsedParameters = new HashMap<String, String>();

    // iterate all variables and one by one
    // evaluate in order to evaluate
    // jsonpath or groovy expression
    for (String variableNameToBeEvaluated : variablesToBeEvaluated) {

      String jsonPathPrefix = getJsonPathPrefixToSearchInInternalPropertiesFile(scmId);
      // contains a raw string chich could be a
      // jsonpath or groovy expression
      String rawValue =
          getValueFromInternalPropertiesFile(variableNameToBeEvaluated, jsonPathPrefix);

      if (rawValue == null) {
        continue;
      }

      if (isJsonPathExpression(rawValue)) {
        try {
          String jsonPathValue = JsonPath.read(document, rawValue);
          parsedParameters.put(variableNameToBeEvaluated, jsonPathValue);
        } catch (Exception e) {
          throw new RequiredParameterWasNotFoundException(variableNameToBeEvaluated
              + " parameter is required. Is not possible get it from webhook json.", e);
        }
      } else if (isGroovyExpression(rawValue)) {
        // get the inner jsonpath expression
        String jsonPathExpression = getJsonPathFromGroovy(rawValue);
        if (isJsonPathExpression(jsonPathExpression)) {
          // evaluate inner jsonpath expression
          String jsonPathValue = null;
          try {
            jsonPathValue = JsonPath.read(document, jsonPathExpression);
          } catch (Exception e) {
            throw new RequiredParameterWasNotFoundException(variableNameToBeEvaluated
                + " parameter is required. Is not possible get it from webhook json.", e);
          }

          // use the obtained inner value to get groovy value
          String groovyPrefix = getGroovyPrefixToSearchInInternalPropertiesFile(scmId);
          // now, we need to get the groovy expression
          String groovyExpression = null;
          try {
            groovyExpression =
                getValueFromInternalPropertiesFile(variableNameToBeEvaluated, groovyPrefix);
          } catch (Exception e) {
            throw new Exception("An error ocurred when groovy expression was searched: "
                + variableNameToBeEvaluated, e);
          }

          // grrovy expression and its variables are ready to use
          HashMap<String, String> variablesToBeUsedInScript = new HashMap<String, String>();
          variablesToBeUsedInScript.put(variableNameToBeEvaluated, jsonPathValue);
          // execute groovy and get return value as expected value
          String finalValue = "" + simpleScriptEvaluator.execute("return " + groovyExpression,
              variablesToBeUsedInScript, this.getClass().getClassLoader());
          parsedParameters.put(variableNameToBeEvaluated, finalValue);

        }
      } else {
        parsedParameters.put(variableNameToBeEvaluated, rawValue);
      }
    }

    return parsedParameters;
  }

  public String getValueFromInternalPropertiesFile(String key, String prefix) throws Exception {
    return ClassPathProperties.getProperty(prefix + key);
  }

  public String getJsonPathPrefixToSearchInInternalPropertiesFile(String scmId) {
    return String.format("%s.jsonpath.expression.", scmId);
  }

  public String getGroovyPrefixToSearchInInternalPropertiesFile(String scmId) {
    return String.format("%s.groovy.expression.", scmId);
  }

  public boolean isJsonPathExpression(String input) {
    if (input != null && input.startsWith("$.")) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isGroovyExpression(String input) {
    Pattern pattern = Pattern.compile("groovy\\(\\$\\..+\\)");
    Matcher matcher = pattern.matcher(input);
    return matcher.find();
  }

  public String getJsonPathFromGroovy(String input) {
    input = input.replace("groovy(", "");
    int last = input.lastIndexOf(")");
    return input.substring(0, last);
  }

}
