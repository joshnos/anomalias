
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

public class Filter_contrast implements PlugInFilter {

    ImagePlus imp;
    double contraste = 1.1;
    int rMin = 0;
    @Override
    public int setup(String arg, ImagePlus imp)
    {
        GenericDialog gd = new GenericDialog("Filter Contraste");
        gd.addNumericField("Contraste: ", contraste, 0); // significa casillas decimales (0)
        gd.addNumericField("Valor minimo: ", rMin, 0);
        gd.showDialog();
        if (gd.wasCanceled()) {
            return DONE;
        }
        contraste = (double) gd.getNextNumber();
        rMin = (int) gd.getNextNumber();
        this.imp = imp;
        return DOES_8G; //Trabaja con cualquier tipo de iamgen 
    }

    @Override
    public void run(ImageProcessor ip) 
    {
       int height = ip.getHeight();
       int width = ip.getWidth();
       int pixel;
       int qMin = 255;
       byte[]pixels = (byte[])ip.getPixels();  
       //-----------------------------------------------------------------------
       //Averigua q min.
        
       for(int i=0; i < height*width; i++) 
     {
        if(qMin > (pixels[i]&0xff))
            qMin = (pixels[i]&0xff);
     }
       
       for(int i=0; i < height*width; i++)
       {
           pixel = (int)((pixels[i] & 0xFF )* contraste + 0.5);
          pixels[i]= (byte)((contraste * (pixel-qMin)) + rMin);  
           
       }
     imp.updateAndDraw();
     
    }
    
   

}
