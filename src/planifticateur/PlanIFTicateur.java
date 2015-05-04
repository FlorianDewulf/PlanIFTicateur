/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planifticateur;

import Utils.Parser;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import planifticateur.domain.MainController;
import planifticateur.ui.MainWindow;

/**
 *
 * @author floriandewulf
 */
public class PlanIFTicateur {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
   
        MainWindow mainWindow = new MainWindow();
        mainWindow.setVisible(true);
    }
}
