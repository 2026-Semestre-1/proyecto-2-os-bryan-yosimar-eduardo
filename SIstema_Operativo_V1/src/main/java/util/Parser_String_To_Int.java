package util;

public class Parser_String_To_Int {

    /**
     * Nombre: parseStringToInt
     * 
     * Descripcion: Esta funcion es un parser seguro para convertir a un valor
     * entero un string.
     * 
     * @param s (String): El string que se desea convertir a un valor entero.
     * @return (Integer): El valor entero del string.
     */
    public static Integer parseStringToInt(String s) {
        try {
            if (s == null || s.isEmpty())
                return Integer.MAX_VALUE;
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return Integer.MAX_VALUE;
        }
    }

}
