/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planifticateur.enumeration;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

/**
 *
 * @author Antoine
 */
public class ActivityColor {
    
    private static ActivityColor instance = new ActivityColor();
    
    private Random randomGenerator;
    
    private ArrayList<Color> availableColors;
    private Hashtable<String, Color> mappedColors;
    
    private ActivityColor() {
        this.randomGenerator = new Random();
        this.availableColors = new ArrayList<Color>();
        this.mappedColors = new Hashtable<String, Color>();
        
        this.availableColors.add(new Color(242, 234, 93));
        this.availableColors.add(new Color(82, 221, 254));
        this.availableColors.add(new Color(235, 252, 174));
        this.availableColors.add(new Color(224, 168, 208));
        this.availableColors.add(new Color(124, 191, 105));
        this.availableColors.add(new Color(162, 182, 242));
        this.availableColors.add(new Color(0, 242, 226));
        this.availableColors.add(new Color(187, 160, 242));
        this.availableColors.add(new Color(242, 200, 138));
        this.availableColors.add(new Color(255, 248, 100));
    }
    
    public static ActivityColor getInstance() {
        return instance;
    }
    
    public Color getColor(String type) {
        
        Color color = this.mappedColors.get(type);
        
        if (color == null && this.availableColors.size() > 0) {
            int index = randomGenerator.nextInt(this.availableColors.size());
            color = this.availableColors.get(index);
            this.mappedColors.put(type, color);
            this.availableColors.remove(index);
        } else if (this.availableColors.size() == 0) {
            color = new Color(252, 255, 252);
        }
        
        return (color);
    }
    
}
