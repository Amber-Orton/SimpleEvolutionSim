package Run.GUI.ControlPanel.InfoPanel;

import java.awt.event.MouseEvent;

import Run.GUI.UpdatableJPanel;
import Things.Helpers.Position;

public abstract class UpdatableDisplayPanel extends UpdatableJPanel{
    public abstract void click(Position position, MouseEvent event);
}
