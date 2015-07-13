package com.otbc.propertymerger;

import java.io.File;
import java.util.Properties;
import java.util.Vector;

import org.apache.tools.ant.types.FileSet;

public interface PropertyMerger  {
	public void addFileset(FileSet fileSet);
	public void setBaseFile(File file);
	public void setOverrideFile(File file);
	public void setDestFile(File file);
	public void execute();
	void mergeProperties();
	void writeFile(String comment);
}
