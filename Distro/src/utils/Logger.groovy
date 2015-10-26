package utils

@Singleton
class Logger {
	
	private static final LOG_PATH = "./Story.log"
	
	public void writeLog(String from, String message, String response) {
		message = message ?: 'N/A'
		response = response ?: 'N/A'
		
		String log = """\n------------- ${from} -------------\n
			\nMessage: ${message}
			\nResponse: ${response}
			\n------------- ${from} -------------\n"""
		
		FileUtils.setContentToPath(LOG_PATH, log);
	}	
	
	public void writeLog(Class from, String message, String response) {
		writeLog(from.toString(), message, response)
	}
}
