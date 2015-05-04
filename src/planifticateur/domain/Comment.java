/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planifticateur.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Antoine
 */
public class Comment {

    private Date date;
    private String version;
    private String text;
    private String code;

    public Comment(String code, String text, String version, Date date) {
        this.code = code;
        this.text = text;
        this.version = version;
        this.date = date;
    }
    
    public String toCSV() {
        SimpleDateFormat parserSDF = new SimpleDateFormat("dd/MM/yyyy");
        return parserSDF.format(this.date) + ";" + this.version.replace(";", "\\;") + ";" + this.text.replace(";", "\\;") + ";" + this.code.replace(";", "\\;") + "\n";
    }
    
    public Date getDate() {
        return date;
    }

    public String getVersion() {
        return version;
    }

    public String getText() {
        return text;
    }

    public String getCode() {
        return code;
    }
    
}
