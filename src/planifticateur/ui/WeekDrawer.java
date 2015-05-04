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
import planifticateur.domain.Activity;
import planifticateur.domain.MainController;
import planifticateur.enumeration.DayEnumeration.Day;
import planifticateur.enumeration.ScaleMacro;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import static java.lang.Math.round;
import java.util.ArrayList;
import planifticateur.domain.CustomError;
import planifticateur.enumeration.TypeErrorEnumeration.TypeError;
/**
 *
 * @author floriandewulf
 */
public class WeekDrawer extends javax.swing.JPanel {
    
    private DayContainer[]      weekDay;
    private MainWindow          mainWindow;
    private boolean             moveCurrently = false;
    private int                 daySelectedActivity = 0;
    
    public WeekDrawer() {
        String[]    dayList = { "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche" };
        Day[]       dayValueList = { Day.MONDAY, Day.TUESDAY, Day.WEDNESDAY, Day.THURSDAY, Day.FRIDAY, Day.SATURDAY, Day.SUNDAY };
        
        mainWindow = null;
        weekDay = new DayContainer[7];
        for (char i = 0 ; i < 7 ; i++) {
            weekDay[i] = new DayContainer(mainWindow, dayList[i], dayValueList[i]);
        }
        
        this.setPreferredSize(new Dimension(ScaleMacro.SCALE_WIDTH_DAY + (ScaleMacro.NBHOUR * 60 * ScaleMacro.SCALEDIVIDER / ScaleMacro.SCALEMINUTE), this.getPreferredSize().height));
        moveCurrently = false;
        this.revalidate();
    }
    
    public WeekDrawer(MainWindow _mainwindow) {
        String[]    dayList = { "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche" };
        Day[]       dayValueList = { Day.MONDAY, Day.TUESDAY, Day.WEDNESDAY, Day.THURSDAY, Day.FRIDAY, Day.SATURDAY, Day.SUNDAY };
        
        mainWindow = _mainwindow;
        weekDay = new DayContainer[7];
        for (char i = 0 ; i < 7 ; i++) {
            weekDay[i] = new DayContainer(mainWindow, dayList[i], dayValueList[i]);
        }
        
        this.setPreferredSize(new Dimension(ScaleMacro.SCALE_WIDTH_DAY + (ScaleMacro.NBHOUR * 60 * ScaleMacro.SCALEDIVIDER / ScaleMacro.SCALEMINUTE), this.getPreferredSize().height));
        this.revalidate();
        moveCurrently = false;
    }
    
    public void     setMoveCurrently(boolean value) {
        moveCurrently = value;
    }
    
    public boolean  getMoveCurrently() {
        return moveCurrently;
    }
    
    public ArrayList<Activity>  setSchedule(ArrayList<Activity> plannifiedActivity, boolean verification) {
        clearWeek();
        ArrayList<Activity>     activitiesWithProblem = new ArrayList<Activity>();
        
        for (Activity activity : plannifiedActivity) {
            if (activity.getDay() != Day.NONE) {
               int returnValue = weekDay[activity.getDay().ordinal()].addElement(activity, verification, false);
               checkErrors();
               if (returnValue == -2) {
                   activitiesWithProblem.add(activity);
               } else if (returnValue == -1) {
                   mainWindow.addErrorToCtrl(new CustomError("[" + activity.toConflictString() + "] Conflit lors du placement du cours.", TypeError.SCHEDULING_CONFLICT));
               }
            }
        }
        
        return activitiesWithProblem;
    }
    
    public void     clearWeek() {
        for (char i = 0 ; i < 7 ; i++) {
            weekDay[i].clear();
        }
    }
    
    public void     sortRows() {
        for (char i = 0 ; i < 7 ; i++) {
            weekDay[i].sort();
        }
    }
    
    /*
    * Logique Evenementielle
    */
    
    public boolean     addActivity(Point pt, boolean validation, Activity element) {
        int offset = ScaleMacro.SCALE_HEIGHT_TIMELINE + ScaleMacro.SCALE_LINE_INTERDAY;
        
        if (pt.y > offset) {
            for (char i = 0 ; i < 7 ; i++) {
                if (pt.y < offset + weekDay[i].getHeight()) {
                    if (weekDay[i].addElement(pt, validation, new ActivityBox(element))) {
                        this.repaint();
                        this.revalidate();
                        checkErrors();
                        return true;
                    }
                    break;
                } else {
                    offset += weekDay[i].getHeight();
                }
            }
        }
        checkErrors();
        return false;
    }
    
    public Activity selectActivity(Point pt) {
        int offset = ScaleMacro.SCALE_HEIGHT_TIMELINE + ScaleMacro.SCALE_LINE_INTERDAY;
        
        if (pt.y > offset) {
            for (char i = 0 ; i < 7 ; i++) {
                if (pt.y < offset + weekDay[i].getHeight()) {
                    daySelectedActivity = i;
                    return weekDay[i].getActivity(pt, offset);
                } else {
                    offset += weekDay[i].getHeight();
                }
            }
        }
        return null;
    }
    
