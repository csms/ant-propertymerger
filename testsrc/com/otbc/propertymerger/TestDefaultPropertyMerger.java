package com.otbc.propertymerger;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.otbc.propertymerger.DefaultPropertyMerger;
import com.otbc.propertymerger.PropertyMerger;

public class TestDefaultPropertyMerger {
	String pathname = "";

	@Before
	public void setup() throws IOException {
		String current = new java.io.File(".").getCanonicalPath();
		pathname = current + File.separator + "resources" + File.separator;
	}

	@Test
	public void testBaseFileAndDestFile() throws IOException {

		DefaultPropertyMerger tested = new DefaultPropertyMerger();

		tested.setBaseFile(new File(pathname + "basefile.properties"));
		tested.setOverrideFile(new File(pathname + "overridefile.properties"));
		File destFile = new File(pathname + "destfile.properties");
		tested.setDestFile(destFile);
		Project p = new Project();		
		tested.setProject(p);
		tested.execute();

		byte[] encoded = Files.readAllBytes(destFile.toPath());
		String content = new String(encoded, Charset.defaultCharset());
		assertTrue(content.contains("override.true=\"newvalue\""));
		assertFalse(content.contains("override.true=\"oldvalue\""));

	}

	@After
	public void teardown() throws IOException {
		File file = new File(pathname + "destfile.properties");
	//	Files.delete(file.toPath());
	}

	@Test
	public void testBaseFileAndOverrideFileAndFileSet() throws IOException {
		DefaultPropertyMerger tested = new DefaultPropertyMerger();
		tested.setBaseFile(new File(pathname + "basefile.properties"));
		tested.setOverrideFile(new File(pathname + "overridefile.properties"));
		File destFile = new File(pathname + "destfile.properties");
		tested.setDestFile(destFile);
		FileSet fileSet = new FileSet();
		fileSet.setDir(new File(pathname+ File.separator+"fileset"));
		fileSet.setIncludes("*.properties");
		Project p = new Project();
		fileSet.setProject(p);
		tested.addFileset(fileSet );
		tested.setProject(p);
	
		tested.execute();

		
		byte[] encoded = Files.readAllBytes(destFile.toPath());
		String content = new String(encoded, Charset.defaultCharset());
		assertTrue(content,content.contains("override.true=2.0"));
		assertTrue(content,content.contains("override.false=\"oldvalue\""));
		assertFalse(content,content.contains("override.true=\"oldvalue\""));

	}
	
	@Test
	public void testNoDestFileSet() throws IOException {
		DefaultPropertyMerger tested = new DefaultPropertyMerger();
		
		try
		{
			Project p = new Project();
			tested.setProject(p);	
			tested.execute();
		}catch(BuildException be)
		{
			assertEquals("destfile must be set",be.getMessage());
		}
	}
	
	@Test
	public void testResolvePlaceHolders() throws IOException {
		DefaultPropertyMerger tested = new DefaultPropertyMerger();
		tested.setBaseFile(new File(pathname + "basefile.properties"));
		tested.setOverrideFile(new File(pathname + "overridefile.properties"));
		File destFile = new File(pathname + "destfile.properties");
		tested.setDestFile(destFile);
		FileSet fileSet = new FileSet();
		fileSet.setDir(new File(pathname+ File.separator+"fileset"));
		fileSet.setIncludes("*.properties");
		Project p = new Project();
		fileSet.setProject(p);
		tested.addFileset(fileSet );
		tested.setProject(p);
		tested.setEvaluatePlaceHolders(true);
		tested.execute();
		
		byte[] encoded = Files.readAllBytes(destFile.toPath());
		String content = new String(encoded, Charset.defaultCharset());
		assertTrue(content,content.contains("evaluated.nested=placeholder.value.nested.value"));
		assertTrue(content,content.contains("evaluated=placeholder.value"));
		


	}
}



