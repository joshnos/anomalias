
import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import static ij.plugin.filter.PlugInFilter.DOES_ALL;
import static ij.plugin.filter.PlugInFilter.DOES_RGB;
import static ij.plugin.filter.PlugInFilter.DONE;
import ij.process.ImageProcessor;

public class multi_scaling implements PlugInFilter {
    
    private ImagePlus imp;
    private float[] kernel;
    private float scale,sigma=1f;
    private int bigHeight,height,bigWidth,width;
    private int kw,size,type,vc,vc2, numberOfLevels;
    
    @Override
    public int setup(String arg, ImagePlus imp) 
    {
        this.imp=imp;
        GenericDialog gd = new GenericDialog("Multi scaling");
        gd.addNumericField("Levels:", 4);
        gd.addNumericField("Sigma:", 1.39, 2);
        gd.showDialog();
        if (gd.wasCanceled())
            return DONE;
        numberOfLevels=(int)gd.getNextNumber();
        sigma=(float)gd.getNextNumber();
        getKernel();
        type=imp.getType();
        return IJ.setupDialog(imp,DOES_ALL-DOES_RGB);
    }
    
    @Override
    public void run(ImageProcessor ip) {
	ImagePlus[] impVector = new ImagePlus[numberOfLevels];
        impVector[0] = imp;
        for (int i = 1; i < numberOfLevels; i++) {
            setValues(impVector[i-1]);
            impVector[i-1] = gaussianVectorFilter(impVector[i-1]);
            impVector[i] = scaleImage(impVector[i-1], 0.5, i+1);
        }
        for (int i = 1; i < numberOfLevels; i++) {
            impVector[i].show();
            impVector[i].trimProcessor();
            impVector[i].changes = true;
        }
    }
    
    public ImagePlus scaleImage(ImagePlus imp, double scale, int level) {
        ImageProcessor ip = imp.getProcessor();
        ImagePlus impAux = imp.createImagePlus();
        int newWidth = (int)Math.round(width*scale);
        int newHeght = (int)Math.round(height*scale);
        impAux.setProcessor("Level: " + level, ip.resize(newWidth, newHeght));
        return impAux;
    }
    
    public void getKernel(){
        int a,i;
        String aux="";
        for (i=1;i<100;i++)
        {
            double gauss=Math.exp(-(i*i)/(2.*sigma*sigma));
            if (gauss>0.003)
                aux+=gauss+",";
            else
                break;
        }
        kernel=new float[aux.split(",").length*2+1];
        kw=kernel.length;
        vc=kw/2;
        vc2=2*vc;
        scale=kernel[vc]=1f;//Central weight of the vector
        String[] weights=aux.split(",");
        for (a=0,i=vc-1;i>=0;i--) 
        {
            kernel[a]=Float.parseFloat(weights[i]);
            scale+=kernel[a];
            kernel[kw-1-a]=Float.parseFloat(weights[i]);
            scale+=kernel[a++];
        }
    }
    
    public void setValues(ImagePlus imp) {
        width=imp.getWidth();
        height=imp.getHeight();
        size=width*height;
        bigWidth=width+kw-1;
        bigHeight=height+kw-1;
    }
    
    public ImagePlus gaussianVectorFilter(ImagePlus imp) {
        ImageProcessor ip = imp.getProcessor();
        int a,b,offset,v,x,y;
        float sum=0;
        //----------------------------------------------------------------------
        float[] pixelsR=new float[size];
        switch(type)//Convert images to float
        {
            case 0://GRAY8
                byte[]pixelsB=(byte[])ip.getPixels();
                for(a=0;a<size;a++)
                    pixelsR[a]=(float)(pixelsB[a]&0xff);
                break;
            case 1://GRAY16
                short[]pixelsS=(short[])ip.getPixels();
                for(a=0;a<size;a++)
                    pixelsR[a]=(float)pixelsS[a];
                break;
            case 2://GRAY32
                float[]pixelsF=(float[])ip.getPixels();
                for(a=0;a<size;a++)
                    pixelsR[a]=pixelsF[a];
                break;
        }
        //----------------------------------------------------------------------
        //Charge the original image
        float[] pixels=new float[bigHeight*bigWidth];
        //Copy the pixels in the middle of the enlarged image
        for(a=y=0;y<height;y++)
        {
            offset=(y+vc)*bigWidth+vc;
            for(x=0;x<width;x++)
                pixels[offset+x]=pixelsR[a++];
        }
        //----------------------------------------------------------------------
        //Duplicate the pixels of the upper and lower parts of the image
        for(x=vc;x<bigWidth-vc;x++)
        {
            y=vc2-1;
            for(a=0;a<vc;a++)
                pixels[a*bigWidth+x]=pixels[y--*bigWidth+x];
            y=bigHeight-vc2;
            for(a=bigHeight-1;a>bigHeight-vc-1;a--)
                pixels[a*bigWidth+x]=pixels[y++*bigWidth+x];
        }
        //----------------------------------------------------------------------
        //Duplicate the pixels of the left and right sides of the image
        for(y=0;y<bigHeight;y++)
        {
            x=vc2-1;
            offset=y*bigWidth;
            for(a=0;a<vc;a++)
                pixels[offset+a]=pixels[offset+x--];
            x=bigWidth-vc2;
            for(a=bigWidth-1;a>bigWidth-vc-1;a--)
                pixels[offset+a]=pixels[offset+x++];
        }
        //----------------------------------------------------------------------
        //Convolve
        float[] pixels2= new float[bigHeight*bigWidth];
        for(y=vc;y<bigHeight-vc;y++)//Convolve in the x axis
        {
            offset=y*bigWidth;
            for(x=vc;x<bigWidth-vc;x++)
            {
                a=offset+x;
                for(b=0,sum=0,v=-vc;v<=vc;v++)
                    sum+=pixels[a+v]*kernel[b++];
                pixels2[a]=sum/scale;
            }
        }
        for(y=vc;y<bigHeight-vc;y++)//Convolve in the y axis
        {
            offset=y*bigWidth;
            for(x=vc;x<bigWidth-vc;x++)
            {
                a=offset+x;
                for(b=0,sum=0,v=-vc;v<=vc;v++)
                    sum+=pixels2[a+v*bigWidth]*kernel[b++];
                pixelsR[(y-vc)*width+x-vc]=sum/scale;
            }
        }
        //----------------------------------------------------------------------
        switch(type)//Convert images to the original type
        {
            case 0://GRAY8
                byte[]pixelsB=(byte[])ip.getPixels();
                for (a=0;a<size;a++)
                    pixelsB[a]=(byte)(pixelsR[a]+.5);
                break;
            case 1://GRAY16
                short[]pixelsS=(short[])ip.getPixels();
                for (a=0;a<size;a++)
                    pixelsS[a]=(short)(pixelsR[a]+.5);
                break;
            case 2://GRAY32
                float[]pixelsF=(float[])ip.getPixels();
                for (a=0;a<size;a++)
                    pixelsF[a]=pixelsR[a];
                break;
        }
        imp.updateImage();
        return imp;
    }
    
}
