package org.jrichardsz.jenkins.plugins.easywebhook;

import hudson.model.Job;
import jenkins.model.ParameterizedJobMixIn;

@SuppressWarnings("rawtypes")
public class CustomParameterizedJobMixIn extends ParameterizedJobMixIn {

  private Job job;

  @Override
  protected Job asJob() {
    return getJob();
  }

  public Job getJob() {
    return job;
  }

  public void setJob(Job job) {
    this.job = job;
  }

}
