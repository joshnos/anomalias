
import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.*;

public class Filter_distance implements PlugInFilter {

    ImagePlus imp;
    

    @Override
    public int setup(String arg, ImagePlus imp) 
    {
        
        this.imp = imp;
        return DOES_8G; //Trabaja con cualquier tipo de iamgen 

    }

    @Override
    public void run(ImageProcessor ip) {
        int h = ip.getHeight();
        int w = ip.getWidth();
        int ab;
        int x;
        int y;
        int Z = 0;
        int l;
        int p = 0;
        int j ;
        byte[] pixels = (byte[]) ip.getPixels();
        
      
    
        int[][] pixelM = new int[h][w]; //Estamos construyecto un arreglo con esa funcion
        for (ab = y = 0; y < h; y++) 
            for (x = 0; x < w; x++) 
               pixelM [y][x] = pixels [ab++] & 0xFF;
               
        //----------------------------------------------------------------------
        // Barrido de arriba para abajo de la matriz 
        for (y = 1; y < h - 1; y++) 
        {
            for (x = 1; x < w - 1; x++) 
            {
                if (pixelM[y][x] > 0) 
                {
                    Z = pixelM[y - 1][x];
                    l = pixelM[x - 1][y];
                    if (Z < l) 
                        pixelM[y][x] = Z + 1;
                    else 
                        pixelM[y][x] = l + 1;  
                }
            }
        }
        //----------------------------------------------------------------------
        // Barrido de abajo para arriba de la matriz 
        for (y = h-2 ; y > 0; y--) 
        {
            for (x = w-2; x > 0; x--) 
            {
                if (pixelM[y][x] > 0) 
                {
                    Z = pixelM[y + 1][x];
                    l = pixelM[x + 1][y];
                    if (Z < l) 
                        ab = Z+1;         
                    else 
                        ab = l+1;
                    if (ab < pixelM[y][x])
                        pixelM[y][x] = ab;
                }
            }
        }
        
        //----------------------------------------------------------------------
        //Conversion de la imagen como un arreglo 2d a un vector
        for (ab = y = 0; y < h; y++) {
            for (x = 0; x < w; x++) {
                pixels[ab++] = (byte) pixelM[y][x]; //De matriz a vector
            }
        }

        imp.updateAndDraw();

    }

}
