/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Toshiba
 */
public class Node {
    
    int index;
    int left, right;
    float vantagePoint;
    float splitDistance;
    float diameter;
    Node leftChild;
    Node rightChild;
    
    public Node() {
    }
    
    public Node(int index) {
        this.index = index;
    }
    
    public void setVantagePoint(float vantagePoint) {
        this.vantagePoint = vantagePoint;
    }
    
    public void setSplitDistance(float splitDistance) {
        this.splitDistance = splitDistance;
    }
    
    public void setDiameter(float diameter) {
        this.diameter = diameter;
    }
    
}
