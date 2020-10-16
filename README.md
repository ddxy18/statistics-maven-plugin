# statistics-maven-plugin
Help you to count lines of a project's source codes.

## Feature
- lines statistics(including total lines, comment lines and code lines)
- calculate how many java files

## Command
```
mvn statistics:statistics
```
<hr>
<br>

## Getting started
1. Download the project `https://github.com/ddxy18/counter-maven-plugin.git`.
2. Use command `mvn clean install` to install the plugin to your local
 maven repository.
3. Add the below code to your pom.xml file
```
<plugin>
    <groupId>com.dxy.plugins</groupId>
    <artifactId>statistics-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
</plugin>
```

