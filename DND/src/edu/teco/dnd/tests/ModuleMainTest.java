package edu.teco.dnd.tests;

import java.util.HashSet;
import java.util.Set;

import edu.teco.dnd.module.ModuleMain;

public class ModuleMainTest {

	public static void main(final String[] args) {
		Set<String[]> params = new HashSet<String[]>();
		
		params.add(new String[]{"module.cfg"});
		params.add(new String[]{"alternateModule.cfg"});
		
		
		
		for (final String[] str : params) {
			
			new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("starting modmain");
				ModuleMain.main(str);
			}
		}).start();
		}

	}

}
