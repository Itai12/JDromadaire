package libs;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map.Entry;

import main.EntryPoint;
import variables.ClassNode;

public class LibLoader {

	public static void registerModule(String name, String module_path) {
		File f = new File(module_path);
		
		if (!f.exists()) {
			EntryPoint.raiseErr("Could not find module file "+module_path);
			return ;
		}
		
		URLClassLoader child = null;
		try {
			child = new URLClassLoader(
			        new URL[] {f.toURI().toURL()},
			        LibLoader.class.getClassLoader()
			);
		} catch (Exception e) {
			EntryPoint.raiseErr("The JAR file "+module_path+" might not be usable");
			e.printStackTrace();
			return;
		}
		
		HashMap<String,ClassNode> exports = new HashMap<String, ClassNode>();
		try {
			Class classToLoad = Class.forName("config.Exporter", true, child);
			Object o = classToLoad.newInstance();
			if (o instanceof LibExporter) {
				exports = ((LibExporter)o).exportClasses();
			}
		} catch (Exception e) {
			EntryPoint.raiseErr("Could not load config.Exporter file from JAR "+module_path);
		}
		
		ClassNode cl = new ClassNode(-1, -1);
		for(Entry<String, ClassNode> obj:exports.entrySet()) {
			cl.objects.put(obj.getKey(), obj.getValue());
		}
		EntryPoint.globalContext.values.put(name, cl);
	}
	
}
