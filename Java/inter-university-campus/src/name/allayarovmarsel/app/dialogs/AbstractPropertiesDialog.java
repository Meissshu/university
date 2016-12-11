package name.allayarovmarsel.app.dialogs;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractPropertiesDialog extends JDialog{
    protected JButton accept;
    protected JButton cancel;
    protected JLabel[] Labels;

    AbstractPropertiesDialog(JFrame parent, String tittle, Dialog.ModalityType modal){super(parent, tittle, modal);}

    abstract protected void mainDialogSetup(JFrame parent);
    abstract protected void buttonsSetup(JFrame parent);

    public void actionPerformed() {
        setVisible(true);
    }


}
