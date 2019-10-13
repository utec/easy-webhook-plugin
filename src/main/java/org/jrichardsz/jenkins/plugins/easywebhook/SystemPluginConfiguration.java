package org.jrichardsz.jenkins.plugins.easywebhook;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Node;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import jenkins.model.Jenkins;

public class SystemPluginConfiguration extends NodeProperty<Node> {

  private String customGitProviderWebhookJsonParameters;
  private String easyWebHookKey;

  @DataBoundConstructor
  public SystemPluginConfiguration(String customGitProviderWebhookJsonParameters,
          String easyWebHookKey) {

    this.customGitProviderWebhookJsonParameters = customGitProviderWebhookJsonParameters;
    this.easyWebHookKey = easyWebHookKey;
  }

  public static SystemPluginConfiguration getCurrentProperties() throws Exception {

    SystemPluginConfiguration property = null;

    for (NodeProperty<?> nodeProperty : Jenkins.getInstance().getGlobalNodeProperties()) {

      if (nodeProperty instanceof SystemPluginConfiguration) {
        property = (SystemPluginConfiguration) nodeProperty;
        break;
      }
    }

    if (property == null) {
      throw new Exception("Failed to get Easy WebHook Plugin configurations from : "
              + "Jenkins > Manage Jenkins > Configure System > Configure Easy WebHook Plugin."
              + "Is your Easy WebHook Plugin configurated? Add the Easy Webhook Key or uninstall this plugin if it is unused.");
    }

    return property;
  }

  @Extension
  public static final class SystemPluginConfigurationDescriptor extends NodePropertyDescriptor {

    public SystemPluginConfigurationDescriptor() {
      super(SystemPluginConfiguration.class);
    }

    @Override
    public String getDisplayName() {
      return "Configure Easy WebHook Plugin";
    }
  }

  public String getCustomGitProviderWebhookJsonParameters() {
    return customGitProviderWebhookJsonParameters;
  }

  public void setCustomGitProviderWebhookJsonParameters(
          String customGitProviderWebhookJsonParameters) {
    this.customGitProviderWebhookJsonParameters = customGitProviderWebhookJsonParameters;
  }

  public String getEasyWebHookKey() {
    return easyWebHookKey;
  }

  public void setEasyWebHookKey(String easyWebHookKey) {
    this.easyWebHookKey = easyWebHookKey;
  }

}
