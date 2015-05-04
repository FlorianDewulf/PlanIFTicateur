/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planifticateur.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import planifticateur.domain.Activity;
import planifticateur.domain.MainController;
import planifticateur.enumeration.ScaleMacro;

/**
 *
 * @author kevin_000
 */
public class ListDrawer extends javax.swing.JPanel {
    
    private MainWindow              mainWindow;
    private ArrayList<ActivityBox>  activityList;
    private int                     maxCharacter = 0;
    private boolean                 currentMove = false;
    
    public ListDrawer() {
        mainWindow = null;
        activityList = new ArrayList<ActivityBox>();
        
        this.repaint();
        this.revalidate();
    }
    
    public ListDrawer(MainWindow _mainwindow) {
        mainWindow = _mainwindow;
        activityList = new ArrayList<ActivityBox>();
    }
    
     @Override
    protected void  paintComponent(Graphics g) {
        if (mainWindow != null) {
            super.paintComponent(g);
            int alreadyDraw = 0;
            for (ActivityBox act : activityList) {
                act.drawInList(g, alreadyDraw * (ScaleMacro.SCALE_HEIGHT_ACTIVITY + ScaleMacro.SCALE_LINE_INTERACTIVITY), maxCharacter * 10 + 20);
                alreadyDraw++;
            }
            if (currentMove == true) {
                MovableBox currentBox = mainWindow.getCurrentBox();
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
                
                Activity currentActivity = MainController.getInstance().getCurrentSelectedActivity();
                if (currentActivity != null) {
                    g.setColor(currentActivity.getColor());
                    g.fillRect(currentBox.x + currentBox.offsetXList, currentBox.y + currentBox.offsetYList, maxCharacter * 10 + 20, currentBox.height);
                    g.setColor(Color.BLACK);
                    g.drawString(currentActivity.getCode() + '-' + currentActivity.getSection() + ' ' + currentActivity.getName(), currentBox.x + 10 + currentBox.offsetXList, currentBox.y + currentBox.height - 10 + currentBox.offsetYList);
                }
            }
        }
    }
    
    public void setActivityList(ArrayList<Activity> _activityList) {
        activityList = new ArrayList<ActivityBox>();
        
        for (Activity act : _activityList) {
            if (maxCharacter < (act.getCode().length() + act.getSection().length() + act.getName().length() + 1)) {
               maxCharacter = act.getCode().length() + act.getSection().length() + act.getName().length() + 2;
            }
            activityList.add(new ActivityBox(act));
        }
        this.setPreferredSize(new Dimension(maxCharacter * 10 + 20, activityList.size() * ScaleMacro.SCALE_HEIGHT_ACTIVITY));
        this.revalidate();
    }
    
    public Activity getSelectedActivity(Point pt) {
        int indexActivity = (int)(pt.getY() / (double)((ScaleMacro.SCALE_HEIGHT_ACTIVITY + ScaleMacro.SCALE_LINE_INTERACTIVITY)));
        if (indexActivity < activityList.size() && pt.x < maxCharacter * 10 + 20) {
            return (activityList.get(indexActivity).getActivity());
        }
        else {
            return (null);
        }
    }
    
    public void setCurrentMove(boolean _currentMove) {
        currentMove = _currentMove;
    }

    public boolean getCurrentMove() {
        return currentMove;
    }

    public void removeActivity(Activity currentSelectedActivity) {
        for (ActivityBox act : activityList) {
            if (act.getActivity() == currentSelectedActivity) {
                activityList.remove(act);
                break;
            }
        }
    }

    public void addActivity(Activity act) {
        if (maxCharacter < (act.getCode().length() + act.getSection().length() + act.getName().length() + 1)) {
            maxCharacter = act.getCode().length() + act.getSection().length() + act.getName().length() + 2;
        }
        activityList.add(new ActivityBox(act));
    }
    
}
