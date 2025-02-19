# TrustSource-Ant-Plugin
TrustSource (https://www.trustsource.io) is a legal resolver and OpenChain compliant workflow engine that allows you to manage your open source dependencies, provide legal compliance and create bill of materials.

The Ant plugin provides TrustSource integration with Apache Ant/Ivy. It uses the [TrustSource Java client](https://github.com/eacg-gmbh/ecs-java-client/) to transfer Ivy based dependency information to TrustSource-Server via its REST-API. 

> [!WARNING]
> **PLEASE NOTE:** We stopped further development on this client. You may still use it, but it will only receive bug fixes. Starting Q4/2024 we decided to focus all efforts on developing [ts-scan](https://github.com/trustsource/ts-scan), which also covers all capabilities of this solution. 

# Quick Setup
It is pretty simple to include the TrustSource scan into your existing Ant projects to scan your Ivy-dependencies. First of all you have to download the latest version of the trustsource-ant-plugin and copy the jar file to your Ant lib directory (ANT_HOME/lib). After that you will be able to declare a new namespace in your build.xml as seen below.

```xml
<project xmlns:trustsource="antlib:de.eacg.ecs.plugin" ...>
    ...
</project>
```

Now you can define a new target for the dependency-scan task or add it to an existing target. You just have to make sure that the Ivy task retrieve is executed before the dependency-scan.

```xml
<target name="trustsource-scan" description="Scans all Ivy-dependencies and uploads the result to TrustSource" depends="resolve">
    <trustsource:dependency-scan apikey="YOUR API KEY" username="YOUR LOGIN NAME (email)" projectname="YOUR PROJECT NAME"/>
</target>
```

Finally you can execute the task to scan your project and upload the result to TrustSource: ``ant trustsource-scan``

PLEASE NOTE: Starting from v2 of TrustSource, the username will not be required anmore. However, you are not required to remove it from older installations as we will tolerate but not use the parameter anymore.

# Advanced Setup
## Use properties file for credentials
The Ant plugin is able to read the TrustSource access credentials (userName, apiKey) from a properties file in JSON format. This allows sharing of the TrustSource credentials with other projects and also with other build tools.

**properties file ‘trustsource-settings.json’ in your home directory:**

```json
{
    "userName": "YOUR LOGIN NAME (email)",
    "apiKey": "YOUR API KEY"
}
```

Adjust the configuration of the Ant plugin by specifying an additional credentials attribute for the dependency-scan task. In the attribute define the path to your properties file and the Ant plugin will then read the properties from this file. The tilde, ‘~’, represents your user home directory, the dot, ‘.’ stands for the current working directory and forward slashes ‘/’ are used to separate sub-directories.

**The modified Build-file:**

```xml
<trustsource:dependency-scan credentials="~/trustsource-settings.json" projectname="YOUR PROJECT NAME"/>
```

# Reference
All configuration properties

* *credentials* (Optional): Path to a JSON file which holds ‘userName’ and ‘apiKey’ credentials. Use ‘~’ as shortcut to your home directory and ‘.’ for the current working directory. A forward slash ‘/’ separates directories. *Default:* apiKey and userName are expected to be set in the task attributes

* *apiKey* (Required, if not specified in credentials file): This key permits the access to TrustSource. Create or retrieve the key from your profile settings of the TrustSource web application.
        
* *userName* (Required before v2.0, if not specified in credentials file): has been used to identify the initiator of the data transfer.
    
* *projectName* (Required): For which project is the dependency information transferred.
    
* *skip* (Optional): Set to true to disable the trustsource-ant-plugin. *Default:* false
    
* *skipTransfer* (Optional): Set to true to execute a dry run and do not transfer anything. *Default:* false

* *verbose* (Optional): Increases the output produced by the plugin to get additional information. *Default:* false

* *proxyUrl* (Optional): Url of the proxy server if a proxy should be used.

* *proxyPort* (Optional): Port of the proxy server. *Default:* 8080

* *proxyUser* (Optional): Username if proxy server requires authentication.

* *proxyPass* (Optional): Password if proxy server requires authentication.

# How to obtain a TrustSource API Key
Every TrustSource account allows you to provide API keys. After login select the gear wheel on the upper right side and select "Scanner & API" from the menu. Then select API-Key and generate the key. Paste it into your local settings file and run your scan. Be compliant ;-)

# How to obtain Support
Write us an email to support@trustsurce.io. We will be happy to hear from you. Or visit our knowledgebase at https://support.trustsource.io for more insights and tutorials.
