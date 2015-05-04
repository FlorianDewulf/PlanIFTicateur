/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planifticateur.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import planifticateur.domain.Activity;
import planifticateur.domain.CustomError;
import planifticateur.enumeration.DayEnumeration.Day;
import planifticateur.enumeration.ScaleMacro;
import planifticateur.enumeration.TypeErrorEnumeration.TypeError;

/**
 *
 * @author floriandewulf
 */
public class DayContainer {
    private ArrayList<ActivityBox>  boxList;
    private MainWindow              mainWindow;
    private int                     nbLine;
    private final String            dayName;
    private final Day               dayValue;

    DayContainer(MainWindow _mainWindow, String _dayName, Day _dayValue) {
        mainWindow = _mainWindow;
        boxList = new ArrayList<ActivityBox>();
        nbLine = 1;
        dayName = _dayName;
        dayValue = _dayValue;
    }

    public int draw(Graphics g, int offset) {
        if (mainWindow != null) {
            fillRectangle(g, 0, offset, ScaleMacro.SCALE_WIDTH_DAY, ScaleMacro.SCALE_HEIGHT_DAY * nbLine +
                                                                    (nbLine - 1) * ScaleMacro.SCALE_LINE_INTERACTIVITY, new Color(20, 159, 254));
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            g.drawString(dayName, 10, offset + ScaleMacro.SCALE_HEIGHT_DAY - 10);
            for (ActivityBox activity : boxList) {
                activity.drawInGrid(g, offset);
            }
            offset += ScaleMacro.SCALE_HEIGHT_DAY * nbLine + (nbLine - 1) * ScaleMacro.SCALE_LINE_INTERACTIVITY + ScaleMacro.SCALE_LINE_INTERDAY;
        }
        return offset;
    }
    
    public int  getHeight() {
        return  ScaleMacro.SCALE_HEIGHT_DAY * nbLine + nbLine * ScaleMacro.SCALE_LINE_INTERACTIVITY + ScaleMacro.SCALE_LINE_INTERDAY;
    }
    
    public Day  getDay() {
        return  dayValue;
    }
    
    public int  getNbLine() {
        return  nbLine;
    }
    
    public void clear() {
        boxList = new ArrayList<ActivityBox>();
        nbLine = 1;
    }
    
    public ActivityBox  getBox(Activity act) {
        for (ActivityBox box : boxList) {
            if (box.getActivity() == act)
                return box;
        }
        return null;
    }
    
    public boolean  addElement(Point positionMouse, boolean verification, ActivityBox activityBox) {
        double      hourPlaced = getHour(positionMouse);
        int         row = 1;
        
        Day         oldDay = activityBox.getActivity().getDay();
        double      oldHour = activityBox.getActivity().getStartHour();
        activityBox.getActivity().setDay(dayValue);
        activityBox.getActivity().setStartHour(hourPlaced);
        
        if (activityBox.getActivity().checkConstraints(hourPlaced) && verification || (hourPlaced < ScaleMacro.HOUR_BEGIN) || (hourPlaced > ScaleMacro.HOUR_BEGIN + ScaleMacro.NBHOUR)) {
            activityBox.getActivity().setDay(oldDay);
            activityBox.getActivity().setStartHour(oldHour);
            return false;
        } else if (activityBox.getActivity().checkConstraints(hourPlaced)) {
            mainWindow.addErrorToCtrl(new CustomError("[" + activityBox.getActivity().toConflictString() + "] Conflit lors du placement du cours.", TypeError.CONSTRAINT_ERROR));
        }
        
        int i = 0;
        while (i < boxList.size()) {

            ActivityBox activityItem = boxList.get(i);
            
            // Si deux cours se chevauchent
            if ((activityBox.getActivity().checkConflicts(activityItem.getActivity()) || 
                  activityItem.getActivity().checkConflicts(activityBox.getActivity())) && verification) {
                activityBox.getActivity().setDay(oldDay);
                activityBox.getActivity().setStartHour(oldHour);
                mainWindow.addErrorToCtrl(new CustomError("[" + activityBox.getActivity().toConflictString() + "] Conflit lors du placement du cours.", TypeError.SCHEDULING_CONFLICT));
                return false;
            }
            else if (activityBox.getActivity().checkConflicts(activityItem.getActivity()) ||
                    activityItem.getActivity().checkConflicts(activityBox.getActivity()) ||
                    activityItem.getActivity().checkVisualConflicts(activityBox.getActivity())) {
                
                if (activityItem.getRow() == row) {
                    row++;
                    i = 0;
                    continue;
                }
            }
            i++;
        }
        
        activityBox.setRow(row);
        boxList.add(activityBox);
        if (row > nbLine)
            nbLine = row + 1;
        cleanEmptyLine();
        
        Collections.sort(boxList);
        
        return true;
    }
    
