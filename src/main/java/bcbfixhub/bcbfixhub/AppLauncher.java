package bcbfixhub.bcbfixhub;

import javafx.application.Application;

public class AppLauncher {
    static void main(String[] args) {
        Application.launch(BcbfixhubApplication.class, args);}
}
/*TODO:
   - Disable Controllers that arent being used for now.
   - Create Repositories for other models. Modify models if necessary.
   - Comment along the way.
   - Fix the error "Caused by: javafx.fxml.LoadException: Error resolving onAction='#onHome', either the event handler is not in the Namespace or there is an error in the script."

*/
