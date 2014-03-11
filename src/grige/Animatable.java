package grige;

public abstract class Animatable extends Drawable
{
	//Animation data
	protected Animation animation;
	protected float timeSinceAnimationUpdate;
	protected float animationPlaySpeed;
	protected int animationPlayMode;
	protected int animationFrame;
	
	public void setAnimation(Animation newAnimation)
	{
		animation = newAnimation;
		animationFrame = 0;
		animationPlaySpeed = 0;
	}
	public void setAnimationSpeed(float playSpeed)
	{
		animationPlaySpeed = playSpeed;
	}
	public void setAnimationFrame(int frame)
	{
		animationFrame = frame;
	}
	
	public Animation getAnimation(){ return animation; }
	public float getAnimationSpeed() { return animationPlaySpeed; }
	public int getAnimationFrame() { return animationFrame; }
	public int getAnimationPlayMode() { return animationPlayMode; }
	
	public void playAnimation(int playMode, float playSpeed)
	{
		animationPlayMode = playMode;
		animationPlaySpeed = playSpeed;
		timeSinceAnimationUpdate = 0;
	}
	public void playAnimation()
	{
		playAnimation(Animation.PLAY_MODE_LOOP, 1);
	}
	public void playAnimation(int playMode)
	{
		playAnimation(playMode, 1);
	}
	public void playAnimationBackwards(int playMode)
	{
		playAnimation(playMode, -1);
	}
	public void pauseAnimation()
	{
		animationPlaySpeed = 0;
	}
	public void stopAnimation()
	{
		if(animationPlaySpeed >= 0)
			animationFrame = 0;
		else if(animationPlaySpeed < 0)
			animationFrame = animation.length()-1;
		
		animationPlaySpeed = 0;
	}
	
	void internalUpdate(float deltaTime)
	{
		if(animation != null && animationPlaySpeed != 0)
		{
			timeSinceAnimationUpdate += deltaTime;
			int frameIncrement = (int)(timeSinceAnimationUpdate*animationPlaySpeed);
			int newFrame = animationFrame + frameIncrement;
			if(newFrame < 0)
			{
				if(animationPlayMode == Animation.PLAY_MODE_LOOP)
					newFrame += animation.length();
				else if(animationPlayMode == Animation.PLAY_MODE_PINGPONG)
				{
					newFrame = -newFrame;
					setAnimationSpeed(-animationPlaySpeed);
				}
				else if(animationPlayMode == Animation.PLAY_MODE_ONCE)
				{
					stopAnimation();
					newFrame = animationFrame;
				}
			}
			else if(newFrame >= animation.length())
			{
				if(animationPlayMode == Animation.PLAY_MODE_LOOP)
					newFrame = newFrame%animation.length();
				else if(animationPlayMode == Animation.PLAY_MODE_PINGPONG)
				{
					newFrame = animation.length() - (newFrame%(animation.length()-1));
					setAnimationSpeed(-animationPlaySpeed);
				}
				else if(animationPlayMode == Animation.PLAY_MODE_ONCE)
				{
					stopAnimation();
					newFrame = animationFrame;
				}
			}
			
			timeSinceAnimationUpdate -= frameIncrement/animationPlaySpeed;
			setAnimationFrame(newFrame);
		}
	}
}
