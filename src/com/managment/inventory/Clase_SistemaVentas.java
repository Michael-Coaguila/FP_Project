package com.managment.inventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;

public class Clase_SistemaVentas {
    public static void SV_Principal(Integer[] id, String[] nombre, Integer[] cantidad, Double[] precio, String[] descripcion, Integer[] categoria, Integer[] promoc_generales_id, String[] promoc_generales_categoria, Double[] promoc_generales_desc, Integer[] promoc_generales_aplica) throws SQLException {
        Scanner input = new Scanner(System.in);
        int opc_sv;

        System.out.println("Sistema de Ventas");
        do {
            System.out.println("1. Cotización-Venta");
            System.out.println("2. Promociones");
            System.out.print("Elija una opción: ");
            opc_sv = input.nextInt();
        } while (opc_sv != 1 && opc_sv != 2);

        switch (opc_sv) {
            case 1:
                SV_Cotizacion(id, nombre, cantidad, precio, descripcion, categoria, promoc_generales_categoria, promoc_generales_desc, promoc_generales_aplica);
                break;
            case 2:
                SV_Comprobacion_DescProm(promoc_generales_id, promoc_generales_categoria, promoc_generales_desc, promoc_generales_aplica);
                break;
        }
    }

    public static void SV_Cotizacion(Integer[] id, String[] nombre, Integer[] cantidad, Double[] precio, String[] descripcion, Integer[] categoria, String[] promoc_generales_categoria, Double[] promoc_generales_desc, Integer[] promoc_generales_aplica) throws SQLException {
        System.out.println("SV_Cotizacion");
        Scanner input = new Scanner(System.in);
        double precio_final = 0.0, precio_sin_desc = 0.0, precio_con_desc = 0.0;
        int num_items = 0, posc_busqueda = -1, cant_product, posc_desct = -1;
        Integer[] id_cotizados = new Integer[10];
        String[] nombre_cotizado = new String[10];
        Integer[] cant_id_cotizados = new Integer[10];
        String nombre_buscar, confirm_desct, cotizar_otro, confirm_busqueda, confirm_compra;
        do {
            confirm_desct = "N";
            confirm_busqueda = "N";
            System.out.print("Ingrese nombre producto a cotizar: ");
            nombre_buscar = input.next();
            Integer categoryId = 0;

            for (int i = 0; i < id.length; i++) {
                if (nombre[i].equals(nombre_buscar)) {
                    System.out.println("ID    NOMBRE    CANTIDAD    PRECIO/UND    DESCRIPCIÓN    CATEGORÍA ");
                    System.out.println(id[i] + " - " + nombre[i] + " - " + cantidad[i] + " - " + precio[i] + " - " +
                            descripcion[i] + " - " + (categoria[i] == 0 ? "NO APLICA" : categoria[i]));
                    posc_busqueda = i;
                    confirm_busqueda = "S";
                    categoryId = (categoria[i] == 0 ? 0 : categoria[i]);
                }
            }
            if (confirm_busqueda.equals("S") && num_items < 10) {
                do {
                    System.out.print("Ingrese cantidad: ");
                    cant_product = input.nextInt();
                } while (cant_product < 1 || cant_product > cantidad[posc_busqueda]);
                id_cotizados[num_items] = id[posc_busqueda];
                nombre_cotizado[num_items] = nombre[posc_busqueda];
                cant_id_cotizados[num_items] = cant_product;
                num_items++;
                precio_sin_desc = cant_product * precio[posc_busqueda];
                precio_con_desc = getDiscount(cant_product, precio_sin_desc, categoryId);
                precio_final = precio_final + precio_con_desc;
            }

            System.out.println("ID:           " + Arrays.toString(id_cotizados));
            System.out.println("ITEM:         " + Arrays.toString(nombre_cotizado));
            System.out.println("CANTIDAD:     " + Arrays.toString(cant_id_cotizados));
            //getDiscount(Integer quantity, Double accAmount, Integer id)
            System.out.println("PRECIO FINAL: " + precio_final);

            System.out.print("¿Desea cotizar otro producto más a su lista? S/N: ");
            cotizar_otro = input.next();
        } while (cotizar_otro.equals("S"));
        System.out.print("¿Desea realizar la compra? S/N: ");
        confirm_compra = input.next();
        if (confirm_compra.equals("S"))
            SV_Venta(id_cotizados, cant_id_cotizados, id, cantidad);

    }

