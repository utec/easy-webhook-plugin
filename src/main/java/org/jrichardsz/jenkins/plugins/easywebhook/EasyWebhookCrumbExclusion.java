package org.jrichardsz.jenkins.plugins.easywebhook;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jrichardsz.jenkins.plugins.common.Constants;

import com.google.common.collect.ImmutableSet;

import hudson.Extension;
import hudson.security.csrf.CrumbExclusion;

@Extension
public class EasyWebhookCrumbExclusion extends CrumbExclusion {

  private static final Logger LOGGER = Logger.getLogger(EasyWebhookCrumbExclusion.class.getName());

  private static final ImmutableSet<String> ALWAYS_READABLE_PATHS = ImmutableSet.of(
          "/ajaxExecutors", "/ajaxBuildQueue", "/administrativeMonitor", "/descriptorByName",
          "/configSubmit");

  @Override
  public boolean process(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
          throws IOException, ServletException {

    String uri = req.getPathInfo();

    for (String name : ALWAYS_READABLE_PATHS) {
      if (uri.startsWith(name)) {
        return false;
      }
    }

    String exclusionPath = null;
    try {
      exclusionPath = "/" + Constants.UNPROTECTED_ROOT_ACTION_PREFIX + "_"
              + SystemPluginConfiguration.getCurrentProperties().getEasyWebHookKey();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    if (uri != null && exclusionPath != null && uri.startsWith(exclusionPath)) {
      chain.doFilter(req, resp);
      return true;
    }
    return false;
  }
}
