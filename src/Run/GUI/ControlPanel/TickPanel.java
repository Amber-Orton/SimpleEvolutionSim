package Run.GUI.ControlPanel;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import Run.Main;
import Run.World;
import Run.WorldPlayer;
import Run.GUI.GUI;
import Run.GUI.UpdateableJPanel;
import Logger.Logger;

// Panel displaying tick information and controls is the top section of the control panel
public class TickPanel extends UpdateableJPanel {
    private final World world;
    private final JLabel ticksPassedNumber;
    private final DefaultTableModel tickInfoTableModel;
    private final JTextField targetTicksNumTextArea;
    private final WorldPlayer worldPlayer;
    private final JButton playPauseButton;
    private volatile boolean tableModelUpdating = false;

    public TickPanel(World world, GUI gui) {
        this.world = world;
        this.setLayout(new BorderLayout());
        this.worldPlayer = new WorldPlayer(world, gui);

        JLabel ticksPassedLabel = new JLabel("Ticks:");
        ticksPassedNumber = new JLabel("0/");
        targetTicksNumTextArea = new JTextField("∞");
        targetTicksNumTextArea.addActionListener( e -> {
            Main.ticksToRun = Integer.parseInt(targetTicksNumTextArea.getText().trim());
            targetTicksNumTextArea.setText(Main.ticksToRun <= 0 ? "∞" : Integer.toString(Main.ticksToRun));
        });

        JPanel ticksPassedInfoPanel = new JPanel();
        ticksPassedInfoPanel.add(ticksPassedLabel);
        ticksPassedInfoPanel.add(ticksPassedNumber);
        ticksPassedInfoPanel.add(targetTicksNumTextArea);
        
        JButton tickButton = new JButton("Tick");
        tickButton.addActionListener(e -> {
            long startTime = System.nanoTime();
            worldPlayer.queueOneTick(startTime);
        });
        playPauseButton = new JButton(Main.play ? "Pause" : "Play");
        playPauseButton.addActionListener(e -> {
            Main.play = !Main.play;
            playPauseButton.setText(Main.play ? "Pause" : "Play");
            if (Main.play) {
                Thread worldPlayerThread = new Thread(worldPlayer);
                worldPlayerThread.start();
            }
        });
        
        JPanel ticksPassedButtonPanel = new JPanel();
        ticksPassedButtonPanel.add(tickButton);
        ticksPassedButtonPanel.add(playPauseButton);
        
        JPanel ticksPassedPanel = new JPanel(new BorderLayout());
        ticksPassedPanel.add(ticksPassedInfoPanel, BorderLayout.CENTER);
        ticksPassedPanel.add(ticksPassedButtonPanel, BorderLayout.SOUTH);


        String[] columnNames = {"","Actual", "Target"};
        tickInfoTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 && row > 0;
            }
        };
        JTable tickInfoTable = new JTable(tickInfoTableModel);

        tickInfoTableModel.addRow(new Object[]{"", "Actual", "Target"});
        tickInfoTableModel.addRow(new Object[]{"MSPT", "", ""});
        tickInfoTableModel.addRow(new Object[]{"TPS", "", ""});
        updateTableModelTarget();;

        tickInfoTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField()));
        DefaultCellEditor editor = (DefaultCellEditor) tickInfoTable.getColumnModel().getColumn(2).getCellEditor();
        editor.setClickCountToStart(1);

        tickInfoTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (tableModelUpdating) return; // ignore changes not made by user
                if (e.getType() != TableModelEvent.UPDATE) return;
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (col != 2 || row == 0) return;
                Object val = tickInfoTableModel.getValueAt(row, col);
                String s = val == null ? "" : val.toString().trim();
                if (s.equals("∞")) return;
                try {
                    long target = Long.parseLong(s);
                    if (row == 2) { //MSPT
                        if (target < 0) target = 0;
                        Main.targetMSPT = target;
                    } else { //TPS
                        if (target <= 0) {
                            Main.targetMSPT = 0;
                        } else {
                            Main.targetMSPT = 1_000L / target;
                        }
                    }
                    updateTableModelTarget();
                } catch (NumberFormatException ex) {
                    Logger.logError("TickPanel", "Invalid number format in tick info table: " + s);
                    updateTableModelTarget();
                }
            }
        });

        this.add(tickInfoTable, BorderLayout.EAST);
        this.add(ticksPassedPanel, BorderLayout.WEST);
    }

    public void updateTableModelTarget() {
        try {
            tableModelUpdating = true;
            tickInfoTableModel.setValueAt(Main.targetMSPT == 0 ? "∞" : Long.toString(1_000L / Main.targetMSPT), 1, 2);
            tickInfoTableModel.setValueAt(Main.targetMSPT == 0 ? "∞" : Long.toString(Main.targetMSPT), 2, 2);
        } finally {
            tableModelUpdating = false;
        }
    }

    public void updateTableModelActual() {
        tickInfoTableModel.setValueAt(Long.toString(Main.lastTickTime / 1_000_000L), 1, 1);
        tickInfoTableModel.setValueAt(Long.toString(1_000_000_000L / Main.lastTickTime), 2, 1);
    }

    public void updatePlayPauseButton() {
        playPauseButton.setText(Main.play ? "Pause" : "Play");
    }

    public void updateAfterTick() {
        targetTicksNumTextArea.setText(Main.ticksToRun <= 0 ? "∞" : Integer.toString(Main.ticksToRun));
        ticksPassedNumber.setText(Integer.toString(world.getTickCount()) + "/");
        updateTableModelActual();
    }
}