package com.company;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws DataFormatException, IOException {
        INIConfig ini = new INIConfig();
        String input = "test.ini";
        if (args.length > 0)
            input = args[0];

        ini.readFile(input);
        System.out.println(ini.getContent());
        INISection values = ini.getSection("VALUES");
        String[] dataType = new String[]{"int", "double", "str", "none"};
        for (String s : dataType) {
            System.out.print(String.format("Getting %12s as String: ", s));
            try {
                System.out.print(values.getString(s));
            } catch (Exception ex) {
                System.out.print("(Error)");
            }
            System.out.print(", as Int: ");
            try {
                System.out.print(values.getInteger(s));
            } catch (Exception ex) {
                System.out.print("(Error)");
            }
            System.out.print(", as Double: ");
            try {
                System.out.print(values.getDouble(s));
            } catch (Exception ex) {
                System.out.print("(Error)");
            }
            System.out.println(".");
        }
        for (INISection section : ini.getSections()) {
            if (section.size() == 0)
                ini.removeSection(section.getName());
        }

        // Integer.parseInt()

        try {
            int i = ini.getSection("Q").getInteger("x");
        } catch (Exception e) {
            e.printStackTrace();
        }

        INISection section = ini.addSection("Facts");
        section.addProperty("Light", "Kira");
        section.addProperty("Life", "isDance");
        section = ini.addSection("Relationships");
        section.addProperty("Kira", "Misa");
        ini.writeToFile("output.ini");
    }
}
