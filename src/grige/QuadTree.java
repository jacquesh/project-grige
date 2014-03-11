package grige;

import java.util.ArrayList;

public class QuadTree
{
	private final int MAX_OBJECTS = 10;
	private final int MAX_LEVELS = 5;
	
	private int level;
	private ArrayList<GameObject> objects;
	private AABB bounds;
	private QuadTree[] nodes;
	
	public QuadTree(int treeLevel, AABB treeBounds)
	{
		level = treeLevel;
		objects = new ArrayList<GameObject>(MAX_OBJECTS);
		bounds = treeBounds;
		nodes = new QuadTree[4];
	}
	
	/*
	 * Inserts an object into the QuadTree. if the node
	 * exceeds capacity it will split and add all objects
	 * to their corresponding child nodes
	 */
	public void insert(GameObject obj)
	{
		AABB rect = obj.getAABB();
		if(nodes[0] != null)
		{
			int index = getIndex(rect);
			if(index != -1)
			{
				nodes[index].insert(obj);
				return;
			}
		}
		
		objects.add(obj);
		
		if(objects.size() > MAX_OBJECTS && level < MAX_LEVELS)
		{
			if(nodes[0] == null)
				split();
			
			int i=0;
			while(i < objects.size())
			{
				int index = getIndex(objects.get(i).getAABB());
				if(index != -1)
					nodes[index].insert(objects.remove(i));
				else
					i++;
			}
		}
	}
	
	public ArrayList<GameObject> retrieve(ArrayList<GameObject> returnObjects, GameObject obj)
	{
		retrieve(returnObjects, obj.getAABB());
		
		return returnObjects;
	}
	
	public ArrayList<GameObject> retrieve(ArrayList<GameObject> returnObjects, AABB rect)
	{
		int index = getIndex(rect);
		if(index != -1 && nodes[0] != null)
			nodes[index].retrieve(returnObjects, rect);
		
		returnObjects.addAll(objects);
		
		return returnObjects;
	}
	
	/*
	 * Clears the entire QuadTree
	 */
	public void clear()
	{
		objects.clear();
		
		for(int i=0; i<4; i++)
			if(nodes[i] != null)
			{
				nodes[i].clear();
				nodes[i] = null;
			}
	}
	
	/*
	 * Determine which node the object belongs to. -1 means
	 * the object cannot fit completely inside a child node and 
	 * must be part of the parent node
	 */
	private int getIndex(AABB rect)
	{
		int index = -1;
		int horizontalMidpoint = (int)(bounds.position.x + bounds.size.x/2);
		int verticalMidpoint = (int)(bounds.position.y + bounds.size.y/2);
		
		//Check if the object can vertically fit entirely into a quadrant
		boolean topQuad = (rect.position.y < horizontalMidpoint && rect.position.y+rect.size.y < horizontalMidpoint);
		boolean bottomQuad = (rect.position.y > horizontalMidpoint);
		
		//Check for an entire fit into the left quadrants
		if(rect.position.x < verticalMidpoint && rect.position.x+rect.size.x < verticalMidpoint)
		{
			if(topQuad)
				index = 1;
			else if(bottomQuad)
				index = 2;
		}
		else if(rect.position.x > verticalMidpoint)
		{
			if(topQuad)
				index = 0;
			else if(bottomQuad)
				index = 3;
		}
		return index;
	}
	
	/*
	 * Splits the QuadTree into 4 sub-trees (as a result of containing too many objects)
	 */
	private void split()
	{
		int subWidth = (int)(bounds.size.x/2);
		int subHeight = (int)(bounds.size.y/2);
		int x = (int)(bounds.position.x);
		int y = (int)(bounds.position.y);
		
		nodes[0] = new QuadTree(level+1, new AABB(x+subWidth, y, subWidth, subHeight));
		nodes[1] = new QuadTree(level+1, new AABB(x, y, subWidth, subHeight));
		nodes[2] = new QuadTree(level+1, new AABB(x, y+subHeight, subWidth, subHeight));
		nodes[3] = new QuadTree(level+1, new AABB(x+subWidth, y+subHeight, subWidth, subHeight));
	}
}
