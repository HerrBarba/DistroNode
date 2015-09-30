package utils

import java.io.Reader;

class FileUtils {

	static File fromPath(String path) {
		return new File(path);
	}    
    
    static String getContentFromReader(Reader reader) {
        return reader.readLines().iterator().join("\n")
    }    
    
    static String getContentFromFile(File file) {
		Reader reader = new BufferedReader(new FileReader(file))
        return reader.readLines().iterator().join("\n")
    }
}