    /*
    * Logique de dessin
    */
    
     @Override
    protected void  paintComponent(Graphics g)
    {
        int offset = 0;
        
        if (mainWindow != null)
        {
            super.paintComponent(g);
            
            if (moveCurrently == true) {
                Activity currentActivity = MainController.getInstance().getCurrentSelectedActivity();
                if (currentActivity != null) {
                    offset = ScaleMacro.SCALE_HEIGHT_TIMELINE + ScaleMacro.SCALE_LINE_INTERDAY;
                    
                    for (int i = 0 ; i < 7 ; i++) {
                        offset = weekDay[i].drawAuhorizeZone(g, mainWindow.getCurrentBox().getRightHour().get(i), offset);
                    }
                }
                else {
                    this.setBackground(new Color(240,240,240));
                }
            }
            
            drawTimeline(g);
            drawGrid(g);
            offset = drawPlanning(g);
            drawMovingElement(g);
            if (offset > this.getHeight()) {
                this.setPreferredSize(new Dimension(ScaleMacro.SCALE_WIDTH_DAY + (ScaleMacro.NBHOUR * 60 * ScaleMacro.SCALEDIVIDER / ScaleMacro.SCALEMINUTE), offset));
                this.invalidate();
                this.repaint();
            }
        }
    }
    
