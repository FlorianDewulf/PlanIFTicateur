/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import planifticateur.domain.Activity;
import planifticateur.domain.Comment;
import planifticateur.domain.CustomError;
import planifticateur.domain.PathGrid;
import planifticateur.domain.Schedule;

/**
 *
 * @author Antoine
 */
public class Parser {

    private ArrayList<Activity> activities;
    private ArrayList<PathGrid> pathGrids;
    private ArrayList<Comment> comments;
    private Schedule schedule;

    private String COUFilename;
    private String CHEFilename;
    private String CMTFilename;
    private String season;

    private COUParser couParser;
    private CHEParser cheParser;
    private CMTParser cmtParser;

    public Parser(String couFilename, String season) {
        this.COUFilename = couFilename;
        this.CHEFilename = Utils.findCheFileName(couFilename);
        this.CMTFilename = Utils.findCmtFileName(couFilename);
        this.season = season;
        this.activities = new ArrayList<Activity>();
        this.pathGrids = new ArrayList<PathGrid>();
        this.comments = new ArrayList<Comment>();
        this.schedule = new Schedule();
        this.couParser = new COUParser(this.activities);
        this.cheParser = new CHEParser(this.activities, this.pathGrids, this.season);
        this.cmtParser = new CMTParser(this.comments);
    }

    public Schedule run() throws CustomError {
        couParser.openFile(this.COUFilename);
        couParser.parse();
        couParser.close();

        cheParser.openFile(this.CHEFilename);
        cheParser.parse();
        cheParser.close();
        
        if (Files.exists(Paths.get(this.CMTFilename)) == true) {
            cmtParser.openFile(this.CMTFilename);
            cmtParser.parse();
            cmtParser.close();
        }

        this.addComments();
        this.findCommonActivities();
        this.addPathGridsToSchedule();
        return (this.schedule);
    }

    private void addComments() {
        this.schedule.setCommentaries(this.comments);
    }

    private void addPathGridsToSchedule() {
        for (PathGrid pathgrid : this.pathGrids) {
            this.schedule.addPathGrid(pathgrid);
        }
    }
    private void findCommonActivities() {
        for (Activity activity : this.activities) {
            if (!activity.hasPathGrid()) {
                activity.setGlobal(true);
                this.schedule.addActivity(activity);
            }
        }
        for (PathGrid pathgrid : this.pathGrids) {
            ArrayList<Activity> activities = pathgrid.getActivities();
            for (Activity activity : activities) {
                activity.addPathGrid(pathgrid);
            }
        }
    }

    public COUParser getCOUParser() {return this.couParser;}
    public CHEParser getCHEParser() {return this.cheParser;}
    public CMTParser getCMTParser() {return this.cmtParser;}
    public String    getCHEContent() {return this.cheParser.getCHEContent();}

    public ArrayList<PathGrid> getPathGrids() {return this.pathGrids;}
    public Schedule getSchedule() {
        return this.schedule;
    }
    public ArrayList<Activity> getActivities() {
        return this.activities;
    }

}