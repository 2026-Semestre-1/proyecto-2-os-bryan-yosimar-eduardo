package Memoria.Modelo;

import java.util.BitSet;

public class Bitmap {

    private final BitSet bits;
    private final int totalFrames;

    public Bitmap(int totalFrames) {
        this.totalFrames = totalFrames;
        this.bits = new BitSet(totalFrames);
    }

    public synchronized int asignarFrame() {
        for (int i = 0; i < totalFrames; i++) {
            if (!bits.get(i)) {
                bits.set(i);
                return i;
            }
        }
        return -1;
    }

    public synchronized int[] asignarFrames(int cantidad) {
        int[] frames = new int[cantidad];
        int count = 0;
        for (int i = 0; i < totalFrames && count < cantidad; i++) {
            if (!bits.get(i)) {
                frames[count] = i;
                bits.set(i);
                count++;
            }
        }
        if (count < cantidad) {
            for (int j = 0; j < count; j++) {
                bits.clear(frames[j]);
            }
            return null;
        }
        return frames;
    }

    public synchronized int[] asignarFramesContiguos(int cantidad) {
        for (int i = 0; i <= totalFrames - cantidad; i++) {
            boolean libre = true;
            for (int j = 0; j < cantidad; j++) {
                if (bits.get(i + j)) {
                    libre = false;
                    break;
                }
            }
            if (libre) {
                int[] frames = new int[cantidad];
                for (int j = 0; j < cantidad; j++) {
                    frames[j] = i + j;
                    bits.set(i + j);
                }
                return frames;
            }
        }
        return null;
    }

    public synchronized void liberarFrame(int frame) {
        if (frame >= 0 && frame < totalFrames) {
            bits.clear(frame);
        }
    }

    public synchronized void liberarFrames(int[] frames) {
        if (frames != null) {
            for (int f : frames) {
                liberarFrame(f);
            }
        }
    }

    public synchronized boolean estaOcupado(int frame) {
        return bits.get(frame);
    }

    public synchronized int framesLibres() {
        return totalFrames - bits.cardinality();
    }

    public synchronized int framesOcupados() {
        return bits.cardinality();
    }

    public int getTotalFrames() {
        return totalFrames;
    }

    public synchronized String aString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < totalFrames; i++) {
            sb.append(bits.get(i) ? '1' : '0');
            if ((i + 1) % 8 == 0 && i + 1 < totalFrames) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }
}
