/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planifticateur.domain;

import java.util.ArrayList;

/**
 *
 * @author Antoine
 */
public class Schedule {
    
    private ArrayList<PathGrid> pathGrid;
    private ArrayList<Activity> plannifiedActivities;
    private ArrayList<Activity> unplannifiedActivities;
    private ArrayList<Comment> commentaries;
    
    public Schedule() {
        this.commentaries = new ArrayList<Comment>();
        this.plannifiedActivities = new ArrayList<Activity>();
        this.unplannifiedActivities = new ArrayList<Activity>();
        this.pathGrid = new ArrayList<PathGrid>();
    }
    
    public void addPathGrid(PathGrid pathGrid) {
        this.pathGrid.add(pathGrid);

        // Setting activities for this pathGrid
        for(Activity activity: pathGrid.getActivities()) {
            if (!(this.plannifiedActivities.contains(activity) || this.unplannifiedActivities.contains(activity))) {
                if (activity.isPlanified()) {
                    this.plannifiedActivities.add(activity);
                } else {
                    this.unplannifiedActivities.add(activity);
                }   
            }
        }
    }
    
    public void addActivity(Activity activity) {
        if (activity.isPlanified()) {
            this.plannifiedActivities.add(activity);
        } else {
            this.unplannifiedActivities.add(activity);
        }
    }
    
    public Activity createPlanning() {
        return (null);
    }
    
    public boolean checkError() {
        return (false);
    }

    public void setCommentaries(ArrayList<Comment> comments) {this.commentaries = comments;}
    public ArrayList<Activity> getPlannifiedActivities() {
        return plannifiedActivities;
    }
    
    public ArrayList<Activity> getUnplannifiedActivities() {
        return unplannifiedActivities;
    }

    public ArrayList<PathGrid> getPathGrid() {
        return pathGrid;
    }

    public ArrayList<Comment> getCommentaries() {
        return commentaries;
    }

}
