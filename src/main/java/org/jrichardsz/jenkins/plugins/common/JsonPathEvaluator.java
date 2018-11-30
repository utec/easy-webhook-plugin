package org.jrichardsz.jenkins.plugins.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

public class JsonPathEvaluator {

  public Map<String, String> execute(String webhookPayload,
          Map<String, String> variablesToBeParsed) {

    if (variablesToBeParsed == null) {
      return null;
    }

    Configuration conf = Configuration.defaultConfiguration();
    Configuration customConf = conf.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

    Object document = customConf.jsonProvider().parse(webhookPayload);

    HashMap<String, String> parsedParameters = new HashMap<String, String>();

    for (Entry<String, String> entry : variablesToBeParsed.entrySet()) {

      String key = entry.getKey();
      String value = entry.getValue();// possibly jsonpath expression

      if (value == null) {
        continue;
      }

      if (isJsonPath(value)) {
        String jsonPathValue = JsonPath.read(document, value);
        parsedParameters.put(key, jsonPathValue);
      } else {
        parsedParameters.put(key, value);
      }
    }

    return parsedParameters;
  }

  public boolean isJsonPath(String input) {
    if (input != null && input.startsWith("$.")) {
      return true;
    } else
      return false;
  }

}
