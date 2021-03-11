
import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import static ij.plugin.filter.PlugInFilter.DOES_ALL;
import static ij.plugin.filter.PlugInFilter.DOES_RGB;
import static ij.plugin.filter.PlugInFilter.DONE;
import ij.process.ImageProcessor;
import util.VpTree;

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
    private ImageProcessor ip;
    private int patch_size, num_patches, nbTrees;
    private int nbPatches;
    private int[] idxPatches;
    private int width, height, nChannels;
    int[] image;
    private VpTree trees[];

    @Override
    public int setup(String string, ImagePlus imp) {
        this.imp=imp;
        GenericDialog gd = new GenericDialog("VP Trees");
        gd.addNumericField("Patch size:", 8);
        gd.addNumericField("Number of Patches:", 16);
        gd.showDialog();
        if (gd.wasCanceled())
            return DONE;
        patch_size=(int)gd.getNextNumber();
        num_patches=(int)gd.getNextNumber();
        nbTrees=4;
        return IJ.setupDialog(imp,DOES_ALL);
    }

    @Override
    public void run(ImageProcessor ip) {
        setValues(ip);
        createTrees();
    }
    
    public void setValues(ImageProcessor ip) {
        this.ip = ip;
        image = (int[]) ip.getPixels();
        width = ip.getWidth();
        height = ip.getHeight();
        nChannels = ip.getNChannels();
        trees = new VpTree[nbTrees];
        nbPatches = (width - patch_size + 1) * (height - patch_size + 1);
        idxPatches = new int[nbPatches];
        getAllPatches();
    }
    
    private void getAllPatches() {
        for (int x = 0, k = 0; x < width - patch_size + 1; x++) {
            for (int y = 0; y < height - patch_size + 1; y++, k++) {
                idxPatches[k] = (x + y * width);
            }
        }
    }
    
    private void createTrees() {
        for (int i = 0; i < nbTrees; i++) {
            trees[i] = new VpTree(num_patches, idxPatches, patch_size, ip);
        }
        for (int i = 0; i < nbTrees; i++) {
            System.out.println("Tree " + i);
            trees[i].printTree(trees[i].root);
        }
    }
    
    /*        r = new int[size];
        g = new int[size];
        b = new int[size]; 
        int[] pixels=(int[])ip.getPixels();
        for (i= 0;i<size;i++)
        {
            int c=pixels[i];
            r[i]=(c&0xff0000)>>16;
            g[i]=(c&0x00ff00)>>8;
            b[i]=c&0x0000ff;
        }
    */
    
}
