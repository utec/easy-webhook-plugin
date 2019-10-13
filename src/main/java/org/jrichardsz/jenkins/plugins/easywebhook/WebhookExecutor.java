package org.jrichardsz.jenkins.plugins.easywebhook;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.jrichardsz.jenkins.plugins.common.ClassPathProperties;
import org.jrichardsz.jenkins.plugins.common.JenkinsUtil;
import org.jrichardsz.jenkins.plugins.easywebhook.exceptions.RequiredParameterWasNotFoundException;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.model.CauseAction;
import hudson.model.Job;
import hudson.model.ParametersAction;

public class WebhookExecutor {
	
	private static final Logger LOGGER = Logger.getLogger(WebHookReceiver.class.getName());
	
	private ScmWebHookJsonParser scmWebHookJsonParser = new ScmWebHookJsonParser();

	public void execute(StaplerRequest req, StaplerResponse resp) throws Exception {
		String webhookPayload = IOUtils.toString(req.getInputStream());

		LOGGER.log(Level.INFO, "Get url query parameters");

		Enumeration<?> requestParameterNames = req.getParameterNames();
		HashMap<String, String> urlQueryParameters = new HashMap<>();

		while (requestParameterNames.hasMoreElements()) {
			String parameterName = (String) requestParameterNames.nextElement();
			String parameterValue = req.getParameter(parameterName);
			urlQueryParameters.put(parameterName, parameterValue);
			LOGGER.log(Level.INFO, String.format("key:%s , value:%s", parameterName, parameterValue));
		}

		// get the source code management id :
		// bitbucket, github, gitlab, etc
		String scmId = req.getParameter("scmId");

		if (scmId == null || scmId.equals("")) {
			throw new RequiredParameterWasNotFoundException(
					"scmId was not found. " + "This value is required to parse specific webhook json");
		}

		String jobToExecute = req.getParameter("jobId");

		if (jobToExecute == null || jobToExecute.equals("")) {
			throw new RequiredParameterWasNotFoundException(
					"jobId was not found. " + "There is not possible launch any job or tasks in jenkins.");
		}

		LOGGER.log(Level.INFO, "Extracting common parameters from "+"["+scmId+"] "+"webhook json");

		Map<String, String> parametersFromJsonWebhook = 
				scmWebHookJsonParser.getCommonValues(scmId, webhookPayload);

		for (Entry<String, String> entry : parametersFromJsonWebhook.entrySet()) {
			LOGGER.log(Level.INFO, String.format("key:%s , value:%s", entry.getKey(), entry.getValue()));
		}

		LOGGER.log(Level.INFO, "jenkins job to execute : " + jobToExecute);
		Job<?, ?> job = JenkinsUtil.getProjectInstanceByJobName(jobToExecute);

		String name = job.getName() + " #" + job.getNextBuildNumber();

		CustomParameterizedJobMixIn customParameterizedJobMixIn = new CustomParameterizedJobMixIn();
		customParameterizedJobMixIn.setJob(job);

		// merge url query parameters + webhook json parameters
		HashMap<String, String> parametersToSendItToJob = new HashMap<>();
		parametersToSendItToJob.putAll(parametersFromJsonWebhook);
		parametersToSendItToJob.putAll(urlQueryParameters);

		LOGGER.log(Level.INFO, "Add constant parameters related to git repository");

		String gitCloneUrlSshPrefix = ClassPathProperties.getProperty(String.format("%s.cloneUrlPrefix.ssh", scmId));

		String gitCloneUrlHttpsPrefix = ClassPathProperties
				.getProperty(String.format("%s.cloneUrlPrefix.https", scmId));

		parametersToSendItToJob.put("gitCloneUrlHttpsPrefix", gitCloneUrlHttpsPrefix);
		parametersToSendItToJob.put("gitCloneUrlSshPrefix", gitCloneUrlSshPrefix);

		ParametersAction parametersAction = JenkinsUtil.simpleMapToParametersAction(parametersToSendItToJob);

		customParameterizedJobMixIn.scheduleBuild2(0, parametersAction,
				new CauseAction(new WebHookEventCause(parametersToSendItToJob.get("actorName"))));
		LOGGER.info(String.format("Job : %s was triguered without errors.", name));
	}

}
