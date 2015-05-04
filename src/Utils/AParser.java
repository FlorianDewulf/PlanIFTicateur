/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import planifticateur.domain.CustomError;
import planifticateur.enumeration.TypeErrorEnumeration.TypeError;

/**
 *
 * @author Antoine
 */
public abstract class AParser implements IParser {
    
    protected BufferedReader fileContent;
    
    @Override
    public void openFile(String filename) throws CustomError {
        try {
            this.fileContent = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException ex) {
            throw new CustomError("File not found.", TypeError.FILE_NOT_FOUND);
        }
    }


}

