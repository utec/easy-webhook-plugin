# Easy Webhook Plugin

![](https://raw.githubusercontent.com/jrichardsz/static_resources/master/easy-webhook-plugin.png)

Plugin that can receive any HTTP post request, parse the json body, extract values using JSONPath and trigger a jenkins job with those values as job parameters.

# How it works?

When a event is triggered in your git repository manager (bitbucket, github, gitlab) or another platform, a jenkins job will be launched with following parameters:

- webhook parameters 
  - repositoryName
  - branchName
  - changeNotes
  - actorName
  - gitCloneUrlHttpsPrefix
  - gitCloneUrlSshPrefix
  
- http uri parameters
  - gitRepositoryManagementId
  - jobId
  - any other uri parameters sent to webhook (http://jenkins.com/easy-webhook-plugin_mykey/var1=value1&var=value2...)


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
  
# Configuration

- You have 4 options to install this plugin:

  - Install from available plugins in Jenkins configurations (coming soon)
  - Download the last version from github releases : https://github.com/utec/easy-webhook-plugin/releases/
  - Download the .hpi file from jenkins official page
  - Build the .hpi file from source code
  
- After that , go to Jenkins > Manage Jenkins > Configure System > Configure Easy WebHook Plugin
- Add the key and save. This key will be used as identificator and must be added to the webhook url published by Jenkins. 

Thats all. You have a new public endpoint in your jenkins, ready to set as webhook in your git platform provider.
  
  
# Usage & Test

After a success installation of this plugin, you can test it with the following steps:

- Create a jenkins job called **my_awessome_jenkins_job** (for instance).
- Add some parameters to this job with exact the same name of parameters listed in **How it works?** section. For instance: repositoryName and branchName.
- Also if you need an extra parameter, you can add it but if you want put a value, you must to include in the webhook url as uri or get parameter. For instance, a new param could be **notificationUsers**

## Using curl

- In order to trigger this job, using curl, you must to perform an http post request with a json body.

```
curl -v -X POST "http://my_jenkins.com/easy-webhook-plugin_mykey/?gitRepositoryManagementId=bitbucket&jobId=my_awessome_jenkins_job" -d @webhook_bitbucket_payload.json \
--header "Content-Type: application/json"
```

Change **my_jenkins.com** to localhost:8080 (local testing), public ip or public domain.

# Using Bitbucket, Github or Gitlab

- Create some git repository and add the following url as webhook for push events:

  `
http://my_jenkins.com/easy-webhook-plugin_mykey/?gitRepositoryManagementId=bitbucket&jobId=my_awessome_jenkins_job&notificationUsers=jane.doe@blindspot.com
  `

Pay attention to these uri params:

- http://my_jenkins.com/easy-webhook-plugin_**mykey**
  - **mykey** is the previous key configured in Jenkins > Manage Jenkins > Configure System > Configure Easy WebHook Plugin
- **gitRepositoryManagementId**=bitbucket
  - Used to extract correct values from json webhook payload
- **jobId**=my_awessome_jenkins_job
  - Used to determinate the specific job to be launched in jenkins
- **notificationUsers**
  -  This is an extra parameter , which will be sent to your **my_awessome_jenkins_job** jenkins job

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
- Gitalb json path expressions
- Add to Jenkins official site

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





