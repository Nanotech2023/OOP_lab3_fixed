package com.company;

import java.util.Map;
import java.util.HashMap;


public class INISection {
    private final String name_;
    private final Map<String, String> properties_ = new HashMap<String, String>();

    public INISection(String name) throws DataFormatException {
        if (name.contains(" "))
            throw new DataFormatException(String.format(
                    "Section name can't contain spaces in name: \"%s\"",
                    name));
        name_ = name;
    }

    public INISection(String name, String sectionToParse) throws DataFormatException {
        this(name);
        parse(sectionToParse);
    }

    public String getName() {
        return name_;
    }

    public void parse(String sectionToParse) throws DataFormatException {
        if (!sectionToParse.endsWith("\n"))
            sectionToParse += "\n";
        int pos = 0;
        while (true) {
            int posNext = sectionToParse.indexOf("\n", pos);
            if (posNext == -1)
                break;
            String line = sectionToParse.substring(pos, posNext);

            if (line.isEmpty() || Utils.deleteSpaces(line).charAt(0) == ';') {
                pos = posNext + 1;
                continue;
            }

            line = String.join(" ", line.split("\\s+"));
            String[] strings;
            if (line.contains(";"))
                strings = line.substring(0, line.indexOf(';')).split("=");
            else
                strings = line.split("=");
            // "".split("=", 2);
            if (strings.length != 2)
                throw new DataFormatException(String.format(
                        "Error while reading ini file at label \"%s\"",
                        name_));
            if (Utils.deleteSpaces(strings[0]).contains(" "))
                throw new DataFormatException(String.format(
                        "Error while reading ini file at label \"%s\", parameter \"%s\" has space(s) in name",
                        strings[0],
                        name_));
            properties_.put(Utils.deleteSpaces(strings[0]).split("\\s+")[0],
                            Utils.deleteSpaces(strings[1]));
            pos = posNext;
        }
    }

    public String getString(String propertyName) throws IndexOutOfBoundsException {
    	if (!properties_.containsKey(propertyName))
            throw new IndexOutOfBoundsException(String.format("No \"%s\" entry found in \"%s\" label", propertyName, name_));
		return properties_.get(propertyName);
    }

    public Double getDouble(String propertyName) throws IndexOutOfBoundsException, NumberFormatException {
    	return new Double(getString(propertyName));
    }
    
    public Integer getInteger(String propertyName) throws IndexOutOfBoundsException, NumberFormatException {
    	return new Integer(getString(propertyName));
    }

    public String[] keys() {
        String[] ans = new String[properties_.size()];
        int i = 0;
        for (String propName : properties_.keySet())
            ans[i++] = propName;
        return ans;
    }

    public int size() {
        return properties_.size();
    }

    public void clear() {
        properties_.clear();
    }

    public void addProperty(String propertyName, String value) throws DataFormatException {
        if (propertyName.contains(" "))
            throw new DataFormatException(String.format(
                    "Property cannot contain spaces in name: \"%s\"",
                    propertyName));
        properties_.put(propertyName, value);
    }

    public void delProperty(String propertyName) {
        properties_.remove(propertyName);
    }
}