    public boolean drawInPNG(File file) {
        String fileName = file.getName();
        if (fileName.length() > 4) {
            String end = fileName.substring(fileName.length() - 4, fileName.length());
            if (!end.equals(".png")) {
                 fileName += ".png";
            }
        }
        else {
            fileName += ".png";
        }
        BufferedImage bi = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_ARGB); 
        Graphics g = bi.createGraphics();
        this.paintComponent(g);
        g.dispose();
        try{
            ImageIO.write(bi,"png",new File(fileName));
            return true;
        } catch (Exception e) {
            mainWindow.addErrorToCtrl(new CustomError("L'export a rencontré un probléme", TypeError.EXPORT_ERROR));
            return false;
        }
    }
    
    private int     drawPlanning(Graphics g) {
        int offset = ScaleMacro.SCALE_HEIGHT_TIMELINE + ScaleMacro.SCALE_LINE_INTERDAY;
        for (char i = 0 ; i < 7 ; i++) {
            offset = weekDay[i].draw(g, offset);
        }
        return offset;
    }
    
    private void    drawTimeline(Graphics g) {
        g.setColor(new Color(83, 83, 83));
        g.fillRect(ScaleMacro.SCALE_WIDTH_DAY, 0, this.getWidth() - ScaleMacro.SCALE_WIDTH_DAY, ScaleMacro.SCALE_HEIGHT_TIMELINE);
        g.setColor(Color.BLACK);
    }
    
    private void    drawGrid(Graphics g) {
        int widthLine = (ScaleMacro.NBHOUR * (60 / ScaleMacro.SCALEMINUTE)) * ScaleMacro.SCALEDIVIDER;
        g.setColor(Color.BLACK);

        int nbActivityLine;
        int totalActivityLine = 0;
        int previousLine = 0;
        for (int nbDay = 0; nbDay <= ScaleMacro.NBDAY; nbDay++) {
            g.fillRect(0, ScaleMacro.SCALE_HEIGHT_TIMELINE +
                          (ScaleMacro.SCALE_HEIGHT_ACTIVITY * previousLine +
                          (totalActivityLine * ScaleMacro.SCALE_LINE_INTERACTIVITY)) +
                           nbDay * ScaleMacro.SCALE_LINE_INTERDAY, widthLine + ScaleMacro.SCALE_WIDTH_DAY, ScaleMacro.SCALE_LINE_INTERDAY);

            if (nbDay == ScaleMacro.NBDAY) { break; }
            
            int nbLine = weekDay[nbDay].getNbLine();
            
            for (nbActivityLine = 0; nbActivityLine < nbLine && nbDay < ScaleMacro.NBDAY; nbActivityLine++) {
                if (nbActivityLine >= 1) {
                    g.fillRect(ScaleMacro.SCALE_WIDTH_DAY, ScaleMacro.SCALE_HEIGHT_TIMELINE +
                               (ScaleMacro.SCALE_HEIGHT_ACTIVITY * previousLine +
                               (totalActivityLine * ScaleMacro.SCALE_LINE_INTERACTIVITY)) +
                                nbDay * ScaleMacro.SCALE_LINE_INTERDAY + ScaleMacro.SCALE_LINE_INTERDAY +
                                nbActivityLine * ScaleMacro.SCALE_HEIGHT_ACTIVITY, widthLine, ScaleMacro.SCALE_LINE_INTERACTIVITY);
                    totalActivityLine++;
                }
            }
            
            previousLine += nbLine;
        }
        
        int nbCol = 0;
        int sizeY = ScaleMacro.SCALE_HEIGHT_TIMELINE +
                    (ScaleMacro.SCALE_HEIGHT_ACTIVITY * previousLine +
                    (totalActivityLine * ScaleMacro.SCALE_LINE_INTERACTIVITY)) +
                    7 * ScaleMacro.SCALE_LINE_INTERDAY + ScaleMacro.SCALE_LINE_INTERDAY;
        for (int nbHour = 0; nbHour < ScaleMacro.NBHOUR; nbHour++) {
            g.setColor(Color.BLACK);
            for (nbCol = 0; nbCol < (60 / ScaleMacro.SCALEMINUTE); nbCol++) {
                g.fillRect(((nbHour * (60 / ScaleMacro.SCALEMINUTE)) + nbCol) * ScaleMacro.SCALEDIVIDER + ScaleMacro.SCALE_WIDTH_DAY, 0, ScaleMacro.SCALE_LINE_INTERSCALE, sizeY);
            }
            g.fillRect(nbCol * nbHour * ScaleMacro.SCALEDIVIDER + ScaleMacro.SCALE_WIDTH_DAY, 0, ScaleMacro.SCALE_LINE_INTERHOUR, sizeY);
            g.setColor(Color.WHITE);
            g.drawString(Integer.toString(ScaleMacro.HOUR_BEGIN + nbHour) + "h", nbCol * nbHour * ScaleMacro.SCALEDIVIDER + ScaleMacro.SCALE_WIDTH_DAY + ScaleMacro.SCALE_LINE_INTERHOUR, ScaleMacro.SCALE_HEIGHT_TIMELINE - 10);
        }
    }
    
    private void    drawMovingElement(Graphics g) {
        if (moveCurrently == true) {
            MovableBox currentBox = mainWindow.getCurrentBox();
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

            Activity currentActivity = MainController.getInstance().getCurrentSelectedActivity();
            if (currentActivity != null) {
                
                String text = currentActivity.getCode() + '-' + currentActivity.getSection() + ' ' + currentActivity.getName();
                int nbLetter = (currentBox.width - 20) / ScaleMacro.SCALE_WIDTH_LETTER;
        
                if (nbLetter > text.length()) {
                    nbLetter = text.length();
                }
                
                g.setColor(currentActivity.getColor());
                g.fillRect(currentBox.x + currentBox.offsetXWeek, currentBox.y + currentBox.offsetYWeek, currentBox.width, currentBox.height);
                g.setColor(Color.BLACK);
                g.drawString(text.substring(0, nbLetter), currentBox.x + 10 + currentBox.offsetXWeek, currentBox.y + currentBox.height - 10 + currentBox.offsetYWeek);
            }
        }
    }

    public void removeActivity(Activity currentSelectedActivity) {
        weekDay[currentSelectedActivity.getDay().ordinal()].removeElement(currentSelectedActivity);
        currentSelectedActivity.setDay(Day.NONE);
        currentSelectedActivity.setStartHour(0);
    }
    
    public boolean moveActivity(Point pt, boolean validation, Activity element) {
        int offset = ScaleMacro.SCALE_HEIGHT_TIMELINE + ScaleMacro.SCALE_LINE_INTERDAY;
        ActivityBox box = weekDay[element.getDay().ordinal()].getBox(element);
        
        if (box == null) { return false; }
        
        if (pt.y > offset) {
            for (char i = 0 ; i < 7 ; i++) {
                if (pt.y < offset + weekDay[i].getHeight()) {
                    weekDay[element.getDay().ordinal()].removeElement(element);
                    boolean returnValue = weekDay[i].moveElement(pt, box, validation);
                    if (!returnValue) {
                        weekDay[element.getDay().ordinal()].addElement(element, false, true);
                    }
                    checkErrors();
                    return returnValue;
                } else {
                    offset += weekDay[i].getHeight();
                }
            }
        }
        return false;
    }

    String      giveTime(Point pt) {
        int     offsetY = ScaleMacro.SCALE_HEIGHT_TIMELINE + ScaleMacro.SCALE_LINE_INTERDAY;
        int     offsetX = ScaleMacro.SCALE_WIDTH_DAY;
        String  toDisplay = "";
        
        if (pt.y > offsetY && pt.x > offsetX) {
            for (char i = 0 ; i < 7 ; i++) {
                if (pt.y < offsetY + weekDay[i].getHeight()) {
                    double time = weekDay[i].getHour(pt);
                    int hour = (int)time;
                    int minutes = (int) round((time - (double)hour) * 60.0);
                    toDisplay = weekDay[i].getDay().toString().substring(0, 1) + weekDay[i].getDay().toString().substring(1).toLowerCase() + " " + hour + ":" + minutes;
                    return toDisplay;
                } else {
                    offsetY += weekDay[i].getHeight();
                }
            }
        }
        
        return toDisplay;
    }
    
    private void    checkErrors() {
        for (int i = 0 ; i < 7 ; i++) {
            weekDay[i].addErrorConflict();
        }
    }
}
