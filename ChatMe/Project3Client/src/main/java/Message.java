import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    boolean newUser = false;
    boolean newGroup = false;
    boolean success = false;
    boolean secreteMessage = false;
    String userID = "";
    String GroupID = "";
    String MessageInfo = "";
    Set<String> userNames;
    String recipient;
}
