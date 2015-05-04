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
import planifticateur.domain.PathGrid;
import planifticateur.enumeration.TypeErrorEnumeration.TypeError;

/**
 *
 * @author Antoine
 */
public class CHEParser extends AParser {

    private ArrayList<Activity> activities;
    private ArrayList<PathGrid> pathGrids;
    private String season;
    private String contentCHE;
    private String delimiter;

    public CHEParser(ArrayList<Activity> activities, ArrayList<PathGrid> pathgrids, String season) {
        this.activities = activities;
        this.pathGrids = pathgrids;
        this.season = season;
        this.contentCHE = "";
        this.delimiter = "";
    }

    @Override
    public void parseLine(String line, int numLine) throws CustomError {
        this.contentCHE += line;
        this.contentCHE += "\n";
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
        String contentLine[] = line.split(this.delimiter);
        String lineError = "La ligne " + numLine + " du fichier CHE ";

        if (contentLine == null) {
            throw new CustomError("On a une erreur à la ligne " + numLine + ". Elle est mal formatée", TypeError.INCORRECT_FILE);
        }

        if (contentLine.length >= 3) {
            String program, version, session;
            ArrayList<String> codes = new ArrayList<String>();

            program = contentLine[0].trim();
            version = contentLine[1].trim();
            session = contentLine[2].trim();

            if (!session.startsWith(this.season))
                return ;
            //  System.out.println("on a une bonne session " + session);
            PathGrid pathGrid = new PathGrid(program, version, session);
            for(int i = 3; i < contentLine.length; i++)
            {
                codes.add(contentLine[i]);
            }
            for (String code : codes) {
                for (Activity activity : activities){
                    if ((code.toLowerCase()).equals(activity.getCode())) {
                        pathGrid.addActivity(activity);
                        activity.setBelongingToPathGrid(true);
                    }
                }
            }
            this.pathGrids.add(pathGrid);
        } else {
            throw new CustomError(lineError +  " contient une erreur : Les lignes du fichier CHE doivent contenir au moins 3 colonnes.", TypeError.INCORRECT_FILE);
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

    public String getCHEContent() { return this.contentCHE;}
}
