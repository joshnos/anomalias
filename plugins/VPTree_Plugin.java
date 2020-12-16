
import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import static ij.plugin.filter.PlugInFilter.DOES_ALL;
import static ij.plugin.filter.PlugInFilter.DOES_RGB;
import static ij.plugin.filter.PlugInFilter.DONE;
import ij.process.ImageProcessor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Toshiba
 */
public class VPTree_Plugin implements PlugInFilter {
    
    private ImagePlus imp;
    private int patch_size, num_patches, nbTrees;
    private int nbPatches;
    private float[] idxPatches;
    private int width, height, nChannels;

    @Override
    public int setup(String string, ImagePlus ip) {
        this.imp=imp;
        GenericDialog gd = new GenericDialog("VP Trees");
        gd.addNumericField("Parch size:", 8);
        gd.addNumericField("Number of Patches:", 16);
        gd.showDialog();
        if (gd.wasCanceled())
            return DONE;
        patch_size=(int)gd.getNextNumber();
        num_patches=(int)gd.getNextNumber();
        nbTrees=4;
        return IJ.setupDialog(imp,DOES_ALL-DOES_RGB);
    }

    @Override
    public void run(ImageProcessor ip) {
        setValues();
    }
    
    public void setValues() {
        width=imp.getWidth();
        height=imp.getHeight();
        nChannels=imp.getNChannels();
        nbPatches=(width - patch_size + 1) * (height - patch_size + 1);
        getAllPackages();
    }
    
    private void getAllPackages() {
        for (int x = 0, k = 0; x < width - patch_size + 1; x++) {
            for (int y = 0; y < height - patch_size + 1; y++, k++) {
                idxPatches[k] = nChannels * (x + y * width);
            }
        }
    }
    
    private void splitTree() {
        
    }
    
}
