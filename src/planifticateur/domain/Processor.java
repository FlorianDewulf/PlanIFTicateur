/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planifticateur.domain;

import static java.lang.Math.floor;
import java.util.ArrayList;
import java.util.Random;
import planifticateur.enumeration.DayEnumeration;
import planifticateur.enumeration.ScaleMacro;
import planifticateur.enumeration.TypeErrorEnumeration;

/**
 *
 * @author Antoine
 */
public class Processor {
    
    private Schedule currentSchedule;
    
    public Processor() {
        
    }
    
    public void setSchedule(Schedule schedule) {
        this.currentSchedule = schedule;
    }
    
    public void scheduleValidation(ArrayList<CustomError> errors) {
        if (this.currentSchedule == null)
            return ;
        ArrayList<Activity> plannifiedActivities = this.currentSchedule.getPlannifiedActivities();
        for (Activity activity : plannifiedActivities) {
            for (Activity activity1 : plannifiedActivities) {
                if (activity != activity1) {
                    if (activity.getDay() == activity1.getDay()) {
                        if (activity.checkConflicts(activity1)) {

                            String errorActivity = activity.getCode() + " " +
                                    activity.getSection() + " " + activity.getName() + " ";
                            String errorActivity1 = activity1.getCode() + " " +
                                    activity1.getSection() + " " + activity1.getName() + " ";
                            CustomError e = new CustomError(errorActivity + " est en conflit avec " + errorActivity1,
                                    TypeErrorEnumeration.TypeError.SCHEDULING_CONFLICT);
                            errors.add(e);
                        }
                    }
                }
            }
        }
    }
    
    public ArrayList<ArrayList<double[]>> getAvailableZones(Activity currentSelectedActivity) {
        ArrayList<ArrayList<double[]>> res;
        res = new ArrayList<ArrayList<double[]>>();
        
        for (int i = 0; i < 7; i++) {
            ArrayList<double[]> hDispoDay = new ArrayList<double[]>();
            // Contraintes horaires du matin
            double[] constraintBegin = {
                ScaleMacro.HOUR_BEGIN,
                currentSelectedActivity.getConstraint().getStartMin()
            };
            
            // Contraintes horaires du soir
            double[] constraintEnd = {
                currentSelectedActivity.getConstraint().getEndMax(),
                ScaleMacro.HOUR_BEGIN + ScaleMacro.NBHOUR
            };
            
            // On doit set les contraintes des cours pour tous les jours
            hDispoDay.add(constraintBegin);
            hDispoDay.add(constraintEnd);
            
            // Reste a ajouter en grisé les zones ou il y a déjà des cours de planifiés
            // On peut pour chaque cours planifié renvoyer les horaires des cours qui sont set
            for (Activity activity : this.currentSchedule.getPlannifiedActivities()) {
                if (activity.getDay().ordinal() == i) {
                    
                    if ((activity.hasCommonPathGrid(currentSelectedActivity) || activity.getProfessor().equals(currentSelectedActivity.getProfessor()))
                            && activity != currentSelectedActivity) {
                        double[] constraint = {
                            activity.getStartHour(),
                            activity.getStartHour() + activity.getDuration()
                        };
                        
                        hDispoDay.add(constraint);
                    }
                    
                    // Refaire maybe un for sur les activités et si l'heure de fin d'une des deux ou l'inverse
                    // est inférieur à la durée de celle la, griser aussi
                    for (Activity activityComp : this.currentSchedule.getPlannifiedActivities()) {
                        if (activityComp.getDay().ordinal() == i) {
                            double trou = activity.getStartHour() - (activityComp.getStartHour() + activityComp.getDuration());
                            trou = Math.abs(trou);
                            if (trou < currentSelectedActivity.getDuration() && activity != activityComp && activity != currentSelectedActivity && activityComp != currentSelectedActivity
                                    && ((activity.hasCommonPathGrid(currentSelectedActivity) && activityComp.hasCommonPathGrid(currentSelectedActivity))
                                       || (activity.getProfessor().equals(currentSelectedActivity.getProfessor()) && activityComp.getProfessor().equals(currentSelectedActivity.getProfessor())))) {
                                double[] constraintTrou = {
                                    activityComp.getStartHour() + activityComp.getDuration(),
                                    activity.getStartHour()
                                };
                                hDispoDay.add(constraintTrou);
                            }
                        }
                    }
                }
            }
            res.add(hDispoDay);
        }
        return (res);
    }
    
    public boolean checkInvalidActivity(Activity activity) {
        if (activity.checkConstraints(activity.getStartHour())) {
            return (true);
        }
        for (Activity tmp : this.currentSchedule.getPlannifiedActivities()) {
            if (tmp != activity && tmp.checkConflicts(activity)) {
                return (true);
            }
        }
        return (false);
    }
    
    /*
        Cette fonction clean les activités planifiées
        qui ont actuellement un problème de conflit ou de contrainte
    */
    private void algoCleanPlanning() {
        for (int i = 0; i < this.currentSchedule.getPlannifiedActivities().size(); i++) {
            Activity activity = this.currentSchedule.getPlannifiedActivities().get(i);
            if (this.checkInvalidActivity(activity)) {
                this.currentSchedule.getPlannifiedActivities().remove(activity);
                this.currentSchedule.getUnplannifiedActivities().add(activity);
                i = -1; // -1 parceque ça passe dans le i++ juste après, donc revient à 0
            }
        }
    }
    
    /*
        Cette fonction applique l'algorithme qui permet de planifier
        une activité
    */
    private boolean algoSetActivity(Activity tmp) {
        double hourInterval;
        hourInterval = (double) (((double) ScaleMacro.SCALEMINUTE) / 60.0);
        
        Random randomGenerator = new Random();
        ArrayList<Integer> days = new ArrayList<Integer>() {{
            add(0);
            add(1);
            add(2);
            add(3);
            add(4);
            add(5);
            add(6);
         }};

        DayEnumeration.Day backupDay = tmp.getDay();
        double backupHour = tmp.getStartHour();
                 
        while (days.size() > 0) {
            int index = randomGenerator.nextInt(days.size());
            Integer day = days.get(index);
            for (double hour = ScaleMacro.HOUR_BEGIN; hour <= (ScaleMacro.HOUR_BEGIN + ScaleMacro.NBHOUR - tmp.getDuration()); hour += hourInterval) {
                tmp.setDay(DayEnumeration.Day.values()[day]);
                tmp.setStartHour(floor(hour * 100.0) / 100.0);
                if (!this.checkInvalidActivity(tmp)) {
                    return (true);
                }
            }
            days.remove(day);
        }
        tmp.setDay(backupDay);
        tmp.setStartHour(backupHour);
        return (false);
    }
    
    /*
        Cette fonction permet de lancer l'algorithme sur tous les cours
        cours afin de les planifier
    */
    private void algoPlanify() {
        for (int i = 0; i < this.currentSchedule.getUnplannifiedActivities().size(); i++) {
            Activity tmp = this.currentSchedule.getUnplannifiedActivities().get(i);
            if (this.algoSetActivity(tmp)) {
                // On switch l'activité de place puisqu'elle est désormais planifiée
                this.currentSchedule.getUnplannifiedActivities().remove(tmp);
                this.currentSchedule.getPlannifiedActivities().add(tmp);
                i = -1; // -1 parceque ça passe dans le i++ juste après, donc revient à 0
            }
        }
    }
    
    public void createPlanning() {
        this.algoCleanPlanning();
        this.algoPlanify();
    }
    
}
