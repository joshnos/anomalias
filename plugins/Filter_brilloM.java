
import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.*;

public class Filter_brilloM implements PlugInFilter {

    ImagePlus imp;
    int brillo = 50;
   
    @Override
    public int setup(String arg, ImagePlus imp) {
        GenericDialog gd = new GenericDialog("Filter Brillo");
        gd.addNumericField("Brillo: ", brillo, 0);
        gd.showDialog();
        if (gd.wasCanceled()) {
            return DONE; //DONE devuelve un "hecho" para cuando es entero
        }

        brillo = (int) gd.getNextNumber();

        this.imp = imp;
        return DOES_8G;

    }

    @Override
    public void run(ImageProcessor ip) {
        int ab;
        int x; 
        int y;
        int h = ip.getHeight();
        int w = ip.getWidth();
        byte[] pixels = (byte[])ip.getPixels();
        int pixel;
        
        int[][] pixelM = new int[h][w];
        for (ab = y = 0; y < h; y++)
        {
            for (x = 0; x < w; x++)
            {
                pixel = (pixels[ab++]&0xff) + 50;
                pixelM[y][x] = (byte) (pixel > 255 ? 255 : pixel < 0 ? 0 : pixel);
            }
        }
        for (ab = y = 0; y < h; y++)
        {
            for (x = 0; x < w; x++)
            {
                 pixels[ab++] = (byte)pixelM[y][x]; //De matriz a vector
            }
        }
        
        imp.updateAndDraw();
        

    }
}
