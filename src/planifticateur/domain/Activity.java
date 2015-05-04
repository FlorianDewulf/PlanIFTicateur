/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planifticateur.domain;

import java.awt.Color;
import java.util.ArrayList;

import planifticateur.enumeration.ActivityColor;
import planifticateur.enumeration.DayEnumeration.Day;
import planifticateur.enumeration.ScaleMacro;
import planifticateur.enumeration.TypeErrorEnumeration;

/**
 *
 * @author Antoine
 */
public class Activity {
 
    private boolean hasPathGrid;
    private ArrayList<PathGrid> pathGrids;
    private boolean isGlobal;

    private String code;
    private String section;
    private String name;
    private String professor;
    private double duration;
    
    private String type;
    private Color color;
    private Constraint constraint;
    
    private Day day;
    private double startHour;
    
    public Activity(String code, String section, String name, String professor, double duration, String type, double startMin, double endMax, Day day, double startHour) {
        this.hasPathGrid = false;
        this.isGlobal = false;
        this.code = code.toLowerCase();
        this.section = section.toLowerCase();
        this.name = name.toLowerCase();
        this.professor = professor.toLowerCase();
        this.duration = duration;
        
        this.type = type.toLowerCase();
        this.color = ActivityColor.getInstance().getColor(type);
        
        this.constraint = new Constraint(startMin, endMax);
        
        this.day = day;
        this.startHour = startHour;
        this.pathGrids = new ArrayList<PathGrid>();
    }

    public void addPathGrid(PathGrid pathgrid) {
        if (!this.pathGrids.contains(pathgrid)) {
            this.getPathGrids().add(pathgrid);
        }
    }
    
    public CustomError update(boolean validation, Processor processor, String name, String section, String type, double duration, String professor) {
        String oldName = this.getName();
        String oldSection = this.getSection();
        String oldType = this.getType();
        double oldDuration = this.getDuration();
        String oldProfessor = this.getProfessor();
        
        this.setName(name);
        this.setSection(section);
        this.setType(type);
        this.setDuration(duration);
        this.setProfessor(professor);
        this.setColor(ActivityColor.getInstance().getColor(type));
        
        if (this.isPlanified() && processor.checkInvalidActivity(this)) {
            if (validation) {
                // On remet les anciennes valeurs
                this.setName(oldName);
                this.setSection(oldSection);
                this.setType(oldType);
                this.setDuration(oldDuration);
                this.setProfessor(oldProfessor);
                this.setColor(ActivityColor.getInstance().getColor(oldType));
                return (new CustomError("L'activité modifiée ne respecte plus les contraintes horaires définies. Puisque la validation automatique est activée, elle n'a pas été modifiée.", TypeErrorEnumeration.TypeError.SCHEDULING_CONFLICT));
            }
            return (new CustomError("L'activité modifiée ne respecte plus les contraintes horaires définies.", TypeErrorEnumeration.TypeError.SCHEDULING_CONFLICT));
        }
        return (null);
    }

    public String toString() {
        String res = "Activité " + this.code + " '" + this.name + "' enseigné par " + this.professor + ". Section : " + this.section + ". Durée : " + this.duration + ".";
        if (this.isPlanified()) {
            res = res + "Cours planifié le " + this.day + " à " + this.startHour;
        }
        return res;
    }
    
    public String toConflictString() {
        String res = "Activité " + this.code + " / Section : " + this.section + " / Type : " + this.type;
        if (this.isPlanified()) {
            res = res + " / Planifié le " + this.day + " à " + this.startHour;
        }
        return res;
    }
    
    public String toCSV() {
        String activity;
        activity = this.code.replace(";", "\\;") + ";" + this.section.replace(";", "\\;") + ";" + this.name.replace(";", "\\;") + ";" +
                this.professor.replace(";", "\\;") + ";" + this.type.replace(";", "\\;") + ";" + this.duration + ";" +
                this.constraint.getStartMin()  + ";" + this.constraint.getEndMax();
        if (this.day != Day.NONE && this.startHour != 0) {
            activity += ";" + this.day.ordinal() + ";" + this.startHour;
        }
        activity += "\n";
        return activity;
    }

    public void updateActivity(Day day, double startHour) {
        this.day = day;
        this.startHour = startHour;
    }
    
    public boolean isPlanified() {
        if (this.day != Day.NONE && this.startHour >= ScaleMacro.HOUR_BEGIN && this.startHour <= (ScaleMacro.HOUR_BEGIN + ScaleMacro.NBHOUR)) {
            return (true);
        }
        return (false);
    }

    public boolean checkConstraints(double hour) {
        if (hour >= this.constraint.getStartMin()
                && (hour + this.duration) <= this.constraint.getEndMax()) {
            return (false);
        }
        return (true);
    }
    
    public boolean hasCommonPathGrid(Activity activity) {
        for (PathGrid pathGrid : this.pathGrids) {
            if (activity.getPathGrids().contains(pathGrid)) {
                return (true);
            }
        }
        
        return (false);
    }
    
    public boolean checkConflicts(Activity activity) {
        if (this.day != activity.getDay() || (!this.hasCommonPathGrid(activity) && !activity.getProfessor().equals(this.professor))) {
            return false;
        }
        return this.detectConflict(activity);
    }
    
    private boolean detectConflict(Activity activity) {
        double endHourActivity = this.startHour + this.getDuration();
        double endHourActivity1 = activity.getStartHour() + activity.getDuration();
        if (endHourActivity > activity.getStartHour() && endHourActivity <= endHourActivity1) {
            return true;
        }
        if (endHourActivity1 > this.startHour && endHourActivity1 <= endHourActivity) {
            return true;
        }
        if (this.startHour == activity.getStartHour() && endHourActivity == endHourActivity1) {
            return true;
        }
        return false;
    }
    
    public boolean checkVisualConflicts(Activity activity) {
        if (this.day != activity.getDay()) {
            return false;
        }
        return this.detectConflict(activity);
    }
    
    /////////////////////////
    // Getters & Setters
    ////////////////////////

    public boolean isGlobal() { return this.isGlobal;}

    public void setGlobal(boolean isGlobal) {this.isGlobal = isGlobal;}

    public boolean hasPathGrid() {
        return hasPathGrid;
    }
    
    public void setBelongingToPathGrid(boolean hasPathGrid) {
        this.hasPathGrid = hasPathGrid;
    }
    
    public String getCode() {
        return code;
    }

    public String getSection() {
        return section;
    }

    public String getName() {
        return name;
    }

    public String getProfessor() {
        return professor;
    }

    public double getDuration() {
        return duration;
    }

    public String getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public Day getDay() {
        return day;
    }

    public double getStartHour() {
        return startHour;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public void setStartHour(double startHour) {
        this.startHour = startHour;
    }

    public ArrayList<PathGrid> getPathGrids() {
        return pathGrids;
    }
}