RULO Repository
===============


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