package Things.Helpers;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import Main.Main;

public class Appearance {
    
    private NibbleGrid8x8 data = new NibbleGrid8x8();
    private volatile BufferedImage image;
    private final Map<DIRECTION, BufferedImage> cache = new ConcurrentHashMap<>();




    /**
     * Creates a new appearance from the given nibble grid data.
     * @param data
     */
    public Appearance(NibbleGrid8x8 data) {
        this.data = data;
    }

    /**
     * Creates a new appearance with random data.
     */
    public Appearance() {
        this.data = new NibbleGrid8x8();
    }


    /**
     * Returns the image representation of this appearance if it has one.
     * @return the image representation of this if isImage() is true null otherwise.
     */
    private BufferedImage getImage() {
        if (image != null) { 
            return image;
        }
        image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int idx = data.get(r, c) & 0xF;
                int rgb = Main.ANIMAL_COLOR_PALETTE[idx] & 0xFFFFFF;
                image.setRGB(c, r, 0xFF000000 | rgb);
            }
        }
        return image;
    }


    public BufferedImage getRotatedImage(DIRECTION direction) {
        if (cache.containsKey(direction)) {
            return cache.get(direction);
        }
        BufferedImage original = getImage();
        if (direction == DIRECTION.NORTH) {
            cache.put(direction, original);
            return original;
        }
        BufferedImage rotated = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                switch (direction) {
                    case DIRECTION.EAST:
                        rotated.setRGB(7 - y, x, original.getRGB(x, y));
                        break;
                    case DIRECTION.SOUTH:
                        rotated.setRGB(7 - x, 7 - y, original.getRGB(x, y));
                        break;
                    case DIRECTION.WEST:
                        rotated.setRGB(y, 7 - x, original.getRGB(x, y));
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid direction: " + direction);
                }
            }
        }
        cache.put(direction, rotated);
        return rotated;
    }

    protected NibbleGrid8x8 getData() {
        return data;
    }

    public Appearance clone() {
        return new Appearance(data.clone());
    }
}

class NibbleGrid8x8 {
    private byte[] data = new byte[32]; // 64 nibbles packed

    protected NibbleGrid8x8(byte[] data) {
        System.arraycopy(data, 0, this.data, 0, 32);
    }

    protected NibbleGrid8x8() {
        Main.getRandom().nextBytes(data); // or pass Random as constructor param
    }

    // idx = row*8 + col
    protected int get(int row, int col) {
        int idx = (row << 3) | col;     // 0..63
        int b = idx >>> 1;              // which byte
        boolean hi = (idx & 1) != 0;    // high or low nibble
        int v = data[b] & 0xFF;
        return hi ? ((v >>> 4) & 0xF) : (v & 0xF);
    }

    protected void set(int row, int col, int val) {
        int idx = (row << 3) | col;
        int b = idx >>> 1;
        boolean hi = (idx & 1) != 0;
        int v = data[b] & 0xFF;
        val &= 0xF;
        data[b] = (byte)(hi ? ((v & 0x0F) | (val << 4)) : ((v & 0xF0) | val));
    }

    private byte[] toBytes() { return data.clone(); }

    protected NibbleGrid8x8 clone() {
        return new NibbleGrid8x8(toBytes());
    }
}