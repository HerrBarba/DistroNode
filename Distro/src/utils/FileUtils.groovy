package utils

class FileUtils {
	
	static File fromPath(String path) {
		return new File(path);
	}    
    
    static String getContentFromReader(Reader reader) {
		StringBuilder content = new StringBuilder(200)

		while (!content.toString().endsWith("</sdo2015>")) {
			char c = reader.read()
			content.append(c)
		}
		
        return content.toString()
    }    
    
    static String getContentFromFile(File file) {
		return getContentFromReader(new BufferedReader(new FileReader(file)))
    }
	
	static void setContentToPath(String path, String str) {
		Writer writer = new BufferedWriter(new FileWriter(path, true))
		writer.write(str)
		writer.close()
	}
}
