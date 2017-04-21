package com.company;
import java.io.*;
import java.util.*;

public class Main {

    public static Scanner teclado = new Scanner(System.in);
    public static PrintStream out = System.out;

    public static void pausar(String mensage) {
        out.print(mensage + "\nPresione <ENTER> para continuar . . . ");
        teclado.nextLine();
        out.println();
    }

    public static String leer_cadena(String mensaje) {
        out.print(mensaje + ": ");
        return teclado.nextLine();
    }

    public static int leer_entero(String mensaje) {
        try {
            return Integer.parseInt(leer_cadena(mensaje));
        } catch (NumberFormatException e) {
            out.print("N\u00FAmero incorrecto.");
            return leer_entero(mensaje);
        }
    }

    public static String ruta = "contactos.tsv";

    public static void main(String[] args) {

        Funcion<Contacto> imprimir = new Funcion<Contacto>() {
            @Override
            public void funcion(Contacto contacto, Object parametros) {
                out.println(contacto);
                int[] contador = (int[]) parametros;
                contador[0]++;
            }
        };
        Funcion<Contacto> imprimirEnArchivo = new Funcion<Contacto>() {
            @Override
            public void funcion(Contacto contacto, Object parametros) {
                PrintStream archivo = (PrintStream) parametros;
                archivo.print(contacto.getAlias() + "\t");
                archivo.print(contacto.getTelefono_fijo() + "\t");
                archivo.print(contacto.getTelefono_movil() + "\t");
                archivo.print(contacto.getCorreo() + "\n");
            }
        };
        if(!System.getProperties().get("os.name").equals("Linux") && System.console()!=null)
            try {
                out = new PrintStream(System.out, true, "CP850");
                teclado = new Scanner(System.in, "CP850");
            } catch (UnsupportedEncodingException e) {}
        Vector<Contacto> vector = new Vector<Contacto>();
        int i, n;
        Contacto dato = null, contacto;
        int[] contador = {0};
        int opcion, subopcion;
        String[] campos;
        try {
            Scanner entrada = new Scanner(new FileReader(ruta));
            while (entrada.hasNextLine()) {
                campos = entrada.nextLine().split("\t");
                contacto = new Contacto();
                contacto.setAlias(campos[0]);
                contacto.setTelefono_fijo(campos[1]);
                contacto.setTelefono_movil(campos[2]);
                contacto.setCorreo(campos[3]);
                vector.add(contacto);
            }
            entrada.close();
        } catch (FileNotFoundException e) {}
        contacto = new Contacto();
        do {
            out.println("MEN\u00DA");
            out.println("1.- Agregar nuevo contacto");
            out.println("2.- Consultas");
            out.println("3.- Modificar contacto");
            out.println("4.- Eliminar contacto");
            out.println("5.- Ordenar registros");
            out.println("6.- Lista de contactos");
            out.println("7.- Salir");
            do {
                opcion = leer_entero ("Seleccione una opci\u00F3n");
                if(opcion<1 || opcion>7)
                    out.println("Opci\u00F3nn no v\u00E1lida.");
            } while (opcion<1 || opcion>7);
            out.println();
            if (vector.isEmpty() && opcion!=1 && opcion!=7) {
                pausar("No hay contactos.\n");
                continue;
            }
            if (opcion<5) {
                contacto.setAlias(leer_cadena ("Ingrese el nombre del contacto"));
                i = vector.indexOf(contacto);
                dato = i<0 ? null : vector.get(i);
                if (dato!=null) {
                    out.println();
                    imprimir.funcion(dato, contador);
                }
            }
            if (opcion==1 && dato!=null)
                out.println("El contacto ya existe.");
            else if (opcion>=2 && opcion<=4 && dato==null)
                out.println("\ncontacto no encontrado.");
            else switch (opcion) {
                    case 1:
                        contacto.setTelefono_fijo(leer_cadena ("Ingrese el telefono fijo"));
                        contacto.setTelefono_movil(leer_cadena ("Ingrese el telefono movil"));
                        contacto.setCorreo(leer_cadena ("Ingrese el correo"));
                        vector.add(contacto);
                        contacto = new Contacto();
                        out.println("\nRegistro agregado correctamente.");
                        break;
                    case 3:
                        out.println("Men\u00FA de modificaci\u00F3n de campos");
                        out.println("1.- telefono fijo");
                        out.println("2.- telefono movil");
                        out.println("3.- correo");
                        do {
                            subopcion = leer_entero ("Seleccione un n\u00FAmero de campo a modificar");
                            if (subopcion<1 || subopcion>3)
                                out.println("Opci\u00F3n no v\u00E1lida.");
                        } while (subopcion<1 || subopcion>3);
                        switch (subopcion) {
                            case 1:
                                dato.setTelefono_fijo(leer_cadena ("Ingrese el nuevo telefono fijo"));
                                break;
                            case 2:
                                dato.setTelefono_movil(leer_cadena ("Ingrese el nuevo telefono movil"));
                                break;
                            case 3:
                                dato.setCorreo(leer_cadena ("Ingrese el nuevo correo"));
                                break;
                        }
                        out.println("\nRegistro actualizado correctamente.");
                        break;
                    case 4:
                        vector.remove(dato);
                        out.println("Registro borrado correctamente.");
                        break;
                    case 5:
                        Collections.sort(vector);
                        out.println("Registros ordenados correctamente.");
                        break;
                    case 6:
                        n = vector.size();
                        contador[0] = 0;
                        for (i=0; i<n; i++)
                            imprimir.funcion(vector.get(i), contador);
                        out.println("Total de registros: " + contador[0] + ".");
                        break;
                }
            if (opcion<7 && opcion>=1)
                pausar("");
        } while (opcion!=7);
        try {
            PrintStream salida = new PrintStream(ruta);
            n = vector.size();
            for (i=0; i<n; i++)
                imprimirEnArchivo.funcion(vector.get(i), salida);
            salida.close();
        } catch (FileNotFoundException e) {}
    }
}

interface Funcion<T extends Comparable<T>> {
    void funcion(T dato, Object parametros);
}

class Contacto implements Comparable<Contacto> {

    private String alias;
    private String telefono_fijo;
    private String telefono_movil;
    private String correo;

    @Override
    public boolean equals(Object contacto) {
        return this==contacto || (contacto instanceof Contacto && alias.equals(((Contacto)contacto).alias));
    }

    @Override
    public int compareTo(Contacto contacto) {
        return alias.compareTo(contacto.alias);
    }

    @Override
    public String toString() {
        return
                "alias         : " + alias + "\n" +
                        "telefono fijo : " + telefono_fijo + "\n" +
                        "telefono movil: " + telefono_movil + "\n" +
                        "correo        : " + correo + "\n";
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTelefono_fijo() {
        return telefono_fijo;
    }

    public void setTelefono_fijo(String telefono_fijo) {
        this.telefono_fijo = telefono_fijo;
    }

    public String getTelefono_movil() {
        return telefono_movil;
    }

    public void setTelefono_movil(String telefono_movil) {
        this.telefono_movil = telefono_movil;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}