    public static void SV_Venta(Integer[] id_cotizados, Integer[] cant_id_cotizados, Integer[] id, Integer[] cantidad) throws SQLException {
        System.out.println("SV_Venta");
        for (int i = 0; i < id_cotizados.length; i++) {
            for (int j = 0; j < id.length; j++) {
                if (id_cotizados[i] == id[j])
                    cantidad[j] = cantidad[j] - cant_id_cotizados[i];
            }
        }
        System.out.println("COMPRA REALIZADA!!");
        System.out.println(Arrays.toString(id));
        System.out.println(Arrays.toString(cantidad));

        /********************************
         * */
        for (int i = 0; i < id.length; i++) {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/sales", "root", "");
            String sql = " update product set quantity=? where id=?";
            PreparedStatement preparedStmt = con.prepareStatement(sql);
            preparedStmt.setInt(1, cantidad[i]);
            preparedStmt.setInt(2, id[i]);
            preparedStmt.execute();
            con.close();
        }
    }

    private static void getDiscountProductList(boolean isSpecific, Integer id) {
        Object[] objDiscountId = Clase_SistemaInventario.getInitInventoryData("DISCOUNT", "ID");
        Integer[] discountId;
        discountId = (Integer[]) objDiscountId;

        Object[] objDiscountName = Clase_SistemaInventario.getInitInventoryData("DISCOUNT", "NAME");
        String[] nombre_descuento;
        nombre_descuento = (String[]) objDiscountName;

        Object[] objPercentage = Clase_SistemaInventario.getInitInventoryData("DISCOUNT", "PERCENTAGE");
        Double[] promoc_generales_desc;
        promoc_generales_desc = (Double[]) objPercentage;

        Object[] objDiscountCategoryId = Clase_SistemaInventario.getInitInventoryData("DISCOUNT", "CATEGORY_ID");
        Integer[] promoc_generales_id_categoria;
        promoc_generales_id_categoria = (Integer[]) objDiscountCategoryId;

        Object[] objCategoryName = Clase_SistemaInventario.getInitInventoryData("DISCOUNT", "CATEGORY_NAME");
        String[] promoc_generales_categoria;
        promoc_generales_categoria = (String[]) objCategoryName;

        Object[] objMinQty = Clase_SistemaInventario.getInitInventoryData("DISCOUNT", "MIN_QUANTITY");
        Integer[] promoc_generales_aplica;
        promoc_generales_aplica = (Integer[]) objMinQty;


        if (isSpecific) {
            for (int i = 0; i < promoc_generales_id_categoria.length; i++) {
                if (promoc_generales_id_categoria[i] == id) {
                    System.out.println("ID    CATEGORIA    DESCUENTO    APLICA"); // Muestra todas las categorias con su id
                    System.out.println(promoc_generales_id_categoria[i] + " - " + promoc_generales_categoria[i] + " - " + promoc_generales_desc[i] + " - " + promoc_generales_aplica[i]);
                }
            }
        } else {
            System.out.println("ID    CATEGORIA    DESCUENTO    APLICA"); // Muestra todas las categorias con su id
            for (int i = 0; i < promoc_generales_id_categoria.length; i++) // Muestra todas las promociones existentes
                System.out.println(promoc_generales_id_categoria[i] + " - " + promoc_generales_categoria[i] + " - " + promoc_generales_desc[i] + " - " + promoc_generales_aplica[i]);
        }
    }

