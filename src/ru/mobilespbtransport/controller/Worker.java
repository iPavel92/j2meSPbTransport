package ru.mobilespbtransport.controller;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 24.05.12
 * Time: 16:49
 * To change this template use File | Settings | File Templates.
 */
public class Worker extends Thread{
	private volatile boolean isStopping = false;

	public void setStopping(boolean stopping) {
		isStopping = stopping;
	}

	public void run() {
		try {
			while(!isStopping){
				Runnable task = null;
				synchronized (Controller.getTasks()) {
					while (Controller.getTasks().isEmpty() && !isStopping) {
						try {
							Controller.getTasks().wait();
						} catch (InterruptedException e) {
							//ignoring
							e.printStackTrace();
						}
					}
					task = Controller.getTasks().pop();
					Controller.getTasks().notify();
				}
				task.run();
			}
		} catch (Exception e) {
			//ignoring
			e.printStackTrace();
		}
	}
}
