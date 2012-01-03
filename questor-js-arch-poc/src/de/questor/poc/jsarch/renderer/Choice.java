package de.questor.poc.jsarch.renderer;

import java.io.Serializable;


public class Choice  implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id = null;
	private int targetId = -1;

	public Choice() {
		super();
	}
		
	public Choice(int targetId) {
		super();
		setTargetId(targetId);
	}

	public int getTargetId()
	{
		return targetId;
	}
	
	public void setTargetId(int targetId)
	{
		this.targetId = targetId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}	
}
