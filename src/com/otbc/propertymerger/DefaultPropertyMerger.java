package com.otbc.propertymerger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class DefaultPropertyMerger extends AbstractPropertyMerger {

	/** stores a collection of properties added to merged file */

	private Properties mergedProperties;
	private TreeMap<String, String> propertySources;
	private boolean evaluatePlaceHolders;

	public void mergeProperties() throws BuildException {
		Vector<File> propertyFiles = getPropertyFiles();
		propertySources = new TreeMap<String, String>();
		mergedProperties = new Properties() {
			@Override
			public synchronized Enumeration<Object> keys() {
				return Collections.enumeration(new TreeSet<Object>(super
						.keySet()));
			}
		};

		for (File propertyFile : propertyFiles) {
			getProject().log(TASK_NAME + "Merging "+ propertyFile.getAbsolutePath(), Project.MSG_VERBOSE);
			Properties properties = new Properties();
			FileInputStream in = null;
			try {
				in = new FileInputStream(propertyFile);
			} catch (FileNotFoundException e2) {
				throw new BuildException(e2);
			}
			try {
				properties.load(in);
			} catch (IOException e1) {

				throw new BuildException(e1);
			}
			try {
				in.close();
			} catch (IOException e) {
				throw new BuildException(e);
			}
			for (String key : properties.stringPropertyNames()) {
				String oldFile = propertySources.get(key);
				if(oldFile!=null)
				{
					getProject().log(TASK_NAME + "replacing "+ key + "=" + mergedProperties.getProperty( key)+ " from " + oldFile + " with " +key+"=" +properties.getProperty(key) + " from "+ propertyFile.getAbsolutePath() , Project.MSG_DEBUG);
				}
				propertySources.put(key, propertyFile.getAbsolutePath());
			}
			mergedProperties.putAll(properties);
		}
			
			if(evaluatePlaceHolders)
			{
				evaluatePlaceHolders();
			}
	}
	
	public boolean isEvaluatePlaceHolders() {
		return evaluatePlaceHolders;
	}

	public void setEvaluatePlaceHolders(boolean evaluatePlaceHolders) {
		this.evaluatePlaceHolders = evaluatePlaceHolders;
	}

	private void evaluatePlaceHolders() {
		Set<String> keys = mergedProperties.stringPropertyNames();
		
		for(String key: keys)
		{
			String value = mergedProperties.getProperty(key);
			PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");
			String replacePlaceholders = propertyPlaceholderHelper.replacePlaceholders(value, mergedProperties);
			mergedProperties.setProperty(key,replacePlaceholders);
			if(!replacePlaceholders.equals(value))
			{
				getProject().log( TASK_NAME + "Replaced placeholders: " + key+"="+value +" -> " + key + "=" + replacePlaceholders, Project.MSG_VERBOSE);
			}
			


		}

	}

	public void writeFile(String comments) throws BuildException {
		OutputStream output = null;
		
			
		try {
			output = new FileOutputStream(getDestFile());
			mergedProperties.store(output, comments);
			output.flush();
			String sources="Property sources:\n";
			for(String key: propertySources.keySet())
			{
				sources+=key + " source: " + propertySources.get(key) + "\n";
			}
			Properties propertiesSource = new Properties();
			propertiesSource.store(output, sources);
			
			output.close();
		} catch (FileNotFoundException e) {
			throw new BuildException(e);
		} catch (IOException e) {
			throw new BuildException(e);

		}
	}
}