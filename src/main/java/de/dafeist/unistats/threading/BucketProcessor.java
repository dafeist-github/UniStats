package de.dafeist.unistats.threading;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import de.dafeist.unistats.parse.StringUtils;

public class BucketProcessor {
	
	  private File directory;

	  public BucketProcessor(File directory) {
	    this.directory = directory;
	  }
	  
	  public Map<String, List<File>> bucket() {
		  Map<String, List<File>> buckets = new HashMap<String, List<File>>();
		  
		  for(File file : directory.listFiles()) {
			  
			  try {
			  String date = StringUtils.parseNumDateFromLog(file.getName());
			  
			  if(!buckets.containsKey(date)) {
				  ArrayList<File> list = new ArrayList<File>();
				  
				  list.add(file);
				  
				  buckets.put(date, list);
				  
			  } else {
				  buckets.get(date).add(file);
			  }
			  
			  } catch(Exception e) {
				  //ignore
			  }
			  
		  }
		  
		  return buckets;
	  }
	  
		  
}
