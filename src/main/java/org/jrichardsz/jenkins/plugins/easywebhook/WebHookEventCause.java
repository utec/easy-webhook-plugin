package org.jrichardsz.jenkins.plugins.easywebhook;

import hudson.triggers.SCMTrigger;

import java.io.File;
import java.io.IOException;

public class WebHookEventCause extends SCMTrigger.SCMTriggerCause {

  private String pushedBy;

  public WebHookEventCause(String actor) {
    this("", actor);
  }

  public WebHookEventCause(String pollingLog, String pusher) {
    super(pollingLog);
    pushedBy = pusher;
  }

  public WebHookEventCause(File pollingLog, String pusher) throws IOException {
    super(pollingLog);
    pushedBy = pusher;
  }

  @Override
  public String getShortDescription() {
    String pusher = pushedBy != null ? pushedBy : "";
    return "Started by WebHook Event push by " + pusher;
  }
}
