package ru.mobilespbtransport.controller;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 24.05.12
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */
public class TaskQueue extends Vector{
	public synchronized void push(Runnable task){
		addElement(task);
	}
	
	public synchronized Runnable pop(){
		if(isEmpty()){
			return null;
		}
		Runnable task = (Runnable)elementAt(0);
		removeElementAt(0);
		return task;
	}
}