    public static void SV_Comprobacion_DescProm(Integer[] promoc_generales_id, String[] promoc_generales_categoria, Double[] promoc_generales_desc, Integer[] promoc_generales_aplica) throws SQLException {
        Scanner input = new Scanner(System.in);
        Integer opc_menu;
        String opc_add_promc, opc_consultar;
        Integer categ_eliminar;
        System.out.println("SV_Comprobacion_DescProm");
        String nombrePromoc;
        double porcentDescuento;
        int cantidadAplica, categoriaId;
        Integer[] idCategoria = promoc_generales_id; //arreglo de prueba
        String[] nombreCategoria = promoc_generales_categoria; //Arreglo de prueba
        do {
            opc_consultar = "N";
            opc_add_promc = "N";
            System.out.println("1. Promociones Generales");
            System.out.println("2. Deshabilitar Promociones");
            System.out.print("Elija una opción: ");
            opc_menu = input.nextInt();
            switch (opc_menu) {
                case 1:
                    // id - nombrePromoc - porcentaje - categoriaId - categoriaNombre - cantidadAplica
                    getDiscountProductList(false, null);
                    System.out.print("¿Desea agregar una nueva promoción? S/N: ");
                    opc_add_promc = input.next();

                    if (opc_add_promc.equals("S")) {
                        System.out.print("Nombre de categoría: ");
                        nombrePromoc = input.next();
                        System.out.print("Porcentaje descuento: ");
                        porcentDescuento = input.nextDouble();
                        porcentDescuento = porcentDescuento / 100.0;
                        System.out.print("Categoria ID: ");
                        categoriaId = input.nextInt();
                        System.out.print("Cantidad minima aplica descuento: ");
                        cantidadAplica = input.nextInt();

                        Connection con = DriverManager.getConnection(
                                "jdbc:mysql://localhost:3306/sales", "root", "");
                        String sq2l = " update product set price=?";
                        String sql = " insert into discount (category_name, percentage, category_id, min_quantity)" +
                                " values (?, ?, ?, ?)";

                        PreparedStatement preparedStmt = con.prepareStatement(sql);
                        preparedStmt.setString(1, nombrePromoc);
                        preparedStmt.setDouble(2, porcentDescuento);
                        preparedStmt.setInt(3, categoriaId);
                        preparedStmt.setInt(4, cantidadAplica);
                        preparedStmt.execute();
                        con.close();

                        //TODO SAVE DISCOUNT
                    }
                    getDiscountProductList(false, null);
                    break;
                case 2:
                    getDiscountProductList(false, null);
                    System.out.print("Ingrese promoción id a deshabilitar: ");
                    categ_eliminar = input.nextInt();
                    //TODO DELETE DISCOUNT - deshabilita
                    Connection con = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/sales", "root", "");
                    String sql = " update discount set enable=false where id=?";
                    PreparedStatement preparedStmt = con.prepareStatement(sql);
                    preparedStmt.setInt(1, categ_eliminar);
                    preparedStmt.execute();
                    con.close();
                    System.out.println("promoción eliminada");
                    break;
                default:
                    System.out.println("Opción Incorrecta");
                    break;
            }
            System.out.print("¿Desea volver a consultar? S/N: ");
            opc_consultar = input.next();
        } while (opc_consultar.equals("S"));
    }

    public static Double getDiscount(Integer quantity, Double accAmount, Integer id) {
        Object[] objDiscountId = Clase_SistemaInventario.getInitInventoryData("DISCOUNT", "ID");
        Integer[] discountId;
        discountId = (Integer[]) objDiscountId;

        Object[] objDiscountName = Clase_SistemaInventario.getInitInventoryData("DISCOUNT", "NAME");
        String[] nombre_descuento;
        nombre_descuento = (String[]) objDiscountName;

        Object[] objPercentage = Clase_SistemaInventario.getInitInventoryData("DISCOUNT", "PERCENTAGE");
        Double[] promoc_generales_desc;
        promoc_generales_desc = (Double[]) objPercentage;

        Object[] objDiscountCategoryId = Clase_SistemaInventario.getInitInventoryData("DISCOUNT", "CATEGORY_ID");
        Integer[] promoc_generales_id_categoria;
        promoc_generales_id_categoria = (Integer[]) objDiscountCategoryId;

        Object[] objCategoryName = Clase_SistemaInventario.getInitInventoryData("DISCOUNT", "CATEGORY_NAME");
        String[] promoc_generales_categoria;
        promoc_generales_categoria = (String[]) objCategoryName;

        Object[] objMinQty = Clase_SistemaInventario.getInitInventoryData("DISCOUNT", "MIN_QUANTITY");
        Integer[] promoc_generales_aplica;
        promoc_generales_aplica = (Integer[]) objMinQty;

        for (int i = 0; i < promoc_generales_id_categoria.length; i++) {
            if (promoc_generales_id_categoria[i] == id) {
                //evaluar si aplica
                if (quantity >= promoc_generales_aplica[i]) {
                    accAmount = accAmount - (accAmount * promoc_generales_desc[i]);
                }
            }
        }
        return accAmount;
    }
}
