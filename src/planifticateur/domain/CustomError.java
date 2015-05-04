/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planifticateur.domain;

import java.util.Date;
import planifticateur.enumeration.TypeErrorEnumeration.TypeError;

/**
 *
 * @author Antoine
 */
public class CustomError extends Exception {
    
    private String text;
    private TypeError type;
    private Date date;
    
    public CustomError(String text, TypeError type) {
        this.text = text;
        this.type = type;
        this.date = new Date();
    }
    
    public String toString() {
        return "[" + this.getDate().toString() + "] - " + this.getText();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TypeError getType() {
        return type;
    }

    public void setType(TypeError type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
