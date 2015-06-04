
<!-- TODO: http://cineca.github.io/dspace-cris/ -->


solr.authority.server = .... solr/authority
το περιγράφει καλύτερα στο dspace manual, σελ 306
αυτό είναι ένα συγκεκριμένο service (το authority). Μπορείς αντίστοιχα να φτιάξεις και άλλα


## TODO

> LOG: /home/tkout/Dropbox/2014_ECVET-STEP/Tasos Notes/tasos-todo.md


* 2015-05-26 - create the ecvet-step module for dspace


* LATER
    - EPIC identifiers


## DONE

* 2015-05-27 - add a "Lookup & Add" button in the input form
    - 2015-05-28 = did this with the LC name lookup (Library of Congress)
* 2015-05-26 - add a solr-based related field
    - 2015-05-28 ~ did this, but the SOLR indexer automagically works (only) with ORCID


## Useful

### Input form

> http://cadair.aber.ac.uk/dspace/bitstream/handle/2160/639/Module+-+Metadata+Input+Customisation.pdf

* JSPUI, extend through org.dspace.app.webui.submit.step.*
    - /home/tkout/dev/rulo-repo/dspace-jspui/src/main/java/org/dspace/app/webui/submit/JSPStep.java
* XMLUI, extend through org.dspace.app.xmlui.submission.AbstractSubmissionStep
    - /home/tkout/dev/rulo-repo/dspace-xmlui/src/main/java/org/dspace/app/xmlui/aspect/submission/submit/DescribeStep.java

