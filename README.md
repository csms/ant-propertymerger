# propertymerger
Ant lib to merge environment specific properties with release specific properties.

# Usage
```
<project name="MyProject" default="dist" basedir=".">

<taskdef resource="com/alfalaval/ecom/propertymerger.properties"/>
	<target name="all">
    <propertymerger destfile="destfile.properties" basefile="resources/basefile.properties" 
    evaluateplaceholders="true">
<fileset dir="resources/fileset">
  <include name="**/*.properties"/>
</fileset>
	</propertymerger>
  </target>
</project>
```

# v0.5
- Handles placeholders(nested properties) 
  - example
    - placeholder=placeholder.value 
    - evaluated=${placeholder} 
      - Evaluates to evaluated=placeholder.value
  - evaluated.nested=${${placeholder}.nested} is also evaluated fully.
- A base file can either be overriden by the content of an override file and/or by files specified in one or more filesets.
- Property files from filesets will be evaluated alphabetically. Properties will be overridden in preceeding order.