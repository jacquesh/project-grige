package grige;

import java.util.logging.Logger;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

public class Graphics
{
	private static final Logger log = Logger.getLogger(Graphics.class.getName());
	
	public static int loadShader(GL2 gl, String vertexShader, String fragmentShader)
	{
		log.info("Loading shader from "+vertexShader+" and "+fragmentShader);
		
		ShaderCode vertShader = ShaderCode.create(gl, GL2.GL_VERTEX_SHADER, 1, Graphics.class, new String[]{"/shaders/"+vertexShader},false);
		vertShader.compile(gl);
		
		ShaderCode fragShader = ShaderCode.create(gl, GL2.GL_FRAGMENT_SHADER, 1, Graphics.class, new String[]{"/shaders/"+fragmentShader},false);
		fragShader.compile(gl);
		
		ShaderProgram newShader = new ShaderProgram();
		newShader.init(gl);
		newShader.add(vertShader);
		newShader.add(fragShader);
		
		newShader.link(gl, System.out);
		
		vertShader.destroy(gl);
		fragShader.destroy(gl);

		return newShader.program();
	}
}
