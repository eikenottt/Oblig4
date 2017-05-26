package no.uib.info233.v2017.rei008_jsi014.oblig4.GUI;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.util.Enumeration;

final class DarkUI extends MetalLookAndFeel {

    private static Object key, value;
    private static String keyLower;

    static void setUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        FontUIResource f = new FontUIResource("Calibri", Font.PLAIN, 22);
        ColorUIResource bg = new ColorUIResource(70,70,70);
        ColorUIResource fg = new ColorUIResource(255,255,255);
        Color buttonBG = new Color(120,120,120);
        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            key = keys.nextElement();
            value = UIManager.get(key);
            keyLower = key.toString().toLowerCase();
            if(value != null) {
                if (value instanceof FontUIResource) UIManager.put(key, f);
                if (value instanceof ColorUIResource) {
                    if(keyLower.contains("background")) UIManager.put(key, bg);
                    if(keyLower.contains("foreground")) UIManager.put(key, fg);
                    if(keyLower.contains("button.background")) UIManager.put(key, buttonBG);
                    if(keyLower.contains("button.foreground")) UIManager.put(key, fg);
                    if(keyLower.contains("button.select")) UIManager.put(key, bg);
                    if(keyLower.contains("button.focus")) UIManager.put(key, buttonBG);
                    if(keyLower.contains("textfield.background")) UIManager.put(key, fg);
                    if(keyLower.contains("textfield.foreground")) UIManager.put(key, bg);
                    if(keyLower.contains("list.background")) UIManager.put(key, buttonBG);
                    if(keyLower.contains("optionpane.messageforeground")) UIManager.put(key, fg);
                    if(keyLower.contains("progressbar.foreground")) UIManager.put(key, ColorUIResource.RED);
                    if(keyLower.contains("menu.foreground")) UIManager.put(key, fg);
                }
            }
        }
    }

    static void changeColor(String whatToChange, Color bgcolor) {
        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            key = keys.nextElement();
            value = UIManager.get(key);
            keyLower = key.toString().toLowerCase();
            if(value != null) if (value instanceof ColorUIResource)
                if (keyLower.contains(whatToChange.toLowerCase())){
                    UIManager.put(key, bgcolor);
                    System.out.println(key);
                }
        }
    }

}
