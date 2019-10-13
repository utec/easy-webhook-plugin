package org.jrichardsz.jenkins.plugins.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.kohsuke.stapler.StaplerResponse;

import hudson.model.Job;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.StringParameterValue;
import hudson.security.ACL;
import jenkins.model.Jenkins;

public class JenkinsUtil {

  public static Job<?, ?> getProjectInstanceByJobName(String jobName) throws Exception {

    Job<?, ?> jobFound = null;

    SecurityContext orig = ACL.impersonate(ACL.SYSTEM);
    try {
      for (Job<?, ?> job : Jenkins.getInstance().getAllItems(Job.class)) {
        if (job.getName().equals(jobName)) {
          jobFound = job;
          break;
        }

      }

      if (jobFound == null) {
        throw new Exception(
                String.format("Job '%s' was not found in jenkins/hudson instance.", jobName));
      }
    } finally {
      SecurityContextHolder.setContext(orig);
    }

    return jobFound;

  }

  public static ParametersAction simpleMapToParametersAction(Map<String, String> parameters) {

    List<ParameterValue> parametersActionList = new ArrayList<ParameterValue>();

    for (Entry<String, String> entry : parameters.entrySet()) {
      parametersActionList.add(new StringParameterValue(entry.getKey(), entry.getValue()));
    }

    ParametersAction parametersAction = new ParametersAction(parametersActionList);

    return parametersAction;

  }

  public static void createStaplerResponseAndFinalize(StaplerResponse resp, int status,
          String finalMessage) throws IOException {
    resp.setStatus(200);
    resp.getWriter().write(finalMessage);
    resp.getWriter().flush();
    resp.getWriter().close();
  }

  public static void createSuccessStaplerResponseAndFinalize(StaplerResponse resp,
          String finalMessage) throws IOException {
    createStaplerResponseAndFinalize(resp, 200, finalMessage);
  }

  public static void createInternalErrorStaplerResponseAndFinalize(StaplerResponse resp,
          String finalMessage) throws IOException {
    createStaplerResponseAndFinalize(resp, 500, finalMessage);
  }
  
  public static void createBadRequestErrorStaplerResponseAndFinalize(StaplerResponse resp,
          String finalMessage) throws IOException {
    createStaplerResponseAndFinalize(resp, 400, finalMessage);
  }  

}
