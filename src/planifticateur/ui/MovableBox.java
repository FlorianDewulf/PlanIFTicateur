/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planifticateur.ui;

import java.awt.Point;
import java.util.ArrayList;
import planifticateur.domain.Activity;
import planifticateur.enumeration.ScaleMacro;

/**
 *
 * @author floriandewulf
 */
public class MovableBox {
    public int                                  x;
    public int                                  y;
    public int                                  width;
    public int                                  height;
    public int                                  offsetXWeek = 0;
    public int                                  offsetXList = 0;
    public int                                  offsetYWeek = 0;
    public int                                  offsetYList = 0;
    public boolean                              originWeek = false;
    public boolean                              originActivity = false;
    private ArrayList<ArrayList<double []> >    rightHours;
    
    public MovableBox(Activity _activity, Point pt, int _offsetXWeek, int _offsetXList,
            int _offsetYWeek, int _offsetYList,
            boolean _originWeek, boolean _originActivity) {
        x = pt.x;
        y = pt.y;
        width = (int)(((_activity.getDuration()) * 60 / (double)ScaleMacro.SCALEMINUTE) * (double)ScaleMacro.SCALEDIVIDER) + 1;
        height = ScaleMacro.SCALE_HEIGHT_ACTIVITY;
        offsetXWeek = _offsetXWeek;
        offsetXList = _offsetXList;
        offsetYWeek = _offsetYWeek;
        offsetYList = _offsetYList;
        originWeek = _originWeek;
        originActivity = _originActivity;
        rightHours = new ArrayList<ArrayList<double []> >();
    }
    
    public void setRightHour(ArrayList<ArrayList<double []> > _hours) {
        rightHours = _hours;
    }
    
    public ArrayList<ArrayList<double []> > getRightHour() {
        return rightHours;
    }
}
