package org.jrichardsz.jenkins.plugins.easywebhook;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jrichardsz.jenkins.plugins.common.Constants;
import org.jrichardsz.jenkins.plugins.common.JenkinsUtil;
import org.jrichardsz.jenkins.plugins.easywebhook.exceptions.RequiredParameterWasNotFoundException;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.RootAction;
import hudson.model.UnprotectedRootAction;

@Extension
public class WebHookReceiver implements UnprotectedRootAction {

  private static final Logger LOGGER = Logger.getLogger(WebHookReceiver.class.getName());
  private WebhookExecutor webhookExecutor = new WebhookExecutor();

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
      allowedUrl = Constants.UNPROTECTED_ROOT_ACTION_PREFIX + "-"
              + SystemPluginConfiguration.getCurrentProperties().getEasyWebHookKey();
    } catch (Exception e) {
      LOGGER.log(Level.FINEST, e.getMessage());
    }

    return (allowedUrl == null) ? "JUST_FOR_TEST" : allowedUrl;
  }

  public void doIndex(StaplerRequest req, StaplerResponse resp) throws Exception {

	  LOGGER.log(Level.INFO, "Starting webhook handler for this url: " + req.getOriginalRequestURI()+
			  " with "+req.getQueryString());
  	  LOGGER.log(Level.INFO, "From: " + req.getHeader("Origin"));


    try {
    	webhookExecutor.execute(req, resp);

      String finalMessage = "Webhook event was received successfully and "+
      "no error was detected when was launched in background.";

      LOGGER.log(Level.INFO, finalMessage);
      JenkinsUtil.createSuccessStaplerResponseAndFinalize(resp, finalMessage);

    } catch (RequiredParameterWasNotFoundException exception) {
        String finalMessage = "Webhook event was received successfully but fail to process it.";
        LOGGER.log(Level.FINEST, finalMessage, exception);
        JenkinsUtil.createBadRequestErrorStaplerResponseAndFinalize(resp, finalMessage +" "+ 
        exception.getLocalizedMessage());
	} catch (Exception exception) {	
	    String finalMessage = "Webhook event was received successfully but fail to process it.";
	    LOGGER.log(Level.SEVERE, finalMessage, exception);
	    JenkinsUtil.createInternalErrorStaplerResponseAndFinalize(resp, finalMessage +" "+ 
	    exception.getLocalizedMessage());
	}
  }

}
