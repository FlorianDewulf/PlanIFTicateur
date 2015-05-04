/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.IOException;
import java.util.ArrayList;
import planifticateur.domain.Activity;
import planifticateur.domain.CustomError;
import planifticateur.enumeration.DayEnumeration.Day;
import planifticateur.enumeration.ScaleMacro;
import planifticateur.enumeration.TypeErrorEnumeration.TypeError;

/**
 *
 * @author Antoine
 */
public class COUParser extends AParser {

    private ArrayList<Activity> activities;
    private String delimiter;

    public COUParser(ArrayList<Activity> activities) {
        this.activities = activities;
        this.delimiter = "";
    }
    
    @Override
    public void parseLine(String line, int numLine) throws CustomError {
        if (numLine == 0) {
            if (line.contains(",")) {
                this.delimiter = ",";
            }
            else if (line.contains(";")) {
                this.delimiter = ";";
            }
            else {
                this.delimiter = ",";
            }
            return ;
        }
        String contentLine[] = line.split("(?<!\\\\)" + this.delimiter);
        String lineError = "La ligne " + numLine + " du fichier COU ";

        if (contentLine == null) {
            throw new CustomError("On a une erreur à la ligne " + numLine + ". Elle est mal formatée", TypeError.INCORRECT_FILE);
        }
        if (contentLine.length == 8 || contentLine.length == 10){
            String code, section, title, profesor, type, duration, startMin, endMax, day, hour;
            code = contentLine[0].trim();

            section = contentLine[1].trim().replace("\\;", ";");
            title = contentLine[2].trim().replace("\\;", ";");
            profesor = contentLine[3].trim().replace("\\;", ";");
            type = contentLine[4].trim().replace("\\;", ";");
            duration = contentLine[5].trim().replace(",", ".").replace("\\;", ";");
            startMin = contentLine[6].trim().replace(",", ".").replace("\\;", ";");
            endMax = contentLine[7].trim().replace(",", ".").replace("\\;", ";");
            day = "0";
            hour = "0";
            if (contentLine.length == 10) {
                day = contentLine[8].trim().replace(",", ".").replace("\\;", ";");
                hour = contentLine[9].trim().replace(",", ".").replace("\\;", ";");
            }
            if (code == "" || section == "" || title == "" || profesor == "" || type == "" || duration == "" || startMin == "" || endMax == "" || day == "" || hour == ""){
                throw new CustomError(lineError + "est mal formaté (Il y a un champ vide).", TypeError.INCORRECT_FILE);
            }
            double durationActivity, startMinActivity, endMaxActivity, hourActivity;
            Day dayActivity = Day.NONE;
            startMinActivity = ScaleMacro.HOUR_BEGIN;
            endMaxActivity = ScaleMacro.HOUR_BEGIN + ScaleMacro.NBHOUR;
            durationActivity  = hourActivity = 0;
            try {
                durationActivity = Double.valueOf(duration);
                startMinActivity = Double.valueOf(startMin);
                endMaxActivity = Double.valueOf(endMax);
                if (contentLine.length == 10){
                    Integer daySelected = Integer.valueOf(day);
                    if (daySelected < 0 || daySelected > 6) {
                        throw new CustomError(lineError + "contient un jour de planification incorrect", TypeError.INCORRECT_FILE);
                    }
                    dayActivity = Day.values()[daySelected];
                    hourActivity = Double.valueOf(hour);
                    if (hourActivity < ScaleMacro.HOUR_BEGIN || hourActivity > (ScaleMacro.HOUR_BEGIN + ScaleMacro.NBHOUR)) {
                        throw new CustomError("L'activité à la ligne " + numLine + " est mal formatée : Les activités doivent être planifiées entre " + ScaleMacro.HOUR_BEGIN + "h et " + (ScaleMacro.HOUR_BEGIN + ScaleMacro.NBHOUR) + "h.", TypeError.INCORRECT_FILE);
                    }
                }
            }
            catch (NumberFormatException e) {
                throw new CustomError("Le fichier COU est invalide (Impossible de parser un nombre).", TypeError.INCORRECT_FILE);
            }
            Activity tmp = new Activity(code, section, title, profesor, durationActivity, type, startMinActivity,  endMaxActivity, dayActivity, hourActivity);
            this.activities.add(tmp);
        } else {
            throw new CustomError(lineError + " est mal formaté (elle doit contenir 9 ou 11 colonne).", TypeError.INCORRECT_FILE);
        }
    }

    @Override
    public void parse() throws CustomError {
        try {
            int numLine = 0;
            if (this.fileContent == null) {
                throw new CustomError("I/O error.", TypeError.IO_ERROR);
            }
            for (String line; (line = this.fileContent.readLine()) != null;) {
                this.parseLine(line, numLine);
                numLine++;
            }
        } catch (IOException ex) {
            throw new CustomError("I/O error.", TypeError.IO_ERROR);
        }
    }

    @Override
    public void close() throws CustomError {
        try {
            this.fileContent.close();
        } catch (IOException ex) {
            throw new CustomError("I/O error.", TypeError.IO_ERROR);
        }
    }
    
}
