package one.modality.event.backoffice.activities.program;


import dev.webfx.stack.orm.domainmodel.activity.viewdomain.impl.ViewDomainActivityBase;
import dev.webfx.stack.ui.controls.button.ButtonFactoryMixin;
import javafx.scene.Node;
import javafx.scene.text.Text;

/**
 * @author Bruno Salmon
 */
public class ProgramActivity extends ViewDomainActivityBase implements ButtonFactoryMixin {

    @Override
    public Node buildUi() {
        return new Text("Program");
    }

}
