/*
*Gaussian_vectorfilter.java
*Created on 21 November 2019,23:38 by Manuel Guillermo Forero-Vargas
*e-mail: mgforero@yahoo.es
*
*Gaussian filter algorithm employing vectors
*Function: This plug-in convolves the image with a Gaussian filter of standard
*deviation giving by the user, employing vectors instead of a mask.
*
*This plugin do not create a new stack.
*
*Copyright (c)2019 by Manuel Guillermo Forero-Vargas
*e-mail: mgforero@yahoo.es
*
*This plugin is free software;you can redistribute it and/or modify
*it under the terms of the GNU General Public License version 2
*as published by the Free Software Foundation.
*
*This program is distributed in the hope that it will be useful,
*but WITHOUT ANY WARRANTY;without even the implied warranty of
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
*GNU General Public License for more details.
*
*You should have received a copy of the GNU General Public License
*along with this plugin;if not,write to the Free Software
*Foundation,Inc.,675 Mass Ave,Cambridge,MA 02139,USA.
*/
import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.*;

public class Gaussian_vectorfilter implements PlugInFilter
{
    ImagePlus imp;
    float[] kernel;
    float scale,sigma=1f;
    int bigHeight,height,bigWidth,width;
    int kw,size,type,vc,vc2;

    @Override
    public int setup(String arg, ImagePlus imp) 
    {
        int a,i;

        this.imp=imp;
        GenericDialog gd=new GenericDialog("Gaussian filter");
        gd.addNumericField("Sigma:",sigma,1);
        gd.showDialog();
        if (gd.wasCanceled())
            return DONE;
        sigma=(float)gd.getNextNumber();
        width=imp.getWidth();
        height=imp.getHeight();
        size=width*height;
        //----------------------------------------------------------------------
        //Gets kernel
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
        //------------------------------------------------------------------
        bigWidth=width+kw-1;
        bigHeight=height+kw-1;
        type=imp.getType();
        return IJ.setupDialog(imp,DOES_ALL-DOES_RGB);
    }

    @Override
    public void run(ImageProcessor ip)
    {
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
/*        //Charge the original image
        float[][] pixels=new float[bigHeight][bigWidth];
        //Copy the pixels in the middle of the enlarged image
        for(a=y=0;y<height;y++)
            for(x=0;x<width;x++)
                pixels[y+vc][x+vc]=pixelsR[a++];
        //----------------------------------------------------------------------
        //Duplicate the pixels of the upper and lower parts of the image
        for(x=vc;x<bigWidth-vc;x++)
        {
            y=vc2-1;
            for(a=0;a<vc;a++)
                pixels[a][x]=pixels[y--][x];
            y=bigHeight-vc2;
            for(a=bigHeight-1;a>bigHeight-vc-1;a--)
                pixels[a][x]=pixels[y++][x];
        }
        //----------------------------------------------------------------------
        //Duplicate the pixels of the left and right sides of the image
        for(y=0;y<bigHeight;y++)
        {
            x=vc2-1;
            for(a=0;a<vc;a++)
                pixels[y][a]=pixels[y][x--];
            x=bigWidth-vc2;
            for(a=bigWidth-1;a>bigWidth-vc-1;a--)
                pixels[y][a]=pixels[y][x++];
        }*/
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
        imp.updateAndDraw();
    }
}