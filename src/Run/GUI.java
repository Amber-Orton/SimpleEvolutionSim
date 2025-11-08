package Run;

import javax.swing.*;

import Things.Animal;
import Things.Egg;
import Things.Food;
import Things.Position;
import Things.Thing;
import Things.Wall;

import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

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
    private JTextArea selectedThingInfoTextLeft;
    private JTextArea selectedThingInfoTextRight;
    private Thing selectedThing;

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
                        displayThingInfo(rr, cc);
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
        selectedThingNameText = new JTextArea("Click on a Thing to show its name here");
        selectedThingNameText.setLineWrap(true);
        selectedThingNameText.setWrapStyleWord(true);
        selectedThingNameText.setEditable(false);
        selectedThingNameText.setMaximumSize(new Dimension(300, 60));
        controlPanel.add(selectedThingNameText);

        // Stats and Neural Net side-by-side (equal width)
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        selectedThingInfoTextLeft = new JTextArea("Click on a Thing to show its information here");
        selectedThingInfoTextRight = new JTextArea("");
        selectedThingInfoTextLeft.setEditable(false);
        selectedThingInfoTextRight.setEditable(false);
        selectedThingInfoTextLeft.setLineWrap(true);
        selectedThingInfoTextLeft.setWrapStyleWord(true);
        selectedThingInfoTextRight.setLineWrap(true);
        selectedThingInfoTextRight.setWrapStyleWord(true);
        infoPanel.add(wrapInScrollIfNeeded(selectedThingInfoTextLeft));
        infoPanel.add(wrapInScrollIfNeeded(selectedThingInfoTextRight));
        infoPanel.setPreferredSize(new Dimension(400, 200));
        controlPanel.add(infoPanel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // World Stats with scrolling content
        JPanel worldStatsContainer = new JPanel(new BorderLayout());
        worldStatsContainer.setBorder(BorderFactory.createTitledBorder("World Stats"));
        worldStatsContainer.setPreferredSize(new Dimension(400, 260)); // initial size

        // Inner content that actually holds the rows
        JPanel worldStatsContent = new JPanel();
        worldStatsContent.setLayout(new BoxLayout(worldStatsContent, BoxLayout.Y_AXIS));

        // Add all rows to the scrollable content
        worldStatsContent.add(makeIntSliderRow(
            "Animal Stats Total",
            50, 200, Main.ANIMAL_STAT_TOTAL,
            v -> {Main.ANIMAL_STAT_TOTAL = v;
            Main.updateAttributes();}
        ));
        worldStatsContent.add(makeIntSliderRow(
            "Animal Attack Cost",
            0, 50, Main.ANIMAL_ATTACK_COST,
            v -> {Main.ANIMAL_ATTACK_COST = v;
            Main.updateAttributes();}
        ));
        worldStatsContent.add(makeZeroToOneSliderRow(
            "Mutation Rate (0..1)",
            Main.MUTATION_RATE,
            v -> {Main.MUTATION_RATE = v;
            Main.updateAttributes();}
        ));
        worldStatsContent.add(makeZeroToOneSliderRow(
            "Mutation Factor (0..1)",
            Main.MUTATION_FACTOR,
            v -> {Main.MUTATION_FACTOR = v;
            Main.updateAttributes();}
        ));
        worldStatsContent.add(makeZeroToOneSliderRow(
            "Node Insert/Delete Rate (0..1)",
            Main.NODE_INSERT_OR_DELETE_RATE,
            v -> {Main.NODE_INSERT_OR_DELETE_RATE = v;
            Main.updateAttributes();}
        ));
        worldStatsContent.add(makeIntSliderRow(
            "Max Neural Net Layers",
            1, 10, Main.MAX_LAYERS,
            v -> {Main.MAX_LAYERS = v;
            Main.updateAttributes();}
        ));
        worldStatsContent.add(makeZeroToOneSliderRow(
            "Food Grow Rate (0..1)",
            Main.FOOD_GROW_RATE,
            v -> {Main.FOOD_GROW_RATE = v;
            Main.updateAttributes();}
        ));
        worldStatsContent.add(makeIntBackedFloatRow(
            "New Food Energy",
            1, 100, Math.round(Main.NEW_FOOD_ENERGY),
            f -> {Main.NEW_FOOD_ENERGY = f;
            Main.updateAttributes();}
        ));

        // Put the content into a scroll pane, keep the title on the container
        JScrollPane worldStatsScroll = new JScrollPane(worldStatsContent);
        worldStatsScroll.setBorder(null); // keep the outer border only
        worldStatsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        worldStatsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        worldStatsScroll.getVerticalScrollBar().setUnitIncrement(16);

        worldStatsContainer.add(worldStatsScroll, BorderLayout.CENTER);
        controlPanel.add(worldStatsContainer);

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
    
    private void displayThingInfo(int row, int col){
        if (!world.posIsNothing(new Position(row, col))) {
            selectedThing = world.getThingAt(row, col);
            selectedThingNameText.setText(selectedThing.getName());
    
            updateSelectedThingInfo(selectedThing);
        }
    }

    private void updateSelectedThingInfo(Thing thing) {
        if (thing == null) { return; }
        if (thing instanceof Animal) {
            showAnimalInfo((Animal) thing);
        } else if (thing instanceof Egg) {
            showEggInfo((Egg) thing);
        } else if (thing instanceof Wall) {
            showWallInfo((Wall) thing);
        } else if (thing instanceof Food) {
            showFoodInfo((Food) thing);
        } else {
            showErrorMessage(thing);
        }
    }

    private void showAnimalInfo(Animal animal) {
        if (animal.getHealth() <= 0) {
            selectedThingInfoTextLeft.setText("This animal is dead.");
        } else {
            selectedThingInfoTextLeft.setText(
                "Health: " + animal.getHealth() + '/' + animal.getAnimalAttributes().getMaxHealth() 
                + "\nEnergy: " + animal.getEnergy() + '/' + animal.getAnimalAttributes().getMaxEnergy()
                + "\nAttack Damage: " + animal.getAnimalAttributes().getAttackDamage()
                + "\nReproduction Cost: " + animal.getAnimalAttributes().getReproductionCost()
                + "\nLast Action: " + animal.getAction());
            selectedThingInfoTextRight.setText(animal.getAnimalAttributes().getNeuralNet().toString());
        }
    }

    private void showEggInfo(Egg egg) {
        if (!egg.isHatched()) {
            selectedThingInfoTextLeft.setText(
                "Max Health: " + egg.getAnimalAttributes().getMaxHealth()
                + "\nMax Energy: " + egg.getAnimalAttributes().getMaxEnergy()
                + "\nAttack Damage: " + egg.getAnimalAttributes().getAttackDamage()
                + "\nStored Energy: " + egg.getAnimalAttributes().getReproductionCost()
            );
            selectedThingInfoTextRight.setText(
                "Parent: " + egg.getParent().getName()
                + "\nCycles to Hatch: " + egg.getCyclesToHatch()
            );
        } else {
            selectedThing = egg.getChild();
            updateSelectedThingInfo(selectedThing);
        }
    }

    private void showWallInfo(Wall wall) {
        selectedThingInfoTextLeft.setText("Wall");
        selectedThingInfoTextRight.setText("Walls are impassable barriers.");
    }

    private void showFoodInfo(Food food) {
        selectedThingInfoTextLeft.setText(
            "Energy: " + food.getEnergy()
        );
        selectedThingInfoTextRight.setText("Food is a source of energy and can be eaten by animals, \nspawns from nothing and from dead animals.");
    }

    private void showErrorMessage(Thing thing) {
        selectedThingInfoTextLeft.setText("Something went wrong invalid selection");
        selectedThingInfoTextRight.setText("");
    }

    private void tick() {
        world.tick();
        updateWorldView();
        updateSelectedThingInfo(selectedThing);
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

    // Build a row: int slider with numeric text readout (editable). Allows out-of-range input.
    // When out-of-range, slider pins to end, and a red badge indicates "below min"/"above max".
    private JPanel makeIntSliderRow(String title, int min, int max, int initial, Consumer<Integer> onChange) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(title));

        JSlider slider = new JSlider(min, max, Math.max(min, Math.min(max, initial)));
        slider.setPaintTicks(true);
        int range = Math.max(1, max - min);
        slider.setMajorTickSpacing(Math.max(1, range / 4));
        slider.setMinorTickSpacing(Math.max(1, range / 20));
        slider.setPaintLabels(true);

        JTextField tf = new JTextField(Integer.toString(initial), 8);
        tf.setMaximumSize(new Dimension(100, 28));
        JLabel badge = new JLabel("");
        badge.setForeground(Color.RED);

        slider.addChangeListener(e -> {
            int v = slider.getValue();
            tf.setText(Integer.toString(v));
            badge.setText("");
            if (!slider.getValueIsAdjusting()) {
                if (onChange != null) onChange.accept(v);
            }
        });

        Runnable applyFromField = () -> {
            try {
                int raw = Integer.parseInt(tf.getText().trim());
                if (raw < min) {
                    badge.setText("below min");
                    slider.setValue(min);
                } else if (raw > max) {
                    badge.setText("above max");
                    slider.setValue(max);
                } else {
                    badge.setText("");
                    slider.setValue(raw);
                }
                if (onChange != null) onChange.accept(raw);
            } catch (NumberFormatException ex) {
                tf.setText(Integer.toString(slider.getValue()));
            }
        };
        tf.addActionListener(e -> applyFromField.run());
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { applyFromField.run(); }
        });

        p.add(slider);
        p.add(Box.createHorizontalStrut(8));
        p.add(new JLabel("Value: "));
        p.add(tf);
        p.add(Box.createHorizontalStrut(6));
        p.add(badge);
        return p;
    }

    // Build a row: 0..1 float mapped to 0..100% slider. Allows any float; badge shows out-of-range.
    private JPanel makeZeroToOneSliderRow(String title, float initial, Consumer<Float> onChange) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(title));

        int initPct = Math.max(0, Math.min(100, Math.round(initial * 100f)));
        JSlider slider = new JSlider(0, 100, initPct);
        slider.setPaintTicks(true);
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintLabels(true);

        JTextField tf = new JTextField(String.format("%.4f", initial), 8);
        tf.setMaximumSize(new Dimension(100, 28));
        JLabel badge = new JLabel("");
        badge.setForeground(Color.RED);

        slider.addChangeListener(e -> {
            float v = slider.getValue() / 100f;
            tf.setText(String.format("%.4f", v));
            badge.setText("");
            if (!slider.getValueIsAdjusting()) {
                if (onChange != null) onChange.accept(v);
            }
        });

        Runnable applyFromField = () -> {
            try {
                float raw = Float.parseFloat(tf.getText().trim());
                if (raw < 0f) {
                    badge.setText("below min");
                    slider.setValue(0);
                } else if (raw > 1f) {
                    badge.setText("above max");
                    slider.setValue(100);
                } else {
                    badge.setText("");
                    slider.setValue(Math.round(raw * 100f));
                }
                if (onChange != null) onChange.accept(raw);
            } catch (NumberFormatException ex) {
                tf.setText(String.format("%.4f", slider.getValue() / 100f));
            }
        };
        tf.addActionListener(e -> applyFromField.run());
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { applyFromField.run(); }
        });

        p.add(slider);
        p.add(Box.createHorizontalStrut(8));
        p.add(new JLabel("Value: "));
        p.add(tf);
        p.add(Box.createHorizontalStrut(6));
        p.add(badge);
        return p;
    }

    // Build a row: int slider but backing value is float (text accepts any float). Badge shows out-of-range.
    private JPanel makeIntBackedFloatRow(String title, int min, int max, int initialAsInt, Consumer<Float> onChange) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(title));

        JSlider slider = new JSlider(min, max, Math.max(min, Math.min(max, initialAsInt)));
        slider.setPaintTicks(true);
        int range = Math.max(1, max - min);
        slider.setMajorTickSpacing(Math.max(1, range / 4));
        slider.setMinorTickSpacing(Math.max(1, range / 20));
        slider.setPaintLabels(true);

        JTextField tf = new JTextField(String.format("%.3f", (float) initialAsInt), 8);
        tf.setMaximumSize(new Dimension(100, 28));
        JLabel badge = new JLabel("");
        badge.setForeground(Color.RED);

        slider.addChangeListener(e -> {
            float v = slider.getValue();
            tf.setText(String.format("%.3f", v));
            badge.setText("");
            if (!slider.getValueIsAdjusting()) {
                if (onChange != null) onChange.accept(v);
            }
        });

        Runnable applyFromField = () -> {
            try {
                float raw = Float.parseFloat(tf.getText().trim());
                if (raw < min) {
                    badge.setText("below min");
                    slider.setValue(min);
                } else if (raw > max) {
                    badge.setText("above max");
                    slider.setValue(max);
                } else {
                    badge.setText("");
                    slider.setValue(Math.round(raw));
                }
                if (onChange != null) onChange.accept(raw);
            } catch (NumberFormatException ex) {
                tf.setText(String.format("%.3f", (float) slider.getValue()));
            }
        };
        tf.addActionListener(e -> applyFromField.run());
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { applyFromField.run(); }
        });

        p.add(slider);
        p.add(Box.createHorizontalStrut(8));
        p.add(new JLabel("Value: "));
        p.add(tf);
        p.add(Box.createHorizontalStrut(6));
        p.add(badge);
        return p;
    }
}
