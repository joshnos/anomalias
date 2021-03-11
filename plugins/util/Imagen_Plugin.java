/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import ij.*;
import ij.process.*;
import ij.plugin.filter.*;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import static ij.plugin.filter.PlugInFilter.DOES_8C;
import static ij.plugin.filter.PlugInFilter.DOES_8G;

public class Imagen_Plugin implements PlugInFilter {

    ImagePlus imp;
    int j, i, p = 0;
    int aux1, aux2;
    int width;
    int height;
    int patch_size;   //tamaño del lado de parche
    int patch_watch;  //parche escodgido
    int nbParches; //numero de parches
    int kw11, kw12, kw21, kw22;
    int bigheight, bigwidth;

    @Override
    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        GenericDialog gd = new GenericDialog("PARAMETROS");
        gd.addNumericField("tamanio de parche", 16, 0);
        gd.addNumericField("Posicion parche requerido", 0, 0);
        gd.showDialog();
        if (gd.wasCanceled()) {
            return -1;
        }
        patch_size = (int) gd.getNextNumber();
        patch_watch = (int) gd.getNextNumber();
        return IJ.setupDialog(imp, DOES_8G + DOES_8C);
    }

    public void run(ImageProcessor ip) {
        setValues(ip);
    }

    public void setValues(ImageProcessor ip) {

        width = imp.getWidth();
        height = imp.getHeight();
        byte[] pixels_original = (byte[]) ip.getPixels();  //imagen

        //---------------------------------------------------------------------------
        //con aux1 se determina si  el ancho de la imagen se deben expandir
        for (int i = width; i > 1; i++) {
            if (i % patch_size == 0) {
                aux1 = i - width;
                IJ.log("aux1=" + aux1);
                break;
            }
        }
        if (aux1 == 0) {
            kw11 = kw12 = 0;
            IJ.log("if: " + kw11 + " y  " + kw12);
        } else if (aux1 % 2 == 0) {
            kw11 = kw12 = aux1 / 2;
            IJ.log("else if: " + kw11 + " y " + kw12);
        } else {
            kw11 = aux1 / 2 + 1;
            kw12 = aux1 / 2;
            IJ.log("else " + kw11 + " y  " + kw12);
        }
        bigwidth = width + kw11 + kw12;
        IJ.log("bigwidth: " + bigwidth);

        //---------------------------------------------------------------------------
        //con aux2 se determina si la altura de la imagen se deben expandir
        for (int i = height; i > 1; i++) {
            if (i % patch_size == 0) {
                aux2 = i - height;
                IJ.log("aux2=" + aux2);
                break;
            }
        }
        if (aux2 == 0) {
            kw21 = kw22 = 0;
            IJ.log("if: " + kw21 + " y  " + kw22);
        } else if (aux2 % 2 == 0) {
            kw21 = kw22 = aux2 / 2;
            IJ.log("else if: " + kw21 + " y " + kw22);
        } else {
            kw21 = aux2 / 2 + 1;
            kw22 = aux2 / 2;
            IJ.log("else " + kw21 + " y  " + kw22);
        }
        bigheight = height + kw21 + kw22;
        IJ.log("bigheight: " + bigheight);

        //---------------------------------------------------------------------------
        //Se pone la imagen en el centro de la matriz aumentada        
        int[][] pixels_nueva = new int[bigheight][bigwidth];
        for (int a = 0, i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixels_nueva[i + kw21][j + kw11] = pixels_original[a++];
            }
        }

        //----------------------------------------------------------------------
        //Duplicate the pixels of the left and right sides of the image 
        for (i = kw21; i < bigheight - kw22; i++) {
            j = kw11 * 2 - 1;
            for (p = 0; p < kw11; p++) {
                pixels_nueva[i][p] = pixels_nueva[i][j--];
            }
            j = (bigwidth - 2 * kw12);
            for (p = bigwidth - 1; p > bigwidth - kw12 - 1; p--) {
                pixels_nueva[i][p] = pixels_nueva[i][j++];
            }
        }

        //----------------------------------------------------------------------
        //Duplicate the pixels of the upper and lower parts of the image
        for (i = 0; i < bigwidth; i++) {
            j = kw21 * 2 - 1;
            for (p = 0; p < kw21; p++) {
                pixels_nueva[p][i] = pixels_nueva[j--][i];
            }
            j = (bigheight - 2 * kw22);
            for (int p = bigheight - 1; p > bigheight - kw22 - 1; p--) {
                pixels_nueva[p][i] = pixels_nueva[j++][i];
            }
        }

        IJ.log("NUMERO DE PARCHES <3: " + (bigwidth / patch_size) * (bigheight / patch_size));

        ImagePlus impH = NewImage.createByteImage("Image with homogeneous blocks", bigwidth, bigheight, 1, NewImage.FILL_BLACK);
        ImageProcessor ipH = impH.getProcessor();
        byte[] pixelss = (byte[]) ipH.getPixels();
        //toca hcaer algo co esa j
        for (i = 0; i < bigheight; i++) {
            for (j = 0; j < bigwidth; j++) {
                pixelss[i * bigwidth + j] = (byte) pixels_nueva[i][j];
            }
        }
        impH.show();
        getDistance();
    }
    
    private void getDistance(){
        
        //int x= (patch1-patch2);
        
        //Math.sqrt();
    }
}
