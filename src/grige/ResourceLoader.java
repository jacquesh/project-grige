package grige;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;

import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.Texture;


public final class ResourceLoader {

	private ResourceLoader(){}
	
	
	public static Texture loadImage(String filepath)
	{
		File sourceFile = new File(filepath);
		
		System.out.println(sourceFile.getAbsolutePath());
		if(!sourceFile.exists())
		{
			System.out.println("Error, attempt to load non-existant file: "+filepath);
			return null;
		}
		
		try{
			GL gl = GameBase.instance.getGLContext().getGL();
			
			TextureData data = TextureIO.newTextureData(gl.getGLProfile(), sourceFile, false, TextureIO.PNG);
			Texture result = new Texture(gl, data);
			
			return result;
			
		}catch(IOException ioe)
		{
			ioe.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}
}
