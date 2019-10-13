package org.jrichardsz.jenkins.plugins.easywebhook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jrichardsz.jenkins.plugins.common.ClassPathProperties;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestGitlabWebHookJsonParser {

  public String jsonInput;

  @Before
  public void setUp() throws IOException {
    jsonInput =
        IOUtils.toString(this.getClass().getResourceAsStream("gitlab_webhook.json"), "UTF-8");
  }

  @Test
  public void t001_getSimpĺeValues() throws Exception {

    ClassPathProperties.customInitialization(TestGitlabWebHookJsonParser.class
        .getResourceAsStream("gitlab-test-simple-jenkins-plugin.properties"));

    ScmWebHookJsonParser webHookJsonParser = new ScmWebHookJsonParser();
    Map<String, String> valuesFromJsonWebhook =
        webHookJsonParser.getCommonValues("gitlab", jsonInput);

    assertNotNull(valuesFromJsonWebhook.get("repositoryName"));
    assertNotNull(valuesFromJsonWebhook.get("branchName"));
    assertNotNull(valuesFromJsonWebhook.get("authorId"));
    assertNotNull(valuesFromJsonWebhook.get("eventMessage"));

    assertEquals("gitlab_repository", valuesFromJsonWebhook.get("repositoryName"));
    assertEquals("refs/heads/lassie_branch", valuesFromJsonWebhook.get("branchName"));
    assertEquals("jrichardsz.java@gmail.com", valuesFromJsonWebhook.get("authorId"));
    assertEquals("Update README.md", valuesFromJsonWebhook.get("eventMessage"));
  }
  
  @Test
  public void t002_getComplexValues() throws Exception {
    ClassPathProperties.customInitialization(TestGitlabWebHookJsonParser.class
        .getResourceAsStream("gitlab-test-complex-jenkins-plugin.properties"));

    ScmWebHookJsonParser webHookJsonParser = new ScmWebHookJsonParser();
    Map<String, String> valuesFromJsonWebhook =
        webHookJsonParser.getCommonValues("gitlab", jsonInput);

    assertNotNull(valuesFromJsonWebhook.get("repositoryName"));
    assertNotNull(valuesFromJsonWebhook.get("branchName"));
    assertNotNull(valuesFromJsonWebhook.get("authorId"));
    assertNotNull(valuesFromJsonWebhook.get("eventMessage"));

    assertEquals("gitlab_repository", valuesFromJsonWebhook.get("repositoryName"));
    assertEquals("lassie_branch", valuesFromJsonWebhook.get("branchName"));
    assertEquals("jrichardsz.java@gmail.com", valuesFromJsonWebhook.get("authorId"));
    assertEquals("Update README.md", valuesFromJsonWebhook.get("eventMessage"));
  }

}
