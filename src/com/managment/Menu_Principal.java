package com.managment;

import com.managment.inventory.Clase_SistemaInventario;
import com.managment.inventory.Clase_SistemaVentas;

import java.sql.SQLException;
import java.util.Scanner;

public class Menu_Principal {
    public static void main(String[] args) throws SQLException {

        Scanner input = new Scanner(System.in);
        int opc_main;

        /*INIT DATA*/
        Object[] objProductIds = Clase_SistemaInventario.getInitInventoryData("PRODUCT", "ID");
        Integer[] productId;
        productId = (Integer[]) objProductIds;
        Object[] objName = Clase_SistemaInventario.getInitInventoryData("PRODUCT", "NAME");
        String[] nombre;
        nombre = (String[]) objName;
        Object[] objPrice = Clase_SistemaInventario.getInitInventoryData("PRODUCT", "PRICE");
        Double[] precio;
        precio = (Double[]) objPrice;
        Object[] objQty = Clase_SistemaInventario.getInitInventoryData("PRODUCT", "QUANTITY");
        Integer[] cantidad;
        cantidad = (Integer[]) objQty;
        Object[] objDes = Clase_SistemaInventario.getInitInventoryData("PRODUCT", "DESCRIPTION");
        String[] descripcion;
        descripcion = (String[]) objDes;
        Object[] objCategoryId = Clase_SistemaInventario.getInitInventoryData("PRODUCT", "CATEGORY_ID");
        Integer[] categoriaId;
        categoriaId = (Integer[]) objCategoryId;
        // registra todas las columnas
        //producto se actualiza precio, cantidad y category_id

        //se registra todas las columnas menos enable
        // se actualiza el porcentaje y cantidad minima
        Object[] objDiscountId = Clase_SistemaInventario.getInitInventoryData("DISCOUNT", "ID");
        Integer[] discountId;
        discountId = (Integer[]) objDiscountId;

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


        do {
            System.out.println("Menú Principal");
            do {
                System.out.println("1. Sistema de Inventario");
                System.out.println("2. Sistema de Ventas");
                System.out.println("3. Salir");
                System.out.print("Elija una opción: ");
                opc_main = input.nextInt();
            } while (opc_main != 1 && opc_main != 2 && opc_main != 3);

            if (opc_main == 1)
                Clase_SistemaInventario.SI_Principal(productId, nombre, cantidad, precio, descripcion, categoriaId);
            else if (opc_main == 2) {
                Clase_SistemaVentas.SV_Principal(productId, nombre, cantidad, precio, descripcion, categoriaId, promoc_generales_id_categoria, promoc_generales_categoria, promoc_generales_desc, promoc_generales_aplica);
            }


        } while (opc_main != 3);

    }
}

