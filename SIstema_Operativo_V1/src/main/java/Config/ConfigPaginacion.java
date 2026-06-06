package Config;

public class ConfigPaginacion {
    private int paginacion;

    public int getPaginacion() {
        return paginacion;
    }

    public void setPaginacion(int paginacion) {
        this.paginacion = paginacion;
    }

    @Override
    public String toString() {
        return "ConfigPaginacion [paginacion=" + paginacion + "]";
    }
}
