package me.markeh.factionsplus.conf.obj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.markeh.factionsplus.FactionsPlus;
import me.markeh.factionsplus.conf.types.TLoc;
import me.markeh.factionsplus.conf.types.TMap;

public abstract class Configuration {
	
	// ------------------------------
	// Fields
	// ------------------------------
	
	private String fileName;
	private String subFolder;
	private File managingFile;
	private PrintWriter writer;
	
	// ------------------------------
	// Methods
	// ------------------------------
	
	// Required: Set the configuration file name
	public final void setName(String fileName) {
		this.fileName = fileName;
	}
	
	// Optional: The folder the file will be stored under
	public final void setSub(String subFolder) {
		this.subFolder = subFolder;
	}
	
	// Save the configuration file  
	public final Configuration save() {
		if (this.fileName == null) return this;

		if (this.managingFile == null) {
			if (this.subFolder != null) {
				File dir = new File(FactionsPlus.get().getDataFolder(), this.subFolder);
				this.managingFile = new File(dir, this.fileName + ".yml");
			} else {
				this.managingFile = new File(FactionsPlus.get().getDataFolder(), this.fileName + ".yml");
			}
		}
		
		try {
			writer = new PrintWriter(this.managingFile, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			FactionsPlus.get().logError(e);
		}
		
		List<String> sectionsCreated = new ArrayList<String>();
		
		for (Field field : this.getClass().getFields()) {			
			if (field.isAnnotationPresent(Option.class)) {
				try {
					String[] metaData = field.getAnnotation(Option.class).value();
					
					String section = metaData[0];
					
					if ( ! sectionsCreated.contains(section)) {
						sectionsCreated.add(section);
						writeSectionLine(section);
					}
					
					String name = metaData[1];
					String description = metaData[2];
					String value = this.buildValue(field, description);
					
					if (value != null) writeKeyLine(name, value, description);

				} catch(Exception e) {
					FactionsPlus.get().logError(e);
				}
			}
		}
		
		writer.close();
		
		return this;
	}
		
	// Build a field value for the configuration file 
	private final String buildValue(Field field, String description) throws IllegalArgumentException, IllegalAccessException {
		Object value = field.get(this);
		Class<?> clazz = field.getType();
		
		if (value == null) return "";
		
		if (clazz == TMap.class) {
			// TTypes handle their own raw
			return ((TMap<?, ?>) value).asRawString();
		} else if (clazz == TLoc.class) {
			return ((TLoc) value).asRawString();
		} else if (clazz == List.class) {
			// Convert the list to a YAML format.
			// We need to print to the writer for this one
			
			writer.println("    # " + description);
			writer.println("    " + field.getName() + ":");
			
			// Make this list raw, and put it on the writer 
			for(String s : this.getRawList((List<?>) value)) {
				writer.println("        - \"" + s + "\"");
			}
			
			// Make sure everything is neat
			writer.println("    ");
			
			// Return null so we don't write anything
			return null;
		} else {
			// Otherwise, assume it's a raw value (Strings, Integers, etc) 
			return value.toString();
		}
	}
	
	// Writes the start of a section line
	private final void writeSectionLine(String section) {
		writer.println("");
		writer.println(section + ":");
	}
	
	// Writes a key line
	private final void writeKeyLine(String key, String value, String comment) {
		writer.println("    # " + comment);
		writer.println("    " + key + ": \"" + value.replaceAll("\"", "\\\\\"") + "\"");
		writer.println("    ");
	}
	
	// loading / reading 
	
	public final Configuration load() {
		if (this.fileName == null) return this;

		if (this.managingFile == null) {
			if (this.subFolder != null) {
				File dir = new File(FactionsPlus.get().getDataFolder(), this.subFolder);
				
				if ( ! dir.exists()) dir.mkdir();
				
				this.managingFile = new File(dir, this.fileName + ".yml");
			} else {
				this.managingFile = new File(FactionsPlus.get().getDataFolder(), this.fileName + ".yml");
			}
		}
		
		if ( ! this.managingFile.exists()) return this;
		
		FileConfiguration yamlConfig = YamlConfiguration.loadConfiguration(this.managingFile);

		for (Field field : this.getClass().getFields()) {
			if (field.isAnnotationPresent(Option.class)) {
				
				try {
					String[] metaData = field.getAnnotation(Option.class).value();
					
					String section = metaData[0];
					String name = metaData[1];
										
					Object value = null;
					
					if (field.getType() == List.class) {
						value = yamlConfig.getList(section + "." + name);
					} else {
						value = yamlConfig.get(section + "." + name);
					}
					
					if (value == null) continue;
					
					this.loadField(field, value);
					
				} catch(Exception e) {
					FactionsPlus.get().logError(e);
				}
			}
		}
		
		return this;
	}
	
	// Load a field 
	@SuppressWarnings("unchecked")
	public final void loadField(Field field, Object value) throws IllegalArgumentException, IllegalAccessException {
		if (field.getType() == TLoc.class) {
			field.set(this, TLoc.fromRaw(value.toString()));
		} else if(field.getType() == TMap.class) {
			field.set(this, TMap.fromRaw(value.toString()));
		} else if(field.getType() == List.class) {			
			field.set(this, this.readList(field, (List<String>) value));
		} else if(field.getType() == Double.class) {
			field.set(this, Double.valueOf(value.toString()));
		} else if(field.getType() == Integer.class) {
			field.set(this, Integer.valueOf(value.toString()));
		} else if(field.getType() == Boolean.class) {
			field.set(this, Boolean.valueOf(value.toString()));
		} else if(field.getType() == String.class) {
			field.set(this, String.valueOf(value));
		} else if(field.getType() == TMap.class) {
			field.set(this, TMap.fromRaw(value.toString()));
		} else {
			// Unsafe, so try as a last resort and ignore errors 
			try {
				field.set(this, value);
			} catch(Exception e) {
				FactionsPlus.get().logError(e);
			}
		}
	}
	
	// Reads a field list and returns the list 
	private final List<Object> readList(Field field, List<String> values) throws IllegalArgumentException, IllegalAccessException {		
		List<Object> convertedList = new ArrayList<>();
		
		for(String rawValue : values) {
			if (field.getType() == TLoc.class) {
				convertedList.add(TLoc.fromRaw(rawValue));
			} else if(field.getType() == TMap.class) {
				convertedList.add(TMap.fromRaw(rawValue));
			} else if(field.getType() == Double.class) {
				convertedList.add(Double.valueOf(rawValue));
			} else if(field.getType() == Integer.class) {
				convertedList.add(Integer.valueOf(rawValue));
			} else if(field.getType() == Boolean.class) {
				convertedList.add(Boolean.valueOf(rawValue));
			} else if(field.getType() == String.class) {
				convertedList.add(String.valueOf(rawValue));
			} else if(field.getType() == TMap.class) {
				convertedList.add(TMap.fromRaw(rawValue));
			} else {
				// Unsafe, so try as a last resort and ignore errors 
				try {
					convertedList.add(rawValue);
				} catch(Exception e) {
					FactionsPlus.get().logError(e);
				}
			}
		}
		
		return null;
	}
	
	// Turns a list of objects into a list of "raw" strings
	private final List<String> getRawList(List<?> values) {
		List<String> rawList = new ArrayList<String>();
		
		for (Object s : values) {
			String toAdd;
			
			if (s instanceof TType) {
				toAdd = ((TType<?>) s).asRawString();
			} else {
				toAdd = s.toString();
			}
			
			rawList.add(toAdd.replaceAll("\"", "\\\""));
		}
		
		return rawList;
	}
}