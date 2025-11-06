package Run;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * GUI for the Simple Evolution Simulation.
 * Uses a custom SquareGridLayout so each cell remains square.
 */
public class GUI {
    private World world;
    private static GUI instance;
    private JPanel[][] gridPanels;

    private JTextField tickCountDisplay;
    private JTextArea selectedThingNameText;

    public static GUI getInstanceOrChangeWorld(World world) {
        if (instance == null) {
            instance = new GUI(world);
        } else {
            instance.world = world;
        }
        return instance;
    }

    private GUI(World world) {
        this.world = world;
    }

    public void run() {
        JFrame frame = new JFrame("Simple Evolution Sim");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);

        // top-level horizontal split: left (grid) and right (controls)
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));

        // LEFT (grid + tick)
        JPanel leftPanel = new JPanel(new BorderLayout());

        // tick controls (top)
        JPanel tickPanel = createTickPanel();
        leftPanel.add(tickPanel, BorderLayout.NORTH);

        // grid (center)
        JPanel gridPanel = createGridPanel();
        leftPanel.add(gridPanel, BorderLayout.CENTER);

        // RIGHT (controls)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        createControlPanel(rightPanel);

        // add to frame
        frame.getContentPane().add(leftPanel);
        frame.getContentPane().add(rightPanel);

        frame.setVisible(true);
    }

    /** update the world view colors into the cell panels */
    private void updateWorldView() {
        Color[][] grid = world.getGridOfColors();
        int rows = world.getWidth();
        int cols = world.getHeight();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // defensive check in case something changed
                if (gridPanels != null && r < gridPanels.length && c < gridPanels[r].length) {
                    gridPanels[r][c].setBackground(grid[r][c]);
                }
            }
        }
    }

    /** Creates the grid panel and cell components using SquareGridLayout */
    private JPanel createGridPanel() {
        int rows = world.getWidth();
        int cols = world.getHeight();

        // gridPanel uses the custom SquareGridLayout so each cell is square
        JPanel gridPanel = new JPanel(new SquareGridLayout(rows, cols));
        gridPanels = new JPanel[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JPanel cell = new JPanel();
                cell.setBackground(Color.LIGHT_GRAY);
                cell.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                gridPanels[r][c] = cell;
                gridPanel.add(cell);

                final int rr = r;
                final int cc = c;
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // displayThingInfo(rr, cc);
                    }
                });
            }
        }

        // initial paint
        updateWorldView();
        return gridPanel;
    }

    /** Creates the right-side control panel with Selected, Stats, Neural Net and World Stats */
    private void createControlPanel(JPanel panel) {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Selected thing name (top)
        selectedThingNameText = new JTextArea("Click on a Thing to show its information here");
        selectedThingNameText.setLineWrap(true);
        selectedThingNameText.setWrapStyleWord(true);
        selectedThingNameText.setEditable(false);
        selectedThingNameText.setMaximumSize(new Dimension(300, 60));
        controlPanel.add(selectedThingNameText);

        // Stats and Neural Net side-by-side (equal width)
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        JTextArea stats = new JTextArea("Stats");
        JTextArea neural = new JTextArea("Neural Net");
        stats.setEditable(false);
        neural.setEditable(false);
        infoPanel.add(wrapInScrollIfNeeded(stats));
        infoPanel.add(wrapInScrollIfNeeded(neural));
        infoPanel.setMaximumSize(new Dimension(400, 200));
        controlPanel.add(infoPanel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // World Stats placeholder below
        JPanel worldStatsPanel = new JPanel();
        worldStatsPanel.setLayout(new BorderLayout());
        worldStatsPanel.setBorder(BorderFactory.createTitledBorder("World Stats"));
        JTextArea worldStatsArea = new JTextArea("World Stats (placeholder)\nYou can fill this later.");
        worldStatsArea.setEditable(false);
        worldStatsPanel.add(new JScrollPane(worldStatsArea), BorderLayout.CENTER);
        worldStatsPanel.setMaximumSize(new Dimension(400, 200));
        controlPanel.add(worldStatsPanel);

        // glue to push content to top if the right panel is taller
        controlPanel.add(Box.createVerticalGlue());

        panel.add(controlPanel);
    }

    private static JComponent wrapInScrollIfNeeded(JTextArea comp) {
        JScrollPane sp = new JScrollPane(comp);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return sp;
    }

    /** tick controls panel */
    private JPanel createTickPanel() {
        JPanel tickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        tickCountDisplay = new JTextField("Tick Count: " + world.getTickCount());
        tickCountDisplay.setColumns(10);
        tickCountDisplay.setEditable(false);
        tickPanel.add(tickCountDisplay);

        JButton tickButton = new JButton("Tick");
        tickButton.addActionListener(e -> tick());
        tickPanel.add(tickButton);

        return tickPanel;
    }

    private void tick() {
        world.tick();
        updateWorldView();
        tickCountDisplay.setText("Tick Count: " + world.getTickCount());
    }

    // ------------------------------------------------------------------------
    // Custom layout manager: SquareGridLayout
    // - Arranges components in a rows x cols grid
    // - Each cell is square (width == height)
    // - Grid is centered in the available area and leaves padding if aspect differs
    // ------------------------------------------------------------------------
    private static class SquareGridLayout implements LayoutManager {
        private final int rows;
        private final int cols;

        public SquareGridLayout(int rows, int cols) {
            this.rows = Math.max(1, rows);
            this.cols = Math.max(1, cols);
        }

        @Override
        public void layoutContainer(Container parent) {
            int totalW = parent.getWidth();
            int totalH = parent.getHeight();

            if (totalW <= 0 || totalH <= 0) return;

            // maximum square cell size that fits both horizontally and vertically
            int cellSize = Math.min(totalW / cols, totalH / rows);
            if (cellSize <= 0) return;

            int gridW = cellSize * cols;
            int gridH = cellSize * rows;

            // center the grid
            int xOffset = (totalW - gridW) / 2;
            int yOffset = (totalH - gridH) / 2;

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    int idx = r * cols + c;
                    if (idx < parent.getComponentCount()) {
                        Component comp = parent.getComponent(idx);
                        int x = xOffset + c * cellSize;
                        int y = yOffset + r * cellSize;
                        comp.setBounds(x, y, cellSize, cellSize);
                    }
                }
            }
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            // prefer each cell 20x20 initially
            return new Dimension(cols * 20, rows * 20);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(cols * 5, rows * 5);
        }

        @Override
        public void addLayoutComponent(String name, Component comp) { }
        @Override
        public void removeLayoutComponent(Component comp) { }
    }
}
