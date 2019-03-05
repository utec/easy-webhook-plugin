package org.jrichardsz.jenkins.plugins.easywebhook;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.jrichardsz.jenkins.plugins.common.ClassPathProperties;
import org.jrichardsz.jenkins.plugins.common.Constants;
import org.jrichardsz.jenkins.plugins.common.JenkinsUtil;
import org.jrichardsz.jenkins.plugins.common.JsonPathEvaluator;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.CauseAction;
import hudson.model.Job;
import hudson.model.ParametersAction;
import hudson.model.RootAction;
import hudson.model.UnprotectedRootAction;

@Extension
public class WebHookReceiver implements UnprotectedRootAction {

  private static final Logger LOGGER = Logger.getLogger(WebHookReceiver.class.getName());

  @Override
  public String getIconFileName() {
    return null;
  }

  @Override
  public String getDisplayName() {
    return null;
  }

  /**
   * Returns the transient {@link Action}s associated with the top page.
   *
   * <p>
   * Adding {@link Action} is primarily useful for plugins to contribute an item
   * to the navigation bar of the top page. See existing {@link Action}
   * implementation for it affects the GUI.
   *
   * <p>
   * To register an {@link Action}, implement {@link RootAction} extension
   * point, or write code like
   * {@code Jenkins.getInstance().getActions().add(...)}.
   *
   * @return Live list where the changes can be made. Can be empty but never
   *         null.
   * @since 1.172
   */
  @Override
  public String getUrlName() {

    String allowedUrl = null;

    try {
      allowedUrl = Constants.UNPROTECTED_ROOT_ACTION_PREFIX + "_"
              + SystemPluginConfiguration.getCurrentProperties().getEasyWebHookKey();
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage());
    }

    return (allowedUrl == null) ? "JUST_FOR_TEST" : allowedUrl;
  }

  public void doIndex(StaplerRequest req, StaplerResponse resp) throws Exception {

    LOGGER.log(Level.INFO, "Starting webhook handler for this url: " + req.getOriginalRequestURI());

    try {
      execute(req, resp);

      String finalMessage = "Webhook event was received successfully and no error was detected when was launched in background.";

      LOGGER.log(Level.INFO, finalMessage);
      JenkinsUtil.createSuccessStaplerResponseAndFinalize(resp, finalMessage);

    } catch (Exception exception) {

      String finalMessage = "Webhook event was received successfully but fail to process it.";
      LOGGER.log(Level.SEVERE, finalMessage, exception);
      JenkinsUtil.createInternalErrorStaplerResponseAndFinalize(resp, finalMessage + exception);
    }
  }

  public void execute(StaplerRequest req, StaplerResponse resp) throws Exception {
    String webhookPayload = IOUtils.toString(req.getInputStream());

    LOGGER.log(Level.INFO, webhookPayload);

    LOGGER.log(Level.INFO, "Get url query parameters");

    Enumeration<?> requestParameterNames = req.getParameterNames();
    HashMap<String, String> urlQueryParameters = new HashMap<>();
    HashMap<String, String> payloadParameter = new HashMap<>();

    payloadParameter.put("payload", webhookPayload);

    while (requestParameterNames.hasMoreElements()) {
      String parameterName = (String) requestParameterNames.nextElement();
      String parameterValue = req.getParameter(parameterName);
      urlQueryParameters.put(parameterName, parameterValue);
      LOGGER.log(Level.INFO, String.format("key:%s , value:%s", parameterName, parameterValue));
    }

    // get git Repository Management Id
    String gitRepositoryManagementId = req.getParameter("gitRepositoryManagementId");

    if (gitRepositoryManagementId == null || gitRepositoryManagementId.equals("")) {
      String finalMessage = "gitRepositoryManagementId was not found.";
      LOGGER.log(Level.INFO, finalMessage);
      JenkinsUtil.createSuccessStaplerResponseAndFinalize(resp, finalMessage);
      return;
    }

    LOGGER.log(Level.INFO, "Get parameters from json webhook payload");

    JsonPathEvaluator jsonPathEvaluator = new JsonPathEvaluator();

    Map<String, String> variablesToBeExtracted = new HashMap<String, String>();
    variablesToBeExtracted.put("repositoryName", ClassPathProperties.getProperty(
            String.format("%s.jsonpath.expression.repositoryName", gitRepositoryManagementId)));
    variablesToBeExtracted.put("branchName", ClassPathProperties.getProperty(
            String.format("%s.jsonpath.expression.branchName", gitRepositoryManagementId)));
    variablesToBeExtracted.put("actorName", ClassPathProperties.getProperty(
            String.format("%s.jsonpath.expression.actorName", gitRepositoryManagementId)));
    variablesToBeExtracted.put("changeNotes", ClassPathProperties.getProperty(
            String.format("%s.jsonpath.expression.eventMessage", gitRepositoryManagementId)));
    
    Map<String, String> parametersFromJsonWebhook = jsonPathEvaluator.execute(webhookPayload,
            variablesToBeExtracted);

    for (Entry<String, String> entry : parametersFromJsonWebhook.entrySet()) {
      LOGGER.log(Level.INFO, String.format("key:%s , value:%s", entry.getKey(), entry.getValue()));
    }   

    String jobToExecute = req.getParameter("jobId");
    LOGGER.log(Level.INFO, "job To Execute : " + jobToExecute);

    if (jobToExecute == null || jobToExecute.equals("")) {
      String finalMessage = "jobId was not found. There is not possible launch any tasks.";
      LOGGER.log(Level.INFO, finalMessage);
      JenkinsUtil.createSuccessStaplerResponseAndFinalize(resp, finalMessage);
      return;
    }

    Job<?, ?> job = JenkinsUtil.getProjectInstanceByJobName(jobToExecute);

    String name = job.getName() + " #" + job.getNextBuildNumber();

    CustomParameterizedJobMixIn customParameterizedJobMixIn = new CustomParameterizedJobMixIn();
    customParameterizedJobMixIn.setJob(job);

    // merge url query parameters + webhook json parameters
    HashMap<String, String> parametersToSendItToJob = new HashMap<>();
    parametersToSendItToJob.putAll(parametersFromJsonWebhook);
    parametersToSendItToJob.putAll(urlQueryParameters);
    parametersToSendItToJob.putAll(payloadParameter);
    
    LOGGER.log(Level.INFO, "Add constant parameters related to git repository");
    
    String gitCloneUrlSshPrefix = ClassPathProperties.getProperty(
            String.format("%s.cloneUrlPrefix.ssh", gitRepositoryManagementId));
    
    String gitCloneUrlHttpsPrefix = ClassPathProperties.getProperty(
            String.format("%s.cloneUrlPrefix.https", gitRepositoryManagementId)); 
    
    parametersToSendItToJob.put("gitCloneUrlHttpsPrefix", gitCloneUrlHttpsPrefix);
    parametersToSendItToJob.put("gitCloneUrlSshPrefix", gitCloneUrlSshPrefix);

    ParametersAction parametersAction = JenkinsUtil
            .simpleMapToParametersAction(parametersToSendItToJob);

    customParameterizedJobMixIn.scheduleBuild2(0, parametersAction,
            new CauseAction(new WebHookEventCause(parametersToSendItToJob.get("actorName"))));
    LOGGER.info(String.format("Job : %s was triguered without errors.", name));

  }

}
