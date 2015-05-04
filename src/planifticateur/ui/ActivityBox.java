/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planifticateur.ui;

import Utils.Utils;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import planifticateur.domain.Activity;
import planifticateur.enumeration.ScaleMacro;

/**
 *
 * @author floriandewulf
 */
public class    ActivityBox implements Comparable {

    Activity    activity;
    int         row = 1;
    
    public ActivityBox(Activity _activity) {
        activity = _activity;
    }
    
    void        drawInGrid(Graphics g, int offset) {
        g.setColor(activity.getColor());
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        
        String text = activity.getCode() + '-' + activity.getSection() + '-' + activity.getName();
        
        int startX = ScaleMacro.SCALE_WIDTH_DAY + (int)(((activity.getStartHour() - (double)ScaleMacro.HOUR_BEGIN) * 60.0 / (double)ScaleMacro.SCALEMINUTE) * (double)ScaleMacro.SCALEDIVIDER) + 1;
        int startY = offset + ((row - 1) * ScaleMacro.SCALE_HEIGHT_ACTIVITY) + ((row - 1) * ScaleMacro.SCALE_LINE_INTERACTIVITY);
        int activityWidth = (int)((activity.getDuration() * 60.0 / (double)ScaleMacro.SCALEMINUTE) * (double)ScaleMacro.SCALEDIVIDER);
        
        int nbLetter = (activityWidth - 20) / ScaleMacro.SCALE_WIDTH_LETTER;
        
        if (nbLetter > text.length()) {
            nbLetter = text.length();
        }
        
        g.fillRect(/*Position x*/   ScaleMacro.SCALE_WIDTH_DAY + (int)(((activity.getStartHour() - (double)ScaleMacro.HOUR_BEGIN) * 60.0 / (double)ScaleMacro.SCALEMINUTE) * (double)ScaleMacro.SCALEDIVIDER) + 2,
                   /*Position y*/   offset + ((row - 1) * ScaleMacro.SCALE_HEIGHT_ACTIVITY) + ((row - 1) * ScaleMacro.SCALE_LINE_INTERACTIVITY),
                   /*Largeur*/      (int)((activity.getDuration() * 60.0 / (double)ScaleMacro.SCALEMINUTE) * (double)ScaleMacro.SCALEDIVIDER) - ScaleMacro.SCALE_LINE_INTERSCALE,
                   /*Hauteur*/      ScaleMacro.SCALE_HEIGHT_ACTIVITY);
        
        g.setColor(Color.BLACK);
        g.drawString(text.substring(0, nbLetter), startX + 10, startY + ScaleMacro.SCALE_HEIGHT_DAY - 10);
    }

    void        drawInList(Graphics g, int offset, int size) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        
        g.setColor(activity.getColor());
        g.fillRect(0, offset, size, ScaleMacro.SCALE_HEIGHT_ACTIVITY);
        g.setColor(Color.BLACK);
        g.drawString(activity.getCode() + '-' + activity.getSection() + ' ' + activity.getName(), 10, offset + ScaleMacro.SCALE_HEIGHT_DAY - 10);
    }

    
    @Override
    public int compareTo(Object activityBox) {
        return (this.row - ((ActivityBox)activityBox).getRow()); 
    }

    public int  getRow() {
        return  row;
    }

    public Activity getActivity() {
        return activity;
    }

    void setRow(int _row) {
        row = _row;
    }
}
