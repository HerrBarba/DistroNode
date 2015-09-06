package Main

import java.util.Date;

class Client {

    static void main(def args) {
        def s = new Socket("localhost", 4444)
        s.withStreams { input, output ->
            def now = Utils.formatDate2String(new Date())
            output << "<Node><NodeId>is682778</NodeId><Timestamp>$now</Timestamp></Node>"
            println "response"
        }
    }
}