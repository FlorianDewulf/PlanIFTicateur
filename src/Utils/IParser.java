/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import planifticateur.domain.CustomError;

/**
 *
 * @author Antoine
 */
public interface IParser {
    public void openFile(String filename) throws CustomError;
    void parseLine(String line, int numLine) throws CustomError;
    public void parse() throws CustomError;
    public void close() throws CustomError;
}
