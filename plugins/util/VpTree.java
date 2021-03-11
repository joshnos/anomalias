/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import ij.process.ImageProcessor;
import static java.lang.Math.max;
import java.util.Random;

/**
 *
 * @author Toshiba
 */
public class VpTree {
    
    public Node root;
    int kNN;
    int leaf_max_size;
    int nbSelectionVP;
    int subSampling;
    int patchSize;
    int[] idxPatches;
    int[] image;
    private int width, height, nChannels;
    private ImageProcessor ip;
    
    public VpTree(int nSimilarPatches, int[] nbPatches, int patch_size, ImageProcessor ip) {
        this.kNN = nSimilarPatches;
        this.leaf_max_size = max(2 * this.kNN + 1, 5);
        this.idxPatches = nbPatches;
        this.patchSize = patch_size; 
        this.ip = ip;
        this.setValues();
        this.root = this.splitTree(0, this.idxPatches.length - 1);
    }
    
    private void setValues() {
        image = (int[]) ip.getPixels();
        width = ip.getWidth();
        height = ip.getHeight();
        nChannels = ip.getNChannels();
        nbSelectionVP = 1;
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
            int vantage = this.computeVantagePoint(left, right);
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
    
    private int computeVantagePoint(int left, int right) {
        this.shuffleArray(idxPatches, left, right + 1);
        int[] elementToConsider = new int[nbSelectionVP];
        
        for (int i = 0; i < nbSelectionVP; i++) {
            elementToConsider[i] = idxPatches[left + i];
        }
        
        float maxVar = 0;
	int interestingPoint = 0;
	if(nbSelectionVP == 1)
	{
            // Construct a VP tree for a forest, in this case no other computation need to be done. We choose a random VP
            return elementToConsider[0];
	} else {
            // Otherwise select the best candidates from a subsample of possible vantage points
            elementToConsider = new int[nbSelectionVP];
            int[] subSample = new int[(Math.min(right-left+1, subSampling))];
            float[] distancesToSubs = new float[(Math.min(right - left + 1, subSampling))];
            
            this.shuffleArray(idxPatches, left, right + 1);
            
            for (int i = 0; i < subSample.length; i++) {
                subSample[i] = idxPatches[left + i];
            }
            
            for(int i = 0; i < nbSelectionVP; ++i) {
                for(int j = 0; j < subSample.length; ++j) {
                    distancesToSubs[j] = this.distance(elementToConsider[i], subSample[j]); 
                }
                
                float medianTemp = quickselect(left, left + distancesToSubs.length - 1, distancesToSubs);
                
                float var = 0;
                for (int j = 0; j < distancesToSubs.length; j++) {
                    var += (distancesToSubs[j] - medianTemp) * (distancesToSubs[j] - medianTemp);
                }
                if(var > maxVar)
                {
                    maxVar = var;
                    interestingPoint = i;
                }
            }
        }
        return elementToConsider[interestingPoint];
    }
    
    private float distance(int patch1, int patch2) {
        float dist = 0.f, dif;
        for (int hy = 0; hy < this.patchSize; hy++) {
            for (int hx = 0; hx < this.patchSize; hx++) {
                float p1 = this.image[patch1 + hx + hy * width];
                float p2 = this.image[patch2 + hx + hy * width];
                dif = p1 - p2;
                dist += (p1 - p2) * dif;
                
            }
        }
        return (float) Math.sqrt(dist / (this.patchSize * this.patchSize)) / 255.f;
    }

    private int partition(int left, int right, int pivot, float[] distances, int n, int l) {
        float valuePivot = distances[pivot - l];

	int temp = idxPatches[pivot];
	idxPatches[pivot] = idxPatches[right];
	idxPatches[right] = temp;
	float tempF = distances[pivot - l];
	distances[pivot - l] = distances[right - l];
	distances[right - l] = tempF;

	int store = left;
	int allEqual = 0;
        
        for (int i = left; i <= right; i++) {
            float candidate = distances[i - l];
            
            if(Math.abs(candidate - valuePivot) < 0.000001) {
                allEqual++;
            }
            
            if(candidate < valuePivot) {
		temp = idxPatches[store];
		idxPatches[store] = idxPatches[i];
		idxPatches[i] = temp;
		tempF = distances[store - l];
		distances[store - l] = distances[i - l];
		distances[i - l] = tempF;
		++store;
            }
        }

        temp = idxPatches[store];
	idxPatches[store] = idxPatches[right];
	idxPatches[right] = temp;
	tempF = distances[store - l];
	distances[store - l] = distances[right - l];
	distances[right - l] = tempF;

	if(allEqual >= (right - left) / 2) {
            return n;
        }

	// return the index where the pivot is
	return store;
    }
    
    private void shuffleArray(int[] a, int primero, int ultimo) {
        int n = a.length;
        Random random = new Random();
        random.nextInt();
        for (int i = primero; i < ultimo; i++) {
            if (n>i) {
                int change = i + random.nextInt(n - i);
                swap(a, i, change);
            }
            
        }
    }

    private void swap(int[] a, int i, int change) {
        int helper = a[i];
        a[i] = a[change];
        a[change] = helper;
    }
    
    public void printTree(Node raiz) {
        System.out.println("Vantage" + raiz.vantagePoint);
        System.out.println("Distance" + raiz.splitDistance);
        if (raiz.leftChild != null) {
            printTree(raiz.leftChild);
        }
        if (raiz.rightChild != null) {
            printTree(raiz.rightChild);
        }
    }
}
