import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 10.09.2014
 *
 * @author Dreistein
 */
public class LogTest {
    public static void main(String[] args) {
        Log log = LogFactory.getLog(LogTest.class);
        try {
            exceptionTest(null);
        } catch (Exception e) {
            log.fatal("Cought an Exception!", e);
        }
    }

    public static void exceptionTest(String s) throws Exception {
        if (s.split("6").length > 5) {
            System.out.println("What a disaster!");
        }
    }
}
