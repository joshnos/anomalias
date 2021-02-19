package newpackage;

import ij.*;
import ij.process.*;
import ij.plugin.filter.*;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import static ij.plugin.filter.PlugInFilter.DOES_8C;
import static ij.plugin.filter.PlugInFilter.DOES_8G;

public class Vp_tree implements PlugInFilter {

    ImagePlus imp;
    int k= 0;
    int aux1,aux2;
    int width;
    int height;
    int patch_size;
    int patch_watch;
    int col = 255;

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
        int sideh; // poscion inicial del parche
        int sidew; // poscion inicial del parche
        int patch_sideh; //tamaño de un lado del parche
        int patch_sidew; //tamaño de un lado del parche
        int h=0; //altura de la imagen en numero de parches 
        int w=0; //ancho de la imagen en numero de parches 
        int nbPatches; //numero de parches
        byte[] parches = new byte[w*h]; //matriz donde van a ir las otras matrices de los parches
        //int parche[] = new int[patch_sidew*patch_sideh];  //este array va en parches[]y corresponde a cada uno de los parches
        byte[] pixels = (byte[]) ip.getPixels(); //imagen
        int[][] pixels2 = new int[width][height]; //pixeles int
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                pixels2[i][j] = ip.getPixel(i, j);
            }
        }
        //SE DETERMINA AUX (SI ES DIVISIBLE O NO LA IMAGEN EN EL PARCHE)
        for (int i = height; i > 1; i--) {
            if (i % patch_size == 0) {
                aux1 = height - i;
                IJ.log("aux1=" + aux1);
                break;
            }
        }
        if (aux1 > 0) {
            h = ((int) (height / patch_size)) + 1;
        } 
        else {
            h = (int) (height / patch_size);
        }
        for (int i = width; i > 1; i--) {
            if (i % patch_size == 0) {
                aux2 = width - i;
                IJ.log("aux2=" + aux2);
                break;
            }
        }        
        if (aux2 > 0) {
            w = ((int) (width / patch_size)) + 1;
        } 
        else {
            w = (int) (width / patch_size);
        }
        nbPatches = h*w;
        IJ.log("NUMERO DE PARCHES: " + nbPatches);
        
        //FILA DE PARCHES
        for (int j = 0; j < h; j++) {
            if (j > 0 && aux1 > 0) {
                sideh = aux1 + (patch_size * (j - 1));
                patch_sideh = patch_size;
            } else if (aux1 == 0) {
                sideh = j * patch_size;
                patch_sideh = patch_size;
            } else {
                sideh = 0;
                patch_sideh = aux1;
            }
            //COLUMNA DE PARCHES
            for (int i = 0; i < w; i++, k++) {
                if (i > 0 && aux2 > 0) {
                    sidew = aux2 + (patch_size * (i - 1));
                    patch_sidew = patch_size;
                } else if (aux2 == 0) {
                    sidew = i * patch_size;
                    patch_sidew = patch_size;
                } else {
                    sidew = 0;
                    patch_sidew = aux2;
                }
                int parche[] = new int[patch_sidew*patch_sideh];
                for (int jj = sideh, y=0; jj < sideh + patch_sideh; jj++,y++) {
                    for (int ii = sidew,x=0; ii < sidew + patch_sidew; ii++,x++) {
                            //AQUI SE ALMACENAN LOS PARCHES. EN UNA MATRIZ DE MATRICES
                            //if (k== patch_watch)
                                parche[y*patch_sidew+x] = pixels2[ii][jj];
                                //IJ.log("MATRIZ PARCHES N°"+ x + ": "+parche[x]);
                            
                    }
                }
                //parches[k] = parche[x];
                //IJ.log("MATRIZ PARCHES  "+ parche[x]);
                if (k == patch_watch){
                    ImagePlus impH = NewImage.createByteImage("Image with homogeneous blocks", patch_sidew, patch_sideh, 1, NewImage.FILL_BLACK);
                    ImageProcessor ipH = impH.getProcessor();
                    //pixeslss es la imagen vacía que comenzamos a llenar
                    byte[] pixelss = (byte[]) ipH.getPixels();
                    for (int jjj = 0; jjj < patch_sideh; jjj++) {
                        for (int iii = 0; iii < patch_sidew; iii++) {
                            //me hace falta almacenarlos en una matriz
                            pixelss[jjj * patch_sidew + iii] = (byte) parche[jjj * patch_sidew + iii];
                        }
                    }
                    impH.show();
                }
                
                /*if (k == patch_watch) {
                    ImagePlus impH = NewImage.createByteImage("Image with homogeneous blocks", patch_sidew, patch_sideh, 1, NewImage.FILL_BLACK);
                    ImageProcessor ipH = impH.getProcessor();
                    //pixeslss es el parche, es donde se almacena y se visualiza
                    byte[] pixelss = (byte[]) ipH.getPixels();
                    for (int jjj = 0; jjj < patch_sideh; jjj++) {
                        for (int iii = 0; iii < patch_sidew; iii++) {
                            //me hace falta almacenarlos en una matriz
                            pixelss[jjj * (patch_sidew) + iii] = (byte) pixels2[iii][jjj];
                        }
                    }
                    impH.show();
                }*/
            }
        }
        //PARA LLAMAR EL PARCHE
        //IJ.log("dddd"+idxPatches[0]);
        //imp.updateAndDraw();
        
        getAllPackages();
        
    }
    private void getAllPackages(){
    
     }
           
}
