package com.managment.inventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Clase_SistemaInventario {
    public static void SI_Principal(Integer[] id, String[] nombre, Integer[] cantidad, Double[] precio, String[] descripcion, Integer[] categoriaId) throws SQLException {
        Scanner input = new Scanner(System.in);
        int opc_si;

        System.out.println("Sistema de Inventario");
        do {
            System.out.println("1. Comprobación de Inventario");
            System.out.println("2. Ingreso Nuevos Productos");
            System.out.println("3. Actualización de Inventario");
            System.out.println("IMPORTANTE: Antes de ingresar un nuevo producto hacer 'Comprobación de Inventario'");
            System.out.print("Elija una opción: ");
            opc_si = input.nextInt();
        } while (opc_si != 1 && opc_si != 2 && opc_si != 3);
        switch (opc_si) {
            case 1:
                SI_Comprobacion(id, nombre, cantidad, precio, descripcion, categoriaId);
                break;
            case 2:
                SI_IngresoProductos();
                break;
            case 3:
                SI_Actualizacion(id, nombre, cantidad, precio, descripcion, categoriaId); //Se envis la BD
                break;
        }
    }

    public static void SI_Comprobacion(Integer[] id, String[] nombre, Integer[] cantidad, Double[] precio, String[] descripcion, Integer[] categoriaId) throws SQLException {
        Scanner input = new Scanner(System.in);
        int opc_si_comprob;
        String opc_busqueda, opc_reg_product;

        do {
            do {
                System.out.println("SI_Comprobación");
                do {
                    System.out.println("1. Inventario Completo");
                    System.out.println("2. Buscar por Codigo");
                    System.out.println("3. Buscar por Nombre");
                    System.out.print("Elija una opción: ");
                    opc_si_comprob = input.nextInt();
                } while (opc_si_comprob != 1 && opc_si_comprob != 2 && opc_si_comprob != 3);
                switch (opc_si_comprob) {
                    case 1:
                        System.out.println("ID    NOMBRE    CANTIDAD    PRECIO/UND    DESCRIPCIÓN    CATEGORÍA");
                        for (int i = 0; i < id.length; i++) {
                            System.out.println(id[i] + " - " + nombre[i] + " - " + cantidad[i] + " - " + precio[i] + " - " + descripcion[i] + " - " + categoriaId[i]);

                        }
                        break;
                    case 2:
                        System.out.print("Ingrese codigo a buscar: ");
                        int id_buscar = 0;
                        id_buscar = input.nextInt();
                        for (int i = 0; i < id.length; i++)
                            if (id[i] == id_buscar) {
                                System.out.println("ID    NOMBRE    CANTIDAD    PRECIO/UND    DESCRIPCIÓN    CATEGORÍA");
                                System.out.println(id[i] + " - " + nombre[i] + " - " + cantidad[i] + " - " + precio[i] + " - " + descripcion[i] + " - " + categoriaId[i]);
                            }
                        break;
                    case 3:
                        System.out.print("Ingrese nombre a buscar: ");
                        String nombre_buscar;
                        nombre_buscar = input.next();
                        for (int i = 0; i < id.length; i++)
                            if (nombre[i].equals(nombre_buscar)) {
                                System.out.println("ID    NOMBRE    CANTIDAD    PRECIO/UND    DESCRIPCIÓN    CATEGORÍA");
                                System.out.println(id[i] + " - " + nombre[i] + " - " + cantidad[i] + " - " + precio[i] + " - " + descripcion[i] + " - " + categoriaId[i]);
                            }
                        break;
                }
                System.out.print("Desea realizar otra búsqueda? S/N: ");
                opc_busqueda = input.next();
            } while (opc_busqueda.equals("S"));
            System.out.print("¿Desea registrar un nuevo producto? S/N: ");
            opc_reg_product = input.next();
            if (opc_reg_product.equals("S"))
                SI_IngresoProductos();
        } while (opc_reg_product.equals("S"));
    }

    public static void SI_IngresoProductos() throws SQLException {
        String name;
        int price;
        int quantity;
        String description;
        int categoryId;
        Scanner input = new Scanner(System.in);

        System.out.print("Nombre del producto: ");
        name = input.next();
        System.out.print("Precio Unidad: ");
        price = input.nextInt();
        System.out.print("Cantidad: ");
        quantity = input.nextInt();
        System.out.print("Descripción: ");
        description = input.next();

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
        System.out.println("ID    CATEGORIA    DESCUENTO    APLICA"); // Muestra todas las categorias con su id
        for (int i = 0; i < promoc_generales_id_categoria.length; i++) // Muestra todas las promociones existentes
            System.out.println(promoc_generales_id_categoria[i] + " - " + promoc_generales_categoria[i] + " - " + promoc_generales_desc[i] + " - " + promoc_generales_aplica[i]);


        System.out.print("Categoria: ");
        categoryId = input.nextInt();
        System.out.print("Registrando ... ");

        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/sales", "root", "");

        String sql = " insert into product (name, price, quantity, description, category_id)" +
                " values (?, ?, ?, ?, ?)";
        PreparedStatement preparedStmt = con.prepareStatement(sql);
        preparedStmt.setString(1, name);
        preparedStmt.setInt(2, price);
        preparedStmt.setInt(3, quantity);
        preparedStmt.setString(4, description);
        preparedStmt.setInt(5, categoryId);
        preparedStmt.execute();
        con.close();
        // return id
        Object[] objIds = getInitInventoryData("PRODUCT", "ID");
        Integer[] id;
        id = (Integer[]) objIds;
        Object[] objName = getInitInventoryData("PRODUCT", "NAME");
        String[] nombre;
        nombre = (String[]) objName;
        Object[] objPrice = getInitInventoryData("PRODUCT", "PRICE");
        Double[] precio;
        precio = (Double[]) objPrice;
        Object[] objQty = getInitInventoryData("PRODUCT", "QUANTITY");
        Integer[] cantidad;
        cantidad = (Integer[]) objQty;
        Object[] objDes = getInitInventoryData("PRODUCT", "DESCRIPTION");
        String[] descripcion;
        descripcion = (String[]) objDes;
        Object[] objCategoryId = getInitInventoryData("PRODUCT", "CATEGORY_ID");
        Integer[] categoriaId;
        categoriaId = (Integer[]) objCategoryId;

        int lastId = objIds.length - 1;
        System.out.println("ID    NOMBRE    CANTIDAD    PRECIO/UND    DESCRIPCIÓN    CATEGORÍA");
        System.out.println(id[lastId] + " - " + nombre[lastId] + " - " + cantidad[lastId] + " - " + precio[lastId] + " - " + descripcion[lastId] + " - " + categoriaId[lastId]);

        //Retorna la visualización de su id generado autoaticamente con sus datos ingresados
    }

    private static void getCurrentProductList(boolean isSpecific, Integer id) {
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

        if (isSpecific) {
            for (int i = 0; i < productId.length; i++) {
                if (productId[i] == id) {
                    System.out.println("ID    NOMBRE    CANTIDAD    PRECIO/UND    DESCRIPCIÓN    CATEGORÍA");
                    System.out.println(productId[i] + " - " + nombre[i] + " - " + cantidad[i] + " - " + precio[i] + " - " + descripcion[i] + " - " + categoriaId[i]);
                }
            }
        } else {
            for (int i = 0; i < productId.length; i++) {
                System.out.println("ID    NOMBRE    CANTIDAD    PRECIO/UND    DESCRIPCIÓN    CATEGORÍA");
                System.out.println(productId[i] + " - " + nombre[i] + " - " + cantidad[i] + " - " + precio[i] + " - " + descripcion[i] + " - " + categoriaId[i]);
            }
        }
    }

    public static void SI_Actualizacion(Integer[] id, String[] nombre, Integer[] cantidad, Double[] precio, String[] descripcion, Integer[] categoriaId) throws SQLException {
        Scanner input = new Scanner(System.in);
        int modificar_item_id, opc_menu, opc_case_actualizar;
        String repetir_actualizacion, opc_eliminar, modificar_otro_id;
        repetir_actualizacion = "N";
        System.out.println("SI_Actualizacion");
        do {
            //imprimir lista actual
            getCurrentProductList(false, null);

            System.out.print("Ingrese el codigo del item a modificar: ");
            modificar_item_id = input.nextInt();

            System.out.println("1. Actualizar Producto");
            System.out.println("2. Eliminar Producto");
            System.out.print("Elija una opción: ");
            opc_menu = input.nextInt();

            do {
                switch (opc_menu) {
                    case 1:
                        getCurrentProductList(true, modificar_item_id);
                        System.out.println("1. Actualizar Cantidad");
                        System.out.println("2. Actualizar Precio");
                        System.out.print("Elija una opción: ");
                        opc_case_actualizar = input.nextInt();
                        if (opc_case_actualizar == 1) {
                            System.out.println("Ingrese cantidad");
                            Integer quantity = input.nextInt();
                            Connection con = DriverManager.getConnection(
                                    "jdbc:mysql://localhost:3306/sales", "root", "");
                            String sql = " update product set quantity=? where id=?";
                            PreparedStatement preparedStmt = con.prepareStatement(sql);
                            preparedStmt.setInt(1, quantity);
                            preparedStmt.setInt(2, modificar_item_id);
                            preparedStmt.execute();
                            con.close();
                            getCurrentProductList(true, modificar_item_id);

                            System.out.println("Cantidad actualizada");

                        } else if (opc_case_actualizar == 2) {
                            System.out.println("Ingrese precio");
                            Double price = input.nextDouble();
                            Connection con = DriverManager.getConnection(
                                    "jdbc:mysql://localhost:3306/sales", "root", "");
                            String sql = " update product set price=? where id=?";
                            PreparedStatement preparedStmt = con.prepareStatement(sql);
                            preparedStmt.setDouble(1, price);
                            preparedStmt.setInt(2, modificar_item_id);
                            preparedStmt.execute();
                            con.close();
                            getCurrentProductList(true, modificar_item_id);
                            System.out.println("Precio actualizando");
                        } else
                            System.out.println("Opción Incorrecta");
                        System.out.print("¿Desea realizar otra actualización de este producto? S/N: ");
                        repetir_actualizacion = input.next();
                        break;
                    case 2:
                        getCurrentProductList(true, modificar_item_id);
                        System.out.print("¿Desea eliminar el producto? S/N: ");
                        opc_eliminar = input.next();
                        if (opc_eliminar.equals("S")) {
                            Connection con = DriverManager.getConnection(
                                    "jdbc:mysql://localhost:3306/sales", "root", "");
                            String sql = " delete from product where id = ?";
                            PreparedStatement preparedStmt = con.prepareStatement(sql);
                            preparedStmt.setInt(1, modificar_item_id);
                            preparedStmt.execute();
                            con.close();
                            System.out.println("Producto Eliminado ");

                        }
                        break;
                    default:
                        break;
                }
            } while (repetir_actualizacion.equals("S"));
            System.out.print("¿Desea modificar otro producto? S/N: ");
            modificar_otro_id = input.next();
        } while (modificar_otro_id.equals("S"));
    }

    public static Object[] getInitInventoryData(String table, String column) {
        switch (table) {
            case "PRODUCT":
                return getCustomProduct(column);
            case "DISCOUNT":
                return getCustomDiscount(column);
            default:
                System.out.println("not found table");
                return null;
        }
    }

    private static Object[] getCustomProduct(String column) {

        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/sales", "root", "");

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from product");

            List<Integer> idList = new ArrayList<>();
            List<String> nameList = new ArrayList<>();
            List<Double> priceList = new ArrayList<>();
            List<Integer> quantityList = new ArrayList<>();
            List<String> descriptionList = new ArrayList<>();
            List<Integer> categoryList = new ArrayList<>();

            while (rs.next()) {
                Integer id = rs.getInt("id");
                idList.add(id);
                String name = rs.getString("name");
                nameList.add(name);
                double price = rs.getDouble("price");
                priceList.add(price);
                int quantity = rs.getInt("quantity");
                quantityList.add(quantity);
                String description = rs.getString("description");
                descriptionList.add(description);
                Integer category = rs.getInt("category_id");
                categoryList.add(category);
            }

            switch (column) {
                case "ID":
                    Object[] id = new Integer[Collections.singletonList(idList).get(0).size()];
                    for (int i = 0; i < Collections.singletonList(idList).get(0).size(); i++)
                        id[i] = ((ArrayList<?>) Collections.singletonList(idList).get(0)).get(i);
                    return id;
                case "NAME":
                    Object[] name = new String[Collections.singletonList(nameList).get(0).size()];
                    for (int i = 0; i < Collections.singletonList(nameList).get(0).size(); i++)
                        name[i] = ((ArrayList<?>) Collections.singletonList(nameList).get(0)).get(i);
                    return name;

                case "PRICE":
                    Object[] price = new Double[Collections.singletonList(priceList).get(0).size()];
                    for (int i = 0; i < Collections.singletonList(priceList).get(0).size(); i++)
                        price[i] = ((ArrayList<?>) Collections.singletonList(priceList).get(0)).get(i);
                    return price;
                case "QUANTITY":
                    Object[] quantity = new Integer[Collections.singletonList(quantityList).get(0).size()];
                    for (int i = 0; i < Collections.singletonList(quantityList).get(0).size(); i++)
                        quantity[i] = ((ArrayList<?>) Collections.singletonList(quantityList).get(0)).get(i);
                    return quantity;
                case "DESCRIPTION":
                    Object[] description = new String[Collections.singletonList(descriptionList).get(0).size()];
                    for (int i = 0; i < Collections.singletonList(descriptionList).get(0).size(); i++)
                        description[i] = ((ArrayList<?>) Collections.singletonList(descriptionList).get(0)).get(i);
                    return description;
                case "CATEGORY_ID":
                    Object[] category = new Integer[Collections.singletonList(categoryList).get(0).size()];
                    for (int i = 0; i < Collections.singletonList(categoryList).get(0).size(); i++)
                        category[i] = ((ArrayList<?>) Collections.singletonList(categoryList).get(0)).get(i);
                    return category;
                default:
                    System.out.println("column not found");
                    return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object[] getCustomDiscount(String column) {

        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/sales", "root", "");

            Statement stmt = con.createStatement();
            // 1° command 2° columnas 3° from 4° tabla 5° condition
            ResultSet rs = stmt.executeQuery("select * from discount where enable");

            List<Integer> idList = new ArrayList<>();
            List<String> nameList = new ArrayList<>();
            List<Double> percentageList = new ArrayList<>();
            List<Integer> categoryIdList = new ArrayList<>();
            List<String> categoryNameList = new ArrayList<>();
            List<Integer> minQtyList = new ArrayList<>();

            while (rs.next()) {
                Integer id = rs.getInt("id");
                idList.add(id);
                double percentage = rs.getDouble("percentage");
                percentageList.add(percentage);
                int categoryId = rs.getInt("category_id");
                categoryIdList.add(categoryId);
                String categoryName = rs.getString("category_name");
                categoryNameList.add(categoryName);
                Integer minQty = rs.getInt("min_quantity");
                minQtyList.add(minQty);
            }

            switch (column) {
                case "ID":
                    Object[] id = new Integer[Collections.singletonList(idList).get(0).size()];
                    for (int i = 0; i < Collections.singletonList(idList).get(0).size(); i++)
                        id[i] = ((ArrayList<?>) Collections.singletonList(idList).get(0)).get(i);
                    return id;
                case "NAME":
                    Object[] name = new String[Collections.singletonList(nameList).get(0).size()];
                    for (int i = 0; i < Collections.singletonList(nameList).get(0).size(); i++)
                        name[i] = ((ArrayList<?>) Collections.singletonList(nameList).get(0)).get(i);
                    return name;

                case "PERCENTAGE":
                    Object[] percentage = new Double[Collections.singletonList(percentageList).get(0).size()];
                    for (int i = 0; i < Collections.singletonList(percentageList).get(0).size(); i++)
                        percentage[i] = ((ArrayList<?>) Collections.singletonList(percentageList).get(0)).get(i);
                    return percentage;
                case "CATEGORY_ID":
                    Object[] categoryId = new Integer[Collections.singletonList(categoryIdList).get(0).size()];
                    for (int i = 0; i < Collections.singletonList(categoryIdList).get(0).size(); i++)
                        categoryId[i] = ((ArrayList<?>) Collections.singletonList(categoryIdList).get(0)).get(i);
                    return categoryId;
                case "CATEGORY_NAME":
                    Object[] categoryName = new String[Collections.singletonList(categoryNameList).get(0).size()];
                    for (int i = 0; i < Collections.singletonList(categoryNameList).get(0).size(); i++)
                        categoryName[i] = ((ArrayList<?>) Collections.singletonList(categoryNameList).get(0)).get(i);
                    return categoryName;
                case "MIN_QUANTITY":
                    Object[] minQty = new Integer[Collections.singletonList(minQtyList).get(0).size()];
                    for (int i = 0; i < Collections.singletonList(minQtyList).get(0).size(); i++)
                        minQty[i] = ((ArrayList<?>) Collections.singletonList(minQtyList).get(0)).get(i);
                    return minQty;
                default:
                    System.out.println("column not found");
                    return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
