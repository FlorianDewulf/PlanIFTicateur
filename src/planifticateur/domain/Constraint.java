/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planifticateur.domain;

/**
 *
 * @author Antoine
 */
public class Constraint {

    private double startMin;
    private double startMax;
    private double endMax;
    
    public Constraint(double startMin, double endMax) {
        this.startMin = startMin;
        this.endMax = endMax;
    }

    public double getStartMin() {
        return startMin;
    }

    public double getEndMax() {
        return endMax;
    }
    
}
