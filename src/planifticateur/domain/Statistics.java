/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planifticateur.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.IntStream;

/**
 *
 * @author Antoine
 */
public class Statistics {
    
    private float maxActivityStudent;
    private float moyActivityStudent;
    private float nbActivityDay;
    private float congestionIndex;
    private float carpoolIndex;
    private HashMap<String, Object> results;
    
    
    public Statistics() {
        this.results = new HashMap<String, Object>();
    }

    public HashMap<String, Object> calcStats(Schedule schedule) {
        this.results = new HashMap<String, Object>();
        this.calcMaxActivity(schedule);
        this.calcMoyActivitySchedule(schedule);
        this.calcNbActivity(schedule);
        this.calcCongestion(schedule);
        this.calcCarPool(schedule);
        return this.results;
    }
    
    private void calcMaxActivity(Schedule schedule) {
        int[] week = {0,0,0,0,0,0,0};
        int sizePathgrid = 0;
        if (schedule != null && schedule.getPathGrid().size() > 0) {
            ArrayList<PathGrid> pathgrids = schedule.getPathGrid();
            sizePathgrid = pathgrids.size();
            for (int a = 0 ; a < 7 ; a++) {
                int[] tmp = new int[sizePathgrid];

                int i = 0;
                for (PathGrid pathgrid : pathgrids) {

                    ArrayList<Activity> activities = pathgrid.getActivities();
                    for (Activity activity : activities) {
                        if (activity.getDay().ordinal() == a) {
                            tmp[i]++;
                        }
                    }
                    i++;
                }
                Arrays.sort(tmp);
                int max = tmp[tmp.length - 1];
                week[a] = max;
            }
        }
        this.results.put("nbMaxCours", week);
    }
    
    private void calcMoyActivitySchedule(Schedule schedule) {
        double[] week = {0,0,0,0,0,0,0};
        int sizePathgrid = 0;

        if (schedule != null) {
            ArrayList<PathGrid> pathgrids = schedule.getPathGrid();
            sizePathgrid = pathgrids.size();
            for (int a = 0 ; a < 7 ; a++) {
                int[] tmp = new int[sizePathgrid];

                int i = 0;
                for (PathGrid pathgrid : pathgrids) {

                    ArrayList<Activity> activities = pathgrid.getActivities();
                    for (Activity activity : activities) {
                        if (activity.getDay().ordinal() == a) {
                            tmp[i]++;
                        }
                    }
                    i++;
                }
                double moyenneJournee = (double)IntStream.of(tmp).sum() / (double)sizePathgrid;
               week[a] = Math.floor(moyenneJournee * 100.0) / 100.0;
            }
        }
        this.results.put("nbMoyCours", week);
    }
    
    private void calcNbActivity(Schedule schedule) {
        int[] week = {0,0,0,0,0,0,0};
        if (schedule != null) {
            ArrayList<Activity> planified = schedule.getPlannifiedActivities();
            for (Activity activity : planified) {
                week[activity.getDay().ordinal()] += 1;
            }
        }
        this.results.put("nbCours", week);
    }
    
    private void calcCongestion(Schedule schedule) {
        int total, matched, percent;
        total = matched =  percent = 0;

        if (schedule != null) {
            ArrayList<Activity> planified = schedule.getPlannifiedActivities();
            total = planified.size();
            for (Activity activity : planified) {
                if (activity.getStartHour() == 8.5) {
                    matched += 1;
                }
            }
            if (total == 0)
                percent = 0;
            else
                percent = (matched * 100) / total;
        }
        this.results.put("indiceCongestion", percent);
    }
    
    private void calcCarPool(Schedule schedule) {
        double[] week = {0,0,0,0,0,0,0};
        if (schedule != null) {
            for (int i = 0; i < 7; i++) {
                ArrayList<double[]> results = new ArrayList<double[]>();
                for (PathGrid pg : schedule.getPathGrid()) {
                    double hMin = Double.MAX_VALUE;
                    double hMax = Double.MIN_VALUE;
                    
                    for (Activity act : pg.getActivities()) {
                        if (act.isPlanified() && act.getDay().ordinal() == i) {
                            if (act.getStartHour() < hMin) {
                                hMin = act.getStartHour();
                            }
                            if ((act.getStartHour() + act.getDuration()) > hMax) {
                                hMax = act.getStartHour() + act.getDuration();
                            }
                        }
                    }
                    if (hMin != Double.MAX_VALUE && hMax != Double.MIN_VALUE) {
                        double tmp[] = {
                            hMin,
                            hMax
                        };
                        results.add(tmp);
                    }
                }
                int nbPathGrid = 0;
                if (results.size() >= 2 && schedule.getPathGrid().size() > 0) {
                    for (int k = 0; k < results.size(); k++) {
                        int tmp = 0;
                        for (int l = k + 1; l < results.size(); l++) {
                            if (results.get(k)[0] == results.get(l)[0] && results.get(k)[1] == results.get(l)[1]) {
                                tmp++;
                            }
                        }
                        if (tmp > nbPathGrid) {
                            nbPathGrid = tmp + 1;
                        }
                    }
                    week[i] = (nbPathGrid / (double) schedule.getPathGrid().size()) * 100;
                } else {
                    week[i] = 0;
                }
            }
        }
        double finalPercent = 0;
        for (int i = 0; i < week.length; i++) {
            finalPercent += week[i];
        }
        finalPercent /= 7;
        
        this.results.put("indiceCovoiturage", Math.floor(finalPercent * 100) / 100);
    }


    public float getMaxActivityStudent() {
        return maxActivityStudent;
    }

    public float getMoyActivityStudent() {
        return moyActivityStudent;
    }

    public float getNbActivityDay() {
        return nbActivityDay;
    }

    public float getCongestionIndex() {
        return congestionIndex;
    }

    public float getCarpoolIndex() {
        return carpoolIndex;
    }
    
}
