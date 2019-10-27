# Easy Webhook Plugin

![](https://raw.githubusercontent.com/jrichardsz/static_resources/master/easy-webhook-plugin.png)

Plugin that can receive HTTP post request from bitbucket , gitlab y github(coming sooon), parse the json body, extract values using JSONPath & groovy and trigger a jenkins job with those values as job parameters.

# How it works?

When a event is triggered in your git repository manager (bitbucket, github, gitlab) or another platform, a jenkins job will be launched with following parameters:

- parameters extracted from github, bitbucket or gitlab
  - repositoryName
  - branchName
  - eventMessage
  - authorId
  - gitCloneUrlHttpsPrefix
  - gitCloneUrlSshPrefix

- any other http query parameters configured by you
  - scmId
  - jobId
  - any other uri parameters sent to webhook (http://jenkins.com/easy-webhook-plugin-mykey/var1=value1&var=value2...)


# Build plugin from source code

## Requirements:

- Java 8
- Maven

## Steps  

Execute:

```
mvn clean package
```

If no error, you must see a file called **easy-webhook-plugin.hpi** in maven target folder.

# Plugin installation

- You have 4 options to get this plugin:

  - Download the last version from github releases : https://github.com/utec/easy-webhook-plugin/releases/
  - Download the .hpi file from jenkins official page(coming soon)
  - Build the .hpi file from source code

# Plugin configuration

- After success installation , go to Jenkins > Manage Jenkins > Configure System > Configure Easy WebHook Plugin
- Add the **secret key** and save. This value helps to hide and protect your **public webhook url**

That's all. You have a new public endpoint in your jenkins, ready to use as webhook in your git platform provider.

# Basic job jenkins

In order to test this plugin, we need a minimal jenkins job. Pipeline job is recommended.

```
node {
   echo 'New build detected with this incoming parameters: '+params
}
```

For demo purposes, we will call **hello_word_job** to this job.

# Usage

After a success installation and configuration you could have this scenario :

| Parameter        | Description  | Example  |
|:------------- |:-----|:----
| jenkins host      | ip or public domain |  my_jenkins.com or localhost:8080
| easy webhook secret key      | plugin configuration | 123456789
| scmId      | one of the well known scm: gitlab, bitbucket or github | gitlab
| jobId      | name of any existent jenkins job | hello_word_job


With the previous parameters, your webhook url will be:

http://my_jenkins.com/easy-webhook-plugin-123456789/?scmId=gitlab&jobId=hello_word_job

# Test using curl

In order to test this plugin, we will simulate a gitlab push event using curl.

For this test, we need a **exact** gitlab webhook json sample. Here an [example](https://gist.github.com/jrichardsz/3d55df91181e3fb83089d08ada6809a8)

Download this json and save in some file like: /tmp/gitlab_webhook.json

I everything is good, you can exec this curl:

```
curl -d @/tmp/gitlab_webhook.json \
-H "Content-Type: application/json" \
-X POST "http://my_jenkins.com/easy-webhook-plugin-123456789/?scmId=gitlab&jobId=hello_word_job"
```

If you go to your jenkins home, you must see a new build execution in your **hello_word_job** job

# Using Bitbucket, Github or Gitlab

If the previous test with curl worked, your webhook is ready to use :D in gitlab for example.

Create some git repository and add the following url as webhook for **push** events. Check this [post](https://jrichardsz.github.io/devops/configure-webhooks-in-github-bitbucket-gitlab) to get a detailed guide for bitbucket, github and gitlab.

The url to register will be something like this:

  `
http://my_jenkins.com/easy-webhook-plugin-123456789/?scmId=gitlab&jobId=hello_word_job
  `
Or if you want, add new http query parameter like **notificationUsers**

  `
http://my_jenkins.com/easy-webhook-plugin-123456789/?scmId=gitlab&jobId=hello_word_job&notificationUsers=jane.doe@blindspot.com
  `

Finally, just push some change and go to your Jenkins to see the new build in progress :D   

# Get hacks from

- https://github.com/jayway/JsonPath
- https://github.com/jenkinsci/build-with-parameters-plugin/blob/master/src/main/java/org/jenkinsci/plugins/buildwithparameters/BuildWithParametersAction.java
- https://github.com/gboissinot/pre-build-plugin/blob/master/pom.xml
- https://github.com/SICSoftwareGmbH/sicci_for_xcode/tree/master/src/main/resources/com/sic/bb/jenkins/plugins/sicci_for_xcode/XcodeUserNodeProperty
- http://stackoverflow.com/questions/31625371/jenkins-jelly-view-how-to-aggregate-multiple-checkboxs-to-display-in-a-single-r
- https://github.com/jenkinsci/bitbucket-plugin/blob/master/src/main/java/com/cloudbees/jenkins/plugins/BitbucketCrumbExclusion.java
- https://www.programcreek.com/java-api-examples/?class=hudson.security.ACL&method=impersonate

# Coming soon

- Unit Tests
- github json path expressions
- Add to Jenkins official site
- Install from available plugins in Jenkins configurations (coming soon)

# Contributors

Thanks goes to these wonderful people :

<table>
  <tbody>
    <td>
      <img src="https://avatars0.githubusercontent.com/u/3322836?s=460&v=4" width="100px;"/>
      <br />
      <label><a href="http://jrichardsz.github.io/">Richard Leon</a></label>
      <br />
    </td>    
  </tbody>
</table>
