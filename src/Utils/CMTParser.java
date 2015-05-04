/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import planifticateur.domain.Comment;
import planifticateur.domain.CustomError;
import planifticateur.enumeration.TypeErrorEnumeration.TypeError;

/**
 *
 * @author Antoine
 */
public class CMTParser extends AParser {

    private ArrayList<Comment> comments;
    private String delimiter;

    public CMTParser(ArrayList<Comment> comments) {
        this.comments = comments;
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
        String lineError = "La ligne " + numLine + " du fichier CMT ";

        if (contentLine == null) {
            throw new CustomError("On a une erreur à la ligne " + numLine + ". Elle est mal formatée", TypeError.INCORRECT_FILE);
        }
        if (contentLine.length == 4){
            String date, version, text, code;
            date = contentLine[0].trim().replace("\\;", ";");

            version = contentLine[1].trim().replace("\\;", ";");
            text = contentLine[2].trim().replace("\\;", ";");
            code = contentLine[3].trim().replace("\\;", ";");
            
            if (code == "" || version == "" || text == "" || date == ""){
                throw new CustomError(lineError + "est mal formaté (Il y a un champ vide).", TypeError.INCORRECT_FILE);
            }
            
            SimpleDateFormat parserSDF = new SimpleDateFormat("dd/MM/yyyy");

            try {
                Comment tmp = new Comment(code, text, version, parserSDF.parse(date));
                this.comments.add(tmp);
            } catch (ParseException ex) {
                throw new CustomError(lineError + " contient une date mal formatée (dd/MM/yyyy)", TypeError.INCORRECT_FILE);
            }

        } else {
            throw new CustomError(lineError + " est mal formaté (elle doit contenir 4 colonnes).", TypeError.INCORRECT_FILE);
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
