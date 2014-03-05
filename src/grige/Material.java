package grige;

import java.io.File;
import java.io.IOException;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class Material
{
	private int diffuseMap;
	private int normalMap;
	private int selfIlluminationMap;
	
	private int width;
	private int height;
	
	private Material(int textureWidth, int textureHeight, int diffuse)
	{
		if(diffuse == 0)
			throw new IllegalArgumentException("Invalid Diffuse Texture, cannot be null");
		
		diffuseMap = diffuse;
		normalMap = 0;
		selfIlluminationMap = 0;
		
		width = textureWidth;
		height = textureHeight;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getDiffuseMap()
	{
		return diffuseMap;
	}
	public int getNormalMap()
	{
		return normalMap;
	}
	public int getSelfIlluminationMap()
	{
		return selfIlluminationMap;
	}
	
	
	public static Material createPixel(GL2 gl)
	{
		int[] tex = new int[1];
		gl.glGenTextures(1, tex, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, tex[0]);
		
		float[] pixels = new float[]{1f, 1f, 1f};
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, 1, 1, 0, GL.GL_RGB, GL.GL_FLOAT, FloatBuffer.wrap(pixels));
		
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		
		return new Material(1, 1, tex[0]);
	}
	
	public static Material load(GL2 gl, String filepath)
	{
		File sourceFile = new File(filepath);
		
		Log.info("Loading texture "+sourceFile.getAbsolutePath());
		if(!sourceFile.exists())
		{
			Log.warn("Error, attempt to load non-existant file: "+filepath);
			return null;
		}
		
		try
		{
			TextureData data = TextureIO.newTextureData(gl.getGLProfile(), sourceFile, false, TextureIO.PNG);
			Texture result = new Texture(gl, data);
			
			return new Material(result.getWidth(), result.getHeight(), result.getTextureObject(gl));
			
		}catch(IOException ioe)
		{
			ioe.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}
	
}
