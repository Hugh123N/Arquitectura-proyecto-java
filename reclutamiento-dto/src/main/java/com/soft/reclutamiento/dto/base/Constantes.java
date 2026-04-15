package com.soft.reclutamiento.dto.base;

/**
 * Constantes de la aplicación
 * Equivalente a Constantes.cs del proyecto .NET
 */
public final class Constantes {

    private Constantes() {
        // Clase de constantes no instanciable
    }

    /**
     * Constantes para tipos de correlativos
     */
    public static final class CORRELATIVO_TIPO {
        public static final String SOLICITUD = "SL";

        private CORRELATIVO_TIPO() {
        }
    }

    /**
     * Constantes para tipos de documento
     */
    public static final class TIPO_DOCUMENTO {
        public static final String CODIGO_DNI = "1";
        public static final String CODIGO_RUC = "6";

        private TIPO_DOCUMENTO() {
        }
    }

    /**
     * Constantes para estados de requerimiento
     */
    public static final class ESTADO_REQUERIMIENTO {
        public static final String ACTIVO = "01";
        public static final String CERRADO = "02";
        public static final String CANCELADO = "03";

        private ESTADO_REQUERIMIENTO() {
        }
    }
}
