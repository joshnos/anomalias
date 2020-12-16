import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

public class Filter_Plugin implements PlugInFilter {
	ImagePlus imp;
        int brillo = 50;

	public int setup(String arg, ImagePlus imp) {
            GenericDialog gd = new GenericDialog("Cambio de Brillo");
            gd.addNumericField("Brillo: ", brillo, 0);
            gd.showDialog();
            if (gd.wasCanceled()) return DONE;
            brillo = (int)gd.getNextNumber();
            this.imp = imp;
            return DOES_8G;
	}

	public void run(ImageProcessor ip) {
            int height = ip.getHeight();
            int width = ip.getWidth();
            byte [] pixels = (byte[]) ip.getPixels();
            int pixel;
            for (int i = 0; i < (height * width); i++) {
                pixel = (pixels[i] & 0xff) + brillo;
                pixels[i] = (byte) (pixel>255?255:pixel<0?0:pixel);
            }
            imp.updateAndDraw();
            //ip.findEdges();
            //ip.invert();
	}

}
