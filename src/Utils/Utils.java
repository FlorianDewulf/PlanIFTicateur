/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;


/**
 *
 * @author Antoine
 */
public class Utils {
    
    public static String findCheFileName(String fileCou) {
        String CHEFilename = "";
        String couPath[];
        couPath = fileCou.split("\\.");
        couPath[couPath.length - 1] = ".che";
        for (String part : couPath) {
            CHEFilename += part;
        }
        return CHEFilename;
    }

    public static String findCmtFileName(String fileCou) {
        String CMTFilename = "";
        String couPath[];
        couPath = fileCou.split("\\.");
        couPath[couPath.length - 1] = ".cmt";
        for (String part : couPath) {
            CMTFilename += part;
        }
        return CMTFilename;
    }
}
