package org.example;

import funciones.FuncionApp;
import javax.persistence.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
            EntityManager em = emf.createEntityManager();

            // Persistir la entidad UnidadMedida en estado "gestionada"
            em.getTransaction().begin();
            // Crear una nueva entidad UnidadMedida en estado "nueva"
            UnidadMedida unidadMedida = UnidadMedida.builder()
                    .denominacion("Kilogramo")
                    .build();
            UnidadMedida unidadMedidapote = UnidadMedida.builder()
                    .denominacion("pote")
                    .build();

            em.persist(unidadMedida);
            em.persist(unidadMedidapote);


            // Crear una nueva entidad Categoria en estado "nueva"
            Categoria categoria = Categoria.builder()
                    .denominacion("Frutas")
                    .esInsumo(true)
                    .build();

            // Crear una nueva entidad Categoria en estado "nueva"
            Categoria categoriaPostre = Categoria.builder()
                    .denominacion("Postre")
                    .esInsumo(false)
                    .build();

            // Persistir la entidad Categoria en estado "gestionada"

            em.persist(categoria);
            em.persist(categoriaPostre);


            // Crear una nueva entidad ArticuloInsumo en estado "nueva"
            ArticuloInsumo articuloInsumo = ArticuloInsumo.builder()
                    .denominacion("Manzana").codigo(UUID.randomUUID().toString())
                    .precioCompra(1.5)
                    .precioVenta(5d)
                    .stockActual(100)
                    .stockMaximo(200)
                    .esParaElaborar(true)
                    .unidadMedida(unidadMedida)
                    .build();


            ArticuloInsumo articuloInsumoPera = ArticuloInsumo.builder()
                    .denominacion("Pera").codigo(UUID.randomUUID().toString())
                    .precioCompra(2.5)
                    .precioVenta(10d)
                    .stockActual(130)
                    .stockMaximo(200)
                    .esParaElaborar(true)
                    .unidadMedida(unidadMedida)
                    .build();

            // Persistir la entidad ArticuloInsumo en estado "gestionada"

            em.persist(articuloInsumo);
            em.persist(articuloInsumoPera);

            Imagen manza1 = Imagen.builder().denominacion("Manzana Verde").
                    build();
            Imagen manza2 = Imagen.builder().denominacion("Manzana Roja").
                    build();

            Imagen pera1 = Imagen.builder().denominacion("Pera Verde").
                    build();
            Imagen pera2 = Imagen.builder().denominacion("Pera Roja").
                    build();


            // Agregar el ArticuloInsumo a la Categoria
            categoria.getArticulos().add(articuloInsumo);
            categoria.getArticulos().add(articuloInsumoPera);
            // Actualizar la entidad Categoria en la base de datos

            // em.merge(categoria);

            // Crear una nueva entidad ArticuloManufacturadoDetalle en estado "nueva"
            ArticuloManufacturadoDetalle detalleManzana = ArticuloManufacturadoDetalle.builder()
                    .cantidad(2)
                    .articuloInsumo(articuloInsumo)
                    .build();


            ArticuloManufacturadoDetalle detallePera = ArticuloManufacturadoDetalle.builder()
                    .cantidad(2)
                    .articuloInsumo(articuloInsumoPera)
                    .build();

            // Crear una nueva entidad ArticuloManufacturado en estado "nueva"
            ArticuloManufacturado articuloManufacturado = ArticuloManufacturado.builder()
                    .denominacion("Ensalada de frutas")
                    .descripcion("Ensalada de manzanas y peras ")
                    .codigo(Long.toString(new Date().getTime()))
                    .precioVenta(150d)
                    .tiempoEstimadoMinutos(10)
                    .preparacion("Cortar las frutas en trozos pequeños y mezclar")
                    .unidadMedida(unidadMedidapote)
                    .build();

            articuloManufacturado.getImagenes().add(manza1);
            articuloManufacturado.getImagenes().add(pera1);

            categoriaPostre.getArticulos().add(articuloManufacturado);
            // Crear una nueva entidad ArticuloManufacturadoDetalle en estado "nueva"

            // Agregar el ArticuloManufacturadoDetalle al ArticuloManufacturado
            articuloManufacturado.getDetalles().add(detalleManzana);
            articuloManufacturado.getDetalles().add(detallePera);
            // Persistir la entidad ArticuloManufacturado en estado "gestionada"
            categoriaPostre.getArticulos().add(articuloManufacturado);
            em.persist(articuloManufacturado);
            em.getTransaction().commit();

            // modificar la foto de manzana roja
            em.getTransaction().begin();
            articuloManufacturado.getImagenes().add(manza2);
            em.merge(articuloManufacturado);
            em.getTransaction().commit();

            //creo y guardo un cliente
            em.getTransaction().begin();
            Cliente cliente = Cliente.builder()
                    .cuit(FuncionApp.generateRandomCUIT())
                    .razonSocial("Juan Perez")
                    .build();
            em.persist(cliente);
            em.getTransaction().commit();

            //creo y guardo una factura
            em.getTransaction().begin();

            FacturaDetalle detalle1 = new FacturaDetalle(3, articuloInsumo);
            detalle1.calcularSubTotal();
            FacturaDetalle detalle2 = new FacturaDetalle(3, articuloInsumoPera);
            detalle2.calcularSubTotal();
            FacturaDetalle detalle3 = new FacturaDetalle(3, articuloManufacturado);
            detalle3.calcularSubTotal();

            Factura factura = Factura.builder()
                    .puntoVenta(2024)
                    .fechaAlta(new Date())
                    .fechaComprobante(FuncionApp.generateRandomDate())
                    .cliente(cliente)
                    .nroComprobante(FuncionApp.generateRandomNumber())
                    .build();
            factura.addDetalleFactura(detalle1);
            factura.addDetalleFactura(detalle2);
            factura.addDetalleFactura(detalle3);
            factura.calcularTotal();

            em.persist(factura);
            em.getTransaction().commit();


            String jpql = "SELECT am FROM ArticuloManufacturado am LEFT JOIN FETCH am.detalles d WHERE am.id = :id";
            Query query = em.createQuery(jpql);
            query.setParameter("id", 3L);
            ArticuloManufacturado articuloManufacturadoCon = (ArticuloManufacturado) query.getSingleResult();

            System.out.println("Artículo manufacturado: " + articuloManufacturado.getDenominacion());
            System.out.println("Descripción: " + articuloManufacturado.getDescripcion());
            System.out.println("Tiempo estimado: " + articuloManufacturado.getTiempoEstimadoMinutos() + " minutos");
            System.out.println("Preparación: " + articuloManufacturado.getPreparacion());

            System.out.println("Líneas de detalle:");
            for (ArticuloManufacturadoDetalle detalle : articuloManufacturado.getDetalles()) {
                System.out.println("- " + detalle.getCantidad() + " unidades de " + detalle.getArticuloInsumo().getDenominacion());

            }

            System.out.println("\nEspacio de consultas JPQL\n");
            System.out.println("===============================================================================================");
            System.out.println("Consulta 1:");
            try {
                List<Cliente> listaClientes = consultarClientes(em);
                listaClientes.forEach(System.out::println);
            } finally {
                System.out.println("\n");
            }

            System.out.println("===============================================================================================");
            System.out.println("Consulta 2:");

            try {
                List<Factura> ultimasFacturas = consultarFacturas(em);
                ultimasFacturas.forEach(System.out::println);
            } finally {
                System.out.println("\n");
            }

            System.out.println("===============================================================================================");
            System.out.println("Consulta 3:");

            System.out.println(obtenerClienteConMayorVenta(em));
            System.out.println("\n");

            System.out.println("===============================================================================================");
            System.out.println("Consulta 4:");

            try {
                articulosMasVendidos(em);
            } finally {
                System.out.println("\n");
            }

            System.out.println("===============================================================================================");
            System.out.println("Consulta 5:");

            try {
                List<Factura> facturasACliente = facturasEmitidasACliente(em);
                facturasACliente.forEach(System.out::println);
            } finally {
                System.out.println("\n");
            }


            System.out.println("===============================================================================================");
            System.out.println("Consulta 6:");

            try {
                Double totalDeCliente = montoTotalPorCliente(em);
                System.out.println("EL total facturado por el cliente de ID " + cliente.getId() + " es de: " +
                        totalDeCliente);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
            } finally {
                System.out.println("\n");
            }

            System.out.println("===============================================================================================");
            System.out.println("Consulta 7:");

            try {
                System.out.println("Los articulos vendidos en la factura" + factura.getId());
                List<Articulo> articulos = listarArticulosPorFactura(em);
                articulos.forEach(System.out::println);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
            } finally {
                System.out.println("\n");
            }


            System.out.println("===============================================================================================");
            System.out.println("Consulta 8:");

            try {
                System.out.println("El artículo más de caro de la factura nro: " + factura.getId() + " es el: ");
                Articulo articulo = buscarArticuloMasCaro(em);
                System.out.println(articulo);
            } catch (IllegalArgumentException iea) {
                iea.printStackTrace();
            } finally {
                System.out.println("\n");
            }

            System.out.println("===============================================================================================");
            System.out.println("Consulta 9:");

            try {
                Long sumatoria = contarFacturas(em);
                System.out.println("La cantidad de facturas generadas en el sistema es de: " + sumatoria);
            } finally {
                System.out.println("\n");
            }

            System.out.println("===============================================================================================");
            System.out.println("Consulta 10:");

            try {
                List<Factura> facturasMayoresA = listarFacturasMayores(em);
                if(facturasMayoresA.isEmpty()){
                    System.out.println("No hay ninguna factura que supere el monto dado.");
                }else{
                    facturasMayoresA.forEach(System.out::println);
                }
            } finally {
                System.out.println("\n");
            }

            System.out.println("===============================================================================================");
            System.out.println("Consulta 11:");

            try {
                List<Factura> facturaConArticulo = listarFacturasConArticuloDado(em);
                facturaConArticulo.forEach(System.out::println);
            } finally {
                System.out.println("\n");
            }

            System.out.println("===============================================================================================");
            System.out.println("Consulta 12:");

            try {

                List<Articulo> articuloPorCodigoParcial = listarArtPorCodigoParcial(em);
                if (articuloPorCodigoParcial.isEmpty()) {
                    System.out.println("Ningun articulo posee ese patron de codigo.");
                } else {
                    articuloPorCodigoParcial.forEach(System.out::println);
                }
            }finally {
                System.out.println("\n");
            }

            System.out.println("===============================================================================================");
            System.out.println("Consulta 13:");

            try{
                List<Articulo> listaPorPromedio = listarArtPorPromedio(em);
                listaPorPromedio.forEach(System.out::println);
            }finally {
                System.out.println("\n");
            }

            System.out.println("===============================================================================================");
            System.out.println("Consulta 13:");

            try{
                List<Cliente> clientesConFactura = listarClientesConFactura(em);
                if(clientesConFactura.isEmpty()){
                    System.out.println("La clausula exists devuelve un tipo de datos booleano");
                    System.out.println("Compruba si una subconsulta devuelve al menos una fila.");
                    System.out.println("No existen clientes con facturas");
                }else {
                    clientesConFactura.forEach(System.out::println);
                }
            }finally {
                System.out.println("\n");
            }

            em.close();
            emf.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static List<Cliente> consultarClientes(EntityManager em) {
        String consulta = "SELECT c FROM Cliente c";
        Query query = em.createQuery(consulta);
        List<Cliente> clientes = query.getResultList();
        return clientes;
    }

    public static List<Factura> consultarFacturas(EntityManager em) {
        Date fechaHasta = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaHasta);
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        Date fechaDesde = calendar.getTime();

        String consulta = "SELECT DISTINCT f FROM Factura f " +
                "LEFT JOIN FETCH f.cliente c " +
                "LEFT JOIN FETCH f.detallesFactura d " +
                "WHERE f.fechaAlta BETWEEN :fechaDesde AND :fechaHasta";
        Query query = em.createQuery(consulta);
        query.setParameter("fechaDesde", fechaDesde);
        query.setParameter("fechaHasta", fechaHasta);
        List<Factura> facturas = query.getResultList();
        return facturas;
    }

    public static Cliente obtenerClienteConMayorVenta(EntityManager em) {
        String consulta = "SELECT c FROM Cliente c JOIN c.facturas f " +
                "GROUP BY c ORDER BY COUNT(f) DESC";
        TypedQuery<Cliente> query = em.createQuery(consulta, Cliente.class);
        query.setMaxResults(1);
        return query.getSingleResult();
    }

    public static void articulosMasVendidos(EntityManager em) {
        String consulta = "SELECT a.denominacion, SUM(fd.cantidad) " +
                "FROM FacturaDetalle fd " +
                "JOIN fd.articulo a " +
                "GROUP BY a.denominacion " +
                "ORDER BY SUM(fd.cantidad) DESC";
        Query query = em.createQuery(consulta);
        List<Object[]> articulos = query.getResultList();
        if (articulos.isEmpty()) {
            System.out.println("No se encontraron ventas de productos.");
        } else {
            System.out.println("Ranking de productos mas vendidos:");
            int ranking = 1;
            for (Object[] art : articulos) {
                String denom = (String) art[0];
                Double totalVendido = (Double) art[1];
                System.out.println("Top " + ranking + ":");
                System.out.println("\t" + "Denominacion: " + denom);
                System.out.println("\t" + "Total vendido: " + totalVendido);
                ranking++;
            }
        }
    }

    public static List<Factura> facturasEmitidasACliente(EntityManager em) {
        Date fechaHasta = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaHasta);
        calendar.add(Calendar.MONTH, -3);
        Date fechaDesde = calendar.getTime();
        String consulta = "SELECT f FROM Factura f " +
                "WHERE f.cliente.id = :idCliente " +
                "AND f.fechaAlta BETWEEN :fechaDesde AND :fechaHasta " +
                "ORDER BY f.fechaAlta DESC";
        TypedQuery<Factura> query = em.createQuery(consulta, Factura.class);
        query.setParameter("idCliente", 1L);
        query.setParameter("fechaDesde", fechaDesde);
        query.setParameter("fechaHasta", fechaHasta);
        List<Factura> facturas = query.getResultList();
        return facturas;
    }

    public static Double montoTotalPorCliente(EntityManager em) {
        String consulta = "SELECT SUM(f.total) FROM Factura f " +
                "WHERE f.cliente.id = :idCliente";
        TypedQuery<Double> query = em.createQuery(consulta, Double.class);
        query.setParameter("idCliente", 1L);
        Double total = query.getSingleResult();
        return total;
    }

    public static List<Articulo> listarArticulosPorFactura(EntityManager em) {
        String consulta = "SELECT fd.articulo FROM FacturaDetalle fd WHERE " +
                "fd.factura.id = :idFactura";
        TypedQuery<Articulo> query = em.createQuery(consulta, Articulo.class);
        query.setParameter("idFactura", 1L);
        List<Articulo> articulos = query.getResultList();
        return articulos;
    }

    public static Articulo buscarArticuloMasCaro(EntityManager em) {
        String consulta = "SELECT fd.articulo FROM FacturaDetalle fd " +
                "WHERE fd.factura.id = :idFactura " +
                "ORDER BY fd.articulo.precioVenta DESC";
        TypedQuery<Articulo> query = em.createQuery(consulta, Articulo.class);
        query.setParameter("idFactura", 1L);
        query.setMaxResults(1);
        try {
            Articulo artMasCaro = query.getSingleResult();
            return artMasCaro;
        } catch (javax.persistence.NoResultException e) {
            return null;
        }
    }

    public static Long contarFacturas(EntityManager em) {
        String consulta = "SELECT COUNT(f) FROM Factura f";
        Query query = em.createQuery(consulta);
        Long cuentaTotal = (Long) query.getSingleResult();
        return cuentaTotal;
    }

    public static List<Factura> listarFacturasMayores(EntityManager em) {
        final Double montoBase = 4500.0;
        String consulta = "SELECT f FROM Factura f WHERE f.total > :total";
        TypedQuery<Factura> query = em.createQuery(consulta, Factura.class);
        query.setParameter("total", montoBase);
        List<Factura> facturas = query.getResultList();
        return facturas;
    }
    public static List<Factura> listarFacturasConArticuloDado(EntityManager em){
        final String denominacion = "Manzana";
        String consulta = "SELECT fd.factura FROM FacturaDetalle fd " +
                "WHERE fd.articulo.denominacion = :denominacion";
        TypedQuery<Factura> query = em.createQuery(consulta, Factura.class);
        query.setParameter("denominacion", denominacion);
        List<Factura> facturas = query.getResultList();
        return facturas;
    }
    public static List<Articulo> listarArtPorCodigoParcial(EntityManager em){
        String patron = "c";
        String consulta = "SELECT a FROM Articulo a WHERE a.codigo LIKE :codigo";
        TypedQuery<Articulo> query = em.createQuery(consulta, Articulo.class);
        query.setParameter("codigo", "%" + patron + "%");
        List<Articulo> listaFinal = query.getResultList();
        return listaFinal;
    }
    public static List<Articulo> listarArtPorPromedio(EntityManager em){
        String consulta = "SELECT art FROM Articulo art " +
                "WHERE art.precioVenta >(" +
                "SELECT AVG(art2.precioVenta) FROM Articulo art2)";
        Query query = em.createQuery(consulta);
        List<Articulo> listaArticulos = query.getResultList();
        return listaArticulos;
    }
    public static List<Cliente> listarClientesConFactura(EntityManager em){
        System.out.println("La clausula exists devuelve un tipo de datos booleano");
        System.out.println("Compruba si una subconsulta devuelve al menos una fila.");
        System.out.println("Ejemplo claro:");
        //A continuacion el ejemplo con la clausula exists.
        String consulta = "SELECT c FROM Cliente c " +
                "WHERE EXISTS(" +
                "SELECT f.id FROM Factura f WHERE f.cliente = c)";
        TypedQuery<Cliente> query = em.createQuery(consulta, Cliente.class);
        List<Cliente> clientes = query.getResultList();
        return clientes;
    }
}



