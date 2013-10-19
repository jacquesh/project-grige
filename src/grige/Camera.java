package grige;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.util.texture.Texture;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;

import com.jogamp.opengl.math.FloatUtil;

import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

public class Camera {	
	
	private final float[] quadVertices = {
			-0.5f, -0.5f, 0.0f,
			-0.5f, 0.5f, 0.0f,
			0.5f, -0.5f, 0.0f,
			0.5f,  0.5f, 0.0f,	
	};
	
	private final float[] quadTextureCoords = {
			0.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,
	};
	
	private final int[] quadIndices = {
			0, 1, 2, 3,
	};
	
	//Camera attributes
	private float x;
	private float y;
	private float width;
	private float height;
	private float depth;
	
	//Current transformation matrices
	private float[] projectionMatrix;
	private float[] viewingMatrix;
	
	//Buffers
	private int[] vertex_array_objects = new int[1];
	private int vao;
	
	private int[] vertex_data_buffers = new int[3];
	
	//Shader Data
	private ShaderProgram shaderProg;
	
	//GL context
	private GL2 gl;
	
	public Camera(float startWidth, float startHeight, float startDepth)
	{
		width = startWidth;
		height = startHeight;
		depth = startDepth;
	}
	
	public void setPosition(float newX, float newY)
	{
		x = newX;
		y = newY;
		
		viewingMatrix = new float[]{1,0,0,0, 0,1,0,0, 0,0,1,0, -x-width/2,-y-height/2,0,1f};
	}
	
	public void setSize(float newWidth, float newHeight, float newDepth)
	{
		width = newWidth;
		height = newHeight;
		depth = newDepth;
		
		projectionMatrix = new float[]{2/width,0,0,0, 0,2/height,0,0, 0,0,-2f/depth,0, 0,0,-1,1};
		setPosition(x,y); //Update the viewing matrix as well, because the size has changed (so we need to translate (0,0) differently)
	}
	
	public float getX() { return x; }
	public float getY() { return y; }
	public float getWidth() { return width; }
	public float getHeight() { return height; }
	public float getDepth() { return depth; }
	
	protected void initialize(GL glContext)
	{
		gl = glContext.getGL2();
		loadShader();
		
		setSize(width,height,depth);
		setPosition(0,0);
		
		//Create our Vertex Array Object
		gl.glGenVertexArrays(1, vertex_array_objects, 0);
		vao = vertex_array_objects[0];
		gl.glBindVertexArray(vao);
		
		//Generate and store the required buffers
		gl.glGenBuffers(3, vertex_data_buffers,0); //Indices, VertexLocations, TextureCoordinates
		int indexBuffer = vertex_data_buffers[0];
		int vertexBuffer = vertex_data_buffers[1];
		int texCoordBuffer = vertex_data_buffers[2];
		
		//Buffer the vertex indices
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, quadIndices.length*(Integer.SIZE/8), IntBuffer.wrap(quadIndices), GL.GL_STATIC_DRAW);
		
		//Buffer the vertex locations
		int positionIndex = gl.glGetAttribLocation(shaderProg.program(), "position");
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBuffer);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, quadVertices.length*(Float.SIZE/8), FloatBuffer.wrap(quadVertices), GL.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(positionIndex);
		gl.glVertexAttribPointer(positionIndex, 3, GL.GL_FLOAT, false, 0, 0);
		
		//Buffer the vertex texture coordinates
		int texCoordIndex = gl.glGetAttribLocation(shaderProg.program(), "texCoord");
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordBuffer);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, quadTextureCoords.length*(Float.SIZE/8), FloatBuffer.wrap(quadTextureCoords), GL.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(texCoordIndex);
		gl.glVertexAttribPointer(texCoordIndex, 2, GL.GL_FLOAT, false, 0, 0);
		
		//Buffer the projection matrix (done every time the it changes)
		int projMatrixIndex = gl.glGetUniformLocation(shaderProg.program(), "projectionMatrix");
		gl.glUniformMatrix4fv(projMatrixIndex, 1, false, projectionMatrix, 0);
		
		//Buffer the viewing matrix (done every time the it changes)
		int viewMatrixIndex = gl.glGetUniformLocation(shaderProg.program(), "viewingMatrix");
		gl.glUniformMatrix4fv(viewMatrixIndex, 1, false, viewingMatrix, 0);
		
		//Tell the shader which texture to use
		int textureSamplerIndex = gl.glGetUniformLocation(shaderProg.program(), "textureUnit");
		gl.glUniform1f(textureSamplerIndex, 0);
	}

	protected void clear()
	{	
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	}
	
	public void draw(Drawable object)
	{
		if(object.getTexture() == null)
			return;
		
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glEnable(vao);
		gl.glBindVertexArray(vao);
		
		float objWidth = object.getTexture().getWidth()*object.scale;
		float objHeight = object.getTexture().getHeight()*object.scale;
		float rotationRadians = object.rotation()*FloatUtil.PI/180;
		
		float[] objectTransformMatrix = new float[]{
				objWidth*FloatUtil.cos(rotationRadians),-objHeight*FloatUtil.sin(rotationRadians),0,0,
				objWidth*FloatUtil.sin(rotationRadians), objHeight*FloatUtil.cos(rotationRadians),0,0,
				0,0,1,0,
				object.x(), object.y(), -object.depth(), 1};
		
		int objTransformIndex = gl.glGetUniformLocation(shaderProg.program(), "objectTransform");
		gl.glUniformMatrix4fv(objTransformIndex, 1, false, objectTransformMatrix, 0);
		
		Texture objTex = object.getTexture();
		objTex.enable(gl);
		objTex.bind(gl);
		
		gl.glDrawElements(GL.GL_TRIANGLE_STRIP, quadIndices.length, GL.GL_UNSIGNED_INT, 0);
		
		objTex.disable(gl);
	}
	
	private void loadShader()
	{
		GL2ES2 gl = this.gl.getGL2ES2();
		
		ShaderCode vertShader = ShaderCode.create(gl, GL2ES2.GL_VERTEX_SHADER, 1, getClass(), new String[]{"/shaders/SimpleVertexShader.vsh"},false);
		vertShader.compile(gl);
		
		ShaderCode fragShader = ShaderCode.create(gl, GL2ES2.GL_FRAGMENT_SHADER, 1, getClass(), new String[]{"/shaders/SimpleFragmentShader.fsh"},false);
		fragShader.compile(gl);
		
		shaderProg = new ShaderProgram();
		shaderProg.init(gl);
		shaderProg.add(vertShader);
		shaderProg.add(fragShader);
		
		shaderProg.link(gl, System.out);
		
		vertShader.destroy(gl);
		fragShader.destroy(gl);
		
		shaderProg.useProgram(gl, true);
	}
}
