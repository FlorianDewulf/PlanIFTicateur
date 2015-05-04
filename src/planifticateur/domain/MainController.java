/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planifticateur.domain;

import Utils.Parser;
import Utils.Utils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import planifticateur.enumeration.TypeErrorEnumeration;
import planifticateur.enumeration.TypeErrorEnumeration.TypeError;

/**
 *
 * @author Antoine
 */
public class MainController {
    
    private static MainController instance = new MainController();
    private boolean validation;
    private Parser parser;
    private Statistics statistic;
    private ArrayList<CustomError> errors;
    private Schedule currentSchedule;
    private Processor processor;
    private Activity currentSelectedActivity;
    private String fileNameCOU;
    
    public MainController() {
        this.validation = false;
        
        this.statistic = new Statistics();
        this.errors = new ArrayList<CustomError>();
        this.processor = new Processor();
    }
    
    public static MainController getInstance() {
        return instance;
    }
    
    public ArrayList<ArrayList<double[]>> getAvailableZones() {
        return this.processor.getAvailableZones(this.currentSelectedActivity);
    }
    
    public void createPlanning() {
        if (this.currentSchedule != null && this.currentSchedule.getPlannifiedActivities() != null && this.currentSchedule.getUnplannifiedActivities() != null ) {
            this.processor.createPlanning();
        } else {
            this.errors.add(new CustomError("Il n'y a rien à planifier", TypeErrorEnumeration.TypeError.GENERIC_ERROR));
        }
    }

    
    public void planifyCurrentActivity() {
        if (this.currentSchedule == null)
            return ;
        this.currentSchedule.getUnplannifiedActivities().remove(this.currentSelectedActivity);
        this.currentSchedule.getPlannifiedActivities().add(this.currentSelectedActivity);
    }
    
    public void unplanifyCurrentActivity() {
        if (this.currentSchedule == null)
            return ;
        this.currentSchedule.getPlannifiedActivities().remove(this.currentSelectedActivity);
        this.currentSchedule.getUnplannifiedActivities().add(this.currentSelectedActivity);
    }
    
    public CustomError updateActivity(Activity activity, String name, String section, String type, double duration, String professor) {
        return (activity.update(this.validation, this.processor, name, section, type, duration, professor));
    }
    
    public void addComment(String version, String text, String code) {
        this.currentSchedule.getCommentaries().add(new Comment(code, text, version, new Date()));
    } 
    
    public void switchValidationMode() {
        if (this.validation == false) {
            this.scheduleValidation();
            validation = true;
        } else {
            validation = false;
        }
    }
    
    public HashMap<String, Object> showStatistics() {
        HashMap<String, Object> stats = this.statistic.calcStats(this.currentSchedule);
        return stats;
    }

    public void openFile(String path, String session) {
        try {
            this.fileNameCOU = path;
            this.parser = new Parser(path, session);
            this.currentSchedule = this.parser.run();
            this.processor.setSchedule(currentSchedule);
        } catch (CustomError e) {
            this.errors.add(e);
        }

    } 
    
    public boolean save() {
        return this.saveAs(this.fileNameCOU);
    }

    // Permet de vérifier tous les cours placés en terme de contrainte et conflit (A appeler lorsqu'on active l'autoverif)
    // Doit ajouter les erreurs dans la liste d'erreur
    private void scheduleValidation() {
        this.processor.scheduleValidation(this.errors);
    }

    private void saveCOU(String filename) throws IOException {
        String content = "";
        File file = new File(filename);
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        ArrayList<Activity> activities = this.parser.getActivities();
        content += "code;section;name;professor;type;duration;contraint_begin;constraint_end;day;hour\n";
        for (Activity activity : activities) {
            content += activity.toCSV();
        }
        bw.write(content);
        bw.close();
    }
    
    private void saveCMT(String filename) throws IOException {
        if (this.currentSchedule.getCommentaries().size() > 0) {
            String content = "";
            File file = new File(Utils.findCmtFileName(filename));
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            content += "date;version;text;code\n";
            for (Comment cmt : this.currentSchedule.getCommentaries()) {
                content += cmt.toCSV();
            }
            bw.write(content);
            bw.close();
        }
    }

    private void saveCHE(String fileName) throws IOException {
        String content = "";
        File file = new File(Utils.findCheFileName(fileName));
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        content += this.parser.getCHEContent();
        bw.write(content);
        bw.close();
    }

    public boolean saveAs(String filename) {
        if (this.currentSchedule != null) {
            try {
                if (!filename.endsWith(".cou")) {
                    filename += ".cou";
                }
                this.saveCOU(filename);
                this.saveCHE(filename);
                this.saveCMT(filename);
                return (true);
            } catch (IOException ex) {
                this.errors.add(new CustomError("I/O Error.", TypeError.IO_ERROR));
                return (false);
            }
        } else {
            this.errors.add(new CustomError("Il faut avoir ouvert un planning pour pouvoir le sauvegarder", TypeError.SAVE_ERROR));
            return (false);
        }
    }
    
    
    /////////////////////////
    // Getters & Setters
    ////////////////////////

    public boolean getValidation() {
        return validation;
    }

    public ArrayList<CustomError> getErrors() {
        return errors;
    }

    public Schedule getCurrentSchedule() {
        return currentSchedule;
    }



    public Activity getCurrentSelectedActivity() {
        return currentSelectedActivity;
    }

    public void setCurrentSelectedActivity(Activity _activity) {
        currentSelectedActivity = _activity;
    }
    
    public void clearErrors() {
        errors.clear();
    }

    public String getFileNameCOU() {
        return fileNameCOU;
    }

    public Processor getProcessor() {
        return processor;
    }


}
