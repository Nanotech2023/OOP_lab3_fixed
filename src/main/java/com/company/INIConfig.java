package com.company;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
//TODO import org.apache.commons.lang.StringUtils;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class INIConfig{
    private final Map<String, INISection> sections_;

    public INIConfig() {
        sections_ = new HashMap<String, INISection>();
    }

    public INISection getSection(String sectionName) {
    	if (!sections_.containsKey(sectionName))
    		throw new IndexOutOfBoundsException(String.format("No such section: [%s]", sectionName));
        return sections_.get(sectionName);
    }

    // FIXME: no rename, now it's really read file
    public void readFile(String fileName) throws DataFormatException, IOException {
        BufferedReader file = null;
        StringBuilder sb;
        try {
            file = new BufferedReader(new FileReader(fileName));
            sb = new StringBuilder();
            while (true) {
                String line = file.readLine();
                if (line == null)
                    break;
                sb.append(line);
                sb.append("\n");
            }
        } finally {
            file.close();
        }
        parse(sb.toString());
    }

    private void parse(String iniToParse) throws DataFormatException {
        int pos = 0;
        while (true) {
            int posNext = iniToParse.indexOf("\n", pos);
            if (posNext == -1)
                break;
            String line = iniToParse.substring(pos, posNext);
            if (line.isEmpty() || line.trim().charAt(0) == ';') {
                pos = posNext + 1;
                continue;
            }
            if (line.charAt(0) == '[') {
                boolean ok = line.contains("]");
                for (int i = line.indexOf(']') + 1; i < line.length(); ++i)
                    if (line.charAt(i) == ';')
                        break;
                    else if (line.charAt(i) != ' ') {
                        ok = false;
                        break;
                    }
                if (!ok)
                    throw new DataFormatException(String.format(
                            "Error while reading ini file: label \"%s\" is broken",
                            line));
                String name = line.substring(1, line.indexOf(']')); // TODO String name = StringUtils.substringBetween(line, "[", "]");
                if (name.contains(" "))
                    throw new DataFormatException(String.format(
                            "Error while reading ini file: label \"%s\" contain spaces",
                            name));
                int posFinish = iniToParse.indexOf("\n[", posNext) != -1 ?
                        iniToParse.indexOf("\n[", posNext) :
                        iniToParse.length();
                addSection(new INISection(name, iniToParse.substring(posNext, posFinish)));
                posNext = posFinish;
            }
            pos = posNext + 1;
        }
    }

    public INISection addSection(String sectionName) throws DataFormatException {
        sections_.put(sectionName, new INISection(sectionName));
        return sections_.get(sectionName);
    }

    public INISection addSection(INISection section) {
        sections_.put(section.getName(), section);
        return sections_.get(section.getName());
    }

    public void removeSection(String sectionName) {
        sections_.remove(sectionName);
    }

    public List<INISection> getSections() {
    	return Collections.unmodifiableList(new ArrayList<INISection>(sections_.values()));
    }

    // FIXME: for what? - want it
    public int size() {
        return sections_.size();
    }

    public void clear() {
        sections_.clear();
    }

    public void writeToFile(String fileName) throws IOException {
        BufferedWriter file = new BufferedWriter(new FileWriter(fileName));
        int i = 0;
        for (INISection section: sections_.values()) {
            file.write(String.format("[%s]\n", section.getName()));
            for (String prop : section.keys())
                file.write(String.format("%s = %s\n", prop, section.getString(prop)));
            if (++i < sections_.size())
                file.write("\n");
        }
        file.close();
    }

    // FIXME: consider using INI-format, not a home-made custom one - want it so
    public String toString() {
        String ans = "[[[\n";
        int i = 0;
        for (String label : sections_.keySet()) {
            ans += label + ": {";
            int j = 0;
            INISection section = sections_.get(label);
            for (String entry : section.keys())
                ans += String.format("%s=%s%s",
                                     entry,
                                     section.getString(entry),
                                     (j++ == section.size() - 1 ? "}" : ", "));
            if (section.size() == 0)
                ans += "}";
            ans += (i++ == sections_.size() - 1 ? "\n]]]" : ";\n");
        }
        if (sections_.size() == 0)
            ans += "]]]";
        return ans;
    }
    String getContent() {
    	StringBuilder sb = new StringBuilder();
    	int i = 0;
        for (INISection section: sections_.values()) {
            sb.append(String.format("[%s]\n", section.getName()));
            for (String prop : section.keys())
                sb.append(String.format("%s = %s\n", prop, section.getString(prop)));
            if (++i < sections_.size())
                sb.append("\n");
        }
        return sb.toString();
    }
}