* **StartSubmissionLookupStep**
StartSubmissionLookupStep is a new submission step, available **since DSpace 4.0** contributed by **CINECA**, that
extends the basic SelectCollectionStep allowing the user to _search or load metadata from an external service_ (
arxiv online, bibtex file, etc.) and **prefill the submission form**. Thanks to the EKT works it is underpinned by the
Biblio Transformation Engine ( https://github.com/EKT/Biblio-Transformation-Engine) framework.

> manual, pg. 186

* ωραίο screencast (παράδειγμα): http://screencast.com/t/NTkxY2Nj


* **Choice Authority Plugin**


You configure a metadata field for *choice management* by selecting a choice authority plugin for it. **This plugin serves as a source of choices.** Whenever the user is entering a metadata value for that field, e.g. in the interactive submission UI or when editing the Item's metadata, **the UI consults that choice authority plugin to get a list of available choices to present to the user**. This list may or may not be affected by the current (or proposed) value of the field.

> https://wiki.duraspace.org/display/DSPACE/Authority+Control+of+Metadata+Values#AuthorityControlofMetadataValues-SourceofChoices

* /home/tkout/dev/rulo-repo/dspace-api/src/main/java/org/dspace/content/authority/LCNameAuthority.java

```
public class LCNameAuthority implements ChoiceAuthority

    // έχει μια εύκολη μέθοδο (1-2 γραμμές)
    public Choices getBestMatch(String field, String text, int collection, String locale)

    // κι άλλη μία
    public Choices getMatches(String field, String text, int collection, int start, int limit, String locale)


```

Επίσης υπάρχει η `SolrAuthority`: 
/home/tkout/dev/rulo-repo/dspace-api/src/main/java/org/dspace/content/authority/SolrAuthority.java


* Library of Congress Personal names authority: **LCNameAuthority**
* Sherpa & Romeo Project, publisher names authority: **SRPublisherAuthority**
* Sherpa & Romeo Project, journal titles authority: **SRJournalTitle**
* DSpace Input Form XML based controlled list: **DCInputAuthority**


* **SWORD**

SWORD (Simple Web-service Offering Repository Deposit) is a protocol that allows the **remote** deposit of items
into repositories. DSpace implements the SWORD protocol via the 'sword' web application. The version of
SWORD v1 currently supported by DSpace is 1.3. The specification and further information can be found at http:
//swordapp.org .




### Identifier service

The Identifier Service manages the generation, reservation and registration of identifiers within DSpace. You can configure it using the config file located in `[dspace]/config/spring/api/identifier-service.xml`. In the file you should already find the code to configure DSpace to register DOIs. Just read the comments and remove the comment signs around the two appropriate beans.


### DSpace source code 



* RR/config - φτιάχνεται αυτόματα από το `ant init-configs`
    - το οποίο τρέχει μέσα στο `RR/dspace/target/dspace-installer`
    - το οποίο έχει φτιαχτεί όμως από το **`MVN`**
        + από τα αρχεία που βρίσκονται μέσα στο `RR/dspace/`



* `[RR]` - The DSpace Installation directory
* `[RR]/dspace/` - The DSpace Assembly project within the DSpace source code
* `[RR]/dspace/target/dspace-installer/` - The directory where MVN builds the installation package for DSpace
* `/usr/share/tomcat7/webapps/` 


* Quick Restart (Just restarts the web server after configuration changes*)
```
systemctl restart tomcat7.service
```

* Quick Build: (Quick build after smaller, usually JSP based or XMLUI-Theme based, changes)
```
# RR is the base directory of the rulo repository
cd [RR]/dspace/
# recompile all DSpace code and rebuild the DSpace installer directory
mvn package
# move to the installer directory and run ANT
cd target/dspace-installer
ant update
```

#### Parameters

Οι παράμετροι βασικά μπαίνουν στο `dspace.cfg`, αλλά μπορείς να τις πειράξεις με settings του mvn (π.χ. στο `~/.m2/settings.xml`

> http://atmire.com/website/?q=content/managing-dspace-18-configuration-maven

In order to configure parameters in this manner, you have to make sure that they match the appropriate `${variable_name}` in **dspace.cfg**. The 1.8 dspace.cfg file is slightly inconsistent in this manner. By default, the variable dspace.dir already points to ${default.dspace.dir}. However, dspace.baseUrl is still defined as http://localhost:8080. In order for the above to work, with regards to the baseUrl property, it should read dspace.baseUrl = ${dspace.baseUrl}.

As a variation on this approach, **DSpace 3.0 features a `build.properties` file** that also contains a subset of variables from the larger dspace.cfg file. So instead of putting your variables, like described above, directly in a pom.xml file, they are now separated out in this new build.properties file. This makes them easier to find and adjust. The key thing to understand here is that both the values from maven pom files and the build.properties file will be injected in dspace.cfg at compile time and that the actual values in dspace.cfg are being used at runtime.






### Maven

* MVN commands
    - exclude modules, eg `mvn package -P -dspace-jspui,-dspace-lni`
    - existing modules are:
        + dspace-jspui (JSPUI)
        + dspace-lni (LNI)
        + dspace-oai (OAI-PMH)
        + dspace-sword (SWORD v.1)
        + dspace-swordv2 (SWORD v.2)
        + dspace-xmlui (XMLUI)


####  MVN configuration

> http://maven.apache.org/guides/mini/guide-configuring-maven.htm

Maven configuration occurs at 3 levels:

* Project - most static configuration occurs in pom.xml
* Installation - this is configuration added once for a Maven installation
* User - this is configuration specific to a particular user `~/.m2/settings.xml`
    - http://maven.apache.org/ref/3.3.3/maven-settings/settings.html

Build profiles:
http://maven.apache.org/guides/introduction/introduction-to-profiles.html

```
mvn groupId:artifactId:goal -P profile-1,profile-2

mvn package -P TK-rulo-repo

mvn help:active-profiles -PTK-rulo-repo,TK-dspace 

```

http://www.tutorialspoint.com/maven/maven_build_profiles.htm

FIXME: check if offline mode is enforced in ~/.m2/settings.xml



#### Mavel "help" plugin
systemctl restart postgresql.service 
systemctl restart tomcat7.service


mvn help:describe -Dplugin=org.apache.maven.plugins:maven-help-plugin

mvn help:active-profiles -PTK-rulo-repo,TK-dspace 





## Local 'dev' environment & workflow

### Step 1. Start tomcat and postgress
```
systemctl restart postgresql.service 
systemctl restart tomcat7.service
```

### Step 2. Open the local installation's homepage

Fire up your browser and check both DSpace's UIs:

* http://localhost:8080/xmlui
* http://localhost:8080/jspui

Login with `repo@ecvet-step.eu` / `3cv3t` and then open a _backend_ page, e.g http://localhost:8080/xmlui/handle/123456789/4/submit


### Step 3. Make some changes and check the results

Start Sublime or your favourite text editor.

```
cd ~/dev/rulo-repo/
git status
subl3 /home/tkout/Sublime/rulo-repo.sublime-project
```

Edit the `~/dev/rulo-repo/dspace/config/input-forms.xml` file

And then run **`maven`** and **`ant`** to rebuild the code and deploy it. 

*Note:* Expect mvn to set you back approximatelly 120 seconds from your life. Ant is much more polite.

```
mvn package
ant -f ./dspace/target/dspace-installer/build.xml update
```

Refresh the form submission page to see your changes: http://localhost:8080/xmlui/handle/123456789/4/submit


### Other useful

* http://localhost:8080/manager/html  (dev / d3v)


### Mirage 2

```
sudo gem install sass -v 3.3.14 --no-user-install
sudo gem install compass -v 1.0.1 --no-user-install

mvn package -Dmirage2.on=true -Dmirage2.deps.included=false
```

Then edit your `xmlui.conf` to configure accordingly



# Implementation notes


### TODO

- [ ]  Setup handles (EPIC?)

### DSpace setup for the impatient

https://wiki.duraspace.org/display/DSDOC4x/Installing+DSpace



### Development

* Follow instructions from https://www.youtube.com/watch?t=603&v=mrLl1qPsy6I to setup your DSpace development environment, which in short will:
    - checkout latest code of DSpace from the [github repository](https://github.com/EcvetStep/rulo-repo)
    - setup intelliJ idea (ii) accordingly
    - use ii to build DSpace (with maven)

* More instructions: 
    - https://wiki.duraspace.org/display/DSPACE/IDE+Integration+-+DSpace+and+IDEA
    - 
    
* Setup apache tomcat

    - http://localhost:8080/manager/html
    - tomcat user / pass: dev / d3v
    
* Setup postgresql
    - create a user for dspace (man creatuser)
    - postgres user / pass: dspace / dspace
    - create the database: dspace

* Configure Ant build (from ii) 
    - will copy compiled files 
    - and take care of database initialisation 
    
* Create a DSpace administrator account:

  [dspace]/bin/dspace create-administrator
  administrator user/pass: repo@ecvet-step.eu / 3cv3t
  
  here you'll find all command-line option: https://wiki.duraspace.org/display/DSDOC6x/Command+Line+Operations

* Backup dspace database

> page 70 DSpace 5 user manual

  pg_dump -U [database-user] -f [backup-file-location] [database-name]
  
* OR create AIP exports
  
  [dspace]/bin/dspace packager -d -t AIP -e <eperson> -i <handle> <file-path>
  e.g.
  [dspace]/bin/dspace packager -d -t AIP -e admin@myu.edu -i 4321/4567 aip4567.zip
  
  for more items:
  [dspace]/bin/dspace packager -d -a -t AIP -e <eperson> -i <handle> <file-path>
  e.g.
  [dspace]/bin/dspace packager -d -a -t AIP -e admin@myu.edu -i 4321/4567 aip4567.zip
  
  to export the whole size:
  [dspace]/bin/dspace packager -d -a -t AIP -e admin@myu.edu -i 4321/0 sitewide-aip.zip
  
  
 ### DEV NOTES
 
 * config: created by ANT `init_configs` target