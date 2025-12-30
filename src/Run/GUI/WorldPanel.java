package Run.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import javax.swing.SwingUtilities;

import Run.Main;
import Run.World.World;
import Run.World.World.Snapshot;
import Things.Thing;
import Things.Helpers.HasAppearance;
import Things.Helpers.Position;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldPanel extends UpdatableJPanel {
    private final GUI gui;
    private final World world;
    private final ExecutorService bufferedImageExecutor;
    private volatile BufferedImage backBuffer;
    private Position selectedPosition;
    private Snapshot snapshot;
    private int imgW;
    private int imgH;
    
    public WorldPanel(World world, GUI gui) {
        this.gui = gui;
        this.world = world;

        this.bufferedImageExecutor = Executors.newSingleThreadExecutor();
        this.snapshot = world.getLatestSnapshot();
        backBuffer = createBufferedImage(snapshot);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                Position pos = getPositionFromCoordinates(e.getX(), e.getY());
                if (pos == null) {
                    return;
                }
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Position prevPosition = selectedPosition;
                    selectedPosition = pos;
                    snapshot.resetChangedGrid(false);
                    if (prevPosition != null) {
                        snapshot.setChangedAt(prevPosition, true);
                        createBufferedImage(snapshot);
                        snapshot.setChangedAt(prevPosition, false);
                    }
                    snapshot.setChangedAt(selectedPosition, true);
                    asyncCreateBufferedImageAndRender(snapshot);
                    Main.setSelectedPosition(selectedPosition);
                }
                gui.click(pos, e);
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                setSize();
                snapshot.resetChangedGrid(true);
                asyncCreateBufferedImage(snapshot);
            }
        });
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        BufferedImage buf = backBuffer;
        if (buf == null) {
            throw new IllegalStateException("Back buffer is null during paintComponent.");
        } else {
            g.drawImage(buf, 0, 0, imgW, imgH, null);
        }
    }

    private void setSize() {
        int maxColWidth = (int) (this.getWidth() / world.getWidth());
        int maxRowHeight = gui.getMainPanel().getHeight() / world.getHeight();
        int cellSize = Math.min(maxColWidth, maxRowHeight);
        imgW = cellSize * world.getWidth();
        imgH = cellSize * world.getHeight();
        setPreferredSize(new Dimension(imgW, imgH));
    }

    private void asyncRender() {
        SwingUtilities.invokeLater(this::repaint);
    }

    private void render() {
        try {
            SwingUtilities.invokeAndWait(this::repaint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void asyncCreateBufferedImageAndRender() {
        bufferedImageExecutor.submit(() -> {
            snapshot = world.getLatestSnapshot();
            backBuffer = createBufferedImage(snapshot);
            asyncRender();
        });
    }

    private void asyncCreateBufferedImageAndRender(Snapshot snapshot) {
        bufferedImageExecutor.submit(() -> {
            backBuffer = createBufferedImage(snapshot);
            asyncRender();
        });
    }

    private void asyncCreateBufferedImage() {
        bufferedImageExecutor.submit(() -> {
            snapshot = world.getLatestSnapshot();
            backBuffer = createBufferedImage(snapshot);
        });
    }

    private void asyncCreateBufferedImage(Snapshot snapshot) {
        bufferedImageExecutor.submit(() -> {
            backBuffer = createBufferedImage(snapshot);
        });
    }

    private BufferedImage createBufferedImage() {
        snapshot = world.getLatestSnapshot();
        return createBufferedImage(snapshot);
    }

    private void createBufferedImageAndRender() {
        backBuffer = createBufferedImage();
        render();
    }

    private BufferedImage createBufferedImage(Snapshot snapshot) {
        final int rows = world.getHeight();
        final int cols = world.getWidth();

        BufferedImage img;
        if (backBuffer != null) {
            img = backBuffer;
        } else {
            img = new BufferedImage(cols * 8, rows * 8, BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g2 = img.createGraphics();
        try {
            // Draw world cells

            Thing[][] thingGrid = snapshot.grid;
            boolean[][] changedGrid = snapshot.changedGrid;

            double pixelsPerBox = Math.min(
                (double) getWidth()  / cols,
                (double) getHeight() / rows
            );
            boolean drawBorders = pixelsPerBox >= 8.0;

            for (int r = 0; r < rows; r++) {
                int y = r * 8;

                for (int c = 0; c < cols; c++) {
                    int x = c * 8;

                    if (changedGrid[r][c]){
                        Thing t = thingGrid[r][c];
                        if (t instanceof HasAppearance) {
                            RenderedImage imgCell = ((HasAppearance) t).getImage();
                            if (imgCell != null) {
                                g2.drawRenderedImage(imgCell, AffineTransform.getTranslateInstance(x, y));
                            } else {
                                throw new IllegalStateException("Thing with appearance has null image.");
                            }
                        } else {
                            g2.setColor(t.getColor());
                            g2.fillRect(x, y, 8, 8);
                        }    
                        // Only draw borders when scaled cells will be large enough
                        if (drawBorders) {
                            g2.setColor(Color.DARK_GRAY);
                            g2.drawRect(x, y, 8, 8);
                        }
                    }
                    if (selectedPosition != null && selectedPosition.getRow() == r && selectedPosition.getCol() == c) {
                        g2.setColor(Color.YELLOW);
                        g2.drawRect(x, y, 8, 8);
                    }
                }
            }

        } finally {
            g2.dispose();
        }
        return img;
    }
    

    /**
     * Get the grid position from pixel coordinates.
     * caller responsible for checking if null
     * @param x pixel x-coordinate
     * @param y pixel y-coordinate
     * @return Position or null if out of bounds
     */
    private Position getPositionFromCoordinates(int x, int y) {
        int cellSize = Math.min(imgW / world.getWidth(), imgH / world.getHeight());
        Position pos = new Position(y / cellSize, x / cellSize);
        if (world.posIsInBounds(pos)) {
            return pos;
        }
        return null;
    }

    @Override
    public void updateAfterTick() {
        if (Main.isDoUpdateWorldView()){
            if (Main.isWaitForLongUpdateAfterTick()){
                createBufferedImageAndRender();
            } else {
                asyncCreateBufferedImageAndRender();
            }
        }
    }
}