    public double  getHour(Point positionMouse) {
        double tmp = (positionMouse.x - ScaleMacro.SCALE_WIDTH_DAY);
        tmp -= tmp % ScaleMacro.SCALEDIVIDER;
        
        tmp = tmp * ScaleMacro.SCALEMINUTE / (ScaleMacro.SCALEDIVIDER * 60);
        tmp = (double)(ScaleMacro.HOUR_BEGIN) + tmp;
        
        return ((double)Math.floor(tmp * 100) / 100);
    }
    
    private void    fillRectangle(Graphics g, int x, int y, int width, int height, Color color) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(new Color(0, 0, 0));
    }

    public Activity getActivity(Point pt, int offset) {
        int         xDefault = pt.x - ScaleMacro.SCALE_WIDTH_DAY;
        int         yDefault = pt.y - offset;
        
        for (ActivityBox activityItem : boxList) {
            if (yDefault > (activityItem.getRow() - 1) * ScaleMacro.SCALE_HEIGHT_ACTIVITY + (activityItem.getRow() - 1) * ScaleMacro.SCALE_LINE_INTERACTIVITY &&
                    yDefault < activityItem.getRow() * ScaleMacro.SCALE_HEIGHT_ACTIVITY + activityItem.getRow() * ScaleMacro.SCALE_LINE_INTERACTIVITY &&
                    xDefault > (int)(((activityItem.getActivity().getStartHour() - ScaleMacro.HOUR_BEGIN) * 60 / (double)ScaleMacro.SCALEMINUTE) * (double)ScaleMacro.SCALEDIVIDER) + 1 &&
                    xDefault < (int)(((activityItem.getActivity().getStartHour() + activityItem.getActivity().getDuration() - ScaleMacro.HOUR_BEGIN) * 60 / (double)ScaleMacro.SCALEMINUTE) * (double)ScaleMacro.SCALEDIVIDER) + 1) {
                return activityItem.getActivity();
            }
        }
        
        return null;
    }

    public int      addElement(Activity activity, boolean verification, boolean byPassError) {
        ActivityBox newItem = new ActivityBox(activity);
        int         row = 1;
        int         returnValue = 0;
        
        if (byPassError == false && (activity.getStartHour() < ScaleMacro.HOUR_BEGIN ||
                                    (activity.getStartHour() + activity.getDuration() > ScaleMacro.HOUR_BEGIN + ScaleMacro.NBHOUR))) {
            return -2;
        }
        
        if (byPassError == false && newItem.getActivity().checkConstraints(newItem.getActivity().getStartHour()) && verification) {
            returnValue = -1;
        }
        
        int i = 0;
        while (i < boxList.size()) {

            ActivityBox activityItem = boxList.get(i);
            // Si deux cours se chevauchent
            if (byPassError == false && ((newItem.getActivity().checkConflicts(activityItem.getActivity()) || 
                  activityItem.getActivity().checkConflicts(newItem.getActivity())) && verification)) {
                returnValue = -1;
            }
            if (newItem.getActivity().checkConflicts(activityItem.getActivity()) ||
                    activityItem.getActivity().checkConflicts(newItem.getActivity()) ||
                    activityItem.getActivity().checkVisualConflicts(newItem.getActivity())) {
                
                if (activityItem.getRow() == row) {
                    row++;
                    i = 0;
                    continue;
                }
            }
            i++;
        }
        
        
        newItem.setRow(row);
        boxList.add(newItem);
        if (row > nbLine)
            nbLine = row + 1;

        cleanEmptyLine();
        
        
        Collections.sort(boxList);
        return returnValue;
    }

    public void removeElement(Activity currentSelectedActivity) {
        for (ActivityBox box : boxList) {
            if (box.getActivity() == currentSelectedActivity) {
                boxList.remove(box);
                cleanEmptyLine();
                break;
            }
        }
    }
    
    public boolean moveElement(Point mousePosition, ActivityBox newItem, boolean validation) {
        if (addElement(mousePosition, validation, newItem)) {
            return true;
        } else {
            return false;
        }
    }
    
    public void     sort() {
        ArrayList<ActivityBox> oldBoxList = boxList;
        boxList = new ArrayList<ActivityBox>();
        
        for (ActivityBox oldBox : oldBoxList) {
            addElement(oldBox.getActivity(), false, true);
        }
    }
    
    private boolean sort(Activity currentSelectedActivity) {
        int     lineToRemove = -1;
        boolean canBeRemove = true;
        
        for (ActivityBox box : boxList) {
            if (box.getActivity() == currentSelectedActivity)
                lineToRemove = box.getRow();
        }
        
        if (lineToRemove != -1) {
            for (ActivityBox box : boxList) {
                if (box.getActivity() != currentSelectedActivity && box.getRow() == lineToRemove) {
                    canBeRemove = false;
                    break;
                }
            }
        
            if (canBeRemove) {
                for (ActivityBox box : boxList) {
                    if (box.getActivity() != currentSelectedActivity) {
                        box.setRow(box.getRow());
                    }
                }
                if (nbLine > 1)
                    nbLine--;
            }
            return true;
        }
        return false;
    }
    
    private void cleanEmptyLine() {
        int i = 0;
        
        while (i < nbLine) {
            boolean canBeRemove = true;
            for (ActivityBox box : boxList) {
                if (box.getRow() == i + 1) {
                    canBeRemove = false;
                }
            }
            if (canBeRemove) {
                for (ActivityBox box : boxList) {
                    if (box.getRow() > i + 1) {
                        box.setRow(box.getRow() - 1);
                    }
                }
            }
            i++;
        }
        
        int maxRow = 1;
        for (ActivityBox box : boxList) {
            if (box.getRow() > maxRow)
                maxRow = box.getRow();
        }
        nbLine = maxRow + 1;
    }
    
    public int drawAuhorizeZone(Graphics g, ArrayList<double[]> authorizeZone, int offset) {
        g.setColor(Color.GRAY);
        for (double[] zone : authorizeZone) {
            g.fillRect( ScaleMacro.SCALE_WIDTH_DAY + (int) ((((zone[0] - (double) ScaleMacro.HOUR_BEGIN) * 60.0) / (double) ScaleMacro.SCALEMINUTE) * (double) ScaleMacro.SCALEDIVIDER ), 
                offset, 
                (int) ((((zone[1] - zone[0]) * 60.0) / (double) ScaleMacro.SCALEMINUTE) * (double) ScaleMacro.SCALEDIVIDER),
                nbLine * (ScaleMacro.SCALE_LINE_INTERACTIVITY + ScaleMacro.SCALE_HEIGHT_ACTIVITY));
        }
        offset += nbLine * (ScaleMacro.SCALE_LINE_INTERACTIVITY + ScaleMacro.SCALE_HEIGHT_ACTIVITY) + ScaleMacro.SCALE_LINE_INTERDAY - ScaleMacro.SCALE_LINE_INTERACTIVITY;
        return (offset);
    }

    ArrayList<ActivityBox> getBoxList() {
        return boxList;
    }
    
    public void        addErrorConflict() {
        for (int i = 0 ; i < boxList.size() ; i++) {
            for (int j = i + 1 ; j < boxList.size() ; j++) {
                if (boxList.get(i).getActivity().checkConflicts(boxList.get(j).getActivity()) ||
                    boxList.get(j).getActivity().checkConflicts(boxList.get(i).getActivity())) {
                    mainWindow.addErrorToCtrl(new CustomError("[" + boxList.get(i).getActivity().toConflictString() + "] Conflit avec le cours : " + boxList.get(j).getActivity().toConflictString(), TypeError.SCHEDULING_CONFLICT));
                }
            }
        }
    }
}