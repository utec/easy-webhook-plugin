package org.jrichardsz.jenkins.plugins.easywebhook;

import java.util.ArrayList;
import java.util.Map;
import org.jrichardsz.jenkins.plugins.common.ExpressionEvaluator;

public class ScmWebHookJsonParser {

  private ExpressionEvaluator jsonPathEvaluator = new ExpressionEvaluator();

  public Map<String, String> getCommonValues(String scmId, String webhookPayload) throws Exception {

    ArrayList<String> variablesToBeEvaluated = new ArrayList<String>();
    variablesToBeEvaluated.add("repositoryName");
    variablesToBeEvaluated.add("branchName");
    variablesToBeEvaluated.add("authorId");
    variablesToBeEvaluated.add("eventMessage");

    // @TODO: Detect event type (push, commit, pr created, branch deletion, etc)
    // and add as variable
    // input : json webhook ; output : event type 
    Map<String, String> parametersFromJsonWebhook =
        jsonPathEvaluator.execute(webhookPayload, variablesToBeEvaluated, scmId);

    return parametersFromJsonWebhook;

  }
}
