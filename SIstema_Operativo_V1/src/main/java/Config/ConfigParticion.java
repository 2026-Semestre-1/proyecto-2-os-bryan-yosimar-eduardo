package Config;

import java.util.List;

public class ConfigParticion {
    private int estatica;
    private List<Integer> dinamica;

    public int getEstatica() {
        return estatica;
    }

    public void setEstatica(int estatica) {
        this.estatica = estatica;
    }

    public List<Integer> getDinamica() {
        return dinamica;
    }

    public void setDinamica(List<Integer> dinamica) {
        this.dinamica = dinamica;
    }

    @Override
    public String toString() {
        return "ConfigParticion [estatica=" + estatica + ", dinamica=" + dinamica + "]";
    }
}