# counter-maven-plugin
help you to count lines of a project's source code and test code

- @Parameter srcDir 
  - defaultValue = "${project.build.sourceDirectory}"
- @Parameter testDir
  - defaultValue = "${project.build.testSourceDirectory}" 

command
```
mvn counter:counter
```
<hr>
<br>

1. download the project and use command `mvn clean install`
to install the plugin to your local maven repository

2. add the below plugin to your pom
```
<plugin>
    <groupId>com.dxy.plugins</groupId>
    <artifactId>counter-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
</plugin>
```

3. use command `mvn clean install` and the console will print the line information

