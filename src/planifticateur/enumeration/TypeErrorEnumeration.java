/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planifticateur.enumeration;

/**
 *
 * @author floriandewulf
 */
public class TypeErrorEnumeration {
    public enum TypeError {
        SCHEDULING_CONFLICT,
        CONSTRAINT_ERROR,
        FILE_NOT_FOUND,
        INCORRECT_FILE,
        EXPORT_ERROR,
        SAVE_ERROR,
        IO_ERROR,
        GENERIC_ERROR
    }
}
