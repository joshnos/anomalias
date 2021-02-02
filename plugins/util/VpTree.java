/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import static java.lang.Math.max;

/**
 *
 * @author Toshiba
 */
//holaaa
public class VpTree {
    
    Node root;
    int kNN;
    int leaf_max_size;
    int nbSelectionVP;
    int subSampling;
    int[] idxPatches;
    
    public VpTree(int nSimilarPatches, int[] nbPatches) {
        this.kNN = nSimilarPatches;
        this.leaf_max_size = max(2 * this.kNN + 1, 5);
        this.idxPatches = nbPatches;
        this.root = this.splitTree(0, this.idxPatches.length);
    }
    
    private Node splitTree(int left, int right) {
        Node tempNode = new Node();
        if( (right - left + 1) <= leaf_max_size){
            tempNode.leftChild = null;
            tempNode.rightChild = null;

            tempNode.left = left;
            tempNode.right = right;
            tempNode.vantagePoint = 0;
            tempNode.splitDistance = 0;
	} else {
            float[] distances = new float[right - left + 1];
            float vantage = this.computeVantagePoint(left, right);
            float maxDist = 0;
            
            for(int i = 0, j = left; j <= right; i++, j++)
            {
		distances[i] = distance(vantage, idxPatches[j]);
		if(distances[i] > maxDist){
                    maxDist = distances[i];
                }
            }
            
            tempNode.left = left;
            tempNode.right = right;

            tempNode.vantagePoint = vantage;

            tempNode.splitDistance = quickselect(left, right, distances);

            tempNode.leftChild = splitTree(left, left + (right - left) / 2);
            tempNode.rightChild = splitTree(left + (right - left) / 2 + 1, right);
        }
        
        return tempNode;
    }
    
    private float quickselect(int left, int right, float[] distances) {
        if(left == right){
            return distances[0];
	}
        int n = left + (right - left) / 2;
	int compteur = 0;
        int l = left;
        int r = right;
        while(true) {
            compteur++;
            int pivot = l + (r - l) / 2;
            pivot = partition(l, r, pivot, distances, n, l);
            if(n == pivot) {
                return distances[n - l];
            } else if(n < pivot) {
		r = pivot - 1;
            } else {
		l = pivot + 1;
            }
        }
    }
    
    private float computeVantagePoint(int left, int right) {
        return 0;
    }
    
    private float distance(float patch1, int patch2) {
        return 0;
    }

    private int partition(int left, int right, int pivot, float[] distances, int n, int l) {
        return 0;
    }
}
