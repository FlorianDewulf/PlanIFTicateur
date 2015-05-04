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
public class PathGrid {
    
    private String program;
    private String version;
    private String session;
    
    private ArrayList<Activity> activities;
    
    public PathGrid(String program, String version, String session) {
        this.program = program.toLowerCase();
        this.version = version.toLowerCase();
        this.session = session.toLowerCase();
        this.activities = new ArrayList<Activity>();
    }
    
    public void addActivity(Activity activity) {
        this.activities.add(activity);
    }
    
    /////////////////////////
    // Getters & Setters
    ////////////////////////

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    public String getProgram() {
        return program;
    }

    public String getVersion() {
        return version;
    }

    public String getSession() {
        return session;
    }

    public String toCSV() {
        String cheminement;

        cheminement = this.program + "," + this.version + "," + this.session;

        for (Activity activity : this.activities) {
            if (!activity.isGlobal()) {
                cheminement += "," + activity.getCode();
            }
        }
        cheminement += "\n";
        return cheminement;
    }
}
